package xyz.apex.forge.fantasytable.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.packets.TicTacToeOpenScreenPacket;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(modid = FantasyTable.ID)
public final class GameCommand
{
	public static final String[] GAMES = new String[] { "tic_tac_toe" };

	public static final String CMD_NAME = "game";
	public static final String ARG_GAME_TYPE = "game_type";
	public static final String ARG_ACCEPT = "accept";
	public static final String ARG_DENY = "deny";
	public static final String ARG_OPPONENT = "opponent";

	public static final String TXT_ACCEPT_DENY_COMMAND = "/%s %s %s";
	public static final String TXT_HAS_ACCEPT_DENY = "%s has %s your %s request";

	public static final String TXT_CHALLENGER = "%s is challenging you to a game of %s. Do you %s or %s? %s";
	public static final String TXT_ALREADY_CHALLENGED = "%s is already being challenged to a game of %s";
	public static final String TXT_MISSING_CHALLENGE = "You do not have and pending ChallengeRequests for %s";

	public static final String TXT_ACCEPT = "Accept";
	public static final String TXT_ACCEPT_CLICK = "Click to Accept Game Challenge!";
	public static final String TXT_ACCEPTED = "Accepted";

	public static final String TXT_DENY = "Deny";
	public static final String TXT_DENY_CLICK = "Click to Deny Game Challenge!";
	public static final String TXT_DENIED = "Denied";

	public static final String TXT_TIMEOUT = "(Timeout 15s)";
	public static final String TXT_TIMEOUT_HOVER = "This request will timeout in 15 seconds.";

	private static final Map<UUID, List<ChallengeRequest>> challengeRequestMap = Maps.newHashMap();

	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		// formatter:off
		dispatcher.register(
				Commands.literal(CMD_NAME)
				        .then(
						        Commands.argument(ARG_GAME_TYPE, StringArgumentType.word())
								        .suggests((ctx, suggestionBuilder) -> ISuggestionProvider.suggest(GAMES, suggestionBuilder))
								        .then(
												Commands.literal(ARG_ACCEPT)
												        // .requires(GameCommand::requiresAtLeastOneChallengeRequest)
												        .executes(ctx -> executeChallengeRequestResponse(ctx, true))
								        )
								        .then(
										        Commands.literal(ARG_DENY)
										                // .requires(GameCommand::requiresAtLeastOneChallengeRequest)
										                .executes(ctx -> executeChallengeRequestResponse(ctx, false))
								        )
								        .then(
												Commands.argument(ARG_OPPONENT, EntityArgument.player())
														.executes(ctx -> {
															PlayerEntity challenger = ctx.getSource().getPlayerOrException();
															String gameType = StringArgumentType.getString(ctx, ARG_GAME_TYPE);
													        PlayerEntity opponent = EntityArgument.getPlayer(ctx, ARG_OPPONENT);
															return setupGameRequest(challenger, opponent, gameType);
												        })
								        )
								        .executes(ctx -> {
									        String gameType = StringArgumentType.getString(ctx, ARG_GAME_TYPE);
											return Command.SINGLE_SUCCESS;
								        })
				        )

		);
		// formatter:on
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event)
	{
		if(!challengeRequestMap.isEmpty())
		{
			if(event.phase == TickEvent.Phase.START)
			{
				MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
				challengeRequestMap.forEach((opponentId, challengeRequests) -> challengeRequests.forEach(challengeRequest -> challengeRequest.onTick(server)));
			}
			else if(event.phase == TickEvent.Phase.END)
			{
				Set<UUID> emptyOpponents = Sets.newHashSet();

				for(UUID opponentId : challengeRequestMap.keySet())
				{
					List<ChallengeRequest> challengeRequests = challengeRequestMap.get(opponentId);

					if(challengeRequests == null || challengeRequests.isEmpty())
					{
						emptyOpponents.add(opponentId);
						continue;
					}

					challengeRequests.removeIf(challengeRequest -> !challengeRequest.isValid);

					if(challengeRequests.isEmpty())
						emptyOpponents.add(opponentId);
				}

				if(!emptyOpponents.isEmpty())
					emptyOpponents.forEach(challengeRequestMap::remove);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
	{
		PlayerEntity opponent = event.getPlayer();
		List<ChallengeRequest> challengeRequests = challengeRequestMap.get(FantasyTable.getPlayerUUID(opponent));

		if(challengeRequests != null && !challengeRequests.isEmpty())
		{
			// force invalidation
			// could just call #invalidate()
			// but this method also sends opponent denied message to challenger
			challengeRequests.forEach(challengeRequest -> challengeRequest.remainingTime = -1L);
		}
	}

	@SubscribeEvent
	public static void onServerShutDown(FMLServerStoppingEvent event)
	{
		// invalidate every current request
		challengeRequestMap.keySet()
		                   .stream()
		                   .map(challengeRequestMap::get)
		                   .filter(Objects::nonNull)
		                   .filter(challengeRequests -> !challengeRequests.isEmpty())
		                   .forEach(challengeRequests -> challengeRequests.forEach(ChallengeRequest::invalidate));

		challengeRequestMap.clear();
	}

	private static boolean requiresAtLeastOneChallengeRequest(CommandSource source)
	{
		// at least 1 request must exist
		// unfortunately due to requires()
		// passing command source and not
		// command context we can not obtain
		// the game_type argument to check if
		// specific game type request exists
		if(challengeRequestMap.isEmpty())
			return false;

		try
		{
			ServerPlayerEntity opponent = source.getPlayerOrException();
			List<ChallengeRequest> challengeRequests = challengeRequestMap.get(FantasyTable.getPlayerUUID(opponent));

			if(challengeRequests == null || challengeRequests.isEmpty())
				return false;

			return challengeRequests.stream().allMatch(challengeRequest -> challengeRequest.isValid);
		}
		catch(CommandSyntaxException e) // only players can use this command
		{
			return false;
		}
	}

	private static int executeChallengeRequestResponse(CommandContext<CommandSource> ctx, boolean accepted) throws CommandSyntaxException
	{
		PlayerEntity opponent = ctx.getSource().getPlayerOrException();
		String gameType = StringArgumentType.getString(ctx, ARG_GAME_TYPE);
		ChallengeRequest challengeRequest = findChallengeRequest(opponent, gameType);

		if(challengeRequest != null && challengeRequest.isValid)
		{
			if(accepted)
				challengeRequest.accept(ctx.getSource().getServer(), opponent);
			else
				challengeRequest.deny(ctx.getSource().getServer(), opponent);

			return Command.SINGLE_SUCCESS;
		}
		else
		{
			opponent.sendMessage(buildMissingChallengeComponent(gameType), FantasyTable.getPlayerUUID(opponent));
			return -1;
		}
	}

	private static IFormattableTextComponent buildChallengeComponent(PlayerEntity challenger, String gameType)
	{
		// @formatter:off
		return new TranslationTextComponent(
				TXT_CHALLENGER,
				challenger.getDisplayName()
				          .copy()
				          .withStyle(style -> style
						          .withItalic(true)
				          ),

				new TranslationTextComponent(gameType)
						.withStyle(style -> style
								.withColor(TextFormatting.AQUA)
								.withItalic(true)
						),

				new TranslationTextComponent(TXT_ACCEPT)
						.withStyle(style -> style
								.withColor(TextFormatting.GREEN)
								.withItalic(true)
								.withHoverEvent(new HoverEvent(
										HoverEvent.Action.SHOW_TEXT,
										new TranslationTextComponent(TXT_ACCEPT_CLICK).withStyle(hoverStyle -> hoverStyle
												.withColor(TextFormatting.GREEN)
												.withItalic(true)
										)
								))
								.withClickEvent(new ClickEvent(
										ClickEvent.Action.RUN_COMMAND,
										String.format(TXT_ACCEPT_DENY_COMMAND, CMD_NAME, gameType, ARG_ACCEPT)
								))
						),

				new TranslationTextComponent(TXT_DENY)
						.withStyle(style -> style
								.withColor(TextFormatting.RED)
								.withItalic(true)
								.withHoverEvent(new HoverEvent(
										HoverEvent.Action.SHOW_TEXT,
										new TranslationTextComponent(TXT_DENY_CLICK).withStyle(hoverStyle -> hoverStyle
												.withColor(TextFormatting.RED)
												.withItalic(true)
										)
								))
								.withClickEvent(new ClickEvent(
										ClickEvent.Action.RUN_COMMAND,
										String.format(TXT_ACCEPT_DENY_COMMAND, CMD_NAME, gameType, ARG_DENY)
								))
						),

				new TranslationTextComponent(TXT_TIMEOUT)
						.withStyle(style -> style
								.withColor(TextFormatting.GRAY)
								.withItalic(true)
								.withHoverEvent(new HoverEvent(
										HoverEvent.Action.SHOW_TEXT,
										new TranslationTextComponent(TXT_TIMEOUT_HOVER).withStyle(hoverStyle -> hoverStyle
												.withColor(TextFormatting.GRAY)
												.withItalic(true)
										)
								))
						)
		).withStyle(style -> style
				.withColor(TextFormatting.GRAY)
				.withItalic(true)
		);
		// @formatter:on
	}

	private static IFormattableTextComponent buildChallengeResponseComponent(PlayerEntity challenger, String gameType, boolean accepted)
	{
		// @formatter:off
		IFormattableTextComponent response = accepted ? (
				new TranslationTextComponent(TXT_ACCEPTED)
						.withStyle(style -> style
								.withColor(TextFormatting.GREEN)
								.withItalic(true)
						)
		) : (
				new TranslationTextComponent(TXT_DENIED)
						.withStyle(style -> style
								.withColor(TextFormatting.RED)
								.withItalic(true)
						)
		);

		return new TranslationTextComponent(
				TXT_HAS_ACCEPT_DENY,

				challenger.getDisplayName()
				          .copy()
				          .withStyle(style -> style
						          .withItalic(true)
				          ),

				response,

				new TranslationTextComponent(gameType)
						.withStyle(style -> style
								.withColor(TextFormatting.AQUA)
								.withItalic(true)
						)
		).withStyle(style -> style
				.withColor(TextFormatting.GRAY)
				.withItalic(true)
		);
		// @formatter:on
	}

	private static IFormattableTextComponent buildChallengeAlreadyExistsComponent(PlayerEntity challenger, String gameType)
	{
		// @formatter:off
		return new TranslationTextComponent(
				TXT_ALREADY_CHALLENGED,

				challenger.getDisplayName()
				          .copy()
				          .withStyle(style -> style
						          .withItalic(true)
				          ),

				new TranslationTextComponent(gameType)
						.withStyle(style -> style
								.withColor(TextFormatting.AQUA)
								.withItalic(true)
						)
		).withStyle(style -> style
				.withColor(TextFormatting.RED)
				.withItalic(true)
		);
		// @formatter:on
	}

	private static IFormattableTextComponent buildMissingChallengeComponent(String gameType)
	{
		// @formatter:off
		return new TranslationTextComponent(
				TXT_MISSING_CHALLENGE,
				new TranslationTextComponent(gameType)
						.withStyle(style -> style
								.withColor(TextFormatting.AQUA)
								.withItalic(true)
						)
		).withStyle(style -> style
				.withColor(TextFormatting.RED)
				.withItalic(true)
		);
		// @formatter:on
	}

	private static int setupGameRequest(PlayerEntity challenger, PlayerEntity opponent, String gameType)
	{
		List<ChallengeRequest> challengeRequests = challengeRequestMap.computeIfAbsent(FantasyTable.getPlayerUUID(opponent), $ -> Lists.newArrayList());

		for(ChallengeRequest challengeRequest : challengeRequests)
		{
			if(challengeRequest.gameType.equals(gameType))
			{
				challenger.sendMessage(buildChallengeAlreadyExistsComponent(challenger, gameType), FantasyTable.getPlayerUUID(challenger));
				return -1;
			}
		}

		challengeRequests.add(new ChallengeRequest(challenger, opponent, gameType));
		opponent.sendMessage(buildChallengeComponent(challenger, gameType), FantasyTable.getPlayerUUID(challenger));
		return Command.SINGLE_SUCCESS;
	}

	@Nullable
	private static ChallengeRequest findChallengeRequest(PlayerEntity opponent, String gameType)
	{
		List<ChallengeRequest> challengeRequests = challengeRequestMap.get(FantasyTable.getPlayerUUID(opponent));

		if(challengeRequests == null || challengeRequests.isEmpty())
			return null;

		// @formatter:off
		return challengeRequests.stream()
		                        .filter(challengeRequest -> challengeRequest.gameType.equals(gameType))
		                        .findFirst()
		                        .orElse(null);
		// @formatter:on

	}

	public static final class ChallengeRequest
	{
		private final UUID challengerId;
		private final UUID opponentId;
		private final String gameType;
		private long remainingTime = 15L * 20L;
		private boolean isValid = true;

		private ChallengeRequest(PlayerEntity challenger, PlayerEntity opponent, String gameType)
		{
			challengerId = FantasyTable.getPlayerUUID(challenger);
			opponentId = FantasyTable.getPlayerUUID(opponent);

			this.gameType = gameType;
		}

		public Optional<PlayerEntity> getChallenger(MinecraftServer server)
		{
			return Optional.ofNullable(server.getPlayerList().getPlayer(challengerId));
		}

		public Optional<PlayerEntity> getOpponent(MinecraftServer server)
		{
			return Optional.ofNullable(server.getPlayerList().getPlayer(opponentId));
		}

		private void accept(MinecraftServer server, PlayerEntity opponent)
		{
			invalidate();
			PlayerEntity challenger = getChallenger(server).orElseThrow(NullPointerException::new);
			challenger.sendMessage(buildChallengeResponseComponent(challenger, gameType, true), FantasyTable.getPlayerUUID(opponent));
			FantasyTable.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) challenger), new TicTacToeOpenScreenPacket(opponent, true));
			FantasyTable.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) opponent), new TicTacToeOpenScreenPacket(challenger, false));
		}

		private void deny(MinecraftServer server, PlayerEntity opponent)
		{
			invalidate();
			getChallenger(server).ifPresent(challenger -> {
				challenger.sendMessage(buildChallengeResponseComponent(challenger, gameType, false), FantasyTable.getPlayerUUID(opponent));
			});
		}

		private void onTick(MinecraftServer server)
		{
			if(isValid)
			{
				remainingTime--;

				if(remainingTime < 0)
				{
					invalidate();

					getChallenger(server).ifPresent(challenger -> {
						UUID senderId = getChallenger(server).map(FantasyTable::getPlayerUUID).orElse(Util.NIL_UUID);
						challenger.sendMessage(buildChallengeResponseComponent(challenger, gameType, false), senderId);
					});
				}
			}
		}

		private void invalidate()
		{
			remainingTime = 0;
			isValid = false;
		}
	}
}
