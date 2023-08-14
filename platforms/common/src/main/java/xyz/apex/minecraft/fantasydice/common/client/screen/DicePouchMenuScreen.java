package xyz.apex.minecraft.fantasydice.common.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import xyz.apex.minecraft.apexcore.common.lib.PhysicalSide;
import xyz.apex.minecraft.apexcore.common.lib.SideOnly;
import xyz.apex.minecraft.apexcore.common.lib.menu.SimpleContainerMenuScreen;
import xyz.apex.minecraft.fantasydice.common.menu.DicePouchMenu;

@SideOnly(PhysicalSide.CLIENT)
public final class DicePouchMenuScreen extends SimpleContainerMenuScreen<DicePouchMenu>
{
    public DicePouchMenuScreen(DicePouchMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }
}
