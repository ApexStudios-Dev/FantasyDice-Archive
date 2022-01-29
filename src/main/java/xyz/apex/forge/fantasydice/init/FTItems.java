package xyz.apex.forge.fantasydice.init;

import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.IDyeableArmorItem;

import net.minecraftforge.common.Tags;

import xyz.apex.forge.fantasydice.item.PouchItem;
import xyz.apex.forge.utility.registrator.entry.ItemEntry;
import xyz.apex.forge.utility.registrator.provider.RegistrateLangExtProvider;
import xyz.apex.repack.com.tterrag.registrate.providers.RegistrateRecipeProvider;

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

	static void bootstrap()
	{
	}
}
