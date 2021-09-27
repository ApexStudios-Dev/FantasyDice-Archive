package xyz.apex.forge.fantasytable.init;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.item.PouchItem;

public final class DItems
{
	// formatter:off
	public static final RegistryEntry<PouchItem> POUCH_ITEM = FantasyTable
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
								.save(provider, new ResourceLocation(FantasyTable.ID, "dice_pouch"))
				)
				.lang("Dice Pouch")
				.color(() -> () -> (stack, tintIndex) -> tintIndex == 0 ? ((IDyeableArmorItem) stack.getItem()).getColor(stack) : 0xFFFFFF)
				.model((ctx, provider) -> provider.generated(ctx).texture("layer1", provider.itemTexture(ctx) + "_string"))
				.properties(properties -> properties.stacksTo(1))
			.register();
	// formatter:on

	@Deprecated // internal use only
	public static void register() { }
}
