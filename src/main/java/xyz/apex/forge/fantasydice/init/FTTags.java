package xyz.apex.forge.fantasydice.init;

import com.tterrag.registrate.providers.ProviderType;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class FTTags
{
	private static final FTRegistry REGISTRY = FTRegistry.getRegistry();

	static void bootstrap()
	{
		Items.bootstrap();
	}

	public static final class Items
	{
		public static final TagKey<Item> DICE = REGISTRY.moddedItemTag("dice");
		public static final TagKey<Item> DICE_SPECIALTY = REGISTRY.moddedItemTag("dice/specialty");

		public static final TagKey<Item> COINS = REGISTRY.moddedItemTag("coins");

		private static void bootstrap()
		{
			REGISTRY.addDataGenerator(ProviderType.ITEM_TAGS, provider -> {
				var specialtyTagBuilder = provider.tag(DICE_SPECIALTY);

				DiceType.getDiceTypes().stream()
				        .filter(t -> t.getType() == DiceType.Type.SPECIALITY)
				        .map(DiceType::getTag)
				        .forEach(specialtyTagBuilder::addTag);
			});
		}
	}
}
