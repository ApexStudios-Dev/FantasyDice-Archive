package xyz.apex.forge.fantasytable.init;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.Rarity;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.DistExecutor;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.item.CoinItem;

public enum Coins
{
	IRON(DStrings.COIN_IRON, TextFormatting.WHITE, Tags.Items.NUGGETS_IRON),
	GOLD(DStrings.COIN_GOLD, TextFormatting.YELLOW, Tags.Items.NUGGETS_GOLD),
	;

	public static final Coins[] TYPES = values();

	public final ITag<Item> craftingItem;
	public final ItemEntry<CoinItem> item;
	public final String name;
	public final TextFormatting color;
	public final Rarity rarity;

	Coins(String name, TextFormatting color, ITag<Item> craftingItem)
	{
		this.name = name;
		this.color = color;
		this.craftingItem = craftingItem;
		rarity = Rarity.create(FantasyTable.ID + ':' + name, color);

		// formatter:off
		item = FantasyTable
				.registrate()
				.object("coin_" + name)
				.item(CoinItem::new)
				.lang(RegistrateLangProvider.toEnglishName(name) + " Coin")
				.tag(DTags.Items.COINS)
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
				.recipe((ctx, provider) ->
						ShapelessRecipeBuilder.shapeless(ctx::get, 4)
						                      .requires(craftingItem)
								              .requires(craftingItem)
								              .requires(craftingItem)
								              .requires(craftingItem)
								              .group("coins/" + name)
								              .unlockedBy("has_nugget", RegistrateRecipeProvider.hasItem(craftingItem))
								              .save(provider, new ResourceLocation(FantasyTable.ID, "coins/" + name))
				)
				.onRegister(item -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> postRegisterClient(item)))
				.register();
		// formatter:on
	}

	@OnlyIn(Dist.CLIENT)
	private void postRegisterClient(CoinItem item)
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

	@Deprecated // internal use only
	public static void register() { }
}
