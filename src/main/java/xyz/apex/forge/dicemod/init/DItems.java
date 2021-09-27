package xyz.apex.forge.dicemod.init;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import xyz.apex.forge.dicemod.DiceMod;
import xyz.apex.forge.dicemod.item.PouchItem;

public final class DItems
{
	// formatter:off
	public static final RegistryEntry<PouchItem> POUCH_ITEM = DiceMod
			.registrate()
			.object(DStrings.ITEM_POUCH)
			.item(PouchItem::new)
				.recipe(
						(ctx, provider) -> ShapedRecipeBuilder
								.shaped(ctx::get, 1)
								.define('S', Tags.Items.STRING)
								.define('L', Tags.Items.LEATHER)
								.pattern(" S ")
								.pattern("L L")
								.pattern(" L ")
								.unlockedBy("has_leather", RegistrateRecipeProvider.hasItem(Tags.Items.LEATHER))
								.unlockedBy("has_string", RegistrateRecipeProvider.hasItem(Tags.Items.STRING))
								.save(provider, new ResourceLocation(DiceMod.ID, "dice_pouch"))
				)
				.lang("Dice Pouch")
				.defaultModel()
				.properties(properties -> properties.stacksTo(1))
			.register();
	// formatter:on

	@Deprecated // internal use only
	public static void register() { }
}
