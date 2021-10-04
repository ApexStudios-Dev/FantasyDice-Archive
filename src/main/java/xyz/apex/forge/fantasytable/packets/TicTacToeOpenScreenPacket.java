package xyz.apex.forge.fantasytable.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.client.screen.TicTacToeGameScreen;

import java.util.UUID;
import java.util.function.Supplier;

public final class TicTacToeOpenScreenPacket
{
	private final UUID opponentId;
	private final ITextComponent opponentName;
	private final boolean isChallenger;

	public TicTacToeOpenScreenPacket(PlayerEntity opponent, boolean isChallenger)
	{
		opponentId = FantasyTable.getPlayerUUID(opponent);
		opponentName = opponent.getDisplayName();
		this.isChallenger = isChallenger;
	}

	public TicTacToeOpenScreenPacket(PacketBuffer buffer)
	{
		opponentId = buffer.readUUID();
		opponentName = buffer.readComponent();
		isChallenger = buffer.readBoolean();
	}

	public static void encode(TicTacToeOpenScreenPacket packet, PacketBuffer buffer)
	{
		buffer.writeUUID(packet.opponentId);
		buffer.writeComponent(packet.opponentName);
		buffer.writeBoolean(packet.isChallenger);
	}

	public static void consume(TicTacToeOpenScreenPacket packet, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> openGameScreen(packet)));
		ctx.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	private static void openGameScreen(TicTacToeOpenScreenPacket packet)
	{
		Minecraft.getInstance().setScreen(new TicTacToeGameScreen(new TranslationTextComponent("TicTacToe"), packet.opponentId, packet.opponentName, packet.isChallenger));
	}
}
