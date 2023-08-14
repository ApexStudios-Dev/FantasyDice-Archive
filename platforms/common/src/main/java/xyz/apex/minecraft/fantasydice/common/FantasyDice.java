package xyz.apex.minecraft.fantasydice.common;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import xyz.apex.lib.Services;
import xyz.apex.minecraft.apexcore.common.lib.registry.Registrar;
import xyz.apex.minecraft.apexcore.common.lib.registry.entry.*;
import xyz.apex.minecraft.apexcore.common.lib.resgen.ProviderTypes;
import xyz.apex.minecraft.apexcore.common.lib.resgen.state.MultiVariantBuilder;
import xyz.apex.minecraft.apexcore.common.lib.resgen.state.PropertyDispatch;
import xyz.apex.minecraft.apexcore.common.lib.resgen.state.Variant;
import xyz.apex.minecraft.fantasydice.common.block.DiceStationBlock;
import xyz.apex.minecraft.fantasydice.common.client.screen.DicePouchMenuScreen;
import xyz.apex.minecraft.fantasydice.common.client.screen.DiceStationMenuScreen;
import xyz.apex.minecraft.fantasydice.common.item.DicePouchItem;
import xyz.apex.minecraft.fantasydice.common.menu.DicePouchMenu;
import xyz.apex.minecraft.fantasydice.common.menu.DiceStationMenu;
import xyz.apex.minecraft.fantasydice.common.recipe.DiceStationRecipe;

@ApiStatus.NonExtendable
public interface FantasyDice
{
    Logger LOGGER = LogManager.getLogger();
    String ID = "fantasydice";

    FantasyDice INSTANCE = Services.singleton(FantasyDice.class);
    Registrar REGISTRAR = Registrar.create(ID);

    BlockEntry<DiceStationBlock> DICE_STATION_BLOCK = diceStation();
    MenuEntry<DiceStationMenu> DICE_STATION_MENU = REGISTRAR.menu(DiceStationMenu::forNetwork, () -> () -> DiceStationMenuScreen::new);
    RegistryEntry<RecipeType<DiceStationRecipe>> DICE_STATION_RECIPE_TYPE = REGISTRAR.<DiceStationRecipe>recipeType().register();
    RegistryEntry<SingleItemRecipe.Serializer<DiceStationRecipe>> DICE_STATION_RECIPE_SERIALIZER = REGISTRAR.recipeSerializer(() -> new SingleItemRecipe.Serializer<>(DiceStationRecipe::new)).register();

    ItemEntry<DicePouchItem> DICE_POUCH_ITEM = dicePouch();
    MenuEntry<DicePouchMenu> DICE_POUCH_MENU = REGISTRAR.menu(DicePouchMenu::forNetwork, () -> () -> DicePouchMenuScreen::new);
    Component DICE_POUCH_TXT_SHIFT = dicePouchShiftComponent();

    RegistryEntry<CreativeModeTab> TAB = creativeModeTab();

    default void bootstrap()
    {
        DiceTypes.bootstrap();
        Coins.bootstrap();
        REGISTRAR.register();
        registerGenerators();
    }

    private void registerGenerators()
    {
        var descriptionKey = "pack.%s.description".formatted(ID);

        ProviderTypes.LANGUAGES.addListener(ID, (provider, lookup) -> provider
                .enUS()
                    .add(descriptionKey, "Fantasy's Dice")
                .end()
        );

        ProviderTypes.registerDefaultMcMetaGenerator(ID, Component.translatable(descriptionKey));
    }

    private static BlockEntry<DiceStationBlock> diceStation()
    {
        return REGISTRAR
                .object("dice_station")
                .block(DiceStationBlock::new)
                .copyInitialPropertiesFrom(() -> Blocks.CRAFTING_TABLE)
                .blockState((lookup, entry) -> MultiVariantBuilder
                        .builder(entry.value(), Variant
                                .variant()
                                .model(lookup
                                        .lookup(ProviderTypes.MODELS)
                                        .withParent(ModelLocationUtils.getModelLocation(entry.value()), "block/cube")
                                        .texture("particle", "#bottom")
                                        .texture("down", "#bottom")
                                        .texture("up", "#top")
                                        .texture("down", "#bottom")
                                        .texture("north", "#front")
                                        .texture("east", "#side")
                                        .texture("south", "#side")
                                        .texture("west", "#side")
                                        .texture("side", TextureMapping.getBlockTexture(entry.value()).withSuffix("/side"))
                                        .texture("front", TextureMapping.getBlockTexture(entry.value()).withSuffix("/front"))
                                        .texture("top", TextureMapping.getBlockTexture(entry.value()).withSuffix("/top"))
                                        .texture("bottom", "block/oak_planks")
                                )
                        )
                        .with(PropertyDispatch
                                .property(BlockStateProperties.HORIZONTAL_FACING)
                                .select(Direction.EAST, Variant
                                        .variant()
                                        .yRot(Variant.Rotation.R90)
                                )
                                .select(Direction.SOUTH, Variant
                                        .variant()
                                        .yRot(Variant.Rotation.R180)
                                )
                                .select(Direction.WEST, Variant
                                        .variant()
                                        .yRot(Variant.Rotation.R270)
                                )
                                .select(Direction.NORTH, Variant.variant())
                        )
                )
                .defaultItem()
        .register();
    }

    private static RegistryEntry<CreativeModeTab> creativeModeTab()
    {
        return REGISTRAR
                .creativeModeTab("items")
                .displayItems((params, output) -> REGISTRAR
                        .getAll(Registries.ITEM)
                        .stream()
                        .filter(RegistryEntry::isPresent)
                        .map(ItemEntry::cast)
                        .filter(item -> item.isEnabled(params.enabledFeatures()))
                        .forEach(output::accept)
                )
                .icon(() -> DiceTypes.GOLDEN.find(20).map(ItemLikeEntry::asStack).orElseGet(() -> new ItemStack(Items.BARRIER)))
                .lang("en_us", "Fantasy's Dice")
        .register();
    }

    private static ItemEntry<DicePouchItem> dicePouch()
    {
        return REGISTRAR
                .object("dice_pouch")
                .item(DicePouchItem::new)
                .stacksTo(1)
                .model((provider, lookup, entry) -> provider
                        .getBuilder(ModelLocationUtils.getModelLocation(entry.value()))
                        .parent("item/generated")
                        .texture("layer0", new ResourceLocation(ID, "item/pouch/pouch"))
                        .texture("layer1", new ResourceLocation(ID, "item/pouch/string"))
                )
                .colorHandler(() -> () -> (stack, tintIndex) -> tintIndex == 0 ? ((DyeableLeatherItem) stack.getItem()).getColor(stack) : -1)
        .register();
    }

    private static Component dicePouchShiftComponent()
    {
        var key = DICE_POUCH_ITEM.getRegistryName().toLanguageKey("item", "view_contents");
        var keyShift = "%s.key".formatted(key);

        ProviderTypes.LANGUAGES.addListener(ID, (provider, lookup) -> provider
                .enUS()
                .add(key, "Press <%s> to view contents")
                .add(keyShift, "SHIFT")
        .end());

        return Component
                .translatable(key, Component
                        .translatable(keyShift)
                        .withStyle(ChatFormatting.YELLOW)
                ).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
    }
}
