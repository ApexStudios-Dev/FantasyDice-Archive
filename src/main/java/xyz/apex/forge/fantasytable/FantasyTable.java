package xyz.apex.forge.fantasytable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.NonNullLazyValue;
import net.minecraft.command.CommandSource;
import net.minecraft.data.TagsProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.RandomChance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.apex.forge.fantasytable.command.RollCommand;
import xyz.apex.forge.fantasytable.config.ServerConfig;
import xyz.apex.forge.fantasytable.init.*;
import xyz.apex.forge.fantasytable.item.FantasyTableItemGroup;
import xyz.apex.forge.fantasytable.util.registrate.CustomRegistrate;

import java.util.*;

@Mod(FantasyTable.ID)
public final class FantasyTable
{
	public static final UUID APEX_ID = UUID.fromString("43fd393b-879d-45ec-b2d5-ce8c4688ab66");
	public static final UUID FANTASY_ID = UUID.fromString("598535bd-f330-4123-b4d0-c6e618390477");
	public static final UUID TOBI_ID = UUID.fromString("ae3f6ca6-b28c-479b-9c97-4be7df600041");

	public static final String ID = "fantasytable";

	public static final String DICE_ROLL_KEY = ID + ".dice.roll";
	public static final String DICE_ROLL_USING_KEY = ID + ".dice.using";
	public static final String DICE_ROLL_DESC_KEY = ID + ".dice.roll.desc";
	public static final String COIN_FLIP_HEADS_KEY = ID + ".coin.flip.heads";
	public static final String COIN_FLIP_HEADS_SINGLE_KEY = ID + ".coin.flip.heads.single";
	public static final String COIN_FLIP_TAILS_KEY = ID + ".coin.flip.tails";
	public static final String COIN_FLIP_TAILS_SINGLE_KEY = ID + ".coin.flip.tails.single";
	public static final String COIN_FLIP_HEADS_AND_TAILS_KEY = ID + ".coin.flip.heads_tails";
	public static final String COIN_FLIP_KEY = ID + ".coin.flip";
	public static final String COIN_FLIP_USING_KEY = ID + ".coin.using";
	public static final String COIN_FLIP_DESC_KEY = ID + ".coin.flip.desc";

	public static final String POUCH_SCREEN_TITLE_KEY = ID + ".pouch.title"; // narrator message for pouch screen

	public static final Logger LOGGER = LogManager.getLogger();
	public static final ServerConfig SERVER_CONFIG = new ServerConfig();
	public static final ItemGroup ITEM_GROUP = new FantasyTableItemGroup();

	public static final ResourceLocation COIN_PREDICATE_NAME = new ResourceLocation(ID, "coin_stack");
	private static final NonNullLazyValue<CustomRegistrate> REGISTRATE_LAZY = CustomRegistrate.create(ID);
	private static final Map<ResourceLocation, Set<ResourceLocation>> INJECT_LOOTABLES = Maps.newHashMap();
	private static final ResourceLocation END_CITY_INJECT_TABLE = new ResourceLocation(FantasyTable.ID, "inject/end_city_treasure");

	public FantasyTable()
	{
		LOGGER.info("Initializing mod...");

		// @formatter:off
		registrate()
				.itemGroup(() -> ITEM_GROUP)
				.addDataGenerator(ProviderType.LOOT, provider -> {
					provider.addLootAction(LootParameterSets.CHEST, consumer -> {
						consumer.accept(
								END_CITY_INJECT_TABLE,
								LootTable.lootTable()
								         .withPool(
										         LootPool
												         .lootPool()
												         .setRolls(ConstantRange.exactly(1))
												         .when(RandomChance.randomChance(.25F))
												         .add(ItemLootEntry.lootTableItem(Coins.EMERALD.item::get))
								         )
						);
					});
				})
				.addDataGenerator(ProviderType.LANG, provider -> {
					provider.add(ITEM_GROUP, "Fantasy's Tabletop");
					provider.add(DICE_ROLL_KEY, "%s rolls %s");
					provider.add(DICE_ROLL_USING_KEY, "Using a %s");
					provider.add(DICE_ROLL_DESC_KEY, "Rolls a random number between %s & %s");
					provider.add(COIN_FLIP_HEADS_KEY, "%s Heads");
					provider.add(COIN_FLIP_HEADS_SINGLE_KEY, "Heads");
					provider.add(COIN_FLIP_TAILS_KEY, "%s Tails");
					provider.add(COIN_FLIP_TAILS_SINGLE_KEY, "Tails");
					provider.add(COIN_FLIP_HEADS_AND_TAILS_KEY, "%s Heads and %s Tails");
					provider.add(COIN_FLIP_KEY, "%s flipped %s (%s coins)");
					provider.add(COIN_FLIP_USING_KEY, "Using a %s");
					provider.add(COIN_FLIP_DESC_KEY, "Flip it and see if you will get Heads or Tails");
					provider.add(POUCH_SCREEN_TITLE_KEY, "Dice Pouch");
				})
				.addDataGenerator(ProviderType.ITEM_TAGS, provider -> {
					provider.tag(FTags.Items.PAPER).add(Items.PAPER);
					provider.tag(FTags.Items.BUTTONS_STONE).add(Items.POLISHED_BLACKSTONE_BUTTON, Items.STONE_BUTTON);
					provider.tag(FTags.Items.STONE_BUTTONS).add(Items.POLISHED_BLACKSTONE_BUTTON, Items.STONE_BUTTON);
					provider.tag(ItemTags.BUTTONS).addTags(FTags.Items.BUTTONS_STONE, FTags.Items.STONE_BUTTONS);

					TagsProvider.Builder<Item> diceBuilder = provider.tag(FTags.Items.DICE).addTags(FTags.Items.DICE_SIX_SIDED, FTags.Items.DICE_TWENTY_SIDED);

					for(Dice dice : Dice.TYPES)
					{
						diceBuilder.addTag(dice.tag);
					}
				});
		// @formatter:on

		FTags.register();
		FContainers.register();
		FBlocks.register();
		FItems.register();
		Dice.register();
		Coins.register();
		FVillagers.register();

		FantasyTable.injectLootTable(LootTables.END_CITY_TREASURE, END_CITY_INJECT_TABLE);

		MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
		MinecraftForge.EVENT_BUS.addListener(this::onLootTableLoad);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.spec);
	}

	private void onLootTableLoad(LootTableLoadEvent event)
	{
		Set<ResourceLocation> tables = INJECT_LOOTABLES.get(event.getName());

		if(tables != null && !tables.isEmpty())
		{
			LootTable table = event.getTable();
			tables.forEach(target -> table.addPool(LootPool.lootPool().add(TableLootEntry.lootTableReference(target)).name(table.toString()).build()));
			event.setTable(table);
		}
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

	public static CustomRegistrate registrate()
	{
		return REGISTRATE_LAZY.get();
	}

	public static void injectLootTable(ResourceLocation source, ResourceLocation... targets)
	{
		Collections.addAll(INJECT_LOOTABLES.computeIfAbsent(source, $ -> Sets.newHashSet()), targets);
	}
}
