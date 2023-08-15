package io.taraxacum.finaltech.util;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.common.util.ReflectionUtil;
import io.taraxacum.common.util.StringUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.command.*;
import io.taraxacum.finaltech.core.enchantment.NullEnchantment;
import io.taraxacum.finaltech.core.interfaces.ExtraParameterItem;
import io.taraxacum.finaltech.core.interfaces.VisibleItem;
import io.taraxacum.finaltech.core.interfaces.impl.ItemCondition;
import io.taraxacum.finaltech.core.interfaces.impl.PermissionCondition;
import io.taraxacum.finaltech.core.interfaces.impl.ResearchCondition;
import io.taraxacum.finaltech.core.interfaces.impl.SimpleSpecialResearch;
import io.taraxacum.finaltech.core.item.AbstractMySlimefunItem;
import io.taraxacum.finaltech.core.item.machine.AbstractMachine;
import io.taraxacum.finaltech.core.listener.ResearchListener;
import io.taraxacum.finaltech.setup.FinalTechItemStacks;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.setup.FinalTechMenus;
import io.taraxacum.libs.plugin.file.ConfigFileManager;
import io.taraxacum.libs.plugin.file.LanguageManager;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.plugin.util.TextUtil;
import io.taraxacum.libs.slimefun.interfaces.SimpleValidItem;
import io.taraxacum.libs.slimefun.service.BlockTickerService;
import io.taraxacum.libs.slimefun.service.impl.BlockStorageDataService;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Final_ROOT
 */
public final class SetupUtil {
    private static final List<String> BELIEVABLE_PLUGIN_ID_LIST = new ArrayList<>();

    static {
        BELIEVABLE_PLUGIN_ID_LIST.add(FinalTech.getInstance().getName());
    }

    public static void setupLanguageManager(@Nonnull LanguageManager languageManager) {
        // Color normal
        languageManager.addFunction(new Function<>() {
            @Override
            public String apply(String s) {
                String[] split = StringUtil.split(s, "{color:", "}");
                if (split.length == 3) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(split[0]);
                    switch (split[1]) {
                        case "normal" -> stringBuilder.append(TextUtil.COLOR_NORMAL);
                        case "stress" -> stringBuilder.append(TextUtil.COLOR_STRESS);
                        case "action" -> stringBuilder.append(TextUtil.COLOR_ACTION);
                        case "initiative" -> stringBuilder.append(TextUtil.COLOR_INITIATIVE);
                        case "passive" -> stringBuilder.append(TextUtil.COLOR_PASSIVE);
                        case "number" -> stringBuilder.append(TextUtil.COLOR_NUMBER);
                        case "positive" -> stringBuilder.append(TextUtil.COLOR_POSITIVE);
                        case "negative" -> stringBuilder.append(TextUtil.COLOR_NEGATIVE);
                        case "conceal" -> stringBuilder.append(TextUtil.COLOR_CONCEAL);
                        case "input" -> stringBuilder.append(TextUtil.COLOR_INPUT);
                        case "output" -> stringBuilder.append(TextUtil.COLOR_OUTPUT);
                        case "random" -> stringBuilder.append(TextUtil.getRandomColor());
                        case "prandom" -> stringBuilder.append(TextUtil.getPseudorandomColor(FinalTech.getSeed()));
                        default -> stringBuilder.append(split[1]);
                    }
                    return stringBuilder.append(this.apply(split[2])).toString();
                } else {
                    return s;
                }
            }
        });
        // SlimefunItem name by id
        languageManager.addFunction(new Function<>() {
            @Override
            public String apply(String s) {
                String[] split = StringUtil.split(s, "{id:", "}");
                if (split.length == 3) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(split[0]);
                    SlimefunItem slimefunItem = SlimefunItem.getById(split[1]);
                    if (slimefunItem != null) {
                        stringBuilder.append(slimefunItem.getItemName());
                    } else {
                        stringBuilder.append(split[1]);
                    }
                    return stringBuilder.append(this.apply(split[2])).toString();
                } else {
                    return s;
                }
            }
        });
        // Color random
        languageManager.addFunction(new Function<>() {
            @Override
            public String apply(String s) {
                String[] split = StringUtil.split(s, "{random-color:start}", "{random-color:end}");
                if (split.length == 3) {
                    return split[0] + TextUtil.colorRandomString(split[1]) + this.apply(split[2]);
                } else {
                    return s;
                }
            }
        });
    }

    private static void setupEnchantment() {
        try {
            ReflectionUtil.setStaticValue(Enchantment.class, "acceptingNew", true);
            Enchantment.registerEnchantment(NullEnchantment.INSTANCE);

            // material
            NullEnchantment.addAndHidden(FinalTechItemStacks.BIT);
            NullEnchantment.addAndHidden(FinalTechItemStacks.META);
            NullEnchantment.addAndHidden(FinalTechItemStacks.UNORDERED_DUST);
            NullEnchantment.addAndHidden(FinalTechItemStacks.ORDERED_DUST);
            NullEnchantment.addAndHidden(FinalTechItemStacks.ETHER);
            NullEnchantment.addAndHidden(FinalTechItemStacks.BUG);
            NullEnchantment.addAndHidden(FinalTechItemStacks.ENTROPY);
            NullEnchantment.addAndHidden(FinalTechItemStacks.COPY_CARD);
            NullEnchantment.addAndHidden(FinalTechItemStacks.ANNULAR);
            NullEnchantment.addAndHidden(FinalTechItemStacks.SINGULARITY);
            NullEnchantment.addAndHidden(FinalTechItemStacks.SPIROCHETE);
            NullEnchantment.addAndHidden(FinalTechItemStacks.SHELL);
            NullEnchantment.addAndHidden(FinalTechItemStacks.PHONY);
            NullEnchantment.addAndHidden(FinalTechItemStacks.JUSTIFIABILITY);
            NullEnchantment.addAndHidden(FinalTechItemStacks.EQUIVALENT_CONCEPT);

            // machine card
            NullEnchantment.addAndHidden(FinalTechItemStacks.MACHINE_CHARGE_CARD_L3);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MACHINE_ACCELERATE_CARD_L3);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MACHINE_ACTIVATE_CARD_L3);

            // advanced quantity module
            NullEnchantment.addAndHidden(FinalTechItemStacks.ENERGIZED_QUANTITY_MODULE);
            NullEnchantment.addAndHidden(FinalTechItemStacks.OVERLOADED_QUANTITY_MODULE);

            // advanced cargo
            NullEnchantment.addAndHidden(FinalTechItemStacks.ADVANCED_POINT_TRANSFER);
            NullEnchantment.addAndHidden(FinalTechItemStacks.ADVANCED_MESH_TRANSFER);
            NullEnchantment.addAndHidden(FinalTechItemStacks.ADVANCED_LINE_TRANSFER);
            NullEnchantment.addAndHidden(FinalTechItemStacks.ADVANCED_LOCATION_TRANSFER);

            // final stage items
            NullEnchantment.addAndHidden(FinalTechItemStacks.MATRIX_MACHINE_CHARGE_CARD);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MATRIX_MACHINE_ACCELERATE_CARD);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MATRIX_MACHINE_ACTIVATE_CARD);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MATRIX_OPERATION_ACCELERATE_CARD);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MATRIX_QUANTITY_MODULE);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MATRIX_OPERATION_ACCELERATOR);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MATRIX_EXPANDED_CAPACITOR);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MATRIX_GENERATOR);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MATRIX_ITEM_DISMANTLE_TABLE);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MATRIX_STORAGE);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MATRIX_ACCELERATOR);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MATRIX_COPY_CARD_DUPLICATOR);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MATRIX_COPY_CARD_FACTORY);
            NullEnchantment.addAndHidden(FinalTechItemStacks.MATRIX_REACTOR);
        } catch (Exception e) {
            e.printStackTrace();
            FinalTech.logger().warning("Some error occurred while registering enchantment");
        }
    }

    public static void setupItem() {
        ItemStackUtil.setLore(FinalTechItemStacks.TROPHY_MEAWERFUL, "§fThanks for some good idea");
        ItemStackUtil.setLore(FinalTechItemStacks.TROPHY_SHIXINZIA, "§fThanks for some test work");

        /* items */
        // material
        FinalTechMenus.SUB_MENU_MATERIAL.addTo(
                FinalTechItems.GEARWHEEL.registerThis(),
                FinalTechItems.BIT.registerThis(),
                FinalTechItems.META.registerThis(),
                FinalTechItems.UNORDERED_DUST.registerThis(),
                FinalTechItems.ORDERED_DUST.registerThis(),
                FinalTechItems.ETHER.registerThis(),
                FinalTechItems.BUG.registerThis(),
                FinalTechItems.ENTROPY.registerThis()
        );
        FinalTechMenus.SUB_MENU_MATERIAL.addTo(
                FinalTechItems.COPY_CARD.registerThis(),
                FinalTechItems.ANNULAR.registerThis(),
                FinalTechItems.SINGULARITY.registerThis(),
                FinalTechItems.SPIROCHETE.registerThis(),
                FinalTechItems.SHELL.registerThis(),
                FinalTechItems.ITEM_PHONY.registerThis(),
                FinalTechItems.JUSTIFIABILITY.registerThis(),
                FinalTechItems.EQUIVALENT_CONCEPT.registerThis()
        );
        FinalTechMenus.SUB_MENU_MATERIAL.addTo(
                FinalTechItems.WATER_CARD.registerThis(),
                FinalTechItems.LAVA_CARD.registerThis(),
                FinalTechItems.MILK_CARD.registerThis(),
                FinalTechItems.FLINT_AND_STEEL_CARD.registerThis(),
                FinalTechItems.QUANTITY_MODULE.registerThis(),
                FinalTechItems.ENERGIZED_QUANTITY_MODULE.registerThis(),
                FinalTechItems.OVERLOADED_QUANTITY_MODULE.registerThis()
        );
        // logic item
        FinalTechMenus.SUB_MENU_LOGIC_ITEM.addTo(
                FinalTechItems.LOGIC_FALSE.registerThis(),
                FinalTechItems.LOGIC_TRUE.registerThis(),
                FinalTechItems.DIGITAL_ZERO.registerThis(),
                FinalTechItems.DIGITAL_ONE.registerThis(),
                FinalTechItems.DIGITAL_TWO.registerThis(),
                FinalTechItems.DIGITAL_THREE.registerThis(),
                FinalTechItems.DIGITAL_FOUR.registerThis(),
                FinalTechItems.DIGITAL_FIVE.registerThis(),
                FinalTechItems.DIGITAL_SIX.registerThis(),
                FinalTechItems.DIGITAL_SEVEN.registerThis(),
                FinalTechItems.DIGITAL_EIGHT.registerThis(),
                FinalTechItems.DIGITAL_NINE.registerThis(),
                FinalTechItems.DIGITAL_TEN.registerThis(),
                FinalTechItems.DIGITAL_ELEVEN.registerThis(),
                FinalTechItems.DIGITAL_TWELVE.registerThis(),
                FinalTechItems.DIGITAL_THIRTEEN.registerThis(),
                FinalTechItems.DIGITAL_FOURTEEN.registerThis(),
                FinalTechItems.DIGITAL_FIFTEEN.registerThis()
        );
        // prop
        FinalTechMenus.SUB_MENU_PROP.addTo(
                FinalTechItems.MENU_VIEWER.registerThis(),
                FinalTechItems.ROUTE_VIEWER.registerThis(),
                FinalTechItems.LOCATION_RECORDER.registerThis(),
                FinalTechItems.MACHINE_CONFIGURATOR.registerThis(),
                FinalTechItems.PORTABLE_ENERGY_STORAGE.registerThis()
        );
        FinalTechMenus.SUB_MENU_PROP.addTo(
                FinalTechItems.POTION_EFFECT_COMPRESSOR.registerThis(),
                FinalTechItems.POTION_EFFECT_DILATOR.registerThis(),
                FinalTechItems.POTION_EFFECT_PURIFIER.registerThis(),
                FinalTechItems.GRAVITY_REVERSAL_RUNE.registerThis(),
                FinalTechItems.STAFF_ELEMENTAL_LINE.registerThis(),
                FinalTechItems.STEPPING_STONE.registerThis(),
                FinalTechItems.STEPPING_STONE_PLACER.registerThis(),
                FinalTechItems.AUTO_FLASH_BACK_POCKET_WATCH.registerThis()
        );
        // consumable
        FinalTechMenus.SUB_MENU_CONSUMABLE.addTo(
                FinalTechItems.ENERGY_CARD_K.registerThis(),
                FinalTechItems.ENERGY_CARD_M.registerThis(),
                FinalTechItems.ENERGY_CARD_B.registerThis(),
                FinalTechItems.ENERGY_CARD_T.registerThis(),
                FinalTechItems.ENERGY_CARD_Q.registerThis(),
                FinalTechItems.ENERGIZED_OPERATION_ACCELERATE_CARD.registerThis(),
                FinalTechItems.OVERLOADED_OPERATION_ACCELERATE_CARD.registerThis()
        );
        FinalTechMenus.SUB_MENU_CONSUMABLE.addTo(
                FinalTechItems.MACHINE_CHARGE_CARD_L1.registerThis(),
                FinalTechItems.MACHINE_CHARGE_CARD_L2.registerThis(),
                FinalTechItems.MACHINE_CHARGE_CARD_L3.registerThis(),
                FinalTechItems.MACHINE_ACCELERATE_CARD_L1.registerThis(),
                FinalTechItems.MACHINE_ACCELERATE_CARD_L2.registerThis(),
                FinalTechItems.MACHINE_ACCELERATE_CARD_L3.registerThis(),
                FinalTechItems.MACHINE_ACTIVATE_CARD_L1.registerThis(),
                FinalTechItems.MACHINE_ACTIVATE_CARD_L2.registerThis(),
                FinalTechItems.MACHINE_ACTIVATE_CARD_L3.registerThis()
        );
        FinalTechMenus.SUB_MENU_CONSUMABLE.addTo(
                FinalTechItems.MAGIC_HYPNOTIC.registerThis(),
                FinalTechItems.RESEARCH_UNLOCK_TICKET.registerThis()
        );
        // weapon
        FinalTechMenus.SUB_MENU_TOOL.addTo(
                FinalTechItems.SUPER_SHOVEL.registerThis(),
                FinalTechItems.ULTIMATE_SHOVEL.registerThis(),
                FinalTechItems.SUPER_PICKAXE.registerThis(),
                FinalTechItems.ULTIMATE_PICKAXE.registerThis(),
                FinalTechItems.SUPER_AXE.registerThis(),
                FinalTechItems.ULTIMATE_AXE.registerThis(),
                FinalTechItems.SUPER_HOE.registerThis(),
                FinalTechItems.ULTIMATE_HOE.registerThis()
        );

        /* electricity system */
        // electric generator
        FinalTechMenus.SUB_MENU_ELECTRIC_GENERATOR.addTo(
                FinalTechItems.BASIC_GENERATOR.registerThis(),
                FinalTechItems.ADVANCED_GENERATOR.registerThis(),
                FinalTechItems.CARBONADO_GENERATOR.registerThis(),
                FinalTechItems.ENERGIZED_GENERATOR.registerThis(),
                FinalTechItems.ENERGIZED_STACK_GENERATOR.registerThis(),
                FinalTechItems.OVERLOADED_GENERATOR.registerThis()
        );
        FinalTechMenus.SUB_MENU_ELECTRIC_GENERATOR.addTo(
                FinalTechItems.DUST_GENERATOR.registerThis(),
                FinalTechItems.TIME_GENERATOR.registerThis(),
                FinalTechItems.ENERGIZED_CHARGE_BASE.registerThis(),
                FinalTechItems.OVERLOADED_CHARGE_BASE.registerThis()
        );
        // electric storage
        FinalTechMenus.SUB_MENU_ELECTRIC_STORAGE.addTo(
                FinalTechItems.SMALL_EXPANDED_CAPACITOR.registerThis(),
                FinalTechItems.MEDIUM_EXPANDED_CAPACITOR.registerThis(),
                FinalTechItems.BIG_EXPANDED_CAPACITOR.registerThis(),
                FinalTechItems.LARGE_EXPANDED_CAPACITOR.registerThis(),
                FinalTechItems.CARBONADO_EXPANDED_CAPACITOR.registerThis(),
                FinalTechItems.ENERGIZED_EXPANDED_CAPACITOR.registerThis()
        );
        FinalTechMenus.SUB_MENU_ELECTRIC_STORAGE.addTo(
                FinalTechItems.ENERGIZED_STACK_EXPANDED_CAPACITOR.registerThis(),
                FinalTechItems.OVERLOADED_EXPANDED_CAPACITOR.registerThis(),
                FinalTechItems.TIME_CAPACITOR.registerThis()
        );
        // electric transmission
        FinalTechMenus.SUB_MENU_ELECTRIC_TRANSMISSION.addTo(
                FinalTechItems.NORMAL_ELECTRICITY_SHOOT_PILE.registerThis(),
                FinalTechItems.NORMAL_TRIGGERABLE_ELECTRICITY_SHOOT_PILE.registerThis(),
                FinalTechItems.NORMAL_CONTROLLABLE_ELECTRICITY_SHOOT_PILE.registerThis(),
                FinalTechItems.NORMAL_CONFIGURABLE_ELECTRICITY_SHOOT_PILE.registerThis(),
                FinalTechItems.ENERGIZED_ELECTRICITY_SHOOT_PILE.registerThis(),
                FinalTechItems.OVERLOADED_ELECTRICITY_SHOOT_PILE.registerThis(),
                FinalTechItems.VARIABLE_WIRE_RESISTANCE.registerThis(),
                FinalTechItems.VARIABLE_WIRE_CAPACITOR.registerThis()
        );
        // electric accelerator
        FinalTechMenus.SUB_MENU_ELECTRIC_ACCELERATOR.addTo(
                FinalTechItems.ENERGIZED_ACCELERATOR.registerThis(),
                FinalTechItems.OVERLOADED_ACCELERATOR.registerThis()
        );

        /* cargo system */
        // storage unit
        FinalTechMenus.SUB_MENU_STORAGE_UNIT.addTo(
                FinalTechItems.NORMAL_STORAGE_UNIT.registerThis(),
                FinalTechItems.DIVIDED_STORAGE_UNIT.registerThis(),
                FinalTechItems.LIMITED_STORAGE_UNIT.registerThis(),
                FinalTechItems.STACK_STORAGE_UNIT.registerThis(),
                FinalTechItems.DIVIDED_LIMITED_STORAGE_UNIT.registerThis(),
                FinalTechItems.DIVIDED_STACK_STORAGE_UNIT.registerThis(),
                FinalTechItems.LIMITED_STACK_STORAGE_UNIT.registerThis()
        );
        FinalTechMenus.SUB_MENU_STORAGE_UNIT.addTo(
                FinalTechItems.RANDOM_INPUT_STORAGE_UNIT.registerThis(),
                FinalTechItems.RANDOM_OUTPUT_STORAGE_UNIT.registerThis(),
                FinalTechItems.RANDOM_ACCESS_STORAGE_UNIT.registerThis(),
                FinalTechItems.DISTRIBUTE_LEFT_STORAGE_UNIT.registerThis(),
                FinalTechItems.DISTRIBUTE_RIGHT_STORAGE_UNIT.registerThis()
        );
        // advanced storage
        FinalTechMenus.SUB_MENU_ADVANCED_STORAGE.addTo(
                FinalTechItems.STORAGE_INTERACT_PORT.registerThis(),
                FinalTechItems.STORAGE_INSERT_PORT.registerThis(),
                FinalTechItems.STORAGE_WITHDRAW_PORT.registerThis(),
                FinalTechItems.STORAGE_CARD.registerThis()
        );
        FinalTechMenus.SUB_MENU_ADVANCED_STORAGE.addTo(
                FinalTechItems.BASIC_STORAGE.registerThis(),
                FinalTechItems.ADVANCED_STORAGE.registerThis(),
                FinalTechItems.ENERGIZED_STORAGE.registerThis(),
                FinalTechItems.OVERLOADED_STORAGE.registerThis(),
                FinalTechItems.STORAGE_BALANCER.registerThis(),
                FinalTechItems.STORAGE_IMPORTER.registerThis(),
                FinalTechItems.STORAGE_EXPORTER.registerThis()
        );
        // accessor
        FinalTechMenus.SUB_MENU_ACCESSOR.addTo(
                FinalTechItems.REMOTE_ACCESSOR.registerThis(),
                FinalTechItems.TRIGGERABLE_REMOTE_ACCESSOR.registerThis(),
                FinalTechItems.CONTROLLABLE_REMOTE_ACCESSOR.registerThis(),
                FinalTechItems.EXPANDED_TRIGGERABLE_REMOTE_ACCESSOR.registerThis(),
                FinalTechItems.EXPANDED_CONTROLLABLE_REMOTE_ACCESSOR.registerThis(),
                FinalTechItems.RANDOM_ACCESSOR.registerThis(),
                FinalTechItems.AREA_ACCESSOR.registerThis()
        );
        FinalTechMenus.SUB_MENU_ACCESSOR.addTo(
                FinalTechItems.TRANSPORTER.registerThis(),
                FinalTechItems.TRIGGERABLE_TRANSPORTER.registerThis(),
                FinalTechItems.CONTROLLABLE_TRANSPORTER.registerThis(),
                FinalTechItems.EXPANDED_TRIGGERABLE_TRANSPORTER.registerThis(),
                FinalTechItems.EXPANDED_CONTROLLABLE_TRANSPORTER.registerThis()
        );
        // logic
        FinalTechMenus.SUB_MENU_LOGIC.addTo(
                FinalTechItems.LOGIC_COMPARATOR_NOTNULL.registerThis(),
                FinalTechItems.LOGIC_COMPARATOR_AMOUNT.registerThis(),
                FinalTechItems.LOGIC_COMPARATOR_SIMILAR.registerThis(),
                FinalTechItems.LOGIC_COMPARATOR_EQUAL.registerThis(),
                FinalTechItems.LOGIC_CRAFTER.registerThis(),
                FinalTechItems.DIGIT_ADDER.registerThis(),
                FinalTechItems.LOGIC_INJECTOR.registerThis(),
                FinalTechItems.DIGIT_INJECTOR.registerThis()
        );
        // cargo
        FinalTechMenus.SUB_MENU_CARGO.addTo(
                FinalTechItems.BASIC_FRAME_MACHINE.registerThis(),
                FinalTechItems.POINT_TRANSFER.registerThis(),
                FinalTechItems.MESH_TRANSFER.registerThis(),
                FinalTechItems.LINE_TRANSFER.registerThis(),
                FinalTechItems.LOCATION_TRANSFER.registerThis(),
                FinalTechItems.ADVANCED_POINT_TRANSFER.registerThis(),
                FinalTechItems.ADVANCED_MESH_TRANSFER.registerThis(),
                FinalTechItems.ADVANCED_LINE_TRANSFER.registerThis(),
                FinalTechItems.ADVANCED_LOCATION_TRANSFER.registerThis()
        );
        FinalTechMenus.SUB_MENU_CARGO.addTo(
                FinalTechItems.CONFIGURATION_COPIER.registerThis(),
                FinalTechItems.CONFIGURATION_PASTER.registerThis(),
                FinalTechItems.CLICK_WORK_MACHINE.registerThis(),
                FinalTechItems.SIMULATE_CLICK_MACHINE.registerThis(),
                FinalTechItems.TRIGGERABLE_SIMULATE_CLICK_MACHINE.registerThis()
        );

        /* functional machines */
        // core machines
        FinalTechMenus.SUB_MENU_CORE_MACHINE.addTo(
                FinalTechItems.RESEARCH_TABLE.registerThis(),
                FinalTechItems.BEDROCK_CRAFT_TABLE.register(),
                FinalTechItems.MATRIX_CRAFTING_TABLE.registerThis()
        );
        FinalTechMenus.SUB_MENU_CORE_MACHINE.addTo(
                FinalTechItems.INFO_FACTORY.registerThis(),
                FinalTechItems.DUST_FACTORY.registerThis(),
                FinalTechItems.ETHER_MINER.registerThis(),
                FinalTechItems.ENERGY_TABLE.registerThis(),
                FinalTechItems.ENERGY_INPUT_TABLE.registerThis(),
                FinalTechItems.ENERGY_OUTPUT_TABLE.registerThis(),
                FinalTechItems.EQUIVALENT_EXCHANGE_TABLE.registerThis(),
                FinalTechItems.COPY_CARD_FACTORY.registerThis(),
                FinalTechItems.PHONY_FACTORY.registerThis()
        );
        FinalTechMenus.SUB_MENU_CORE_MACHINE.addTo(
                FinalTechItems.STORAGE_CARD_MERGE_TABLE.registerThis(),
                FinalTechItems.STORAGE_CARD_DISTRIBUTION_TABLE.registerThis(),
                FinalTechItems.ENTROPY_CRAFT_TABLE.registerThis(),
                FinalTechItems.ANNULAR_CRAFT_TABLE.registerThis(),
                FinalTechItems.SHELL_CRAFT_TABLE.registerThis(),
                FinalTechItems.COPY_CARD_DUPLICATION_TABLE.registerThis(),
                FinalTechItems.PHONY_CRAFT_TABLE.registerThis(),
                FinalTechItems.INFINITY_STORAGE_CARD_CRAFT_TABLE.registerThis()
        );
        FinalTechMenus.SUB_MENU_CORE_MACHINE.addTo(
                FinalTechItems.ITEM_DISMANTLE_TABLE.registerThis(),
                FinalTechItems.AUTO_ITEM_DISMANTLE_TABLE.registerThis(),
                FinalTechItems.COPY_CARD_DUPLICATOR.registerThis(),
                FinalTechItems.ADVANCED_AUTO_CRAFT_FRAME.registerThis(),
                FinalTechItems.ADVANCED_AUTO_CRAFT.registerThis(),
                FinalTechItems.MULTI_FRAME_MACHINE.registerThis(),
                FinalTechItems.CRAFT_STORAGE.registerThis(),
                FinalTechItems.ADVANCED_CRAFT_STORAGE.registerThis()
        );

        // special machines
        FinalTechMenus.SUB_MENU_SPECIAL_MACHINE.addTo(
                FinalTechItems.ITEM_FIXER.registerThis(),
                FinalTechItems.COBBLESTONE_FACTORY.registerThis(),
                FinalTechItems.HEALTH_CURER.registerThis(),
                FinalTechItems.EFFECT_CURER.registerThis()
        );
        FinalTechMenus.SUB_MENU_SPECIAL_MACHINE.addTo(
                FinalTechItems.FUEL_CHARGER.registerThis(),
                FinalTechItems.FUEL_ACCELERATOR.registerThis(),
                FinalTechItems.FUEL_OPERATOR.registerThis(),
                FinalTechItems.OPERATION_ACCELERATOR.registerThis(),
                FinalTechItems.ENERGIZED_OPERATION_ACCELERATOR.registerThis(),
                FinalTechItems.OVERLOADED_OPERATION_ACCELERATOR.registerThis()
        );
        // tower
        FinalTechMenus.SUB_MENU_TOWER.addTo(
                FinalTechItems.CURE_TOWER.registerThis(),
                FinalTechItems.PURIFY_LEVEL_TOWER.registerThis(),
                FinalTechItems.PURIFY_TIME_TOWER.registerThis()
        );

        /* productive machine */
        // manual machine
        FinalTechMenus.SUB_MENU_MANUAL_MACHINE.addTo(
                FinalTechItems.MANUAL_CRAFT_MACHINE,
                FinalTechItems.MANUAL_CRAFTING_TABLE.registerThis(),
                FinalTechItems.MANUAL_ENHANCED_CRAFTING_TABLE.registerThis(),
                FinalTechItems.MANUAL_GRIND_STONE.registerThis(),
                FinalTechItems.MANUAL_ARMOR_FORGE.registerThis(),
                FinalTechItems.MANUAL_ORE_CRUSHER.registerThis(),
                FinalTechItems.MANUAL_COMPRESSOR.registerThis(),
                FinalTechItems.MANUAL_SMELTERY.registerThis(),
                FinalTechItems.MANUAL_PRESSURE_CHAMBER.registerThis(),
                FinalTechItems.MANUAL_MAGIC_WORKBENCH.registerThis(),
                FinalTechItems.MANUAL_ORE_WASHER.registerThis(),
                FinalTechItems.MANUAL_COMPOSTER.registerThis(),
                FinalTechItems.MANUAL_GOLD_PAN.registerThis(),
                FinalTechItems.MANUAL_CRUCIBLE.registerThis(),
                FinalTechItems.MANUAL_JUICER.registerThis(),
                FinalTechItems.MANUAL_ANCIENT_ALTAR.registerThis(),
                FinalTechItems.MANUAL_HEATED_PRESSURE_CHAMBER.registerThis()
        );
        // basic machines
        FinalTechMenus.SUB_MENU_BASIC_MACHINE.addTo(
                FinalTechItems.BASIC_LOGIC_FACTORY.registerThis()
        );
        // advanced machine
        FinalTechMenus.SUB_MENU_ADVANCED_MACHINE.addTo(
                FinalTechItems.ADVANCED_COMPOSTER.registerThis(),
                FinalTechItems.ADVANCED_JUICER.registerThis(),
                FinalTechItems.ADVANCED_FURNACE.registerThis(),
                FinalTechItems.ADVANCED_GOLD_PAN.registerThis(),
                FinalTechItems.ADVANCED_DUST_WASHER.registerThis(),
                FinalTechItems.ADVANCED_INGOT_FACTORY.registerThis(),
                FinalTechItems.ADVANCED_CRUCIBLE.registerThis(),
                FinalTechItems.ADVANCED_ORE_GRINDER.registerThis(),
                FinalTechItems.ADVANCED_HEATED_PRESSURE_CHAMBER.registerThis(),
                FinalTechItems.ADVANCED_INGOT_PULVERIZER.registerThis(),
                FinalTechItems.ADVANCED_AUTO_DRIER.registerThis(),
                FinalTechItems.ADVANCED_PRESS.registerThis(),
                FinalTechItems.ADVANCED_FOOD_FACTORY.registerThis(),
                FinalTechItems.ADVANCED_FREEZER.registerThis(),
                FinalTechItems.ADVANCED_CARBON_PRESS.registerThis(),
                FinalTechItems.ADVANCED_SMELTERY.registerThis()
        );
        // conversion
        FinalTechMenus.SUB_MENU_CONVERSION.addTo(
                FinalTechItems.GRAVEL_CONVERSION.registerThis(),
                FinalTechItems.SOUL_SAND_CONVERSION.registerThis(),
                FinalTechItems.LOGIC_TO_DIGITAL_CONVERSION.registerThis()
        );
        // extraction
        FinalTechMenus.SUB_MENU_EXTRACTION.addTo(
                FinalTechItems.DIGITAL_EXTRACTION.registerThis()
        );
        // generator
        FinalTechMenus.SUB_MENU_GENERATOR.addTo(
                FinalTechItems.LIQUID_CARD_GENERATOR.registerThis(),
                FinalTechItems.LOGIC_GENERATOR.registerThis(),
                FinalTechItems.DIGITAL_GENERATOR.registerThis()
        );

        /* final stage item */
        FinalTechMenus.SUB_MENU_FINAL_ITEM.addTo(
                FinalTechItems.ENTROPY_SEED.registerThis(),
                FinalTechItems.MATRIX_MACHINE_CHARGE_CARD.registerThis(),
                FinalTechItems.MATRIX_MACHINE_ACCELERATE_CARD.registerThis(),
                FinalTechItems.MATRIX_MACHINE_ACTIVATE_CARD.registerThis(),
                FinalTechItems.MATRIX_OPERATION_ACCELERATE_CARD.registerThis(),
                FinalTechItems.MATRIX_QUANTITY_MODULE.registerThis(),
                FinalTechItems.MATRIX_OPERATION_ACCELERATOR.registerThis()
        );
        FinalTechMenus.SUB_MENU_FINAL_ITEM.addTo(
                FinalTechItems.ENTROPY_CONSTRUCTOR.registerThis(),
                FinalTechItems.MATRIX_EXPANDED_CAPACITOR.registerThis(),
                FinalTechItems.MATRIX_GENERATOR.registerThis(),
                FinalTechItems.MATRIX_ITEM_DISMANTLE_TABLE.registerThis(),
                FinalTechItems.MATRIX_STORAGE.registerThis(),
                FinalTechItems.MATRIX_ACCELERATOR.registerThis(),
                FinalTechItems.MATRIX_COPY_CARD_FACTORY.registerThis(),
                FinalTechItems.MATRIX_COPY_CARD_DUPLICATOR.registerThis(),
                FinalTechItems.MATRIX_REACTOR.registerThis()
        );
        FinalTechMenus.SUB_MENU_TROPHY.addTo(
                FinalTechItems.TROPHY_MEAWERFUL,
                FinalTechItems.TROPHY_SHIXINZIA
        );
        FinalTechMenus.SUB_MENU_COMING_SOON.addTo(
                FinalTechItems.CRAFT_STORAGE,
                FinalTechItems.ADVANCED_CRAFT_STORAGE
        );
        FinalTechMenus.SUB_MENU_DEPRECATED.addTo(
                FinalTechItems.ITEM_SERIALIZATION_CONSTRUCTOR.registerThis(),
                FinalTechItems.ITEM_DESERIALIZE_PARSER.registerThis(),
                FinalTechItems.MATRIX_ITEM_SERIALIZATION_CONSTRUCTOR.registerThis(),
                FinalTechItems.MATRIX_ITEM_DESERIALIZE_PARSER.registerThis(),
                FinalTechItems.DUST_FACTORY_DIRT.registerThis(),
                FinalTechItems.DUST_FACTORY_STONE.registerThis(),
                FinalTechItems.CARD_OPERATION_TABLE.registerThis()
        );
    }

    private static void setupMenu() {
        FinalTech finalTech = FinalTech.getInstance();

        FinalTechMenus.MAIN_MENU.setTier(0);

        FinalTechMenus.MENU_ITEMS.setTier(0);
        FinalTechMenus.MENU_ELECTRICITY_SYSTEM.setTier(0);
        FinalTechMenus.MENU_CARGO_SYSTEM.setTier(0);
        FinalTechMenus.MENU_FUNCTIONAL_MACHINE.setTier(0);
        FinalTechMenus.MENU_PRODUCTIVE_MACHINE.setTier(0);
        FinalTechMenus.MENU_DISC.setTier(0);

        for (SlimefunItem slimefunItem : FinalTechMenus.SUB_MENU_DEPRECATED.getSlimefunItems()) {
            try {
                Class<SlimefunItem> clazz = SlimefunItem.class;
                Field declaredField = clazz.getDeclaredField("blockTicker");
                declaredField.setAccessible(true);
                declaredField.set(slimefunItem, null);
                declaredField.setAccessible(false);

                Field ticking = clazz.getDeclaredField("ticking");
                ticking.setAccessible(true);
                ticking.set(slimefunItem, false);
                ticking.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        /* Menus */
        // item
        FinalTechMenus.MAIN_ITEM_GROUP.addTo(FinalTechMenus.MAIN_MENU_ITEM,
                FinalTechMenus.SUB_MENU_MATERIAL,
                FinalTechMenus.SUB_MENU_LOGIC_ITEM,
                FinalTechMenus.SUB_MENU_PROP,
                FinalTechMenus.SUB_MENU_CONSUMABLE,
                FinalTechMenus.SUB_MENU_TOOL
        );
        FinalTechMenus.MAIN_MENU_ITEM.addFrom(
                FinalTechMenus.SUB_MENU_MATERIAL,
                FinalTechMenus.SUB_MENU_LOGIC_ITEM,
                FinalTechMenus.SUB_MENU_PROP,
                FinalTechMenus.SUB_MENU_CONSUMABLE,
                FinalTechMenus.SUB_MENU_TOOL
        );
        // electricity system
        FinalTechMenus.MAIN_ITEM_GROUP.addTo(FinalTechMenus.MAIN_MENU_ELECTRICITY_SYSTEM,
                FinalTechMenus.SUB_MENU_ELECTRIC_GENERATOR,
                FinalTechMenus.SUB_MENU_ELECTRIC_STORAGE,
                FinalTechMenus.SUB_MENU_ELECTRIC_TRANSMISSION,
                FinalTechMenus.SUB_MENU_ELECTRIC_ACCELERATOR
        );
        FinalTechMenus.MAIN_MENU_ELECTRICITY_SYSTEM.addFrom(
                FinalTechMenus.SUB_MENU_ELECTRIC_GENERATOR,
                FinalTechMenus.SUB_MENU_ELECTRIC_STORAGE,
                FinalTechMenus.SUB_MENU_ELECTRIC_TRANSMISSION,
                FinalTechMenus.SUB_MENU_ELECTRIC_ACCELERATOR
        );
        // cargo system
        FinalTechMenus.MAIN_ITEM_GROUP.addTo(FinalTechMenus.MAIN_MENU_CARGO_SYSTEM,
                FinalTechMenus.SUB_MENU_STORAGE_UNIT,
                FinalTechMenus.SUB_MENU_ADVANCED_STORAGE,
                FinalTechMenus.SUB_MENU_ACCESSOR,
                FinalTechMenus.SUB_MENU_LOGIC,
                FinalTechMenus.SUB_MENU_CARGO
        );
        FinalTechMenus.MAIN_MENU_CARGO_SYSTEM.addFrom(
                FinalTechMenus.SUB_MENU_STORAGE_UNIT,
                FinalTechMenus.SUB_MENU_ADVANCED_STORAGE,
                FinalTechMenus.SUB_MENU_ACCESSOR,
                FinalTechMenus.SUB_MENU_LOGIC,
                FinalTechMenus.SUB_MENU_CARGO
        );
        // functional machine
        FinalTechMenus.MAIN_ITEM_GROUP.addTo(FinalTechMenus.MAIN_MENU_FUNCTIONAL_MACHINE,
                FinalTechMenus.SUB_MENU_CORE_MACHINE,
                FinalTechMenus.SUB_MENU_SPECIAL_MACHINE,
                FinalTechMenus.SUB_MENU_TOWER
        );
        FinalTechMenus.MAIN_MENU_FUNCTIONAL_MACHINE.addFrom(
                FinalTechMenus.SUB_MENU_CORE_MACHINE,
                FinalTechMenus.SUB_MENU_SPECIAL_MACHINE,
                FinalTechMenus.SUB_MENU_TOWER
        );
        // productive machine
        FinalTechMenus.MAIN_ITEM_GROUP.addTo(FinalTechMenus.MAIN_MENU_PRODUCTIVE_MACHINE,
                FinalTechMenus.SUB_MENU_MANUAL_MACHINE,
                FinalTechMenus.SUB_MENU_BASIC_MACHINE,
                FinalTechMenus.SUB_MENU_ADVANCED_MACHINE,
                FinalTechMenus.SUB_MENU_CONVERSION,
                FinalTechMenus.SUB_MENU_EXTRACTION,
                FinalTechMenus.SUB_MENU_GENERATOR
        );
        FinalTechMenus.MAIN_MENU_PRODUCTIVE_MACHINE.addFrom(
                FinalTechMenus.SUB_MENU_MANUAL_MACHINE,
                FinalTechMenus.SUB_MENU_BASIC_MACHINE,
                FinalTechMenus.SUB_MENU_ADVANCED_MACHINE,
                FinalTechMenus.SUB_MENU_CONVERSION,
                FinalTechMenus.SUB_MENU_EXTRACTION,
                FinalTechMenus.SUB_MENU_GENERATOR
        );
        // disc
        FinalTechMenus.MAIN_ITEM_GROUP.addTo(FinalTechMenus.MAIN_MENU_DISC,
                FinalTechMenus.SUB_MENU_FINAL_ITEM,
                FinalTechMenus.SUB_MENU_TROPHY,
                FinalTechMenus.SUB_MENU_DEPRECATED,
                FinalTechMenus.SUB_MENU_COMING_SOON
        );
        FinalTechMenus.MAIN_MENU_DISC.addFrom(
                FinalTechMenus.SUB_MENU_FINAL_ITEM,
                FinalTechMenus.SUB_MENU_TROPHY,
                FinalTechMenus.SUB_MENU_DEPRECATED,
                FinalTechMenus.SUB_MENU_COMING_SOON
        );

        FinalTechMenus.MAIN_ITEM_GROUP.setTier(0);
        FinalTechMenus.MAIN_ITEM_GROUP.register(finalTech);

        FinalTechMenus.START_MENU.add(FinalTechItems.RESEARCH_TABLE);
        FinalTechMenus.START_MENU.register(FinalTech.getInstance());
    }

    private static void setupResearch() {
        PluginManager pluginManager = FinalTech.getInstance().getServer().getPluginManager();
        pluginManager.registerEvents(new ResearchListener(), FinalTech.getInstance().getJavaPlugin());

        LanguageManager researchManager = FinalTech.getResearchManager();
        researchManager.getFunction().addAll(FinalTech.getLanguageManager().getFunction());

        List<String> defaultItemRawConsumeSuccessLore = researchManager.getStringList("text", "items", "consume-true", "success", "lore");
        List<String> defaultItemRawConsumeSuccessChat = researchManager.getStringList("text", "items", "consume-true", "success", "chat");
        List<String> defaultItemRawConsumeFailLore = researchManager.getStringList("text", "items", "consume-true", "fail", "lore");
        List<String> defaultItemRawConsumeFailChat = researchManager.getStringList("text", "items", "consume-true", "fail", "chat");
        List<String> defaultItemRawSuccessLore = researchManager.getStringList("text", "items", "consume-false", "success", "lore");
        List<String> defaultItemRawSuccessChat = researchManager.getStringList("text", "items", "consume-false", "success", "chat");
        List<String> defaultItemRawFailLore = researchManager.getStringList("text", "items", "consume-false", "fail", "lore");
        List<String> defaultItemRawFailChat = researchManager.getStringList("text", "items", "consume-false", "fail", "chat");

        List<String> defaultResearchSuccessLore = researchManager.getStringList("text", "researches", "success", "lore");
        List<String> defaultResearchSuccessChat = researchManager.getStringList("text", "researches", "success", "chat");
        List<String> defaultResearchFailLore = researchManager.getStringList("text", "researches", "fail", "lore");
        List<String> defaultResearchFailChat = researchManager.getStringList("text", "researches", "fail", "chat");

        List<String> defaultPermissionSuccessLore = researchManager.getStringList("text", "permissions", "success", "lore");
        List<String> defaultPermissionSuccessChat = researchManager.getStringList("text", "permissions", "success", "chat");
        List<String> defaultPermissionFailLore = researchManager.getStringList("text", "permissions", "fail", "lore");
        List<String> defaultPermissionFailChat = researchManager.getStringList("text", "permissions", "fail", "chat");

        List<Research> researchList = Slimefun.getRegistry().getResearches();
        Map<String, Research> researchIdMap = researchList.stream().collect(Collectors.toMap(research -> research.getKey().getKey(), research -> research));

        List<String> stringList = researchManager.getStringList("researches");
        while (!stringList.isEmpty()) {
            int size = stringList.size();
            try {
                while (!stringList.isEmpty()) {
                    String researchId = stringList.remove(0);
                    String intIdStr = researchManager.getString("researches", researchId, "int-id");
                    int intId;
                    if ("{1}".equals(intIdStr)) {
                        intId = researchId.hashCode();
                    } else {
                        intId = Integer.parseInt(intIdStr);
                    }
                    String name = researchManager.getString("researches", researchId, "name");
                    SimpleSpecialResearch research = new SimpleSpecialResearch(new NamespacedKey(FinalTech.getInstance(), researchId), intId, name, 0);

                    List<String> lore = researchManager.getStringList("researches", researchId, "lore");
                    research.getBaseLore().addAll(lore);

                    List<String> slimefunItemIdList = researchManager.getStringList("researches", researchId, "items");
                    if (slimefunItemIdList.isEmpty()) {
                        // TODO logger
                        continue;
                    }

                    List<SlimefunItem> slimefunItemList = new ArrayList<>(slimefunItemIdList.size());
                    for (String slimefunItemId : slimefunItemIdList) {
                        SlimefunItem slimefunItem = SlimefunItem.getById(slimefunItemId);
                        if (slimefunItem != null) {
                            slimefunItemList.add(slimefunItem);
                        }
                    }

                    if (researchManager.containPath("researches", researchId, "unlock-condition")) {
                        if (researchManager.containPath("researches", researchId, "unlock-condition", "items")) {
                            for (String index : researchManager.getStringList("researches", researchId, "unlock-condition", "items")) {
                                String itemId = researchManager.getString("researches", researchId, "unlock-condition", "items", index, "id");
                                int amount = researchManager.getOrDefault(1, "researches", researchId, "unlock-condition", "items", index, "amount");
                                boolean consume = researchManager.getOrDefault(true, "researches", researchId, "unlock-condition", "items", index, "consume");

                                List<String> successLoreStringList;
                                if (researchManager.containPath("researches", researchId, "unlock-condition", "items", index, "text", "success", "lore")) {
                                    successLoreStringList = researchManager.getStringList("researches", researchId, "unlock-condition", "items", index, "text", "success", "lore");
                                } else {
                                    successLoreStringList = consume ? defaultItemRawConsumeSuccessLore : defaultItemRawSuccessLore;
                                }

                                List<String> successChatStringList;
                                if (researchManager.containPath("researches", researchId, "unlock-condition", "items", index, "text", "success", "chat")) {
                                    successChatStringList = researchManager.getStringList("researches", researchId, "unlock-condition", "items", index, "text", "success", "chat");
                                } else {
                                    successChatStringList = consume ? defaultItemRawConsumeSuccessChat : defaultItemRawSuccessChat;
                                }

                                List<String> failLoreStringList;
                                if (researchManager.containPath("researches", researchId, "unlock-condition", "items", index, "text", "fail", "lore")) {
                                    failLoreStringList = researchManager.getStringList("researches", researchId, "unlock-condition", "items", index, "text", "fail", "lore");
                                } else {
                                    failLoreStringList = consume ? defaultItemRawConsumeFailLore : defaultItemRawFailLore;
                                }

                                List<String> failChatStringList;
                                if (researchManager.containPath("researches", researchId, "unlock-condition", "items", index, "text", "fail", "chat")) {
                                    failChatStringList = researchManager.getStringList("researches", researchId, "unlock-condition", "items", index, "text", "fail", "chat");
                                } else {
                                    failChatStringList = consume ? defaultItemRawConsumeFailChat : defaultItemRawFailChat;
                                }

                                ItemStack itemStack;
                                SlimefunItem slimefunItem = SlimefunItem.getById(itemId);
                                if (slimefunItem instanceof ExtraParameterItem extraParameterItem) {
                                    itemStack = extraParameterItem.getByExtraParameter(researchManager.getStringArray("researches", researchId, "unlock-condition", "items", index, "extra-args"));
                                } else if (slimefunItem instanceof SimpleValidItem simpleValidItem) {
                                    itemStack = simpleValidItem.getValidItem();
                                } else if (slimefunItem != null) {
                                    itemStack = slimefunItem.getItem();
                                } else {
                                    Material material = Material.valueOf(itemId);
                                    itemStack = new ItemStack(material);
                                }

                                if (itemStack == null) {
                                    throw new IllegalArgumentException("Can not generate item for " + researchId + " in index " + index);
                                }

                                ItemCondition itemCondition = new ItemCondition(itemStack, amount, consume);
                                itemCondition.getRawSuccessLore().addAll(successLoreStringList);
                                itemCondition.getRawSuccessChat().addAll(successChatStringList);
                                itemCondition.getRawFailLore().addAll(failLoreStringList);
                                itemCondition.getRawFailChat().addAll(failChatStringList);

                                research.addCondition(itemCondition);
                            }
                        }

                        if (researchManager.containPath("researches", researchId, "unlock-condition", "researches")) {
                            for (String index : researchManager.getStringList("researches", researchId, "unlock-condition", "researches")) {
                                String targetResearchId = researchManager.getString("researches", researchId, "unlock-condition", "researches", index, "id");
                                Research targetResearch = researchIdMap.get(targetResearchId);
                                if (targetResearch == null) {
                                    throw new NullPointerException("Can not find research with id: " + targetResearchId);
                                }

                                List<String> successLoreStringList;
                                if (researchManager.containPath("researches", researchId, "unlock-condition", "researches", index, "text", "success", "lore")) {
                                    successLoreStringList = researchManager.getStringList("researches", researchId, "unlock-condition", "researches", index, "text", "success", "lore");
                                } else {
                                    successLoreStringList = defaultResearchSuccessLore;
                                }

                                List<String> successChatStringList;
                                if (researchManager.containPath("researches", researchId, "unlock-condition", "researches", index, "text", "success", "chat")) {
                                    successChatStringList = researchManager.getStringList("researches", researchId, "unlock-condition", "researches", index, "text", "success", "chat");
                                } else {
                                    successChatStringList = defaultResearchSuccessChat;
                                }

                                List<String> failLoreStringList;
                                if (researchManager.containPath("researches", researchId, "unlock-condition", "researches", index, "text", "fail", "lore")) {
                                    failLoreStringList = researchManager.getStringList("researches", researchId, "unlock-condition", "researches", index, "text", "fail", "lore");
                                } else {
                                    failLoreStringList = defaultResearchFailLore;
                                }

                                List<String> failChatStringList;
                                if (researchManager.containPath("researches", researchId, "unlock-condition", "researches", index, "text", "fail", "chat")) {
                                    failChatStringList = researchManager.getStringList("researches", researchId, "unlock-condition", "researches", index, "text", "fail", "chat");
                                } else {
                                    failChatStringList = defaultResearchFailChat;
                                }

                                ResearchCondition researchCondition = new ResearchCondition(targetResearch);
                                researchCondition.getRawSuccessLore().addAll(successLoreStringList);
                                researchCondition.getRawSuccessChat().addAll(successChatStringList);
                                researchCondition.getRawFailChat().addAll(failLoreStringList);
                                researchCondition.getRawFailChat().addAll(failChatStringList);

                                research.addCondition(researchCondition);
                            }
                        }

                        if (researchManager.containPath("researches", researchId, "unlock-condition", "permissions")) {
                            for (String index : researchManager.getStringList("researches", researchId, "unlock-condition", "permissions")) {
                                String permissionCode = researchManager.getString("researches", researchId, "unlock-condition", "permissions", index, "id");

                                List<String> successLoreStringList;
                                if (researchManager.containPath("researches", researchId, "unlock-condition", "permissions", index, "text", "success", "lore")) {
                                    successLoreStringList = researchManager.getStringList("researches", researchId, "unlock-condition", "permissions", index, "text", "success", "lore");
                                } else {
                                    successLoreStringList = defaultPermissionSuccessLore;
                                }

                                List<String> successChatStringList;
                                if (researchManager.containPath("researches", researchId, "unlock-condition", "permissions", index, "text", "success", "chat")) {
                                    successChatStringList = researchManager.getStringList("researches", researchId, "unlock-condition", "permissions", index, "text", "success", "chat");
                                } else {
                                    successChatStringList = defaultPermissionSuccessChat;
                                }

                                List<String> failLoreStringList;
                                if (researchManager.containPath("researches", researchId, "unlock-condition", "permissions", index, "text", "fail", "lore")) {
                                    failLoreStringList = researchManager.getStringList("researches", researchId, "unlock-condition", "permissions", index, "text", "fail", "lore");
                                } else {
                                    failLoreStringList = defaultPermissionFailLore;
                                }

                                List<String> failChatStringList;
                                if (researchManager.containPath("researches", researchId, "unlock-condition", "permissions", index, "text", "fail", "chat")) {
                                    failChatStringList = researchManager.getStringList("researches", researchId, "unlock-condition", "permissions", index, "text", "fail", "chat");
                                } else {
                                    failChatStringList = defaultPermissionFailChat;
                                }

                                PermissionCondition permissionCondition = new PermissionCondition(permissionCode);
                                permissionCondition.getRawSuccessLore().addAll(successLoreStringList);
                                permissionCondition.getRawSuccessChat().addAll(successChatStringList);
                                permissionCondition.getRawFailChat().addAll(failLoreStringList);
                                permissionCondition.getRawFailChat().addAll(failChatStringList);

                                research.addCondition(permissionCondition);
                            }
                        }
                    }

                    research.register();
                    for (SlimefunItem slimefunItem : slimefunItemList) {
                        slimefunItem.setResearch(research);
                    }
                    researchIdMap.put(researchId, research);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (size == stringList.size()) {
                break;
            }
        }
    }

    private static void setupVisible() {
        ConfigFileManager visibleManager = FinalTech.getVisibleManager();
        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (slimefunItem instanceof AbstractMySlimefunItem abstractMySlimefunItem) {
                if (visibleManager.containPath(abstractMySlimefunItem.getId())) {
                    if (visibleManager.containPath(abstractMySlimefunItem.getId(), "items")) {
                        for (String itemId : visibleManager.getStringList(abstractMySlimefunItem.getId(), "items")) {
                            abstractMySlimefunItem.addVisibleFunction(player -> {
                                SlimefunItem sfItem = SlimefunItem.getById(itemId);
                                if (sfItem instanceof VisibleItem visibleItem) {
                                    return visibleItem.isVisible(player);
                                } else if (sfItem != null) {
                                    return !sfItem.isHidden();
                                } else {
                                    return true;
                                }
                            });
                        }
                    }
                    if (visibleManager.containPath(abstractMySlimefunItem.getId(), "researches")) {
                        for (String researchId : visibleManager.getStringList(abstractMySlimefunItem.getId(), "researches")) {
                            researchId = researchId.toLowerCase(Locale.ROOT);
                            Research targetResearch = null;
                            for (Research research : Slimefun.getRegistry().getResearches()) {
                                if (research.getKey().getKey().equals(researchId)) {
                                    targetResearch = research;
                                    break;
                                }
                            }
                            if (targetResearch != null) {
                                final Research finalResearch = targetResearch;
                                abstractMySlimefunItem.addVisibleFunction(player -> {
                                    Optional<PlayerProfile> optionalPlayerProfile = PlayerProfile.find(player);
                                    return optionalPlayerProfile.map(playerProfile -> playerProfile.hasUnlocked(finalResearch)).orElse(true);
                                });
                            }
                        }
                    }
                    if (visibleManager.containPath(abstractMySlimefunItem.getId(), "permissions")) {
                        for (String permissionCode : visibleManager.getStringList(abstractMySlimefunItem.getId(), "permissions")) {
                            abstractMySlimefunItem.addVisibleFunction(player -> player.hasPermission(permissionCode));
                        }
                    }
                }
            }
        }
    }

    private static void setupCommand() {
        FinalTech finalTech = FinalTech.getInstance();

        finalTech.getCommand("finaltech-copy-card").setExecutor(new TransformToCopyCardItem());
        finalTech.getCommand("finaltech-storage-card").setExecutor(new TransformToStorageItem());
        finalTech.getCommand("finaltech-info").setExecutor(new ShowItemInfo());
        finalTech.getCommand("finaltech-valid-item").setExecutor(new TransformToValidItem());
        finalTech.getCommand("finaltech-show-task-size").setExecutor(new ShowTaskSize());
    }

    public static void init() {
        ConfigFileManager configManager = FinalTech.getConfigManager();

        if(configManager.getOrDefault(true, "enable", "item")) {
            // Yeah, you may not want new items from this plugin.
            setupEnchantment();
            setupItem();
            setupMenu();
            setupResearch();
            setupVisible();
        }

        setupCommand();
    }

    public static void registerBlockTicker() {
        SetupUtil.registerBlockTicker(0);
    }

    private static void registerBlockTicker(int begin) {
        try {
            BiConsumer<Runnable, LocationData> defaultTicker = (runnable, locationData) -> FinalTech.getLocationRunnableFactory().waitThenRun(() -> {
                long t1 = Slimefun.getProfiler().newEntry();
                if (locationData.getLocation().getChunk().isLoaded()) {
                    // It may be run in next tick.
                    runnable.run();
                }
                Slimefun.getProfiler().closeEntry(locationData.getLocation(), LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), locationData), t1);
            }, locationData.getLocation());

            ConfigFileManager configManager = FinalTech.getConfigManager();
            List<SlimefunItem> slimefunItemList = Slimefun.getRegistry().getAllSlimefunItems();
            for (int size = slimefunItemList.size(); begin < size; begin++) {
                SlimefunItem slimefunItem = slimefunItemList.get(begin);
                if (slimefunItem.getBlockTicker() != null) {
                    SlimefunAddon slimefunAddon = slimefunItem.getAddon();
                    BlockTicker blockTicker = slimefunItem.getBlockTicker();

                    BiConsumer<Runnable, LocationData> biConsumer = BlockTickerService.DEFAULT_TICKER;
                    List<Function<LocationData, Boolean>> functionList = new ArrayList<>();
                    List<Consumer<LocationData>> consumerList = new ArrayList<>();
                    List<Runnable> runnableList = new ArrayList<>();

                    if (FinalTech.isAntiAccelerateSlimefunItem(slimefunItem.getId()) || FinalTech.getAntiAccelerateSlimefunPluginSet().contains(slimefunAddon.getName())) {
                        functionList.add(BlockTickerUtil.functionAntiAcceleration(FinalTech.getLocationDataService()));
                        FinalTech.logger().info(slimefunItem.getId() + "(" + ChatColor.stripColor(slimefunItem.getItemName()) + ")" + " is tweaked for anti accelerate");
                    }

                    if (FinalTech.isPerformanceLimitSlimefunItem(slimefunItem.getId()) || FinalTech.getPerformanceLimitSlimefunPluginSet().contains(slimefunAddon.getName())) {
                        functionList.add(BlockTickerUtil.functionPerformanceLimit(FinalTech.getLocationDataService()));
                        FinalTech.logger().info(slimefunItem.getId() + "(" + ChatColor.stripColor(slimefunItem.getItemName()) + ")" + " is tweaked for performance limit");
                    }

                    if (configManager.containPath("tweak", "interval", "general", slimefunItem.getId())) {
                        int interval = configManager.getOrDefault(-1, "tweak", "interval", "general", slimefunItem.getId());
                        if (interval > 0) {
                            functionList.add(BlockTickerUtil.functionIndependentIntervalBefore(FinalTech.getLocationDataService()));
                            consumerList.add(BlockTickerUtil.functionIndependentIntervalAfter(FinalTech.getLocationDataService(), interval));
                            FinalTech.logger().info(slimefunItem.getId() + "(" + ChatColor.stripColor(slimefunItem.getItemName()) + ")" + " is tweaked for general interval limit");
                        } else {
                            FinalTech.logger().warning("wrong value of tweak.interval.general." + slimefunItem.getId() + " in config file");
                        }
                    }

                    if (configManager.containPath("tweak", "interval", "independent", slimefunItem.getId())) {
                        int interval = configManager.getOrDefault(-1, "tweak", "interval", "independent", slimefunItem.getId());
                        if (interval > 1) {
                            functionList.add(BlockTickerUtil.functionGeneralInterval(interval));
                            FinalTech.logger().info(slimefunItem.getId() + "(" + ChatColor.stripColor(slimefunItem.getItemName()) + ")" + " is tweaked for independent interval limit");
                        } else {
                            FinalTech.logger().warning("wrong value of tweak.interval.independent." + slimefunItem.getId() + " in config file");
                        }
                    }

                    if(configManager.containPath("tweak", "range-limit", slimefunItem.getId(), "range")) {
                        int range = configManager.getOrDefault(-1, "tweak", "range-limit", slimefunItem.getId(), "range");
                        if (range > 0) {
                            int mulRange = configManager.getOrDefault(0, "tweak", "range-limit", slimefunItem.getId(), "mul-range");
                            boolean dropSelf = configManager.getOrDefault(false, "tweak", "range-limit", slimefunItem.getId(), "drop-self");
                            String message = configManager.getOrDefault("{1} is not allowed to be placed too closely", "tweak", "range-limit", slimefunItem.getId(), "message");

                            BlockTickerUtil.RangeLimitHandler rangeLimitHandler = new BlockTickerUtil.RangeLimitHandler(range, mulRange, dropSelf, message);
                            functionList.add(BlockTickerUtil.functionRangeLimitBefore(FinalTech.getLocationDataService(), rangeLimitHandler));
                            consumerList.add(BlockTickerUtil.functionRangeLimitAfter(rangeLimitHandler));
                            runnableList.add(BlockTickerUtil.functionRangeLimitUnique(rangeLimitHandler));
                            FinalTech.logger().info(slimefunItem.getId() + "(" + ChatColor.stripColor(slimefunItem.getItemName()) + ")" + " is tweaked for range limit");
                        } else {
                            FinalTech.logger().warning("wrong value of tweak.range." + slimefunItem.getId() + " in config file");
                        }
                    }

                    if(configManager.containPath("tweak", "live-time", slimefunItem.getId(), "time")) {
                        int time = configManager.getOrDefault(-1, "tweak", "live-time", slimefunItem.getId(), "time");
                        if (time > 0) {
                            boolean dropSelf = configManager.getOrDefault(false, "tweak", "live-time", slimefunItem.getId(), "drop-self");
                            List<Integer> dropSlots = configManager.getOrDefault(new ArrayList<>(), "tweak", "live-time", slimefunItem.getId(), "drop-slots");
                            consumerList.add(BlockTickerUtil.functionLiveTimeAfter(FinalTech.getLocationDataService(), slimefunItem.getId(), time, dropSelf, JavaUtil.toArray(dropSlots)));
                            FinalTech.logger().info(slimefunItem.getId() + "(" + ChatColor.stripColor(slimefunItem.getItemName()) + ")" + " is tweaked for living time");
                        }
                    }

                    boolean forceAsync = !blockTicker.isSynchronized()
                            && (FinalTech.getForceSlimefunMultiThread()
                                || FinalTech.isAsyncSlimefunItem(slimefunItem.getId())
                                || FinalTech.getAsyncSlimefunPluginSet().contains(slimefunAddon.getName()));

                    if (forceAsync) {
                        biConsumer = defaultTicker;
                    }

                    if (configManager.getOrDefault(false, "super-ban") && slimefunItem.isDisabled()) {
                        blockTicker = null;
                        FinalTech.logger().info(slimefunItem.getId() + "(" + ChatColor.stripColor(slimefunItem.getItemName()) + ")" + " is tweaked to remove block ticker");
                    } else if (FinalTech.isNoBlockTickerSlimefunItem(slimefunItem.getId()) || FinalTech.getNoBlockTickerSlimefunPluginSet().contains(slimefunAddon.getJavaPlugin().getName())){
                        blockTicker = null;
                        FinalTech.logger().info(slimefunItem.getId() + "(" + ChatColor.stripColor(slimefunItem.getItemName()) + ")" + " is tweaked to remove block ticker");
                    } else {
                        Function<LocationData, Boolean>[] functions = new Function[functionList.size()];
                        Consumer<LocationData>[] consumers = new Consumer[consumerList.size()];
                        Runnable[] runnables = new Runnable[runnableList.size()];

                        for(int i = 0; i < functions.length; i++) {
                            functions[i] = functionList.get(i);
                        }

                        for(int i = 0; i < consumers.length; i++) {
                            consumers[i] = consumerList.get(i);
                        }

                        for(int i = 0; i < runnables.length; i++) {
                            runnables[i] = runnableList.get(i);
                        }

                        if(functions.length != 0 || consumers.length != 0 || biConsumer != BlockTickerService.DEFAULT_TICKER) {
                            blockTicker = FinalTech.getBlockTickerService().warp(blockTicker, biConsumer, functions, consumers, runnables);
                        }
                    }

                    if (slimefunItem.getBlockTicker() != blockTicker) {
                        Class<SlimefunItem> clazz = SlimefunItem.class;
                        Field declaredField = clazz.getDeclaredField("blockTicker");
                        declaredField.setAccessible(true);
                        declaredField.set(slimefunItem, blockTicker);
                        declaredField.setAccessible(false);
                        if (blockTicker == null) {
                            Field ticking = clazz.getDeclaredField("ticking");
                            ticking.setAccessible(true);
                            ticking.set(slimefunItem, false);
                            ticking.setAccessible(false);
                            Slimefun.getRegistry().getTickerBlocks().remove(slimefunItem.getId());
                        }
                        if (forceAsync) {
                            FinalTech.logger().info(slimefunItem.getId() + "(" + ChatColor.stripColor(slimefunItem.getItemName()) + ")" + " is tweaked for multi-thread！");
                            FinalTech.addAsyncSlimefunItem(slimefunItem.getId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            SetupUtil.registerBlockTicker(++begin);
        }
    }

    public static void dataLossFix() {
        if(!(FinalTech.getLocationDataService() instanceof BlockStorageDataService blockStorageDataService)) {
            // This fix is only for block storage.
            // Others should not need this.
            return;
        }
        for(World world : FinalTech.getInstance().getServer().getWorlds()) {
            BlockStorage storage = BlockStorage.getStorage(world);
            if(storage != null) {
                try {
                    Map<Location, BlockMenu> inventories = ReflectionUtil.getProperty(storage, BlockStorage.class, "inventories");
                    if(inventories != null) {
                        int count = 0;
                        FinalTech.logger().info("Data Loss Fix: start work for world: " + world.getName());
                        for(Map.Entry<Location, BlockMenu> entry : inventories.entrySet()) {
                            Location location = entry.getKey();
                            if(location.getBlock().getType().isAir()) {
                                continue;
                            }
                            LocationData locationData = blockStorageDataService.getLocationData(location);
                            if(locationData == null) {
                                String id = entry.getValue().getPreset().getID();
                                SlimefunItem slimefunItem = SlimefunItem.getById(id);
                                if(slimefunItem != null && !(slimefunItem instanceof AbstractMachine) && slimefunItem.getItem().getType().equals(location.getBlock().getType())) {
                                    FinalTech.logger().warning("Data Loss Fix: location " + location + " seems loss its data. There should be " + id + " (" + ChatColor.stripColor(slimefunItem.getItemName()) + ")");
                                    Map<String, String> configMap = FinalTech.getDataLossFixCustomMap(id);
                                    if(configMap == null) {
                                        FinalTech.logger().warning("Data Loss Fix: I don't know how to fix it. Config me in config.yml with path: " + "data-loss-fix-custom" + "." + "config" + "." + id);
                                        continue;
                                    }

                                    BlockStorage.addBlockInfo(location, ConstantTableUtil.CONFIG_ID, id);
                                    for(Map.Entry<String, String> configEntry : configMap.entrySet()) {
                                        BlockStorage.addBlockInfo(location, configEntry.getKey(), configEntry.getValue());
                                    }
                                    FinalTech.logger().info("Data Loss Fix: added location info to location: " + location);
                                    count++;
                                }
                            }
                        }
                        if(count > 0) {
                            FinalTech.logger().info("Data Loss Fix: totally " + count + " block" + (count == 1 ? " is" : "s are") + " fixed");
                        } else {
                            FinalTech.logger().info("Data Loss Fix: nothing changed! This is the best situation!");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
