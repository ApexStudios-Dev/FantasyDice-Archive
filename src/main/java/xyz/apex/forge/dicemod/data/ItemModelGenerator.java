package xyz.apex.forge.dicemod.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import xyz.apex.forge.dicemod.Dice;
import xyz.apex.forge.dicemod.DiceMod;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

public final class ItemModelGenerator extends ItemModelProvider
{
	public static final ModelFile.UncheckedModelFile GENERATED = new ModelFile.UncheckedModelFile("item/generated");

	public ItemModelGenerator(DataGenerator generator, ExistingFileHelper fileHelper)
	{
		super(generator, DiceMod.ID, fileHelper);
	}

	@Override
	protected void registerModels()
	{
		for(Dice dice : Dice.TYPES)
		{
			dice.onGenerateItemModel(this::getBuilder);
		}
	}

	private ItemModelBuilder getBuilder(Supplier<Item> item)
	{
		return getBuilder(Objects.requireNonNull(item.get().getRegistryName()).toString());
	}

	@Nonnull
	@Override
	public String getName()
	{
		return "DiceMod, ItemModelGenerator";
	}
}
