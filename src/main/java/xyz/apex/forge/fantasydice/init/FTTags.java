package xyz.apex.forge.fantasydice.init;

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

		private static void bootstrap()
		{
			REGISTRY.addDataGenerator(ProviderType.ITEM_TAGS, provider -> {
				provider.tag(DICE_SPECIALTY).addTags(FTDiceTypes.DICE_FANTASY.getTag(), FTDiceTypes.DICE_TOBI.getTag(), FTDiceTypes.DICE_APEX.getTag(), FTDiceTypes.DICE_SYMACON.getTag());
			});
		}
	}
}
