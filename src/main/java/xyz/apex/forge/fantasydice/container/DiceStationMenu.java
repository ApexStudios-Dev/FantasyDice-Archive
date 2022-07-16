package xyz.apex.forge.fantasydice.container;

import com.google.common.util.concurrent.Runnables;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import xyz.apex.forge.apexcore.lib.container.BaseMenu;
import xyz.apex.forge.fantasydice.init.FTBlocks;
import xyz.apex.forge.fantasydice.init.FTRecipes;
import xyz.apex.forge.fantasydice.item.crafting.DiceStationRecipe;

import java.util.Collections;
import java.util.List;

public final class DiceStationMenu extends BaseMenu
{
	private final ContainerLevelAccess access;
	private final DataSlot selectedRecipeIndex = DataSlot.standalone();
	private final Level level;
	private List<DiceStationRecipe> recipes = Collections.emptyList();
	private ItemStack input = ItemStack.EMPTY;
	private long lastSoundTime;
	private final Slot inputSlot;
	private final Slot resultSlot;
	private Runnable slotUpdateListener = Runnables.doNothing();

	public final Container container = new SimpleContainer(1) {
		@Override
		public void setChanged()
		{
			super.setChanged();
			DiceStationMenu.this.slotsChanged(this);
			DiceStationMenu.this.slotUpdateListener.run();
		}
	};

	private final ResultContainer resultContainer = new ResultContainer();

	public DiceStationMenu(@Nullable MenuType<? extends DiceStationMenu> menuType, int windowId, Inventory playerInventory, FriendlyByteBuf buffer)
	{
		super(menuType, windowId, playerInventory, buffer);

		this.access = buffer.readBoolean() ? ContainerLevelAccess.create(playerInventory.player.level, pos) : ContainerLevelAccess.NULL;
		level = playerInventory.player.level;
		inputSlot = addSlot(new Slot(container, 0, 20, 33));
		resultSlot = addSlot(new Slot(resultContainer, 1, 143, 33) {
			@Override
			public boolean mayPlace(ItemStack stack)
			{
				return false;
			}

			@Override
			public void onTake(Player player, ItemStack stack)
			{
				stack.onCraftedBy(level, player, stack.getCount());
				DiceStationMenu.this.resultContainer.awardUsedRecipes(player);
				var input = DiceStationMenu.this.inputSlot.remove(1);

				if(!input.isEmpty())
					DiceStationMenu.this.setupResultSlot();

				access.execute((world, pos) -> {
					var l = world.getGameTime();

					if(DiceStationMenu.this.lastSoundTime != l)
					{
						world.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1F, 1F);
						DiceStationMenu.this.lastSoundTime = l;
					}
				});
			}
		});

		bindPlayerInventory(this, 8, 84);
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

	private void setupRecipeList(Container inventory, ItemStack stack)
	{
		recipes.clear();
		selectedRecipeIndex.set(-1);
		resultSlot.set(ItemStack.EMPTY);

		if(!stack.isEmpty())
			recipes = level.getRecipeManager().getRecipesFor(FTRecipes.DICE_STATION_RECIPE_TYPE.get(), inventory, level);
	}

	private void setupResultSlot()
	{
		var recipeIndex = selectedRecipeIndex.get();

		if(!recipes.isEmpty() && isValidRecipeIndex(recipeIndex))
		{
			var recipe = recipes.get(recipeIndex);
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
	public boolean stillValid(Player player)
	{
		return super.stillValid(player) && stillValid(access, player, FTBlocks.DICE_STATION.get());
	}

	@Override
	public boolean clickMenuButton(Player player, int id)
	{
		if(isValidRecipeIndex(id))
		{
			selectedRecipeIndex.set(id);
			setupResultSlot();
		}

		return true;
	}

	@Override
	public void slotsChanged(Container inventory)
	{
		var stack = inputSlot.getItem();

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
	public ItemStack quickMoveStack(Player player, int slotIndex)
	{
		var stack = ItemStack.EMPTY;
		var slot = slots.get(slotIndex);

		if(slot != null && slot.hasItem())
		{
			var stack1 = slot.getItem();
			var item = stack1.getItem();
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
			else if(level.getRecipeManager().getRecipeFor(FTRecipes.DICE_STATION_RECIPE_TYPE.get(), new SimpleContainer(stack1), level).isPresent())
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
	public void removed(Player player)
	{
		super.removed(player);
		resultContainer.removeItemNoUpdate(1);
		access.execute((world, pos) -> clearContainer(player, container));
	}
}