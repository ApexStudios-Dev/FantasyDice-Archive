package xyz.apex.minecraft.fantasydice.common.menu;

import com.google.common.collect.Lists;
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
import xyz.apex.minecraft.apexcore.common.lib.menu.EnhancedSlot;
import xyz.apex.minecraft.apexcore.common.lib.menu.SimpleContainerMenu;
import xyz.apex.minecraft.fantasydice.common.FantasyDice;
import xyz.apex.minecraft.fantasydice.common.recipe.DiceStationRecipe;

import java.util.Collections;
import java.util.List;

public final class DiceStationMenu extends AbstractContainerMenu
{
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_RESULT = 1;

    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private final ContainerLevelAccess levelAccess;
    private final Level level;
    private List<DiceStationRecipe> recipes = Lists.newArrayList();
    private ItemStack input = ItemStack.EMPTY;
    private long lastSoundTime = 0L;
    private final Slot inputSlot;
    private final Slot resultSlot;
    private Runnable slotUpdateListener = () -> { };
    private final InputContainer inputContainer = new InputContainer();
    private final ResultContainer resultContainer = new ResultContainer();

    public DiceStationMenu(MenuType<? extends DiceStationMenu> menuType, int syncId, Inventory playerInventory, ContainerLevelAccess levelAccess)
    {
        super(menuType, syncId);

        this.levelAccess = levelAccess;
        level = playerInventory.player.level();

        inputSlot = addSlot(new EnhancedSlot(inputContainer, SLOT_INPUT, 20, 33));
        resultSlot = addSlot(new ResultSlot());

        SimpleContainerMenu.bindPlayerInventory(playerInventory, 8, 84, this::addSlot);

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

    public void registerUpdateListener(Runnable slotUpdateListener)
    {
        this.slotUpdateListener = slotUpdateListener;
    }

    private boolean isValidRecipeIndex(int index)
    {
        return index >= 0 && index < recipes.size();
    }

    private void setupRecipeList(Container container, ItemStack stack)
    {
        recipes.clear();
        selectedRecipeIndex.set(-1);
        resultSlot.set(ItemStack.EMPTY);

        if(!stack.isEmpty())
            recipes.addAll(level.getRecipeManager().getRecipesFor(FantasyDice.DICE_STATION_RECIPE_TYPE.value(), container, level));
    }

    private void setupResultSlot()
    {
        var index = selectedRecipeIndex.get();
        resultSlot.set(ItemStack.EMPTY);

        if(!recipes.isEmpty() && isValidRecipeIndex(index))
        {
            var recipe = recipes.get(index);
            var result = recipe.assemble(inputContainer, level.registryAccess());

            if(result.isItemEnabled(level.enabledFeatures()))
            {
                resultContainer.setRecipeUsed(recipe);
                resultSlot.set(result);
            }
        }

        broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player)
    {
        return AbstractContainerMenu.stillValid(levelAccess, player, FantasyDice.DICE_STATION_BLOCK.value());
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
    public void slotsChanged(Container container)
    {
        var stack = inputSlot.getItem();

        if(!stack.is(input.getItem()))
        {
            input = stack.copy();
            setupRecipeList(container, stack);
        }
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot)
    {
        return slot.container != resultContainer && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        var stack = ItemStack.EMPTY;
        var slot = slots.get(index);

        if(slot.hasItem())
        {
           var stack2 = slot.getItem();
           var item = stack2.getItem();
           stack = stack2.copy();

           if(index == 1)
           {
               item.onCraftedBy(stack2, player.level(), player);

               if(!moveItemStackTo(stack2, 2, 38, true))
                   return ItemStack.EMPTY;

               slot.onQuickCraft(stack2, stack);
           }
           else if(index == 0)
           {
               if(!this.moveItemStackTo(stack2, 2, 38, false))
                   return ItemStack.EMPTY;
           }
           else if(this.level.getRecipeManager().getRecipeFor(FantasyDice.DICE_STATION_RECIPE_TYPE.value(), new SimpleContainer(stack2), this.level).isPresent())
           {
               if(!this.moveItemStackTo(stack2, 0, 1, false))
                   return ItemStack.EMPTY;
           }
           else if(index >= 2 && index < 29)
           {
               if(!this.moveItemStackTo(stack2, 29, 38, false))
                   return ItemStack.EMPTY;
           }
           else if(index >= 29 && index < 38)
           {
               if(!this.moveItemStackTo(stack2, 2, 29, false))
                   return ItemStack.EMPTY;
           }

           if(stack2.isEmpty())
               slot.setByPlayer(ItemStack.EMPTY);

           slot.setChanged();

           if(stack2.getCount() == stack.getCount())
               return ItemStack.EMPTY;

           slot.onTake(player, stack2);
           broadcastChanges();
        }

        return stack;
    }

    @Override
    public void removed(Player player)
    {
        super.removed(player);
        resultContainer.removeItemNoUpdate(1);
        levelAccess.execute((level1, pos) -> clearContainer(player, inputContainer));
    }

    public static DiceStationMenu create(int syncId, Inventory inventory, ContainerLevelAccess levelAccess)
    {
        return new DiceStationMenu(FantasyDice.DICE_STATION_MENU.value(), syncId, inventory, levelAccess);
    }

    public static DiceStationMenu forNetwork(MenuType<? extends DiceStationMenu> menuType, int syncId, Inventory inventory, FriendlyByteBuf buffer)
    {
        return new DiceStationMenu(menuType, syncId, inventory, ContainerLevelAccess.NULL);
    }

    private final class InputContainer extends SimpleContainer
    {
        private InputContainer()
        {
            super(1);
        }

        @Override
        public void setChanged()
        {
            super.setChanged();

            slotsChanged(this);
            slotUpdateListener.run();
        }
    }

    private final class ResultSlot extends EnhancedSlot
    {
        private ResultSlot()
        {
            super(resultContainer, SLOT_RESULT, 143, 33);
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack)
        {
            stack.onCraftedBy(player.level(), player, stack.getCount());
            resultContainer.awardUsedRecipes(player, Collections.singletonList(inputSlot.getItem()));
            var input = inputSlot.remove(1);

            if(!input.isEmpty())
                setupResultSlot();

            levelAccess.execute((level, pos) -> {
                var gameTime = level.getGameTime();

                if(lastSoundTime != gameTime)
                {
                    level.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1F, 1F);
                    lastSoundTime = gameTime;
                }
            });

            super.onTake(player, stack);
        }
    }
}
