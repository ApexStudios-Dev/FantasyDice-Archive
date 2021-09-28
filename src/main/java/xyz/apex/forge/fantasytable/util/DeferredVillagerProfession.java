package xyz.apex.forge.fantasytable.util;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;

public class DeferredVillagerProfession extends VillagerProfession
{
	private final RegistryObject<PointOfInterestType> poiType;

	private DeferredVillagerProfession(RegistryObject<PointOfInterestType> poiType, ImmutableSet<Item> requestedItems, ImmutableSet<Block> secondaryPoi, @Nullable SoundEvent workSound)
	{
		super(poiType.getId().toString(), null, requestedItems, secondaryPoi, workSound);

		this.poiType = poiType;
	}

	@Override
	public PointOfInterestType getJobPoiType()
	{
		return poiType.get();
	}

	public static VillagerProfession create(RegistryObject<PointOfInterestType> poiType, ImmutableSet<Item> requestedItems, ImmutableSet<Block> secondaryPoi, @Nullable SoundEvent workSound)
	{
		return new DeferredVillagerProfession(poiType, requestedItems, secondaryPoi, workSound);
	}

	public static VillagerProfession create(RegistryObject<PointOfInterestType> poiType, @Nullable SoundEvent workSound)
	{
		return create(poiType, ImmutableSet.of(), ImmutableSet.of(), workSound);
	}
}
