package xyz.apex.forge.fantasydice.init;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;

import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

import xyz.apex.forge.fantasydice.block.DiceStationBlock;
import xyz.apex.forge.utility.registrator.entry.BlockEntry;
import xyz.apex.forge.utility.registrator.provider.RegistrateLangExtProvider;

public final class FTBlocks
{
	private static final FTRegistry REGISTRY = FTRegistry.getRegistry();

	public static final BlockEntry<DiceStationBlock> DICE_STATION = REGISTRY
			.block("dice_station", DiceStationBlock::new)
				.lang("Dice Station")
				.lang(RegistrateLangExtProvider.EN_GB, "Dice Station")

				.initialProperties(Material.WOOD)
				.strength(2.5F)
				.sound(SoundType.WOOD)

				.recipe((ctx, provider) -> ShapelessRecipeBuilder
							.shapeless(ctx.get(), 1)
							.requires(FTItems.POUCH)
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

				.simpleItem()
			.register();

	static void bootstrap() { }
}
