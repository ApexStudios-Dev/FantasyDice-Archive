package xyz.apex.forge.fantasytable.handler;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.NonNullPredicate;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.init.Coins;
import xyz.apex.forge.fantasytable.init.FTags;
import xyz.apex.forge.fantasytable.init.FVillagers;
import xyz.apex.forge.fantasytable.util.DiceHelper;

import java.util.List;

@Mod.EventBusSubscriber(modid = FantasyTable.ID)
public final class LoadDiceHandler
{
	@SubscribeEvent
	public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
	{
		LivingEntity living = event.getEntityLiving();

		if(living instanceof VillagerEntity)
		{
			VillagerEntity villager = (VillagerEntity) living;

			if(FVillagers.GAMBLER_PROFESSION.is(villager.getVillagerData().getProfession()))
			{
				// emerald coins touching villager
				List<ItemEntity> coins = getTouching(villager, Coins.EMERALD.item::isIn);
				// any unloaded dice touching villager
				List<ItemEntity> dice = getTouching(villager, item -> item.getItem().is(FTags.Items.DICE) && !DiceHelper.isDieLoaded(item));

				// 1 of each must exist
				if(coins.isEmpty() || dice.isEmpty())
					return;

				for(ItemEntity coinEntity : coins)
				{
					if(dice.isEmpty())
						break;

					ItemEntity diceEntity = dice.get(0);

					ItemStack coinStack = coinEntity.getItem();
					ItemStack diceStack = diceEntity.getItem();

					ItemStack loadedDice = DiceHelper.setLoadedState(diceStack.copy(), true);
					loadedDice.setCount(Math.min(coinStack.getCount(), diceStack.getCount()));

					throwItem(loadedDice, villager);

					coinStack.shrink(loadedDice.getCount());
					diceStack.shrink(loadedDice.getCount());

					if(coinStack.isEmpty())
						coinEntity.remove();
					if(diceStack.isEmpty())
						diceEntity.remove();
				}
			}
		}
	}

	private static void throwItem(ItemStack stack, LivingEntity thrower)
	{
		ItemEntity loadedDiceEntity = new ItemEntity(thrower.level, thrower.getX(), thrower.getEyeY(), thrower.getZ(), stack);
		loadedDiceEntity.setPickUpDelay(40);
		loadedDiceEntity.setThrower(thrower.getUUID());

		/*float f = thrower.getRandom().nextFloat() * .5F;
		float f1 = thrower.getRandom().nextFloat() * ((float) Math.PI * 2F);
		loadedDiceEntity.setDeltaMovement(-MathHelper.sign(f1) * f, .2F, MathHelper.cos(f1) * f);*/

		thrower.level.addFreshEntity(loadedDiceEntity);
	}

	private static List<ItemEntity> getTouching(LivingEntity entity, NonNullPredicate<ItemStack> predicate)
	{
		return entity.level.getEntities(EntityType.ITEM, entity.getBoundingBox().inflate(4), item -> {
			ItemStack stack = item.getItem();
			return !stack.isEmpty() && predicate.test(stack);
		});
	}
}
