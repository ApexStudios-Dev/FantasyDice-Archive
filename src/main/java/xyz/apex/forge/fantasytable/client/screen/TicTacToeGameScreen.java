package xyz.apex.forge.fantasytable.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.glfw.GLFW;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.init.TicTacToeState;
import xyz.apex.forge.fantasytable.packets.TicTacToeGameStatePacket;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class TicTacToeGameScreen extends GameScreen
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(FantasyTable.ID, "textures/gui/game/tic_tac_toe_widgets.png");

	private static final int TEXTURE_WIDTH = 256;
	private static final int TEXTURE_HEIGHT = 256;
	private static final int BACKGROUND_WIDTH = 80;
	private static final int BACKGROUND_HEIGHT = 80;
	private static final int WIDGET_OFFSET_X = 81;
	private static final int WIDGET_OFFSET_Y = 1;
	private static final int SLOT_WIDTH = 22;
	private static final int SLOT_HEIGHT = 22;
	private static final int SLOT_DISABLED_OFFSET_X = SLOT_WIDTH * 2 + 4;

	private boolean calculateProperties = true;
	private int backgroundTextureX;
	private int backgroundTextureY;
	@Nullable private ITextComponent challengerName;
	private int challengerWidth;
	@Nullable private ITextComponent opponentName;
	private int opponentWidth;
	private int maxPlayerNameWidth;
	private int playerBackgroundX;
	private int playerBackgroundY;
	private int playerBackgroundWidth;
	private int playerBackgroundHeight;
	private float playerBackgroundSegments;
	private int playerBackgroundFullSegments;
	private int playerBackgroundRemaining;
	private boolean canInteract = false;
	private boolean isGameRunning = true;

	private final boolean isChallenger;
	private final TicTacToeState localState;
	private boolean isWinner = false;
	private final UUID localOpponentId;
	private final ITextComponent localOpponentName;
	private final TicTacToeState[] localGrid = new TicTacToeState[9];
	private TicTacToeState localCurrentMove = TicTacToeState.EMPTY;

	private final int[] winningSlots = new int[3];

	public TicTacToeGameScreen(ITextComponent title, UUID opponentId, ITextComponent opponentName, boolean isChallenger)
	{
		super(title);

		localOpponentId = opponentId;
		localOpponentName = opponentName;
		this.isChallenger = isChallenger;
		localState = isChallenger ? TicTacToeState.CROSS : TicTacToeState.NOUGHT;
		Arrays.fill(localGrid, TicTacToeState.EMPTY);
		Arrays.fill(winningSlots, -1);
		localCurrentMove = isChallenger ? localState : localState.opponent();
	}

	@Override
	protected void init()
	{
		calculateProperties = true;
		opponentName = null;
		challengerName = null;
	}

	@Override
	public void resize(Minecraft minecraft, int screenWidth, int screenHeight)
	{
		super.resize(minecraft, screenWidth, screenHeight);
		calculateProperties = true;
	}

	@Override
	public void tick()
	{
		boolean canInteractNow;

		if(isGameRunning)
		{
			canInteractNow = !minecraft.player.isSpectator() && minecraft.player.isAlive();

			if(updateWinState())
			{
				canInteractNow = false;
				isGameRunning = false;
			}
		}
		else
			canInteractNow = false;

		if(canInteractNow != canInteract)
		{
			calculateProperties = true;
			canInteract = canInteractNow;
		}

		calculateScreenProperties();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		renderBackground(matrixStack);

		minecraft.textureManager.bind(TEXTURE);
		renderGameBoardBackground(matrixStack, mouseX, mouseY, partialTicks);

		super.render(matrixStack, mouseX, mouseY, partialTicks);

		renderGameBoardForeground(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
	{
		if(clickedSlot(mouseX, mouseY, mouseButton))
			return true;
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onClose()
	{
		super.onClose();
	}

	private void renderGameBoardBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		// board background
		blit(matrixStack, backgroundTextureX, backgroundTextureY, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

		// player name background
		/* TL */ blit(matrixStack, playerBackgroundX, playerBackgroundY, WIDGET_OFFSET_X, WIDGET_OFFSET_Y, 3, 3);
		/* BL */ blit(matrixStack, playerBackgroundX, playerBackgroundY + playerBackgroundHeight, WIDGET_OFFSET_X, WIDGET_OFFSET_Y + 9, 3, 3);

		/* TR */ blit(matrixStack, playerBackgroundX + playerBackgroundWidth, playerBackgroundY, WIDGET_OFFSET_X + 9, WIDGET_OFFSET_Y, 3, 3);
		/* BR */ blit(matrixStack, playerBackgroundX + playerBackgroundWidth, playerBackgroundY + playerBackgroundHeight, WIDGET_OFFSET_X + 9, WIDGET_OFFSET_Y + 9, 3, 3);

		for(int i = 0; i < playerBackgroundWidth - 2; i++)
		{
			int sliceIndex = i % 8;
			blit(matrixStack, playerBackgroundX + 2 + i, playerBackgroundY, WIDGET_OFFSET_X + 2 + sliceIndex, WIDGET_OFFSET_Y, 1, 3);
			blit(matrixStack, playerBackgroundX + 2 + i, playerBackgroundY + playerBackgroundHeight, WIDGET_OFFSET_X + 2 + sliceIndex, WIDGET_OFFSET_Y + 9, 1, 3);
		}

		for(int i = 0; i < playerBackgroundHeight - 2; i++)
		{
			int sliceIndex = i % 8;
			blit(matrixStack, playerBackgroundX, playerBackgroundY + 2 + i, WIDGET_OFFSET_X, WIDGET_OFFSET_Y + 2 + sliceIndex, 3, 1);
			blit(matrixStack, playerBackgroundX + playerBackgroundWidth, playerBackgroundY + 2 + i, WIDGET_OFFSET_X + 9, WIDGET_OFFSET_Y + 2 + sliceIndex, 3, 1);
		}

		for(int i = 0; i < playerBackgroundWidth - 3; i++)
		{
			int sliceX = i % 7;

			if(playerBackgroundHeight - 3 > 8)
			{
				for(int j = 0; j < playerBackgroundSegments; j++)
				{
					int sliceY = j % 7;
					blit(matrixStack, playerBackgroundX + 3 + i, playerBackgroundY + 3 + j, WIDGET_OFFSET_X + 2 + sliceX, WIDGET_OFFSET_Y + 2 + sliceY, 1, 8);
				}

				if(playerBackgroundRemaining > 0)
				{
					for(int j = 0; j < playerBackgroundRemaining; j++)
					{
						blit(matrixStack, playerBackgroundX + 3 + i, playerBackgroundY + 4 + playerBackgroundFullSegments + j, WIDGET_OFFSET_X + 2 + sliceX, WIDGET_OFFSET_Y + 2, 1, 8);
					}
				}
			}
			else
				blit(matrixStack, playerBackgroundX + 3 + i, playerBackgroundY + 3, WIDGET_OFFSET_X + 2 + sliceX, WIDGET_OFFSET_Y + 2, 1, 8);
		}

		// slots
		for(int i = 0; i < 3; i++)
		{
			int slotX = calcSlotDisplayX(i);

			for(int j = 0; j < 3; j++)
			{
				int slotY = calcSlotDisplayY(j);
				int slotIndex = calcSlotIndex(i, j);

				int slotTextureOffsetX = WIDGET_OFFSET_X;
				int slotTextureOffsetY = 15;

				if(!canInteract || !localGrid[slotIndex].isEmpty() || !localCurrentMove.is(localState))
					slotTextureOffsetX += SLOT_DISABLED_OFFSET_X;
				if(localCurrentMove.is(localState))
					slotTextureOffsetY += SLOT_HEIGHT + 2;
				if(!isGameRunning && (winningSlots[0] == slotIndex || winningSlots[1] == slotIndex || winningSlots[2] == slotIndex))
					slotTextureOffsetX = WIDGET_OFFSET_X + SLOT_WIDTH + 2;

				blit(matrixStack, slotX, slotY, slotTextureOffsetX, slotTextureOffsetY, SLOT_WIDTH, SLOT_HEIGHT);

				if(isGameRunning)
				{
					if(canInteract)
					{
						if(localGrid[slotIndex].isEmpty() && localCurrentMove.is(localState))
						{
							if(isHoveringOverSlot(i, j, mouseX, mouseY))
								renderSlotHighlight(matrixStack, slotX, slotY);
						}
					}
				}
				// adding highlight on top of
				// disabled texture is pointless
				// it displays the same color
				// as enabled texture
				/*else
				{
					if(winningSlots[i] == slotIndex)
						renderSlotHighlight(matrixStack, slotX, slotY);
				}*/
			}
		}
	}

	private void renderGameBoardForeground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		// player names
		int challengerColor = 16777215;
		int opponentColor = 16777215;

		if(!isGameRunning)
		{
			if(isWinner)
			{
				challengerColor = isChallenger ? 5635925 : 16733525;
				opponentColor = isChallenger ? 16733525 : 5635925;
			}
			else
			{
				challengerColor = isChallenger ? 16733525 : 5635925;
				opponentColor = isChallenger ? 5635925 : 16733525;
			}
		}

		if(challengerName != null)
			drawString(matrixStack, font, challengerName, playerBackgroundX + 3, playerBackgroundY + 4, challengerColor);
		if(opponentName != null)
			drawString(matrixStack, font, opponentName, playerBackgroundX + 3, playerBackgroundY + font.lineHeight + 4, opponentColor);

		// slot contents
		for(int i = 0; i < 3; i++)
		{
			int slotX = calcSlotDisplayX(i);

			for(int j = 0; j < 3; j++)
			{
				int slotY = calcSlotDisplayY(j);
				int slotIndex = calcSlotIndex(i, j);
				int slotColor = 16777215;

				if(!isGameRunning && (winningSlots[0] == slotIndex || winningSlots[1] == slotIndex || winningSlots[2] == slotIndex))
					slotColor = 16777045;

				if(localGrid[slotIndex].isCross())
					drawCenteredString(matrixStack, font, "X", slotX + (SLOT_WIDTH / 2), slotY + (SLOT_HEIGHT / 2) - (font.lineHeight / 2), slotColor);
				else if(localGrid[slotIndex].isNought())
					drawCenteredString(matrixStack, font, "0", slotX + (SLOT_WIDTH / 2), slotY + (SLOT_HEIGHT / 2) - (font.lineHeight / 2), slotColor);
			}
		}
	}

	private void renderSlotHighlight(MatrixStack matrixStack, int slotX, int slotY)
	{
		RenderSystem.disableDepthTest();
		RenderSystem.colorMask(true, true, true, false);
		fillGradient(matrixStack, slotX + 1, slotY + 1, slotX + (SLOT_WIDTH - 1), slotY + (SLOT_HEIGHT - 1), -2130706433, -2130706433);
		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.enableDepthTest();
	}

	private boolean clickedSlot(double mouseX, double mouseY, int mouseButton)
	{
		if(!isGameRunning)
			return false;
		if(!localCurrentMove.is(localState) || !canInteract)
			return false;
		if(mouseButton != GLFW.GLFW_MOUSE_BUTTON_1)
			return false;

		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 3; j++)
			{
				if(isHoveringOverSlot(i, j, mouseX, mouseY))
				{
					int slotIndex = calcSlotIndex(i, j);

					if(localGrid[slotIndex].isEmpty())
					{
						setSlot(slotIndex, localState);
						minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean isHoveringOverSlot(int slotIndexX, int slotIndexY, double mouseX, double mouseY)
	{
		int slotX = calcSlotDisplayX(slotIndexX);
		int slotY = calcSlotDisplayY(slotIndexY);
		return mouseX >= slotX - 1 && mouseX < slotX + SLOT_WIDTH + 1 && mouseY >= slotY - 1 && mouseY < slotY + SLOT_HEIGHT + 1;
	}

	private int calcSlotDisplayX(int slotIndexX)
	{
		return backgroundTextureX + 5 + ((SLOT_WIDTH + 2) * slotIndexX);
	}

	private int calcSlotDisplayY(int slotIndexY)
	{
		return backgroundTextureY + 5 + ((SLOT_HEIGHT + 2) * slotIndexY);
	}

	private int calcSlotIndex(int slotIndexX, int slotIndexY)
	{
		return slotIndexY + slotIndexX * 3;
	}

	private void calculateScreenProperties()
	{
		if(challengerName == null)
		{
			ITextComponent challengerDisplayName = isChallenger ? minecraft.player.getDisplayName() : localOpponentName;

			calculateProperties = true;
			challengerName = new TranslationTextComponent("0 - ").append(challengerDisplayName);
			challengerWidth = font.width(challengerName.getString());
		}

		if(opponentName == null)
		{
			ITextComponent opponentDisplayNameName = isChallenger ? localOpponentName : minecraft.player.getDisplayName();

			calculateProperties = true;
			opponentName = new TranslationTextComponent("X - ").append(opponentDisplayNameName);
			opponentWidth = font.width(opponentName.getString());
		}

		if(calculateProperties)
		{
			backgroundTextureX = (width / 2) - (BACKGROUND_WIDTH / 2);
			backgroundTextureY = (height / 2) - (BACKGROUND_HEIGHT / 2);

			maxPlayerNameWidth = Math.max(challengerWidth, opponentWidth);

			playerBackgroundX = (width / 2) - (maxPlayerNameWidth / 2);
			playerBackgroundY = backgroundTextureY + BACKGROUND_HEIGHT + 1;
			playerBackgroundWidth = maxPlayerNameWidth + 3;
			playerBackgroundHeight = font.lineHeight * 2 + 3;

			playerBackgroundSegments = (playerBackgroundHeight - 3) / 8F;
			playerBackgroundFullSegments = MathHelper.floor(playerBackgroundSegments);

			playerBackgroundRemaining = playerBackgroundHeight - 3 - playerBackgroundFullSegments - 8;

			calculateProperties = false;
		}
	}

	private void setSlot(int slotIndex, TicTacToeState slotState)
	{
		localGrid[slotIndex] = slotState;
		localCurrentMove = localState.opponent();
		FantasyTable.NETWORK.sendToServer(new TicTacToeGameStatePacket(localGrid, localCurrentMove, localOpponentId, false));
	}

	private boolean updateWinState()
	{
		Triple<Integer, Integer, Integer> winState = findWinState(localState, localGrid);

		if(winState != null)
		{
			isGameRunning = false;
			isWinner = true;
			winningSlots[0] = winState.getLeft();
			winningSlots[1] = winState.getMiddle();
			winningSlots[2] = winState.getRight();
			return true;
		}

		winState = findWinState(localState.opponent(), localGrid);

		if(winState != null)
		{
			isGameRunning = false;
			isWinner = false;
			winningSlots[0] = winState.getLeft();
			winningSlots[1] = winState.getMiddle();
			winningSlots[2] = winState.getRight();
			return true;
		}

		return false;
	}

	public void updateFromRemote(TicTacToeState[] slotStates, TicTacToeState currentMove)
	{
		Validate.isTrue(localGrid.length == slotStates.length);
		System.arraycopy(slotStates, 0, localGrid, 0, slotStates.length);
		localCurrentMove = currentMove;
		calculateProperties = true;
	}

	@Nullable
	private static Triple<Integer, Integer, Integer> findWinState(TicTacToeState winningState, TicTacToeState[] grid)
	{
		/*
		 *      0 1 2
		 *      3 4 5
		 *      6 7 8
		 */

		if(grid[0].is(winningState) && grid[1].is(winningState) && grid[2].is(winningState))
			return Triple.of(0, 1, 2);
		if(grid[3].is(winningState) && grid[4].is(winningState) && grid[5].is(winningState))
			return Triple.of(3, 4, 5);
		if(grid[6].is(winningState) && grid[7].is(winningState) && grid[8].is(winningState))
			return Triple.of(6, 7, 8);
		if(grid[0].is(winningState) && grid[3].is(winningState) && grid[6].is(winningState))
			return Triple.of(0, 3, 6);
		if(grid[1].is(winningState) && grid[4].is(winningState) && grid[7].is(winningState))
			return Triple.of(1, 4, 7);
		if(grid[2].is(winningState) && grid[5].is(winningState) && grid[8].is(winningState))
			return Triple.of(2, 5, 8);
		if(grid[0].is(winningState) && grid[4].is(winningState) && grid[8].is(winningState))
			return Triple.of(0, 4, 8);
		if(grid[2].is(winningState) && grid[4].is(winningState) && grid[6].is(winningState))
			return Triple.of(2, 4, 6);

		return null;
	}
}
