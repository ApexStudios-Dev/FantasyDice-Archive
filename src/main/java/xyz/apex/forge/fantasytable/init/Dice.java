package xyz.apex.forge.fantasytable.init;

import com.tterrag.registrate.Registrate;
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
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.Tags;
import xyz.apex.forge.fantasytable.FantasyTable;
import xyz.apex.forge.fantasytable.item.DiceItem;
import xyz.apex.forge.fantasytable.item.DyeableDiceItem;
import xyz.apex.forge.fantasytable.util.DiceHelper;

import java.util.Arrays;

import static net.minecraftforge.client.model.generators.ModelProvider.ITEM_FOLDER;

public enum Dice
{
	PAPER(DStrings.DICE_PAPER, TextFormatting.WHITE, true, DTags.Items.PAPER),
	BONE(DStrings.DICE_BONE, TextFormatting.WHITE, false, Tags.Items.BONES),
	IRON(DStrings.DICE_IRON, TextFormatting.GRAY, false, Tags.Items.INGOTS_IRON),
	GOLD(DStrings.DICE_GOLD, TextFormatting.YELLOW, false, Tags.Items.INGOTS_GOLD),
	DIAMOND(DStrings.DICE_DIAMOND, TextFormatting.AQUA, false, Tags.Items.GEMS_DIAMOND),
	EMERALD(DStrings.DICE_EMERALD, TextFormatting.GREEN, false, Tags.Items.GEMS_EMERALD),
	LAPIS_LAZULI(DStrings.DICE_LAPIS_LAZULI, TextFormatting.BLUE, false, Tags.Items.GEMS_LAPIS),
	;

	public static final Dice[] TYPES = values();

	public final Tags.IOptionalNamedTag<Item> tag;
	public final ITag<Item> craftingItem;
	public final ItemEntry<DiceItem> six_sided_die;
	public final ItemEntry<DiceItem> twenty_sided_die;
	public final String typeName;
	public final TextFormatting typeColor;
	public final Rarity rarity;

	Dice(String typeName, TextFormatting typeColor, boolean dyeable, ITag<Item> craftingItem)
	{
		this.typeName = typeName;
		this.typeColor = typeColor;
		this.craftingItem = craftingItem;
		this.rarity = Rarity.create(FantasyTable.ID + ':' + typeName, typeColor);

		tag = ItemTags.createOptional(new ResourceLocation(FantasyTable.ID, DStrings.TAG_DICE + '/' + typeName));

		// formatter:off
		six_sided_die = register(
				this,
				DStrings.ITEM_SIX_SIDED_DIE,
				6,
				dyeable,
				DTags.Items.DICE_SIX_SIDED,
				recipe -> recipe.pattern("II ")
				                .pattern("II ")
				                .pattern("   ")
		);

		twenty_sided_die = register(
				this,
				DStrings.ITEM_TWENTY_SIDED_DIE,
				20,
				dyeable,
				DTags.Items.DICE_TWENTY_SIDED,
				recipe -> recipe.pattern(" I ")
				                .pattern("III")
				                .pattern(" I ")
		);
		// formatter:on
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
		return new TranslationTextComponent(
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
	}

	public static Dice byItem(ItemStack stack)
	{
		return Arrays.stream(Dice.TYPES).filter(dice -> stack.getItem().is(dice.tag)).findFirst().orElse(BONE);
	}

	private static Registrate base(Dice dice, String itemName)
	{
		return FantasyTable.registrate().object(dice.typeName + '_' + itemName);
	}

	private static ItemBuilder<DiceItem, Registrate> dyeable(Dice dice, String itemName)
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

	private static ItemBuilder<DiceItem, Registrate> generic(Dice dice, String itemName)
	{
		return base(dice, itemName).item(DiceItem::new);
	}

	private static ItemEntry<DiceItem> register(Dice dice, String itemName, int sides, boolean dyeable, ITag.INamedTag<Item> sidedTag, NonNullUnaryOperator<ShapedRecipeBuilder> recipePattern)
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
					.model((ctx, provider) -> provider.generated(ctx, new ResourceLocation(FantasyTable.ID, ITEM_FOLDER + "/dice/" + dice.typeName + '/' + itemName)))
					.recipe((ctx, provider) -> recipePattern
							// create the recipe builder
							// then apply the pattern provider
							.apply(ShapedRecipeBuilder
									.shaped(ctx::get, 8)
									.define('I', dice.craftingItem))
							// finalize recipe builder & save
							.group("dice/" + dice.typeName)
							.unlockedBy("has_item", RegistrateRecipeProvider.hasItem(dice.craftingItem))
							.save(provider, new ResourceLocation(FantasyTable.ID, dice.typeName + '/' + itemName))
					)
				.register();
		// formatter:on
	}

	@Deprecated // internal use only
	public static void register() { }
}
