package xyz.apex.forge.fantasytable.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

import javax.annotation.Nullable;

public class GamblingTableContainer extends Container
{
	public GamblingTableContainer(@Nullable ContainerType<?> containerType, int windowId)
	{
		super(containerType, windowId);
	}

	@Override
	public boolean stillValid(PlayerEntity player)
	{
		return true;
	}
}
