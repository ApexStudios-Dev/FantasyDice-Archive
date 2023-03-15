package xyz.apex.forge.fantasydice.init;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import xyz.apex.forge.apexcore.registrate.entry.BlockEntry;
import xyz.apex.forge.commonality.tags.BlockTags;
import xyz.apex.forge.fantasydice.block.DiceStationBlock;

public final class FTBlocks
{
	public static final BlockEntry<DiceStationBlock> DICE_STATION = FTRegistry
			.REGISTRATE
			.object("dice_station")
			.block(DiceStationBlock::new)
				.lang("Dice Station")

				.initialProperties(Material.WOOD)
				.strength(2.5F)
				.sound(SoundType.WOOD)

				.recipe((ctx, provider) -> ShapelessRecipeBuilder
							.shapeless(RecipeCategory.MISC, ctx.get(), 1)
							.requires(FTItems.POUCH.get())
							.requires(Blocks.CRAFTING_TABLE)
							.unlockedBy("has_crafting_table", RegistrateRecipeProvider.has(Blocks.CRAFTING_TABLE))
							.group(ctx.getName())
							.save(provider, ctx.getId())
				)
				.blockState((ctx, provider) -> {
					BlockModelBuilder model = provider.models().cube(
							ctx.getName(),
							provider.mcLoc("block/oak_planks"),
							provider.modLoc("block/dice_table_top"),
							provider.modLoc("block/dice_table_front"),
							provider.modLoc("block/dice_table_side"),
							provider.modLoc("block/dice_table_side"),
							provider.modLoc("block/dice_table_side")
					).texture("particle", provider.modLoc("block/dice_table_front"));

					provider.horizontalBlock(ctx.getEntry(), model);
				})
				.tag(BlockTags.Vanilla.MINEABLE_WITH_AXE)

				.simpleItem()
			.register();

	static void bootstrap() { }
}
