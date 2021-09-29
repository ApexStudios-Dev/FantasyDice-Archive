package xyz.apex.forge.fantasytable.util;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public final class RainbowHelper
{
	private static final Color[] RAINBOW_COLORS = new Color[] {
			Color.fromRgb(16733525), // TextFormatting.RED
			Color.fromRgb(16777045), // TextFormatting.YELLOW
			Color.fromRgb(5635925), // TextFormatting.GREEN
			Color.fromRgb(5636095), // TextFormatting.AQUA
			Color.fromRgb(16733695), // TextFormatting.LIGHT_PURPLE
	};

	private static final Color[] RAINBOW_COLORS_PASTEL = new Color[] {
			Color.fromRgb(0xED9898), // red
			Color.fromRgb(0xF2ED98), // yellow
			Color.fromRgb(0xABCDA9), // green
			Color.fromRgb(0x9EBDDB), // blue
			Color.fromRgb(0xB9A8D6) // purple
	};

	public static IFormattableTextComponent rainbowifyComponent(ITextComponent component, boolean pastel)
	{
		return rainbowifyComponent(component, pastel ? RAINBOW_COLORS_PASTEL : RAINBOW_COLORS);
	}

	public static IFormattableTextComponent rainbowifyComponent(ITextComponent component, Color[] colors)
	{
		String msg = component.getString();
		IFormattableTextComponent result = new StringTextComponent("");

		for(int i = 0; i < msg.length(); i++)
		{
			final int color = i; // must be effectively final to be used in lambda
			result.append(new StringTextComponent(String.valueOf(msg.charAt(i))).withStyle(style -> {
				Color rainbow_color = colors[color % colors.length];
				return style.withColor(rainbow_color);
			}));
		}

		return result;
	}
}
