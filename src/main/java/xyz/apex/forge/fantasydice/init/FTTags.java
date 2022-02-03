package xyz.apex.forge.fantasydice.init;

import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;

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
		public static final ITag.INamedTag<Item> DICE = REGISTRY.itemTagModded("dice");
		public static final ITag.INamedTag<Item> DICE_SPECIALTY = REGISTRY.itemTagModded("dice/specialty");

		public static final ITag.INamedTag<Item> COINS = REGISTRY.itemTagModded("coins");

		private static void bootstrap()
		{
			REGISTRY.addDataGenerator(ProviderType.ITEM_TAGS, provider -> {
				TagsProvider.Builder<Item> specialtyTagBuilder = provider.tag(DICE_SPECIALTY);

				DiceType.getDiceTypes().stream()
				        .filter(t -> t.getType() == DiceType.Type.SPECIALITY)
				        .map(DiceType::getTag)
				        .forEach(specialtyTagBuilder::addTag);
			});
		}
	}
}
