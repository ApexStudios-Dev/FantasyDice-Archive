package xyz.apex.forge.fantasydice.init;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;

import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.DyeableLeatherItem;

import xyz.apex.forge.commonality.init.ItemTags;
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
				.color(() -> () -> (stack, tintIndex) -> tintIndex == 0 ? ((DyeableLeatherItem) stack.getItem()).getColor(stack) : 0xFFFFFFFF)
				.recipe((ctx, provider) -> ShapedRecipeBuilder
						.shaped(ctx::get, 1)
						.define('S', ItemTags.Forge.STRING)
						.define('L', ItemTags.Forge.LEATHER)
						.pattern(" S ")
						.pattern("L L")
						.pattern(" L ")
						.unlockedBy("has_leather", RegistrateRecipeProvider.has(ItemTags.Forge.LEATHER))
						.unlockedBy("has_string", RegistrateRecipeProvider.has(ItemTags.Forge.STRING))
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
						.requires(ItemTags.Forge.NUGGETS_IRON)
						.requires(ItemTags.Forge.NUGGETS_IRON)
						.group("coin")
						.unlockedBy("has_iron_nugget", RegistrateRecipeProvider.has(ItemTags.Forge.NUGGETS_IRON))
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
						.requires(ItemTags.Forge.NUGGETS_GOLD)
						.requires(ItemTags.Forge.NUGGETS_GOLD)
						.group("coin")
						.unlockedBy("has_golden_nugget", RegistrateRecipeProvider.has(ItemTags.Forge.NUGGETS_GOLD))
						.save(provider, ctx.getId()))
				.stacksTo(8)
				.tag(FTTags.Items.COINS)
			.register();

	static void bootstrap()
	{
	}
}
