package xyz.apex.forge.fantasytable.init;

import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.Tags;
import org.apache.commons.lang3.Validate;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.item.DiceItem;
import xyz.apex.forge.fantasytable.item.DyeableDiceItem;
import xyz.apex.forge.fantasytable.util.DiceHelper;
import xyz.apex.forge.fantasytable.util.registrate.CustomRegistrate;

import javax.annotation.Nullable;
import java.util.Arrays;

import static net.minecraftforge.client.model.generators.ModelProvider.ITEM_FOLDER;

public enum Dice
{
	WOOD(FStrings.DICE_WOOD, TextFormatting.WHITE, false, ItemTags.WOODEN_BUTTONS),
	STONE(FStrings.DICE_STONE, TextFormatting.WHITE, false, FTags.Items.STONE_BUTTONS),
	PAPER(FStrings.DICE_PAPER, TextFormatting.WHITE, true, FTags.Items.PAPER),
	BONE(FStrings.DICE_BONE, TextFormatting.WHITE, false, Tags.Items.BONES),
	IRON(FStrings.DICE_IRON, TextFormatting.GRAY, false, null),
	GOLD(FStrings.DICE_GOLD, TextFormatting.YELLOW, false, null),
	DIAMOND(FStrings.DICE_DIAMOND, TextFormatting.AQUA, false, null),
	EMERALD(FStrings.DICE_EMERALD, TextFormatting.GREEN, false, null),
	CREATIVE(FStrings.DICE_CREATIVE, TextFormatting.LIGHT_PURPLE, false, null)
	;

	public static final Dice[] TYPES = values();

	public final Tags.IOptionalNamedTag<Item> tag;
	public final ItemEntry<DiceItem> six_sided_die;
	public final ItemEntry<DiceItem> twenty_sided_die;
	public final String typeName;
	public final Color typeColor;
	public final Rarity rarity;

	// do not use formatting types and only colors from TextFormatting
	// or just use other constructor
	@Deprecated
	Dice(String typeName, TextFormatting typeColor, boolean dyeable, @Nullable ITag<Item> craftingItem)
	{
		this(typeName, Validate.notNull(Color.fromLegacyFormat(typeColor)), typeColor, dyeable, craftingItem);
	}

	Dice(String typeName, Color typeColor, TextFormatting rarityColor, boolean dyeable, @Nullable ITag<Item> craftingItem)
	{
		this.typeName = typeName;
		this.typeColor = typeColor;
		this.rarity = Rarity.create(FantasyTable.ID + ':' + typeName, rarityColor);

		tag = ItemTags.createOptional(new ResourceLocation(FantasyTable.ID, FStrings.TAG_DICE + '/' + typeName));

		// @formatter:off
		ItemBuilder<DiceItem, CustomRegistrate> sixSidedDieBuilder = register(
				this,
				FStrings.ITEM_SIX_SIDED_DIE,
				6,
				dyeable,
				FTags.Items.DICE_SIX_SIDED
		);

		ItemBuilder<DiceItem, CustomRegistrate> twentySidedDieBuilder = register(
				this,
				FStrings.ITEM_TWENTY_SIDED_DIE,
				20,
				dyeable,
				FTags.Items.DICE_TWENTY_SIDED
		);
		// @formatter:on

		if(craftingItem != null)
		{
			// @formatter:off
			sixSidedDieBuilder = registerRecipe(
					sixSidedDieBuilder,
					FStrings.ITEM_SIX_SIDED_DIE,
					craftingItem,
					recipe -> recipe
							.pattern("II ")
							.pattern("II ")
							.pattern("   ")
			);

			twentySidedDieBuilder = registerRecipe(
					twentySidedDieBuilder,
					FStrings.ITEM_TWENTY_SIDED_DIE,
					craftingItem,
					recipe -> recipe
							.pattern(" I ")
							.pattern("III")
							.pattern(" I ")
			);
			// @formatter:on
		}

		six_sided_die = sixSidedDieBuilder.register();
		twenty_sided_die = twentySidedDieBuilder.register();
	}

	private ItemBuilder<DiceItem, CustomRegistrate> registerRecipe(ItemBuilder<DiceItem, CustomRegistrate> itemBuilder, String itemName, ITag<Item> craftingItem, NonNullUnaryOperator<ShapedRecipeBuilder> recipePattern)
	{
		return itemBuilder.recipe((ctx, provider) -> recipePattern
			// create the recipe builder
			// then apply the pattern provider
			.apply(ShapedRecipeBuilder
					.shaped(ctx::get, 8)
					.define('I', craftingItem))
			// finalize recipe builder & save
			.group("dice/" + typeName)
			.unlockedBy("has_item", RegistrateRecipeProvider.hasItem(craftingItem))
			.save(provider, new ResourceLocation(FantasyTable.ID, typeName + '/' + itemName))
		);
	}

	public Style getTextComponentStyleColor(ItemStack die, Style style)
	{
		if(die.getItem() instanceof IDyeableArmorItem)
			return style.withColor(Color.fromRgb(((IDyeableArmorItem) die.getItem()).getColor(die)));
		else
			return style.withColor(typeColor);
	}

	public Style getTextComponentStyle(ItemStack die, Style style)
	{
		return getTextComponentStyleColor(die, style).withItalic(true);
	}

	public IFormattableTextComponent createItemTooltipComponent(ItemStack die, int min)
	{
		int sides = DiceHelper.getSides(die);

		// formatter:off
		return new TranslationTextComponent(
				FantasyTable.DICE_ROLL_DESC_KEY,
				new StringTextComponent("" + min).withStyle(style -> style.withColor(TextFormatting.DARK_GRAY).withItalic(true)),
				new StringTextComponent("" + sides).withStyle(style -> style.withColor(TextFormatting.DARK_GRAY).withItalic(true))
		).withStyle(style -> style.withColor(TextFormatting.DARK_GRAY));
		// formatter:on
	}

	public IFormattableTextComponent createItemTooltipComponent(ItemStack die)
	{
		return createItemTooltipComponent(die, 1);
	}

	public IFormattableTextComponent createTextComponent(PlayerEntity thrower, ItemStack die, int roll)
	{
		int sides = DiceHelper.getSides(die);
		String strAmount = "";

		if(die.getCount() > 1)
			strAmount += die.getCount();

		// formatter:off
		IFormattableTextComponent component = new TranslationTextComponent(
				FantasyTable.DICE_ROLL_KEY,
				thrower.getDisplayName(),
				// TODO: Should this component be localized?
				// <dice_roll> ([amount_of_rolls]d<dice_sides>)
				new StringTextComponent(roll + " (" + strAmount + "d" + sides + ")").withStyle(style -> getTextComponentStyle(die, style))
		)
				.withStyle(style ->
						style.withHoverEvent(
								new HoverEvent(
										HoverEvent.Action.SHOW_TEXT,
										new TranslationTextComponent(FantasyTable.DICE_ROLL_USING_KEY, die.getHoverName()).withStyle(hoverStyle -> getTextComponentStyleColor(die, hoverStyle))
								)
						)
				);
		// formatter:on

		if(this == CREATIVE)
			return rainbowifyComponent(component);
		return component;
	}

	public static Dice byItem(ItemStack stack)
	{
		return Arrays.stream(Dice.TYPES).filter(dice -> stack.getItem().is(dice.tag)).findFirst().orElse(BONE);
	}

	private static CustomRegistrate base(Dice dice, String itemName)
	{
		return FantasyTable.registrate().object(dice.typeName + '_' + itemName);
	}

	private static ItemBuilder<DiceItem, CustomRegistrate> dyeable(Dice dice, String itemName)
	{
		// formatter:off
		return base(dice, itemName)
				// casting here is needed so that the ternary inside
				// #register() can work, without it, we get compile errors
				// due to types not matching
				// although DyeableDiceItem is instanceof DiceItem and can simply be cast to it
				.item(properties -> (DiceItem) new DyeableDiceItem(properties))
				.color(() -> () -> (stack, tintIndex) -> ((IDyeableArmorItem) stack.getItem()).getColor(stack));
		// formatter:on
	}

	private static ItemBuilder<DiceItem, CustomRegistrate> generic(Dice dice, String itemName)
	{
		return base(dice, itemName).item(DiceItem::new);
	}

	private static ItemBuilder<DiceItem, CustomRegistrate> register(Dice dice, String itemName, int sides, boolean dyeable, ITag.INamedTag<Item> sidedTag)
	{
		// formatter:off
		// ternary to create item based on if dyeable or not
		// 2 things need to happen based on this flag
		// 1) built item instance must be instanced of IDyeableArmorItem
		//      This is to allow the dyeable recipes to work and have the dyed color stored correctly on the item
		//      This is done by creating DyeableItemInstance which implements the interface
		// 2) `.color()` on the item builder must be invoked
		//      This register an `ItemColors` for the item which returns the
		//      color used during the item rendering process
		//      Since our item will implement `IDyeableArmorItem` we can just use the `.getColor(ItemStack)`
		//      method `IDyeableArmorItem` has, to obtain the color from the ItemStack
		return (dyeable ? dyeable(dice, itemName) : generic(dice, itemName))

					.lang(RegistrateLangProvider.toEnglishName(dice.typeName) + " d" + sides)
					/*
					.lang(item -> {
						// splits by all underscores `_` and capitalizes first letter of each token
						// then collects together with space between each token
						//      `diamond`->`Diamond`
						//      `lapis_lazuli`->`Lapis Lazuli`
						/\*
						String typeNameEnglish = Arrays.stream(dice.typeName.split("_"))
						                               .map(token ->
								                               token.substring(0, 1).toUpperCase(Locale.ROOT) + // first letter of token in uppercase
	                                                           token.substring(1).toLowerCase(Locale.ROOT)) // entire token without first letter in lowercase
						                               .collect(Collectors.joining(" "));
						*\/
						String typeNameEnglish = RegistrateLangProvider.toEnglishName(dice.typeName);
						return typeNameEnglish + " d" + sides;
					})
					*/

					.tag(sidedTag, dice.tag)
					.properties(properties -> properties.stacksTo(8).rarity(dice.rarity))
					// simple `item/generated` model
					// but texture is relocated from `<modid>:textures/item/<item_name>.png`
					// into `<modid>:textures/item/dice/<dice_type>/<item_name>.png`
					.model((ctx, provider) -> provider.generated(ctx, new ResourceLocation(FantasyTable.ID, ITEM_FOLDER + "/dice/" + dice.typeName + '/' + itemName)));
		// formatter:on
	}

	// TODO: Maybe move this to some utility class
	private static final TextFormatting[] RAINBOW_COLORS = new TextFormatting[] {
			TextFormatting.RED,
			TextFormatting.YELLOW,
			TextFormatting.GREEN,
			TextFormatting.AQUA,
			TextFormatting.LIGHT_PURPLE
	};

	public static IFormattableTextComponent rainbowifyComponent(IFormattableTextComponent component)
	{
		String msg = component.getString();
		IFormattableTextComponent result = new StringTextComponent("");
		int offset = MathHelper.floor(Math.random() * msg.length());

		for(int i = 0; i < msg.length(); i++)
		{
			final int color = i + offset; // must be effectively final to be used in lambda
			result.append(new StringTextComponent(String.valueOf(msg.charAt(i))).withStyle(style -> {
				TextFormatting rainbow_color = RAINBOW_COLORS[color % RAINBOW_COLORS.length];
				return style.withColor(rainbow_color);
			}));
		}

		return result;
	}

	@Deprecated // internal use only
	public static void register() { }
}
