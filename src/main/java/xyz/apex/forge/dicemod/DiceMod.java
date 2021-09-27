package xyz.apex.forge.dicemod;

import com.mojang.brigadier.CommandDispatcher;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.NonNullLazyValue;
import net.minecraft.command.CommandSource;
import net.minecraft.data.TagsProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.apex.forge.dicemod.command.RollCommand;
import xyz.apex.forge.dicemod.config.ServerConfig;
import xyz.apex.forge.dicemod.init.DContainers;
import xyz.apex.forge.dicemod.init.DItems;
import xyz.apex.forge.dicemod.init.DTags;
import xyz.apex.forge.dicemod.init.Dice;
import xyz.apex.forge.dicemod.item.DiceItemGroup;

@Mod(DiceMod.ID)
public final class DiceMod
{
	public static final String ID = "dicemod";
	public static final String DICE_ROLL_KEY = DiceMod.ID + ".dice.roll";
	public static final String DICE_ROLL_USING_KEY = DiceMod.ID + ".dice.using";
	public static final String DICE_ROLL_DESC_KEY = DiceMod.ID + ".dice.roll.desc";
	public static final String POUCH_SCREEN_TITLE_KEY = DiceMod.ID + ".pouch.title"; // narrator message for pouch screen

	public static final Logger LOGGER = LogManager.getLogger();
	public static final ServerConfig SERVER_CONFIG = new ServerConfig();
	public static final ItemGroup ITEM_GROUP = new DiceItemGroup();

	private static final NonNullLazyValue<Registrate> REGISTRATE_LAZY = new NonNullLazyValue<>(() -> Registrate.create(ID));

	public DiceMod()
	{
		LOGGER.info("Initializing mod...");


		registrate()
				.itemGroup(() -> ITEM_GROUP)
				.addDataGenerator(ProviderType.LANG, provider -> {
					provider.add(ITEM_GROUP, "Dice");
					provider.add(DICE_ROLL_KEY, "%s rolls %s");
					provider.add(DICE_ROLL_USING_KEY, "Using a %s");
					provider.add(DICE_ROLL_DESC_KEY, "Rolls a random number between %s & %s");
					provider.add(POUCH_SCREEN_TITLE_KEY, "Dice Pouch");
				})
				.addDataGenerator(ProviderType.ITEM_TAGS, provider -> {
					provider.tag(DTags.Items.PAPER).add(Items.PAPER);
					TagsProvider.Builder<Item> diceBuilder = provider.tag(DTags.Items.DICE).addTags(DTags.Items.DICE_SIX_SIDED, DTags.Items.DICE_TWENTY_SIDED);

					for(Dice dice : Dice.TYPES)
					{
						diceBuilder.addTag(dice.tag);
					}
				});

		DTags.register();
		DContainers.register();
		DItems.register();
		Dice.register();

		MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.spec);
	}

	private void onRegisterCommands(RegisterCommandsEvent event)
	{
		CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();

		RollCommand.register(dispatcher);
	}

	public static void sendMessageToPlayers(PlayerEntity thrower, ITextComponent component)
	{
		MinecraftServer server = thrower.getServer();

		if(server == null) // singleplayer?
		{
			thrower.sendMessage(component, thrower.getUUID());
			return;
		}

		for(PlayerEntity player : server.getPlayerList().getPlayers())
		{
			player.sendMessage(component, thrower.getUUID());
		}
	}

	public static Registrate registrate()
	{
		return REGISTRATE_LAZY.get();
	}
}
