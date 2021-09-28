package xyz.apex.forge.fantasytable.util.registrate;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.NonNullLazyValue;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.apex.forge.fantasytable.util.registrate.builders.PointOfInterestTypeBuilder;
import xyz.apex.forge.fantasytable.util.registrate.builders.VillagerProfessionBuilder;

public class CustomRegistrate extends AbstractRegistrate<CustomRegistrate>
{
	protected CustomRegistrate(String modId)
	{
		super(modId);
	}

	// Point of Interest Types
	public PointOfInterestTypeBuilder<PointOfInterestType, CustomRegistrate> pointOfInterestType()
	{
		return pointOfInterestType(self(), currentName(), PointOfInterestType::new);
	}

	public <T extends PointOfInterestType> PointOfInterestTypeBuilder<T, CustomRegistrate> pointOfInterestType(PointOfInterestTypeBuilder.PointOfInterestTypeFactory<T> factory)
	{
		return pointOfInterestType(self(), currentName(), factory);
	}

	public PointOfInterestTypeBuilder<PointOfInterestType, CustomRegistrate> pointOfInterestType(String name)
	{
		return pointOfInterestType(self(), name, PointOfInterestType::new);
	}

	public <T extends PointOfInterestType> PointOfInterestTypeBuilder<T, CustomRegistrate> pointOfInterestType(String name, PointOfInterestTypeBuilder.PointOfInterestTypeFactory<T> factory)
	{
		return pointOfInterestType(self(), name, factory);
	}

	public <P> PointOfInterestTypeBuilder<PointOfInterestType, P> pointOfInterestType(P parent)
	{
		return pointOfInterestType(parent, currentName(), PointOfInterestType::new);
	}

	public <T extends PointOfInterestType, P> PointOfInterestTypeBuilder<T, P> pointOfInterestType(P parent, PointOfInterestTypeBuilder.PointOfInterestTypeFactory<T> factory)
	{
		return pointOfInterestType(parent, currentName(), factory);
	}

	public <P>PointOfInterestTypeBuilder<PointOfInterestType, P> pointOfInterestType(P parent, String name)
	{
		return pointOfInterestType(parent, name, PointOfInterestType::new);
	}

	public <T extends PointOfInterestType, P> PointOfInterestTypeBuilder<T, P> pointOfInterestType(P parent, String name, PointOfInterestTypeBuilder.PointOfInterestTypeFactory<T> factory)
	{
		return entry(name, callback -> PointOfInterestTypeBuilder.create(this, parent, name, callback, factory));
	}

	// Villager Professions
	public VillagerProfessionBuilder<VillagerProfession, CustomRegistrate> villagerProfession()
	{
		return villagerProfession(self(), currentName(), VillagerProfession::new);
	}

	public <T extends VillagerProfession> VillagerProfessionBuilder<T, CustomRegistrate> villagerProfession(VillagerProfessionBuilder.VillagerProfessionFactory<T> factory)
	{
		return villagerProfession(self(), currentName(), factory);
	}

	public VillagerProfessionBuilder<VillagerProfession, CustomRegistrate> villagerProfession(String name)
	{
		return villagerProfession(self(), name, VillagerProfession::new);
	}

	public <T extends VillagerProfession> VillagerProfessionBuilder<T, CustomRegistrate> villagerProfession(String name, VillagerProfessionBuilder.VillagerProfessionFactory<T> factory)
	{
		return villagerProfession(self(), name, factory);
	}

	public <P> VillagerProfessionBuilder<VillagerProfession, P> villagerProfession(P parent)
	{
		return villagerProfession(parent, currentName(), VillagerProfession::new);
	}

	public <T extends VillagerProfession, P> VillagerProfessionBuilder<T, P> villagerProfession(P parent, VillagerProfessionBuilder.VillagerProfessionFactory<T> factory)
	{
		return villagerProfession(parent, currentName(), factory);
	}

	public <P>VillagerProfessionBuilder<VillagerProfession, P> villagerProfession(P parent, String name)
	{
		return villagerProfession(parent, name, VillagerProfession::new);
	}

	public <T extends VillagerProfession, P> VillagerProfessionBuilder<T, P> villagerProfession(P parent, String name, VillagerProfessionBuilder.VillagerProfessionFactory<T> factory)
	{
		return entry(name, callback -> VillagerProfessionBuilder.create(this, parent, name, callback, factory));
	}

	public static NonNullLazyValue<CustomRegistrate> create(String modId)
	{
		return new NonNullLazyValue<>(() -> new CustomRegistrate(modId).registerEventListeners(FMLJavaModLoadingContext.get().getModEventBus()));
	}
}
