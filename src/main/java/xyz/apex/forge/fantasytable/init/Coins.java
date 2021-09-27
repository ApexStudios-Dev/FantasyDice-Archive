package xyz.apex.forge.fantasytable.init;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.Tags;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.item.CoinItem;

import static net.minecraftforge.client.model.generators.ModelProvider.ITEM_FOLDER;

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
				.tag(DTags.Items.COINS)
				.properties(properties -> properties.rarity(rarity))
				// simple `item/generated` model
				// but texture is relocated from `<modid>:textures/item/<item_name>.png`
				// into `<modid>:textures/item/<coin_type>/<item_name>.png`
				.model((ctx, provider) -> provider.generated(ctx, new ResourceLocation(FantasyTable.ID, ITEM_FOLDER + "/coin/" + name)))
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
				.register();
		// formatter:on
	}

	@Deprecated // internal use only
	public static void register() { }
}
