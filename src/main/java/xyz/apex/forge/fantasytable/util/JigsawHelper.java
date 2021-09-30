package xyz.apex.forge.fantasytable.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import xyz.apex.forge.fantasytable.FantasyTable;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public final class JigsawHelper
{
	private static final Field FIELD;

	static
	{
		try
		{
			FIELD = Field.class.getDeclaredField("modifiers");
			FIELD.setAccessible(true);
		}
		catch(NoSuchFieldException e)
		{
			FantasyTable.LOGGER.fatal(e);
			throw new RuntimeException(e);
		}
	}

	public static final StructureProcessorList PROCESSOR_LIST_DEFAULT = ProcessorLists.EMPTY;
	public static final JigsawPattern.PlacementBehaviour PLACEMENT_DEFAULT = JigsawPattern.PlacementBehaviour.RIGID;
	public static final int WEIGHT_DEFAULT = 10;

	public static void registerJigsaw(MinecraftServer server, ResourceLocation poolLocation, ResourceLocation structureLocation, @Nullable StructureProcessorList processorList, JigsawPattern.PlacementBehaviour placementBehaviour, int weight)
	{
		DynamicRegistries manager = server.registryAccess();
		MutableRegistry<JigsawPattern> pools = manager.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);

		JigsawPattern pool = pools.get(poolLocation);

		// can not inject jigsaw for unknown pool
		if(pool == null)
			return;
		if(processorList == null)
			processorList = manager.registryOrThrow(Registry.PROCESSOR_LIST_REGISTRY).getOptional(poolLocation).orElse(PROCESSOR_LIST_DEFAULT);

		List<JigsawPiece> templates = getOrDuplicateList(JigsawPattern.class, pool, "field_214953_e");
		List<Pair<JigsawPiece, Integer>> rawTemplates = getOrDuplicateList(JigsawPattern.class, pool, "field_214952_d");

		JigsawPiece element = JigsawPiece.legacy(structureLocation.toString(), processorList).apply(placementBehaviour);
		rawTemplates.add(Pair.of(element, weight));

		for(int i = 0; i < weight; i++)
		{
			templates.add(element);
		}
	}

	public static void registerJigsaw(MinecraftServer server, ResourceLocation poolLocation, ResourceLocation structureLocation, StructureProcessorList processorList)
	{
		registerJigsaw(server, poolLocation, structureLocation, processorList, PLACEMENT_DEFAULT, WEIGHT_DEFAULT);
	}

	public static void registerJigsaw(MinecraftServer server, ResourceLocation poolLocation, ResourceLocation structureLocation, StructureProcessorList processorList, JigsawPattern.PlacementBehaviour placementBehaviour)
	{
		registerJigsaw(server, poolLocation, structureLocation, processorList, placementBehaviour, WEIGHT_DEFAULT);
	}

	public static void registerJigsaw(MinecraftServer server, ResourceLocation poolLocation, ResourceLocation structureLocation, StructureProcessorList processorList, int weight)
	{
		registerJigsaw(server, poolLocation, structureLocation, processorList, PLACEMENT_DEFAULT, weight);
	}

	public static void registerJigsaw(MinecraftServer server, ResourceLocation poolLocation, ResourceLocation structureLocation, JigsawPattern.PlacementBehaviour placementBehaviour)
	{
		registerJigsaw(server, poolLocation, structureLocation, null, placementBehaviour, WEIGHT_DEFAULT);
	}

	public static void registerJigsaw(MinecraftServer server, ResourceLocation poolLocation, ResourceLocation structureLocation, JigsawPattern.PlacementBehaviour placementBehaviour, int weight)
	{
		registerJigsaw(server, poolLocation, structureLocation, null, placementBehaviour, weight);
	}

	public static void registerJigsaw(MinecraftServer server, ResourceLocation poolLocation, ResourceLocation structureLocation, int weight)
	{
		registerJigsaw(server, poolLocation, structureLocation, null, PLACEMENT_DEFAULT, weight);
	}

	public static void registerJigsaw(MinecraftServer server, ResourceLocation poolLocation, ResourceLocation structureLocation)
	{
		registerJigsaw(server, poolLocation, structureLocation, null, PLACEMENT_DEFAULT, WEIGHT_DEFAULT);
	}

	private static <T, E> List<T> getOrDuplicateList(Class<? super E> clazz, E instance, String fieldName)
	{
		List<T> templates = ObfuscationReflectionHelper.getPrivateValue(clazz, instance, fieldName);

		if(templates == null || templates instanceof ImmutableList)
		{
			removeFinal(clazz, fieldName);
			templates = templates == null ? Lists.newArrayList() : Lists.newArrayList(templates);
			ObfuscationReflectionHelper.setPrivateValue(clazz, instance, templates, fieldName);
		}

		return templates;
	}

	private static <E> void removeFinal(Class<? super E> clazz, String fieldName)
	{
		Field field = ObfuscationReflectionHelper.findField(clazz, fieldName);
		int modifiers = field.getModifiers();

		if(Modifier.isFinal(modifiers))
		{
			try
			{
				FIELD.setInt(field, modifiers & ~Modifier.FINAL);
			}
			catch(IllegalAccessException e)
			{
				FantasyTable.LOGGER.error("Failed to remove Final modifier on Field '{}#{}'", clazz.getSimpleName(), field.getName(), e);
			}
		}
	}
}
