package xyz.apex.forge.dicemod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import xyz.apex.forge.dicemod.DiceMod;
import xyz.apex.forge.dicemod.container.PouchContainer;
import xyz.apex.forge.dicemod.init.DContainers;
import xyz.apex.forge.dicemod.init.DStrings;

public class PouchItem extends Item
{
	public PouchItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		ItemStack pouch = player.getItemInHand(hand);

		if(player instanceof ServerPlayerEntity)
		{
			DContainers.POUCH_CONTAINER.open(
					(ServerPlayerEntity) player,
					pouch.hasCustomHoverName() ? pouch.getHoverName() : new TranslationTextComponent(DiceMod.POUCH_SCREEN_TITLE_KEY),
					(windowId, playerInventory, plr) -> new PouchContainer(DContainers.POUCH_CONTAINER.get(), windowId, playerInventory, new Inv(pouch))
			);
		}

		return ActionResult.sidedSuccess(pouch, world.isClientSide);
	}

	public static final class Inv extends Inventory
	{
		private final ItemStack pouch;

		public Inv(ItemStack pouch)
		{
			super(PouchContainer.SLOTS);

			this.pouch = pouch;
		}

		@Override
		public void startOpen(PlayerEntity player)
		{
			CompoundNBT invTag = pouch.getTagElement(DStrings.NBT_POUCH_INVENTORY);

			if(invTag != null)
			{
				ListNBT slotTag = invTag.getList(DStrings.NBT_ITEMS, Constants.NBT.TAG_COMPOUND);

				for(int i = 0; i < slotTag.size(); i++)
				{
					CompoundNBT itemTag = slotTag.getCompound(i);
					int slotIndex = itemTag.getByte(DStrings.NBT_SLOT) & 255;

					if(slotIndex >= 0 && slotIndex < getContainerSize())
						setItem(slotIndex, ItemStack.of(itemTag));
				}
			}
		}

		@Override
		public void stopOpen(PlayerEntity player)
		{
			CompoundNBT invTag = new CompoundNBT();
			ListNBT slotTag = new ListNBT();

			for(int i = 0; i < getContainerSize(); i++)
			{
				ItemStack stack = getItem(i);

				if(!stack.isEmpty())
				{
					CompoundNBT itemTag = new CompoundNBT();
					itemTag.putByte(DStrings.NBT_SLOT, (byte) i);
					stack.save(itemTag);
					slotTag.add(itemTag);
				}
			}

			invTag.put(DStrings.NBT_ITEMS, slotTag);
			pouch.addTagElement(DStrings.NBT_POUCH_INVENTORY, invTag);
		}
	}
}
