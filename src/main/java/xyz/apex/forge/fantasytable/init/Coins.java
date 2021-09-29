package xyz.apex.forge.fantasytable.init;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.fml.DistExecutor;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.item.DiceItem;

import java.util.Arrays;

public enum Coins
{
	IRON(FStrings.COIN_IRON, TextFormatting.WHITE),
	GOLD(FStrings.COIN_GOLD, TextFormatting.YELLOW),
	EMERALD(FStrings.COIN_EMERALD, TextFormatting.GREEN)
	;

	public static final Coins[] TYPES = values();

	public final ItemEntry<DiceItem> item;
	public final String name;
	public final TextFormatting color;
	public final Rarity rarity;

	Coins(String name, TextFormatting color)
	{
		this.name = name;
		this.color = color;
		rarity = Rarity.create(FantasyTable.ID + ':' + name, color);

		// formatter:off
		item = FantasyTable
				.registrate()
				.object("coin_" + name)
				.item(DiceItem::new)
				.lang(RegistrateLangProvider.toEnglishName(name) + " Coin")
				.tag(FTags.Items.COINS)
				.properties(properties -> properties.rarity(rarity).stacksTo(12))
				.model((ctx, provider) ->
						provider.getBuilder(provider.name(ctx))
								.parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))

						        // single
								.override()
									.predicate(FantasyTable.COIN_PREDICATE_NAME, 0F)
									.model(
											provider.getBuilder(provider.name(ctx) + "_single")
											        .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
													.texture("layer0", new ResourceLocation(FantasyTable.ID, "item/coin/" + name + "_1"))
									)
								.end()

						        // double
								.override()
									.predicate(FantasyTable.COIN_PREDICATE_NAME, .5F)
									.model(
											provider.getBuilder(provider.name(ctx) + "_double")
											        .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
											        .texture("layer0", new ResourceLocation(FantasyTable.ID, "item/coin/" + name + "_2"))
									)
								.end()

						        // triple
								.override()
									.predicate(FantasyTable.COIN_PREDICATE_NAME, 1F)
									.model(
											provider.getBuilder(provider.name(ctx) + "_triple")
											        .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
											        .texture("layer0", new ResourceLocation(FantasyTable.ID, "item/coin/" + name + "_3"))
									)
								.end()
				)
				.onRegister(item -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> postRegisterClient(item)))
				.register();
		// formatter:on
	}

	@OnlyIn(Dist.CLIENT)
	private void postRegisterClient(DiceItem item)
	{
		ItemModelsProperties.register(item, FantasyTable.COIN_PREDICATE_NAME, (stack, world, entity) -> {
			int count = stack.getCount();

			// 1 == triple
			// .5 == double
			// 0 == single

			if(count <= 4)
				return 0F;
			if(count <= 8)
				return .5F;
			return 1F;
		});
	}

	public IFormattableTextComponent createTextComponent(PlayerEntity thrower, ItemStack coin, int[] rolls)
	{
		int heads = 0;
		int tails = 0;

		for(int roll : rolls)
		{
			if(roll > 1)
				heads++;
			else
				tails++;
		}

		// @formatter:off
		return new TranslationTextComponent(
				FantasyTable.COIN_FLIP_KEY,
				thrower.getDisplayName(),
				buildHeadsTailsComponent(heads, tails).withStyle(style -> style.withItalic(true).withColor(color)),
				coin.getCount()
		)
				.withStyle(style ->
						style.withHoverEvent(
								new HoverEvent(
										HoverEvent.Action.SHOW_TEXT,
										new TranslationTextComponent(FantasyTable.COIN_FLIP_USING_KEY, coin.getHoverName()).withStyle(hoverStyle -> style.withColor(color))
								)
						)
				);
		// @formatter:on
	}

	public IFormattableTextComponent createItemTooltipComponent(ItemStack die, int min, int sides)
	{
		return new TranslationTextComponent(FantasyTable.COIN_FLIP_DESC_KEY).withStyle(style -> style.withColor(TextFormatting.DARK_GRAY).withItalic(true));
	}

	private static IFormattableTextComponent buildHeadsTailsComponent(int heads, int tails)
	{
		/*
		provider.add(COIN_FLIP_HEADS_KEY, "%s Heads");
		provider.add(COIN_FLIP_HEADS_SINGLE_KEY, "Heads");

		provider.add(COIN_FLIP_TAILS_KEY, "%s Tails");
		provider.add(COIN_FLIP_TAILS_SINGLE_KEY, "Tails");

		provider.add(COIN_FLIP_HEADS_AND_TAILS_KEY, "%s Heads and %s Tails");
		*/

		// FantasyTable.LOGGER.info("Heads: {}, Tails: {}", heads, tails);

		// heads is plural & no tails
		if(heads != 1 && tails == 0)
			return new TranslationTextComponent(FantasyTable.COIN_FLIP_HEADS_KEY, heads);
		// tails is plural & no heads
		if(tails != 1 && heads == 0)
			return new TranslationTextComponent(FantasyTable.COIN_FLIP_TAILS_KEY, tails);

		// heads in singular & no tails
		if(heads == 1 && tails == 0)
			return new TranslationTextComponent(FantasyTable.COIN_FLIP_HEADS_SINGLE_KEY);
		// tails is singular & no heads
		if(tails == 1 && heads == 0)
			return new TranslationTextComponent(FantasyTable.COIN_FLIP_TAILS_SINGLE_KEY);

		// display both values
		return new TranslationTextComponent(FantasyTable.COIN_FLIP_HEADS_AND_TAILS_KEY, heads, tails);
	}

	public static Coins byItem(ItemStack stack)
	{
		return Arrays.stream(TYPES).filter(coin -> coin.item.isIn(stack)).findFirst().orElse(IRON);
	}

	@Deprecated // internal use only
	public static void register() { }
}
