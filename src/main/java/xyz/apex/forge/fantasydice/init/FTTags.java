package xyz.apex.forge.fantasydice.init;

import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

import xyz.apex.repack.com.tterrag.registrate.providers.ProviderType;

@SuppressWarnings("unchecked")
public final class FTTags
{
	private static final FTRegistry REGISTRY = FTRegistry.getRegistry();

	static void bootstrap()
	{
		Items.bootstrap();
	}

	public static final class Items
	{
		public static final Tag.Named<Item> DICE = REGISTRY.itemTagModded("dice");
		public static final Tag.Named<Item> DICE_SPECIALTY = REGISTRY.itemTagModded("dice/specialty");

		public static final Tag.Named<Item> COINS = REGISTRY.itemTagModded("coins");

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
