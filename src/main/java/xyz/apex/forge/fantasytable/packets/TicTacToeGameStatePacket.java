package xyz.apex.forge.fantasytable.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.client.screen.TicTacToeGameScreen;
import xyz.apex.forge.fantasytable.init.TicTacToeState;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public final class TicTacToeGameStatePacket
{
	public final TicTacToeState[] slotStates;
	public final TicTacToeState currentMove;
	private final UUID opponentId;
	private boolean isUpdate;

	public TicTacToeGameStatePacket(TicTacToeState[] slotStates, TicTacToeState currentMove, UUID opponentId, boolean isUpdate)
	{
		this.slotStates = new TicTacToeState[slotStates.length];
		System.arraycopy(slotStates, 0, this.slotStates, 0, slotStates.length);
		this.currentMove = currentMove;
		this.opponentId = opponentId;
		this.isUpdate = isUpdate;
	}

	public TicTacToeGameStatePacket(PacketBuffer buffer)
	{
		slotStates = new TicTacToeState[buffer.readInt()];
		IntStream.range(0, slotStates.length).forEach(i -> slotStates[i] = buffer.readEnum(TicTacToeState.class));
		currentMove = buffer.readEnum(TicTacToeState.class);
		opponentId = buffer.readUUID();
		isUpdate = buffer.readBoolean();
	}

	public static void encode(TicTacToeGameStatePacket packet, PacketBuffer buffer)
	{
		buffer.writeInt(packet.slotStates.length);
		Arrays.stream(packet.slotStates).forEach(buffer::writeEnum);
		buffer.writeEnum(packet.currentMove);
		buffer.writeUUID(packet.opponentId);
		buffer.writeBoolean(packet.isUpdate);
	}

	public static void consume(TicTacToeGameStatePacket packet, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			if(packet.isUpdate)
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> consumerClient(packet));
			else
			{
				ServerPlayerEntity player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(packet.opponentId);
				FantasyTable.NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new TicTacToeGameStatePacket(packet.slotStates, packet.currentMove, packet.opponentId, true));
			}

		});

		ctx.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	private static void consumerClient(TicTacToeGameStatePacket packet)
	{
		Minecraft mc = Minecraft.getInstance();

		if(mc.screen instanceof TicTacToeGameScreen)
		{
			TicTacToeGameScreen gameScreen = (TicTacToeGameScreen) mc.screen;
			gameScreen.updateFromRemote(packet.slotStates, packet.currentMove);
		}
	}
}
