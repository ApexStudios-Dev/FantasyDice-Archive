package xyz.apex.forge.fantasydice.init;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;

import xyz.apex.repack.com.tterrag.registrate.providers.ProviderType;

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

		private static void bootstrap()
		{
			REGISTRY.addDataGenerator(ProviderType.ITEM_TAGS, provider -> {

			});
		}
	}
}
