package xyz.apex.forge.dicemod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import xyz.apex.forge.dicemod.DiceMod;
import xyz.apex.forge.dicemod.container.PouchContainer;
import xyz.apex.forge.dicemod.data.LanguageGenerator;

public class PouchItem extends Item
{
	public PouchItem(Properties properties)
	{
		super(properties);
	}

	@Deprecated // internal use only
	public PouchItem()
	{
		this(new Properties().stacksTo(1).tab(DiceMod.ITEM_GROUP));
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		ItemStack pouch = player.getItemInHand(hand);

		if(player instanceof ServerPlayerEntity)
		{
			NetworkHooks.openGui(
					(ServerPlayerEntity) player,
					new SimpleNamedContainerProvider(
							(windowId, playerInventory, plr) -> new PouchContainer(DiceMod.POUCH_CONTAINER.get(), playerInventory, new Inv(pouch), windowId),
							pouch.hasCustomHoverName() ? pouch.getHoverName() : new TranslationTextComponent(LanguageGenerator.POUCH_SCREEN_TITLE_KEY)
					),
					data -> data.writeEnum(hand)
			);
		}

		return ActionResult.sidedSuccess(pouch, world.isClientSide);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return false;
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
			CompoundNBT invTag = pouch.getTagElement("PouchInventory");

			if(invTag != null)
			{
				ListNBT slotTag = invTag.getList("Items", Constants.NBT.TAG_COMPOUND);

				for(int i = 0; i < slotTag.size(); i++)
				{
					CompoundNBT itemTag = slotTag.getCompound(i);
					int slotIndex = itemTag.getByte("Slot") & 255;

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
					itemTag.putByte("Slot", (byte) i);
					stack.save(itemTag);
					slotTag.add(itemTag);
				}
			}

			invTag.put("Items", slotTag);
			pouch.addTagElement("PouchInventory", invTag);
		}
	}
}
