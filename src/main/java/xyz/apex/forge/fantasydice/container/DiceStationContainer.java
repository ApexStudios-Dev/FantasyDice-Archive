package xyz.apex.forge.fantasydice.container;

import com.google.common.util.concurrent.Runnables;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import xyz.apex.forge.apexcore.lib.container.BaseContainer;
import xyz.apex.forge.fantasydice.init.FTBlocks;
import xyz.apex.forge.fantasydice.init.FTRecipes;
import xyz.apex.forge.fantasydice.item.crafting.DiceStationRecipe;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public final class DiceStationContainer extends BaseContainer
{
	private final IWorldPosCallable access;
	private final IntReferenceHolder selectedRecipeIndex = IntReferenceHolder.standalone();
	private final World level;
	private List<DiceStationRecipe> recipes = Collections.emptyList();
	private ItemStack input = ItemStack.EMPTY;
	private long lastSoundTime;
	private final Slot inputSlot;
	private final Slot resultSlot;
	private Runnable slotUpdateListener = Runnables.doNothing();

	public final IInventory container = new Inventory(1) {
		@Override
		public void setChanged()
		{
			super.setChanged();
			DiceStationContainer.this.slotsChanged(this);
			DiceStationContainer.this.slotUpdateListener.run();
		}
	};

	private final CraftResultInventory resultContainer = new CraftResultInventory();

	private boolean addSlots;

	public DiceStationContainer(@Nullable ContainerType<?> menuType, int windowId, PlayerInventory playerInventory)
	{
		this(menuType, windowId, playerInventory, IWorldPosCallable.NULL);
	}

	public DiceStationContainer(@Nullable ContainerType<?> menuType, int windowId, PlayerInventory playerInventory, IWorldPosCallable access)
	{
		super(menuType, windowId, playerInventory);

		this.access = access;
		level = playerInventory.player.level;
		inputSlot = new Slot(container, 0, 20, 33);
		resultSlot = new Slot(resultContainer, 1, 143, 33) {
			@Override
			public boolean mayPlace(ItemStack stack)
			{
				return false;
			}

			@Override
			public ItemStack onTake(PlayerEntity player, ItemStack stack)
			{
				stack.onCraftedBy(level, player, stack.getCount());
				DiceStationContainer.this.resultContainer.awardUsedRecipes(player);
				ItemStack input = DiceStationContainer.this.inputSlot.remove(1);

				if(!input.isEmpty())
					DiceStationContainer.this.setupResultSlot();

				access.execute((world, pos) -> {
					long l = world.getGameTime();

					if(DiceStationContainer.this.lastSoundTime != l)
					{
						world.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1F, 1F);
						DiceStationContainer.this.lastSoundTime = l;
					}
				});

				return super.onTake(player, stack);
			}
		};

		addSlots = true;
		addContainerSlots();
		addPlayerInventorySlots();
		addDataSlot(selectedRecipeIndex);
	}

	public int getSelectedRecipeIndex()
	{
		return selectedRecipeIndex.get();
	}

	public List<DiceStationRecipe> getRecipes()
	{
		return recipes;
	}

	public int getNumRecipes()
	{
		return recipes.size();
	}

	public boolean hasInputItem()
	{
		return inputSlot.hasItem() && !recipes.isEmpty();
	}

	private boolean isValidRecipeIndex(int recipeIndex)
	{
		return recipeIndex >= 0 && recipeIndex < recipes.size();
	}

	private void setupRecipeList(IInventory inventory, ItemStack stack)
	{
		recipes.clear();
		selectedRecipeIndex.set(-1);
		resultSlot.set(ItemStack.EMPTY);

		if(!stack.isEmpty())
			recipes = level.getRecipeManager().getRecipesFor(FTRecipes.DICE_STATION_RECIPE.asRecipeType(), inventory, level);
	}

	private void setupResultSlot()
	{
		int recipeIndex = selectedRecipeIndex.get();

		if(!recipes.isEmpty() && isValidRecipeIndex(recipeIndex))
		{
			DiceStationRecipe recipe = recipes.get(recipeIndex);
			resultContainer.setRecipeUsed(recipe);
			resultSlot.set(recipe.assemble(container));
		}
		else
			resultSlot.set(ItemStack.EMPTY);
	}

	public void registerUpdateListener(Runnable slotUpdateListener)
	{
		this.slotUpdateListener = slotUpdateListener;
	}

	@Override
	public boolean isPlayerValid(PlayerEntity player)
	{
		return super.isPlayerValid(player) && stillValid(access, player, FTBlocks.DICE_STATION.asBlock());
	}

	@Override
	public boolean clickMenuButton(PlayerEntity player, int id)
	{
		if(isValidRecipeIndex(id))
		{
			selectedRecipeIndex.set(id);
			setupResultSlot();
		}

		return true;
	}

	@Override
	public void slotsChanged(IInventory inventory)
	{
		ItemStack stack = inputSlot.getItem();

		if(stack.getItem() != input.getItem())
		{
			input = stack.copy();
			setupRecipeList(inventory, stack);
		}
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slot)
	{
		return slot.container != resultContainer && super.canTakeItemForPickAll(stack, slot);
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int slotIndex)
	{
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = slots.get(slotIndex);

		if(slot != null && slot.hasItem())
		{
			ItemStack stack1 = slot.getItem();
			Item item = stack1.getItem();
			stack = stack1.copy();

			if(slotIndex == 1)
			{
				item.onCraftedBy(stack1, level, player);

				if(!moveItemStackTo(stack1, 2, 38, true))
					return ItemStack.EMPTY;

				slot.onQuickCraft(stack1, stack);
			}
			else if(slotIndex == 0)
			{
				if(!moveItemStackTo(stack1, 2, 38, false))
					return ItemStack.EMPTY;
			}
			else if(level.getRecipeManager().getRecipeFor(FTRecipes.DICE_STATION_RECIPE.asRecipeType(), new Inventory(stack1), level).isPresent())
			{
				if(!moveItemStackTo(stack1, 0, 1, false))
					return ItemStack.EMPTY;
			}
			else if(slotIndex >= 2 && slotIndex < 29)
			{
				if(!moveItemStackTo(stack1, 29, 38, false))
					return ItemStack.EMPTY;
			}
			else if(slotIndex >= 29 && slotIndex < 38 && !moveItemStackTo(stack1, 2, 29, false))
				return ItemStack.EMPTY;

			if(stack1.isEmpty())
				slot.set(ItemStack.EMPTY);

			slot.setChanged();

			if(stack1.getCount() == stack.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, stack1);
			broadcastChanges();
		}

		return stack;
	}

	@Override
	public void removed(PlayerEntity player)
	{
		super.removed(player);
		resultContainer.removeItemNoUpdate(1);
		access.execute((world, pos) -> clearContainer(player, level, container));
	}

	@Override
	protected void addPlayerInventorySlots()
	{
		if(addSlots)
			super.addPlayerInventorySlots();
	}

	@Override
	protected void addContainerSlots()
	{
		if(addSlots)
		{
			addSlot(inputSlot);
			addSlot(resultSlot);
		}
	}
}
