package xyz.apex.forge.dicemod.data;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import xyz.apex.forge.dicemod.Dice;
import xyz.apex.forge.dicemod.DiceMod;

public final class ItemTagGenerator extends ForgeItemTagsProvider
{
	public ItemTagGenerator(DataGenerator generator, BlockTagsProvider blockTagProvider, ExistingFileHelper fileHelper)
	{
		super(generator, blockTagProvider, fileHelper);
	}

	@Override
	public void addTags()
	{
		TagsProvider.Builder<Item> diceBuilder = tag(DiceMod.DICE).addTags(DiceMod.DICE_SIX_SIDED, DiceMod.DICE_TWENTY_SIDED);
		TagsProvider.Builder<Item> sixSidedBuilder = tag(DiceMod.DICE_SIX_SIDED);
		TagsProvider.Builder<Item> twentySidedBuilder = tag(DiceMod.DICE_TWENTY_SIDED);

		for(Dice dice : Dice.values())
		{
			dice.onGenerateTags(this::tag, sixSidedBuilder, twentySidedBuilder);
			diceBuilder.addTag(dice.tag);
		}
	}

	@Override
	public String getName()
	{
		return "DiceMod, ItemTagGenerator";
	}
}
