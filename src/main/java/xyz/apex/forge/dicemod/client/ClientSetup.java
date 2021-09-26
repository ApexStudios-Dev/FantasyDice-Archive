package xyz.apex.forge.dicemod.client;

import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.apex.forge.dicemod.Dice;
import xyz.apex.forge.dicemod.DiceMod;

@OnlyIn(Dist.CLIENT)
public final class ClientSetup
{
	public ClientSetup()
	{
		DiceMod.LOGGER.info("Initializing mod Client Side...");
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		bus.addListener(this::onItemColors);
	}

	private void onItemColors(ColorHandlerEvent.Item event)
	{
		ItemColors itemColors = event.getItemColors();

		itemColors.register((stack, tintIndex) -> ((IDyeableArmorItem) stack.getItem()).getColor(stack), Dice.PAPER.six_sided_die::get, Dice.PAPER.twenty_sided_die::get);
	}
}
