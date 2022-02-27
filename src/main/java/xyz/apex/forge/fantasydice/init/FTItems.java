package xyz.apex.forge.fantasydice.init;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;

import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraftforge.common.Tags;

import xyz.apex.forge.fantasydice.item.CoinItem;
import xyz.apex.forge.fantasydice.item.PouchItem;
import xyz.apex.forge.utility.registrator.entry.ItemEntry;
import xyz.apex.forge.utility.registrator.provider.RegistrateLangExtProvider;

public final class FTItems
{
	private static final FTRegistry REGISTRY = FTRegistry.getRegistry();

	public static final ItemEntry<PouchItem> POUCH = REGISTRY
			.item("pouch", PouchItem::new)
				.lang("Dice Pouch")
				.lang(RegistrateLangExtProvider.EN_GB, "Dice Pouch")
				.color(() -> () -> (stack, tintIndex) -> tintIndex == 0 ? ((IDyeableArmorItem) stack.getItem()).getColor(stack) : 0xFFFFFFFF)
				.recipe((ctx, provider) -> ShapedRecipeBuilder
						.shaped(ctx::get, 1)
						.define('S', Tags.Items.STRING)
						.define('L', Tags.Items.LEATHER)
						.pattern(" S ")
						.pattern("L L")
						.pattern(" L ")
						.unlockedBy("has_leather", RegistrateRecipeProvider.hasItem(Tags.Items.LEATHER))
						.unlockedBy("has_string", RegistrateRecipeProvider.hasItem(Tags.Items.STRING))
						.save(provider, ctx.getId())
				)
				.model((ctx, provider) -> provider.generated(ctx, provider.modLoc("item/pouch/pouch"), provider.modLoc("item/pouch/string")))
				.stacksTo(1)
			.register();

	public static final ItemEntry<CoinItem> IRON_COIN = REGISTRY
			.item("iron_coin", CoinItem::new)
				.lang("Iron Coin")
				.lang(RegistrateLangExtProvider.EN_GB, "Iron Coin")
				.recipe((ctx, provider) -> ShapelessRecipeBuilder
						.shapeless(ctx::get, 1)
						.requires(Tags.Items.NUGGETS_IRON)
						.requires(Tags.Items.NUGGETS_IRON)
						.group("coin")
						.unlockedBy("has_iron_nugget", RegistrateRecipeProvider.hasItem(Tags.Items.NUGGETS_IRON))
						.save(provider, ctx.getId()))
				.stacksTo(8)
				.tag(FTTags.Items.COINS)
			.register();

	public static final ItemEntry<CoinItem> GOLDEN_COIN = REGISTRY
			.item("golden_coin", CoinItem::new)
				.lang("Golden Coin")
				.lang(RegistrateLangExtProvider.EN_GB, "Golden Coin")
				.recipe((ctx, provider) -> ShapelessRecipeBuilder
						.shapeless(ctx::get, 1)
						.requires(Tags.Items.NUGGETS_GOLD)
						.requires(Tags.Items.NUGGETS_GOLD)
						.group("coin")
						.unlockedBy("has_golden_nugget", RegistrateRecipeProvider.hasItem(Tags.Items.NUGGETS_GOLD))
						.save(provider, ctx.getId()))
				.stacksTo(8)
				.tag(FTTags.Items.COINS)
			.register();

	static void bootstrap()
	{
	}
}
