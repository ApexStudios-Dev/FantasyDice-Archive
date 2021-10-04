package xyz.apex.forge.fantasytable.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class CloseScreenPacket
{
	public CloseScreenPacket()
	{
	}

	public CloseScreenPacket(PacketBuffer buffer)
	{
		this();
	}

	public static void encode(CloseScreenPacket packet, PacketBuffer buffer)
	{
	}

	public static void consume(CloseScreenPacket packet, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> CloseScreenPacket::closeScreen));
		ctx.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	private static void closeScreen()
	{
		Minecraft.getInstance().setScreen(null);
	}
}
