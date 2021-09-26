package xyz.apex.forge.dicemod;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.apex.forge.dicemod.client.ClientSetup;
import xyz.apex.forge.dicemod.config.ServerConfig;
import xyz.apex.forge.dicemod.container.PouchContainer;
import xyz.apex.forge.dicemod.data.ItemModelGenerator;
import xyz.apex.forge.dicemod.data.ItemTagGenerator;
import xyz.apex.forge.dicemod.data.LanguageGenerator;
import xyz.apex.forge.dicemod.data.RecipeGenerator;
import xyz.apex.forge.dicemod.item.DiceItemGroup;
import xyz.apex.forge.dicemod.item.PouchItem;

@Mod(DiceMod.ID)
public final class DiceMod
{
	public static final String ID = "dicemod";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final ServerConfig SERVER_CONFIG = new ServerConfig();
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, ID);
	public static final RegistryObject<Item> POUCH = ITEMS.register("pouch", PouchItem::new);
	public static final RegistryObject<ContainerType<PouchContainer>> POUCH_CONTAINER = CONTAINERS.register("pouch", () -> IForgeContainerType.create(PouchContainer::new));
	public static final Tags.IOptionalNamedTag<Item> DICE = ItemTags.createOptional(new ResourceLocation(ID, "dice"));
	public static final Tags.IOptionalNamedTag<Item> DICE_SIX_SIDED = ItemTags.createOptional(new ResourceLocation(ID, "dice/six_sided"));
	public static final Tags.IOptionalNamedTag<Item> DICE_TWENTY_SIDED = ItemTags.createOptional(new ResourceLocation(ID, "dice/twenty_sided"));
	public static final Tags.IOptionalNamedTag<Item> PAPER = ItemTags.createOptional(new ResourceLocation(ForgeVersion.MOD_ID, "paper")); // does not exist by default
	public static final ItemGroup ITEM_GROUP = new DiceItemGroup();

	public DiceMod()
	{
		LOGGER.info("Initializing mod...");
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientSetup::new);

		bus.addListener(this::onGatherData);

		Dice.register();
		ITEMS.register(bus);
		CONTAINERS.register(bus);

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
