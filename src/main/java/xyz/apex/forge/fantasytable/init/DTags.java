package xyz.apex.forge.fantasytable.init;

import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.versions.forge.ForgeVersion;
import xyz.apex.forge.fantasytable.FantasyTable;

public final class DTags
{
	@Deprecated // internal use only
	public static void register()
	{
		Items.register();
	}

	public static final class Items
	{
		public static final Tags.IOptionalNamedTag<Item> DICE = ItemTags.createOptional(Names.DICE);
		public static final Tags.IOptionalNamedTag<Item> DICE_SIX_SIDED = ItemTags.createOptional(Names.DICE_SIX_SIDED);
		public static final Tags.IOptionalNamedTag<Item> DICE_TWENTY_SIDED = ItemTags.createOptional(Names.DICE_TWENTY_SIDED);
		public static final Tags.IOptionalNamedTag<Item> PAPER = ItemTags.createOptional(Names.PAPER); // does not exist by default
		public static final Tags.IOptionalNamedTag<Item> COINS = ItemTags.createOptional(Names.COINS);

		private static void register() { }
	}

	public static final class Names
	{
		public static final ResourceLocation DICE = new ResourceLocation(FantasyTable.ID, DStrings.TAG_DICE);
		public static final ResourceLocation DICE_SIX_SIDED = new ResourceLocation(FantasyTable.ID, DStrings.TAG_SIX_SIDED);
		public static final ResourceLocation DICE_TWENTY_SIDED = new ResourceLocation(FantasyTable.ID, DStrings.TAG_TWENTY_SIDED);
		public static final ResourceLocation PAPER = new ResourceLocation(ForgeVersion.MOD_ID, DStrings.TAG_PAPER);
		public static final ResourceLocation COINS = new ResourceLocation(FantasyTable.ID, DStrings.TAG_COINS);
	}
}
