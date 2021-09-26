package xyz.apex.forge.dicemod;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.apex.forge.dicemod.config.ServerConfig;
import xyz.apex.forge.dicemod.data.ItemModelGenerator;
import xyz.apex.forge.dicemod.data.ItemTagGenerator;
import xyz.apex.forge.dicemod.data.LanguageGenerator;
import xyz.apex.forge.dicemod.data.RecipeGenerator;
import xyz.apex.forge.dicemod.item.DiceItemGroup;

@Mod(DiceMod.ID)
public final class DiceMod
{
	public static final String ID = "dicemod";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final ServerConfig SERVER_CONFIG = new ServerConfig();
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
	public static final Tags.IOptionalNamedTag<Item> DICE = ItemTags.createOptional(new ResourceLocation(ID, "dice"));
	public static final Tags.IOptionalNamedTag<Item> DICE_SIX_SIDED = ItemTags.createOptional(new ResourceLocation(ID, "dice/six_sided"));
	public static final Tags.IOptionalNamedTag<Item> DICE_TWENTY_SIDED = ItemTags.createOptional(new ResourceLocation(ID, "dice/twenty_sided"));
	public static final ItemGroup ITEM_GROUP = new DiceItemGroup();

	public DiceMod()
	{
		LOGGER.info("Initializing mod...");
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		bus.addListener(this::onGatherData);

		Dice.register();
		ITEMS.register(bus);

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.spec);
	}

	private void onGatherData(GatherDataEvent event)
	{
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();

		generator.addProvider(new LanguageGenerator(generator));
		generator.addProvider(new RecipeGenerator(generator));
		generator.addProvider(new ItemModelGenerator(generator, fileHelper));

		BlockTagsProvider blockTagsProvider = new BlockTagsProvider(generator, ID, fileHelper) {
			@Override
			protected void addTags()
			{
			}
		};

		generator.addProvider(blockTagsProvider);
		generator.addProvider(new ItemTagGenerator(generator, blockTagsProvider, fileHelper));
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
}
