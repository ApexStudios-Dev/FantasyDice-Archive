package xyz.apex.forge.fantasytable.util.registrate.entry;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;

public class VillagerProfessionEntry<T extends VillagerProfession> extends RegistryEntry<T>
{
	public VillagerProfessionEntry(AbstractRegistrate<?> owner, RegistryObject<T> delegate)
	{
		super(owner, delegate);
	}

	public boolean is(@Nullable VillagerProfession other)
	{
		return other != null && other == get();
	}
}
