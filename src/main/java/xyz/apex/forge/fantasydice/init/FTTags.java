package xyz.apex.forge.fantasydice.init;

import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import xyz.apex.forge.commonality.Mods;
import xyz.apex.forge.commonality.tags.ItemTags;

public final class FTTags
{
	static void bootstrap()
	{
		Items.bootstrap();
	}

	public static final class Items
	{
		public static final TagKey<Item> DICE = ItemTags.tag(Mods.FANTASY_DICE, "dice");
		public static final TagKey<Item> DICE_SPECIALTY = ItemTags.tag(Mods.FANTASY_DICE, "dice/specialty");

		public static final TagKey<Item> COINS = ItemTags.tag(Mods.FANTASY_DICE, "coins");

		private static void bootstrap()
		{
			FTRegistry.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, provider -> {
				var specialtyTagBuilder = provider.addTag(DICE_SPECIALTY);

				DiceType.getDiceTypes().stream()
				        .filter(t -> t.getType() == DiceType.Type.SPECIALITY)
				        .map(DiceType::getTag)
				        .forEach(specialtyTagBuilder::addTag);
			});
		}
	}
}
