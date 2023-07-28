package io.taraxacum.finaltech.setup;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.util.ConfigUtil;
import org.bukkit.Material;

/**
 * @author Final_ROOT
 */
public final class FinalTechItemStacks {
    /* item */
    // material
    public static final SlimefunItemStack GEARWHEEL = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_GEARWHEEL", Material.REDSTONE_TORCH, "Gear Wheel");
    public static final SlimefunItemStack BIT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_BIT", Material.IRON_NUGGET, "Bit");
    public static final SlimefunItemStack META = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_META", Material.GOLD_NUGGET, "Meta");
    public static final SlimefunItemStack UNORDERED_DUST = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_UNORDERED_DUST", Material.WHEAT_SEEDS, "Unordered Dust");
    public static final SlimefunItemStack ORDERED_DUST = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ORDERED_DUST", Material.SLIME_BALL, "Ordered Dust");
    public static final SlimefunItemStack ETHER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ETHER", Material.BLACK_DYE, "Ether");
    public static final SlimefunItemStack BUG = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_BUG", Material.BONE_MEAL, "Bug");
    public static final SlimefunItemStack ENTROPY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENTROPY", Material.COAL, "Entropy");

    public static final SlimefunItemStack COPY_CARD = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_COPY_CARD", Material.FLINT, "Copy Card");
    public static final SlimefunItemStack ANNULAR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ANNULAR", Material.BOWL, "Annular");
    public static final SlimefunItemStack SINGULARITY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_SINGULARITY", Material.NETHER_STAR, "Singularity");
    public static final SlimefunItemStack SPIROCHETE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_SPIROCHETE", Material.STRING, "Spirochete");
    public static final SlimefunItemStack SHELL = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_SHELL", Material.NETHERITE_SCRAP, "Shell");
    public static final SlimefunItemStack PHONY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_PHONY", Material.END_CRYSTAL, "Phony");
    public static final SlimefunItemStack JUSTIFIABILITY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_JUSTIFIABILITY", Material.IRON_NUGGET, "Justifiability");
    public static final SlimefunItemStack EQUIVALENT_CONCEPT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_EQUIVALENT_CONCEPT", Material.BLACK_CONCRETE, "Equivalent Concept");

    public static final SlimefunItemStack WATER_CARD = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_WATER_CARD", Material.PAPER, "Water Card");
    public static final SlimefunItemStack LAVA_CARD = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(),"FINALTECH_LAVA_CARD", Material.PAPER, "Lava Card");
    public static final SlimefunItemStack MILK_CARD = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(),"FINALTECH_MILK_CARD", Material.PAPER, "Milk Card");
    public static final SlimefunItemStack FLINT_AND_STEEL_CARD = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_FLINT_AND_STEEL_CARD", Material.PAPER, "Flint And Steel Card");

    public static final SlimefunItemStack QUANTITY_MODULE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_QUANTITY_MODULE", Material.AMETHYST_SHARD, "Quantity Module");
    public static final SlimefunItemStack ENERGIZED_QUANTITY_MODULE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGIZED_QUANTITY_MODULE", Material.AMETHYST_SHARD, "Energized Quantity Module");
    public static final SlimefunItemStack OVERLOADED_QUANTITY_MODULE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_OVERLOADED_QUANTITY_MODULE", Material.AMETHYST_SHARD, "Overloaded Quantity Module");

    // logic component
    public static final SlimefunItemStack LOGIC_FALSE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LOGIC_FALSE", Material.STICK, "LOGIC FALSE");
    public static final SlimefunItemStack LOGIC_TRUE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LOGIC_TRUE", Material.STICK, "LOGIC TRUE");

    public static final SlimefunItemStack DIGITAL_ZERO = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_ZERO", Material.BLAZE_ROD, "DIGITAL 0");
    public static final SlimefunItemStack DIGITAL_ONE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_ONE", Material.BLAZE_ROD, "DIGITAL 1");
    public static final SlimefunItemStack DIGITAL_TWO = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_TWO", Material.BLAZE_ROD, "DIGITAL 2");
    public static final SlimefunItemStack DIGITAL_THREE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_THREE", Material.BLAZE_ROD, "DIGITAL 3");
    public static final SlimefunItemStack DIGITAL_FOUR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_FOUR", Material.BLAZE_ROD, "DIGITAL 4");
    public static final SlimefunItemStack DIGITAL_FIVE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_FIVE", Material.BLAZE_ROD, "DIGITAL 5");
    public static final SlimefunItemStack DIGITAL_SIX = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_SIX", Material.BLAZE_ROD, "DIGITAL 6");
    public static final SlimefunItemStack DIGITAL_SEVEN = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_SEVEN", Material.BLAZE_ROD, "DIGITAL 7");
    public static final SlimefunItemStack DIGITAL_EIGHT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_EIGHT", Material.BLAZE_ROD, "DIGITAL 8");
    public static final SlimefunItemStack DIGITAL_NINE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_NINE", Material.BLAZE_ROD, "DIGITAL 9");
    public static final SlimefunItemStack DIGITAL_TEN = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_TEN", Material.BLAZE_ROD, "DIGITAL 10");
    public static final SlimefunItemStack DIGITAL_ELEVEN = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_ELEVEN", Material.BLAZE_ROD, "DIGITAL 11");
    public static final SlimefunItemStack DIGITAL_TWELVE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_TWELVE", Material.BLAZE_ROD, "DIGITAL 12");
    public static final SlimefunItemStack DIGITAL_THIRTEEN = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_THIRTEEN", Material.BLAZE_ROD, "DIGITAL 13");
    public static final SlimefunItemStack DIGITAL_FOURTEEN = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_FOURTEEN", Material.BLAZE_ROD, "DIGITAL 14");
    public static final SlimefunItemStack DIGITAL_FIFTEEN = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_FIFTEEN", Material.BLAZE_ROD, "DIGITAL 15");

    // prop
    public static final SlimefunItemStack MENU_VIEWER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MENU_VIEWER", Material.SPYGLASS, "Menu Viewer");
    public static final SlimefunItemStack ROUTE_VIEWER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ROUTE_VIEWER", Material.ENDER_EYE, "Route Viewer");
    public static final SlimefunItemStack LOCATION_RECORDER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LOCATION_RECORDER", Material.COMPASS, "Location Recorder");
    public static final SlimefunItemStack MACHINE_CONFIGURATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MACHINE_CONFIGURATOR", Material.CLOCK, "Machine Configurator");
    public static final SlimefunItemStack PORTABLE_ENERGY_STORAGE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_PORTABLE_ENERGY_STORAGE", Material.FISHING_ROD, "Portable Energy Storage");

    public static final SlimefunItemStack POTION_EFFECT_COMPRESSOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_POTION_EFFECT_COMPRESSOR", Material.GOLD_INGOT, "Potion Effect Compressor");
    public static final SlimefunItemStack POTION_EFFECT_DILATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_POTION_EFFECT_DILATOR", Material.COPPER_INGOT, "Potion Effect Dilator");
    public static final SlimefunItemStack POTION_EFFECT_PURIFIER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_POTION_EFFECT_PURIFIER", Material.IRON_INGOT, "Potion Effect Purifier");
    public static final SlimefunItemStack GRAVITY_REVERSAL_RUNE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_GRAVITY_REVERSAL_RUNE", Material.FEATHER, "Gravity Reversal Rune");
    public static final SlimefunItemStack STAFF_ELEMENTAL_LINE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_STAFF_ELEMENTAL_LINE", Material.STICK, "&6Elemental Staff &7- &f&oLine");
    public static final SlimefunItemStack STEPPING_STONE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_STEPPING_STONE", Material.GLASS, "Stepping Stone");
    public static final SlimefunItemStack STEPPING_STONE_PLACER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_STEPPING_STONE_PLACER", Material.ICE, "Stepping Stone Placer");
    public static final SlimefunItemStack AUTO_FLASH_BACK_POCKET_WATCH = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_AUTO_FLASH_BACK_POCKET_WATCH", Material.LANTERN, "Auto Flash Back Pocket Watch");

    // consumable
    public static final SlimefunItemStack ENERGY_CARD_K = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGY_CARD_K", Material.PAPER, "Energy Card - 1K");
    public static final SlimefunItemStack ENERGY_CARD_M = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGY_CARD_M", Material.PAPER, "Energy Card - 1M");
    public static final SlimefunItemStack ENERGY_CARD_B = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGY_CARD_B", Material.PAPER, "Energy Card - 1B");
    public static final SlimefunItemStack ENERGY_CARD_T = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGY_CARD_T", Material.PAPER, "Energy Card - 1T");
    public static final SlimefunItemStack ENERGY_CARD_Q = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGY_CARD_Q", Material.PAPER, "Energy Card - 1Q");
    public static final SlimefunItemStack ENERGIZED_OPERATION_ACCELERATE_CARD = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGIZED_OPERATION_ACCELERATE_CARD", Material.NETHERITE_INGOT, "Energized Operation Accelerate Card");
    public static final SlimefunItemStack OVERLOADED_OPERATION_ACCELERATE_CARD = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_OVERLOADED_OPERATION_ACCELERATE_CARD", Material.NETHERITE_INGOT, "Overloaded Operation Accelerate Card");

    public static final SlimefunItemStack MACHINE_CHARGE_CARD_L1 = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MACHINE_CHARGE_CARD_L1", Material.BRICK, "Charge Card L1");
    public static final SlimefunItemStack MACHINE_CHARGE_CARD_L2 = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MACHINE_CHARGE_CARD_L2", Material.BRICK, "Charge Card L2");
    public static final SlimefunItemStack MACHINE_CHARGE_CARD_L3 = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MACHINE_CHARGE_CARD_L3", Material.BRICK, "Charge Card L3");
    public static final SlimefunItemStack MACHINE_ACCELERATE_CARD_L1 = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MACHINE_ACCELERATE_CARD_L1", Material.NETHER_BRICK, "Accelerate Card L1");
    public static final SlimefunItemStack MACHINE_ACCELERATE_CARD_L2 = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MACHINE_ACCELERATE_CARD_L2", Material.NETHER_BRICK, "Accelerate Card L2");
    public static final SlimefunItemStack MACHINE_ACCELERATE_CARD_L3 = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MACHINE_ACCELERATE_CARD_L3", Material.NETHER_BRICK, "Accelerate Card L3");
    public static final SlimefunItemStack MACHINE_ACTIVATE_CARD_L1 = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MACHINE_ACTIVATE_CARD_L1", Material.COPPER_INGOT, "Activate Card L1");
    public static final SlimefunItemStack MACHINE_ACTIVATE_CARD_L2 = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MACHINE_ACTIVATE_CARD_L2", Material.COPPER_INGOT, "Activate Card L2");
    public static final SlimefunItemStack MACHINE_ACTIVATE_CARD_L3 = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MACHINE_ACTIVATE_CARD_L3", Material.COPPER_INGOT, "Activate Card L3");

    public static final SlimefunItemStack MAGIC_HYPNOTIC = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MAGIC_HYPNOTIC", Material.DRAGON_BREATH, "Magic Hypnotic");
    public static final SlimefunItemStack RESEARCH_UNLOCK_TICKET = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_RESEARCH_UNLOCK_TICKET", Material.ENCHANTED_BOOK, "Research Unlock Ticket");

    // tool
    public static final SlimefunItemStack SUPER_SHOVEL = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_SUPER_SHOVEL", Material.IRON_SHOVEL, "Super Shovel");
    public static final SlimefunItemStack ULTIMATE_SHOVEL = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ULTIMATE_SHOVEL", Material.DIAMOND_SHOVEL, "Ultimate Shovel");
    public static final SlimefunItemStack SUPER_PICKAXE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_SUPER_PICKAXE", Material.IRON_PICKAXE, "Super Pickaxe");
    public static final SlimefunItemStack ULTIMATE_PICKAXE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ULTIMATE_PICKAXE", Material.DIAMOND_PICKAXE, "Ultimate Pickaxe");
    public static final SlimefunItemStack SUPER_AXE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_SUPER_AXE", Material.IRON_AXE, "Super Axe");
    public static final SlimefunItemStack ULTIMATE_AXE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ULTIMATE_AXE", Material.DIAMOND_AXE, "Ultimate Axe");
    public static final SlimefunItemStack SUPER_HOE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_SUPER_HOE", Material.IRON_HOE, "Super Hoe");
    public static final SlimefunItemStack ULTIMATE_HOE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ULTIMATE_HOE", Material.DIAMOND_HOE, "Ultimate Hoe");

    /* electricity system */
    // electric generator
    public static final SlimefunItemStack BASIC_GENERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_BASIC_GENERATOR", Material.GLOWSTONE, "Basic Generator");
    public static final SlimefunItemStack ADVANCED_GENERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_GENERATOR", Material.GLOWSTONE, "Advanced Generator");
    public static final SlimefunItemStack CARBONADO_GENERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_CARBONADO_GENERATOR", Material.GLOWSTONE, "Carbonado Generator");
    public static final SlimefunItemStack ENERGIZED_GENERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGIZED_GENERATOR", Material.GLOWSTONE, "Energized Generator");
    public static final SlimefunItemStack ENERGIZED_STACK_GENERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGIZED_STACK_GENERATOR", Material.GLOWSTONE, "Energized Stack Generator");
    public static final SlimefunItemStack OVERLOADED_GENERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_OVERLOADED_GENERATOR", Material.GLOWSTONE, "Overloaded Generator");

    public static final SlimefunItemStack DUST_GENERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DUST_GENERATOR", Material.BROWN_MUSHROOM_BLOCK, "Dust Generator");
    public static final SlimefunItemStack TIME_GENERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_TIME_GENERATOR", Material.MUSHROOM_STEM, "Time Generator");
    public static final SlimefunItemStack ENERGIZED_CHARGE_BASE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGIZED_CHARGE_BASE", Material.CRIMSON_FENCE, "Energized Charge Base");
    public static final SlimefunItemStack OVERLOADED_CHARGE_BASE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_OVERLOADED_CHARGE_BASE", Material.WARPED_FENCE, "Overloaded Charge Base");

    // electric storage
    public static final SlimefunItemStack SMALL_EXPANDED_CAPACITOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_SMALL_EXPANDED_CAPACITOR", Material.YELLOW_STAINED_GLASS, "Small Expanded Capacitor");
    public static final SlimefunItemStack MEDIUM_EXPANDED_CAPACITOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MEDIUM_EXPANDED_CAPACITOR", Material.YELLOW_STAINED_GLASS, "Medium Expanded Capacitor");
    public static final SlimefunItemStack BIG_EXPANDED_CAPACITOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_BIG_EXPANDED_CAPACITOR", Material.YELLOW_STAINED_GLASS, "Big Expanded Capacitor");
    public static final SlimefunItemStack LARGE_EXPANDED_CAPACITOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LARGE_EXPANDED_CAPACITOR", Material.YELLOW_STAINED_GLASS, "Large Expanded Capacitor");
    public static final SlimefunItemStack CARBONADO_EXPANDED_CAPACITOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_CARBONADO_EXPANDED_CAPACITOR", Material.YELLOW_STAINED_GLASS, "Carbonado Expanded Capacitor");
    public static final SlimefunItemStack ENERGIZED_EXPANDED_CAPACITOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGIZED_EXPANDED_CAPACITOR", Material.YELLOW_STAINED_GLASS, "Energized Expanded Capacitor");

    public static final SlimefunItemStack ENERGIZED_STACK_EXPANDED_CAPACITOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGIZED_STACK_EXPANDED_CAPACITOR", Material.YELLOW_STAINED_GLASS, "Energized Stack Expanded Capacitor");
    public static final SlimefunItemStack OVERLOADED_EXPANDED_CAPACITOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_OVERLOADED_EXPANDED_CAPACITOR", Material.YELLOW_STAINED_GLASS, "Overloaded Expanded Capacitor");
    public static final SlimefunItemStack TIME_CAPACITOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_TIME_CAPACITOR", Material.BLUE_STAINED_GLASS, "Time Capacitor");

    // electric transmission
    public static final SlimefunItemStack NORMAL_ELECTRICITY_SHOOT_PILE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_NORMAL_ELECTRICITY_SHOOT_PILE", Material.DISPENSER, "Normal Electricity Shoot Pile");
    public static final SlimefunItemStack NORMAL_TRIGGERABLE_ELECTRICITY_SHOOT_PILE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_NORMAL_TRIGGERABLE_ELECTRICITY_SHOOT_PILE", Material.DISPENSER, "Normal Electricity Shoot Pile - Triggerable");
    public static final SlimefunItemStack NORMAL_CONTROLLABLE_ELECTRICITY_SHOOT_PILE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_NORMAL_CONTROLLABLE_ELECTRICITY_SHOOT_PILE", Material.DISPENSER, "Normal Electricity Shoot Pile - Controller");
    public static final SlimefunItemStack NORMAL_CONFIGURABLE_ELECTRICITY_SHOOT_PILE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_NORMAL_CONFIGURABLE_ELECTRICITY_SHOOT_PILE", Material.DISPENSER, "Normal Electricity Shoot Pile - Configurable");
    public static final SlimefunItemStack ENERGIZED_ELECTRICITY_SHOOT_PILE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGIZED_ELECTRICITY_SHOOT_PILE", Material.DISPENSER, "Energized Electricity Shoot Pile");
    public static final SlimefunItemStack OVERLOADED_ELECTRICITY_SHOOT_PILE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_OVERLOADED_ELECTRICITY_SHOOT_PILE", Material.DISPENSER, "Overloaded Electricity Shoot Pile");
    public static final SlimefunItemStack VARIABLE_WIRE_RESISTANCE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_VARIABLE_WIRE_RESISTANCE", Material.RED_STAINED_GLASS, "Variable Wire（Resistance）");
    public static final SlimefunItemStack VARIABLE_WIRE_CAPACITOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_VARIABLE_WIRE_CAPACITOR", Material.GREEN_STAINED_GLASS, "Variable Wire（Capacitor）");

    // electric accelerator
    public static final SlimefunItemStack ENERGIZED_ACCELERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGIZED_ACCELERATOR", Material.TARGET, "Energized Accelerator");
    public static final SlimefunItemStack OVERLOADED_ACCELERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_OVERLOADED_ACCELERATOR", Material.TARGET, "Overloaded Accelerator");

    /* cargo system */
    // storage unit
    public static final SlimefunItemStack NORMAL_STORAGE_UNIT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_NORMAL_STORAGE_UNIT", Material.GLASS, "Storage Unit - Normal");
    public static final SlimefunItemStack DIVIDED_STORAGE_UNIT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIVIDED_STORAGE_UNIT", Material.GLASS, "Storage Unit - Divided");
    public static final SlimefunItemStack LIMITED_STORAGE_UNIT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LIMITED_STORAGE_UNIT", Material.GLASS, "Storage Unit - Limited");
    public static final SlimefunItemStack STACK_STORAGE_UNIT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_STACK_STORAGE_UNIT", Material.GLASS, "Storage Unit - Stack");
    public static final SlimefunItemStack DIVIDED_LIMITED_STORAGE_UNIT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIVIDED_LIMITED_STORAGE_UNIT", Material.GLASS, "Storage Unit - Divided | Limited");
    public static final SlimefunItemStack DIVIDED_STACK_STORAGE_UNIT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIVIDED_STACK_STORAGE_UNIT", Material.GLASS, "Storage Unit - Divided | Stack");
    public static final SlimefunItemStack LIMITED_STACK_STORAGE_UNIT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LIMITED_STACK_STORAGE_UNIT", Material.GLASS, "Storage Unit - Limited | Stack");

    public static final SlimefunItemStack RANDOM_INPUT_STORAGE_UNIT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_RANDOM_INPUT_STORAGE_UNIT", Material.GLASS, "Random Input Storage Unit");
    public static final SlimefunItemStack RANDOM_OUTPUT_STORAGE_UNIT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_RANDOM_OUTPUT_STORAGE_UNIT", Material.GLASS, "Random Output Storage Unit");
    public static final SlimefunItemStack RANDOM_ACCESS_STORAGE_UNIT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_RANDOM_ACCESS_STORAGE_UNIT", Material.GLASS, "Random Access Storage Unit");
    public static final SlimefunItemStack DISTRIBUTE_LEFT_STORAGE_UNIT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DISTRIBUTE_LEFT_STORAGE_UNIT", Material.GLASS, "Distribute Left Storage Unit");
    public static final SlimefunItemStack DISTRIBUTE_RIGHT_STORAGE_UNIT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DISTRIBUTE_RIGHT_STORAGE_UNIT", Material.GLASS, "Distribute Right Storage Unit");

    // advanced storage
    public static final SlimefunItemStack STORAGE_INTERACT_PORT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_STORAGE_INTERACT_PORT", Material.BOOKSHELF, "Storage Interact Port");
    public static final SlimefunItemStack STORAGE_INSERT_PORT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_STORAGE_INSERT_PORT", Material.BOOKSHELF, "Storage Insert Port");
    public static final SlimefunItemStack STORAGE_WITHDRAW_PORT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_STORAGE_WITHDRAW_PORT", Material.BOOKSHELF, "Storage Withdraw Port");
    public static final SlimefunItemStack STORAGE_CARD = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_STORAGE_CARD", Material.MUSIC_DISC_BLOCKS, "Storage Card");

    public static final SlimefunItemStack BASIC_STORAGE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_BASIC_STORAGE", Material.GRAY_WOOL, "Basic Storage");
    public static final SlimefunItemStack ADVANCED_STORAGE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_STORAGE", Material.GRAY_WOOL, "Advanced Storage");
    public static final SlimefunItemStack ENERGIZED_STORAGE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGIZED_STORAGE", Material.GRAY_WOOL, "Energized Storage");
    public static final SlimefunItemStack OVERLOADED_STORAGE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_OVERLOADED_STORAGE", Material.GRAY_WOOL, "Overloaded Storage");
    public static final SlimefunItemStack STORAGE_BALANCER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_STORAGE_BALANCER", Material.YELLOW_WOOL, "Storage Balancer");
    public static final SlimefunItemStack STORAGE_IMPORTER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_STORAGE_IMPORTER", Material.ORANGE_WOOL, "Storage Importer");
    public static final SlimefunItemStack STORAGE_EXPORTER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_STORAGE_EXPORTER", Material.ORANGE_WOOL, "Storage Exporter");

    // accessor
    public static final SlimefunItemStack REMOTE_ACCESSOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_REMOTE_ACCESSOR", Material.OBSERVER, "Remote Accessor");
    public static final SlimefunItemStack TRIGGERABLE_REMOTE_ACCESSOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_TRIGGERABLE_REMOTE_ACCESSOR", Material.OBSERVER, "Remote Accessor - Triggerable");
    public static final SlimefunItemStack CONTROLLABLE_REMOTE_ACCESSOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_CONTROLLABLE_REMOTE_ACCESSOR", Material.OBSERVER, "Remote Accessor - Controller");
    public static final SlimefunItemStack EXPANDED_TRIGGERABLE_REMOTE_ACCESSOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_EXPANDED_TRIGGERABLE_REMOTE_ACCESSOR", Material.OBSERVER, "Expanded Remote Accessor - Triggerable");
    public static final SlimefunItemStack EXPANDED_CONTROLLABLE_REMOTE_ACCESSOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_EXPANDED_CONTROLLABLE_REMOTE_ACCESSOR", Material.OBSERVER, "Expanded Remote Accessor - Controller");
    public static final SlimefunItemStack RANDOM_ACCESSOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_RANDOM_ACCESSOR", Material.CHISELED_STONE_BRICKS, "Random Accessor");
    public static final SlimefunItemStack AREA_ACCESSOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_AREA_ACCESSOR", Material.REDSTONE_LAMP, "Area Accessor");

    public static final SlimefunItemStack TRANSPORTER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_TRANSPORTER", Material.PISTON, "Transporter");
    public static final SlimefunItemStack TRIGGERABLE_TRANSPORTER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_TRIGGERABLE_TRANSPORTER", Material.PISTON, "Transporter - Triggerable");
    public static final SlimefunItemStack CONTROLLABLE_TRANSPORTER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_CONTROLLABLE_TRANSPORTER", Material.PISTON, "Transporter - Controller");
    public static final SlimefunItemStack EXPANDED_TRIGGERABLE_TRANSPORTER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_EXPANDED_TRIGGERABLE_TRANSPORTER", Material.PISTON, "Expanded Transporter - Triggerable");
    public static final SlimefunItemStack EXPANDED_CONTROLLABLE_TRANSPORTER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_EXPANDED_CONTROLLABLE_TRANSPORTER", Material.PISTON, "Expanded Transporter - Controller");

    // logic
    public static final SlimefunItemStack LOGIC_COMPARATOR_NOTNULL = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LOGIC_COMPARATOR_NOTNULL", Material.STONE_BRICKS, "Logic Comparator - Not Null");
    public static final SlimefunItemStack LOGIC_COMPARATOR_AMOUNT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LOGIC_COMPARATOR_AMOUNT", Material.STONE_BRICKS, "Logic Comparator - Same Amount");
    public static final SlimefunItemStack LOGIC_COMPARATOR_SIMILAR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LOGIC_COMPARATOR_SIMILAR", Material.STONE_BRICKS, "Logic Comparator - Similar Without Amount");
    public static final SlimefunItemStack LOGIC_COMPARATOR_EQUAL = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LOGIC_COMPARATOR_EQUAL", Material.STONE_BRICKS, "Logic Comparator - Equal");
    public static final SlimefunItemStack LOGIC_CRAFTER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LOGIC_CRAFTER", Material.LOOM, "Logic Crafter");
    public static final SlimefunItemStack DIGIT_ADDER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGIT_ADDER", Material.HAY_BLOCK, "Digit Adder");
    public static final SlimefunItemStack LOGIC_INJECTOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LOGIC_INJECTOR", Material.LIGHTNING_ROD, "Logic Injector");
    public static final SlimefunItemStack DIGIT_INJECTOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGIT_INJECTOR", Material.LIGHTNING_ROD, "Digit Injector");

    // cargo
    public static final SlimefunItemStack BASIC_FRAME_MACHINE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_BASIC_FRAME_MACHINE", Material.STONE, "Basic Frame Machine");
    public static final SlimefunItemStack POINT_TRANSFER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_POINT_TRANSFER", Material.END_ROD, "Point Transfer");
    public static final SlimefunItemStack MESH_TRANSFER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MESH_TRANSFER", Material.TINTED_GLASS, "Station Transfer");
    public static final SlimefunItemStack LINE_TRANSFER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LINE_TRANSFER", Material.DROPPER, "Line Transfer");
    public static final SlimefunItemStack LOCATION_TRANSFER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LOCATION_TRANSFER", Material.NOTE_BLOCK, "Location Transfer");
    public static final SlimefunItemStack ADVANCED_POINT_TRANSFER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_POINT_TRANSFER", Material.END_ROD, "Advanced Point Transfer");
    public static final SlimefunItemStack ADVANCED_MESH_TRANSFER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_MESH_TRANSFER", Material.TINTED_GLASS, "Advanced Station Transfer");
    public static final SlimefunItemStack ADVANCED_LINE_TRANSFER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_LINE_TRANSFER", Material.DROPPER, "Advanced Line Transfer");
    public static final SlimefunItemStack ADVANCED_LOCATION_TRANSFER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_LOCATION_TRANSFER", Material.NOTE_BLOCK, "Advanced Location Transfer");

    public static final SlimefunItemStack CONFIGURATION_COPIER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_CONFIGURATION_COPIER", Material.STICKY_PISTON, "Configuration Copier");
    public static final SlimefunItemStack CONFIGURATION_PASTER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_CONFIGURATION_PASTER", Material.STICKY_PISTON, "Configuration Paster");
    public static final SlimefunItemStack CLICK_WORK_MACHINE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_CLICK_WORK_MACHINE", Material.RED_NETHER_BRICKS, "Click Work Machine");
    public static final SlimefunItemStack SIMULATE_CLICK_MACHINE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_SIMULATE_CLICK_MACHINE", Material.EMERALD_BLOCK, "Simulate Click Machine");
    public static final SlimefunItemStack TRIGGERABLE_SIMULATE_CLICK_MACHINE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_TRIGGERABLE_SIMULATE_CLICK_MACHINE", Material.EMERALD_BLOCK, "Simulate Click Machine - Triggerable");

    /* functional machines */
    // core machine
    public static final SlimefunItemStack RESEARCH_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_RESEARCH_TABLE", Material.SMITHING_TABLE, "Research Table");
    public static final SlimefunItemStack BEDROCK_CRAFT_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_BEDROCK_CRAFT_TABLE", Material.LODESTONE, "Bedrock Craft Table");
    public static final SlimefunItemStack MATRIX_CRAFTING_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_CRAFTING_TABLE", Material.LECTERN, "Matrix Crafting Table");

    public static final SlimefunItemStack INFO_FACTORY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_INFO_FACTORY", Material.DARK_OAK_WOOD, "Info Factory");
    public static final SlimefunItemStack DUST_FACTORY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DUST_FACTORY", Material.DIRT, "Dust Factory");
    public static final SlimefunItemStack ETHER_MINER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ETHER_MINER", Material.CHISELED_DEEPSLATE, "Ether Miner");
    public static final SlimefunItemStack ENERGY_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGY_TABLE", Material.CHISELED_SANDSTONE, "Energy table");
    public static final SlimefunItemStack ENERGY_INPUT_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGY_INPUT_TABLE", Material.CHISELED_SANDSTONE, "Energy Input table");
    public static final SlimefunItemStack ENERGY_OUTPUT_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGY_OUTPUT_TABLE", Material.CHISELED_SANDSTONE, "Energy Output table");
    public static final SlimefunItemStack EQUIVALENT_EXCHANGE_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_EQUIVALENT_EXCHANGE_TABLE", Material.RESPAWN_ANCHOR, "Equivalent Exchange Table");
    public static final SlimefunItemStack COPY_CARD_FACTORY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_COPY_CARD_FACTORY", Material.AMETHYST_BLOCK, "Copy Card Factory");
    public static final SlimefunItemStack PHONY_FACTORY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_PHONY_FACTORY", Material.AMETHYST_BLOCK, "Phony Factory");

    public static final SlimefunItemStack STORAGE_CARD_MERGE_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_STORAGE_CARD_MERGE_TABLE", Material.STONE, "Storage Card Merge Table");
    public static final SlimefunItemStack STORAGE_CARD_DISTRIBUTION_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_STORAGE_CARD_DISTRIBUTION_TABLE", Material.STONE, "Storage Card Distribution Table");
    public static final SlimefunItemStack ENTROPY_CRAFT_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENTROPY_CRAFT_TABLE", Material.STONE, "Entropy Craft Table");
    public static final SlimefunItemStack ANNULAR_CRAFT_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ANNULAR_CRAFT_TABLE", Material.STONE, "Annular Craft Table");
    public static final SlimefunItemStack SHELL_CRAFT_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_SHELL_CRAFT_TABLE", Material.STONE, "Shell Craft Table");
    public static final SlimefunItemStack COPY_CARD_DUPLICATION_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_COPY_CARD_DUPLICATION_TABLE", Material.STONE, "Copy Card Duplication Table");
    public static final SlimefunItemStack PHONY_CRAFT_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_PHONY_CRAFT_TABLE", Material.STONE, "PHONY Craft Table");
    public static final SlimefunItemStack INFINITY_STORAGE_CARD_CRAFT_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_INFINITY_STORAGE_CARD_CRAFT_TABLE", Material.STONE, "Infinity Storage Card Craft Table");

    public static final SlimefunItemStack ITEM_DISMANTLE_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ITEM_DISMANTLE_TABLE", Material.OXIDIZED_CUT_COPPER, "Item Dismantle Table");
    public static final SlimefunItemStack AUTO_ITEM_DISMANTLE_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_AUTO_ITEM_DISMANTLE_TABLE", Material.CUT_COPPER, "Auto Item Dismantle Table");
    public static final SlimefunItemStack COPY_CARD_DUPLICATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_COPY_CARD_DUPLICATOR", Material.AMETHYST_BLOCK, "Copy Card Duplicator");
    public static final SlimefunItemStack ADVANCED_AUTO_CRAFT_FRAME = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_AUTO_CRAFT_FRAME", Material.BEACON, "Advanced Auto Craft Frame");
    public static final SlimefunItemStack ADVANCED_AUTO_CRAFT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_AUTO_CRAFT", Material.BEACON, "Advanced Auto Craft");
    public static final SlimefunItemStack MULTI_FRAME_MACHINE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MULTI_FRAME_MACHINE", Material.COBBLESTONE, "Multi Frame Machine");
    public static final SlimefunItemStack CRAFT_STORAGE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_CRAFT_STORAGE", Material.COBBLESTONE, "Craft Storage");
    public static final SlimefunItemStack ADVANCED_CRAFT_STORAGE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_CRAFT_STORAGE", Material.COBBLESTONE, "Advanced Craft Storage");

    // special machine
    public static final SlimefunItemStack ITEM_FIXER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ITEM_FIXER", Material.SLIME_BLOCK, "Item Fixer");
    public static final SlimefunItemStack COBBLESTONE_FACTORY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_COBBLESTONE_FACTORY", Material.CHISELED_POLISHED_BLACKSTONE, "Cobblestone Erupter");
    public static final SlimefunItemStack HEALTH_CURER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_HEALTH_CURER", Material.STONE, "Health Curer");
    public static final SlimefunItemStack EFFECT_CURER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_EFFECT_CURER", Material.STONE, "Effect Curer");

    public static final SlimefunItemStack FUEL_CHARGER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_FUEL_CHARGER", Material.BRICK_WALL, "Fuel Charger");
    public static final SlimefunItemStack FUEL_ACCELERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_FUEL_ACCELERATOR", Material.RED_NETHER_BRICK_WALL, "Fuel Accelerator");
    public static final SlimefunItemStack FUEL_OPERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_FUEL_OPERATOR", Material.PRISMARINE_WALL, "Fuel Operator");
    public static final SlimefunItemStack OPERATION_ACCELERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_OPERATION_ACCELERATOR", Material.DEEPSLATE_TILE_WALL, "Operation Accelerator");
    public static final SlimefunItemStack ENERGIZED_OPERATION_ACCELERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENERGIZED_OPERATION_ACCELERATOR", Material.DEEPSLATE_TILE_WALL, "Energized Operation Accelerator");
    public static final SlimefunItemStack OVERLOADED_OPERATION_ACCELERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_OVERLOADED_OPERATION_ACCELERATOR", Material.DEEPSLATE_TILE_WALL, "Overloaded Operation Accelerator");

    // tower
    public static final SlimefunItemStack CURE_TOWER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_CURE_TOWER", Material.RED_GLAZED_TERRACOTTA, "Cure Tower");
    public static final SlimefunItemStack PURIFY_LEVEL_TOWER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_PURIFY_LEVEL_TOWER", Material.WHITE_GLAZED_TERRACOTTA, "Purify Level Tower");
    public static final SlimefunItemStack PURIFY_TIME_TOWER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_PURIFY_TIME_TOWER", Material.WHITE_GLAZED_TERRACOTTA, "Purify Time Tower");

    /* productive machine */
    // manual machine
    public static final SlimefunItemStack MANUAL_CRAFT_MACHINE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_CRAFT_MACHINE", Material.BOOK, "Manual Craft Machine");
    public static final SlimefunItemStack MANUAL_CRAFTING_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_CRAFTING_TABLE", Material.CRAFTING_TABLE, "Manual Crafting Table");
    public static final SlimefunItemStack MANUAL_ENHANCED_CRAFTING_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_ENHANCED_CRAFTING_TABLE", Material.CRAFTING_TABLE, "Manual Enhanced Crafting Table");
    public static final SlimefunItemStack MANUAL_GRIND_STONE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_GRIND_STONE", Material.DISPENSER, "Manual Grind Table");
    public static final SlimefunItemStack MANUAL_ARMOR_FORGE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_ARMOR_FORGE", Material.IRON_BLOCK, "Manual Armor Forge");
    public static final SlimefunItemStack MANUAL_ORE_CRUSHER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_ORE_CRUSHER", Material.DROPPER, "Manual Ore Crusher");
    public static final SlimefunItemStack MANUAL_COMPRESSOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_COMPRESSOR", Material.PISTON, "Manual Compressor");
    public static final SlimefunItemStack MANUAL_SMELTERY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_SMELTERY", Material.BLAST_FURNACE, "Manual Smeltery");
    public static final SlimefunItemStack MANUAL_PRESSURE_CHAMBER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_PRESSURE_CHAMBER", Material.STICKY_PISTON, "Manual Pressure Chamber");
    public static final SlimefunItemStack MANUAL_MAGIC_WORKBENCH = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_MAGIC_WORKBENCH", Material.BOOKSHELF, "Manual Magic Workbench");
    public static final SlimefunItemStack MANUAL_ORE_WASHER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_ORE_WASHER", Material.BLUE_STAINED_GLASS, "Manual Ore Washer");
    public static final SlimefunItemStack MANUAL_COMPOSTER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_COMPOSTER", Material.CAULDRON, "Manual Composter");
    public static final SlimefunItemStack MANUAL_GOLD_PAN = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_GOLD_PAN", Material.BROWN_TERRACOTTA, "Manual Gold Pan");
    public static final SlimefunItemStack MANUAL_CRUCIBLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_CRUCIBLE", Material.RED_TERRACOTTA, "Manual Crucible");
    public static final SlimefunItemStack MANUAL_JUICER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_JUICER", Material.GLASS, "Manual Juicer");
    public static final SlimefunItemStack MANUAL_ANCIENT_ALTAR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_ANCIENT_ALTAR", Material.ENCHANTING_TABLE, "Manual Ancient Altar");
    public static final SlimefunItemStack MANUAL_HEATED_PRESSURE_CHAMBER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MANUAL_HEATED_PRESSURE_CHAMBER", Material.LIGHT_GRAY_STAINED_GLASS, "Manual Heated Pressure Chamber");

    // basic machine
    public static final SlimefunItemStack BASIC_LOGIC_FACTORY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_BASIC_LOGIC_FACTORY", Material.OBSIDIAN, "Basic Logic Factory");

    // advanced machine
    public static final SlimefunItemStack ADVANCED_COMPOSTER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_COMPOSTER", Material.CAULDRON, "Advanced Composter");
    public static final SlimefunItemStack ADVANCED_JUICER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_JUICER", Material.GLASS, "Advanced Juicer");
    public static final SlimefunItemStack ADVANCED_FURNACE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_FURNACE", Material.FURNACE, "Advanced Furnace");
    public static final SlimefunItemStack ADVANCED_GOLD_PAN = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_GOLD_PAN", Material.BROWN_TERRACOTTA, "Advanced Gold Pan");
    public static final SlimefunItemStack ADVANCED_DUST_WASHER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_DUST_WASHER", Material.BLUE_STAINED_GLASS, "Advanced Dust Washer");
    public static final SlimefunItemStack ADVANCED_INGOT_FACTORY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_INGOT_FACTORY", Material.RED_TERRACOTTA, "Advanced Ingot Factory");
    public static final SlimefunItemStack ADVANCED_CRUCIBLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_CRUCIBLE", Material.RED_TERRACOTTA, "Advanced Crucible");
    public static final SlimefunItemStack ADVANCED_ORE_GRINDER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_ORE_GRINDER", Material.FURNACE, "Advanced Ore Grinder");
    public static final SlimefunItemStack ADVANCED_HEATED_PRESSURE_CHAMBER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_HEATED_PRESSURE_CHAMBER", Material.LIGHT_GRAY_STAINED_GLASS, "Advanced Heated Pressure Chamber");
    public static final SlimefunItemStack ADVANCED_INGOT_PULVERIZER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_INGOT_PULVERIZER", Material.FURNACE, "Advanced Ingot Pulverizer");
    public static final SlimefunItemStack ADVANCED_AUTO_DRIER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_AUTO_DRIER", Material.SMOKER, "Advanced Auto Drier");
    public static final SlimefunItemStack ADVANCED_PRESS = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_PRESS", HeadTexture.ELECTRIC_PRESS.getTexture(), "Advanced Press");
    public static final SlimefunItemStack ADVANCED_FOOD_FACTORY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_FOOD_FACTORY", Material.GREEN_TERRACOTTA, "Advanced Food Factory");
    public static final SlimefunItemStack ADVANCED_FREEZER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_FREEZER", Material.LIGHT_BLUE_STAINED_GLASS, "Advanced Freezer");
    public static final SlimefunItemStack ADVANCED_CARBON_PRESS = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_CARBON_PRESS", Material.BLACK_STAINED_GLASS, "Advanced Carbon Press");
    public static final SlimefunItemStack ADVANCED_SMELTERY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ADVANCED_SMELTERY", Material.FURNACE, "Advanced Smeltery");

    // conversion
    public static final SlimefunItemStack GRAVEL_CONVERSION = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_GRAVEL_CONVERSION", Material.POLISHED_ANDESITE, "Gravel Conversion");
    public static final SlimefunItemStack SOUL_SAND_CONVERSION = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_SOUL_SAND_CONVERSION", Material.SOUL_SOIL, "Soul Sand Conversion");
    public static final SlimefunItemStack LOGIC_TO_DIGITAL_CONVERSION = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LOGIC_TO_DIGITAL_CONVERSION", Material.BONE_BLOCK, "Logic To Digital Conversion");

    // extraction
    public static final SlimefunItemStack DIGITAL_EXTRACTION = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_EXTRACTION", Material.RED_NETHER_BRICKS, "Digital Extraction");

    // generator
    public static final SlimefunItemStack LIQUID_CARD_GENERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LIQUID_CARD_GENERATOR", Material.BLUE_TERRACOTTA, "Liquid Card Generator");
    public static final SlimefunItemStack LOGIC_GENERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_LOGIC_GENERATOR", Material.WARPED_HYPHAE, "Logic Generator");
    public static final SlimefunItemStack DIGITAL_GENERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_DIGITAL_GENERATOR", Material.CRIMSON_HYPHAE, "Digital Generator");

    /* final stage item */
    public static final SlimefunItemStack ENTROPY_SEED = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENTROPY_SEED", Material.BLACK_CONCRETE, "Entropy Seed");
    public static final SlimefunItemStack MATRIX_MACHINE_CHARGE_CARD = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_MACHINE_CHARGE_CARD", Material.BRICK, "Matrix Charge Card");
    public static final SlimefunItemStack MATRIX_MACHINE_ACCELERATE_CARD = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_MACHINE_ACCELERATE_CARD", Material.NETHER_BRICK, "Matrix Accelerate Card");
    public static final SlimefunItemStack MATRIX_MACHINE_ACTIVATE_CARD = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_MACHINE_ACTIVATE_CARD", Material.COPPER_INGOT, "Matrix Active Card");
    public static final SlimefunItemStack MATRIX_OPERATION_ACCELERATE_CARD = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_OPERATION_ACCELERATE_CARD", Material.NETHERITE_INGOT, "Matrix Operation Accelerate Card");
    public static final SlimefunItemStack MATRIX_QUANTITY_MODULE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_QUANTITY_MODULE", Material.AMETHYST_SHARD, "Matrix Quantity Module");
    public static final SlimefunItemStack MATRIX_OPERATION_ACCELERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_OPERATION_ACCELERATOR", Material.DEEPSLATE_TILE_WALL, "Matrix Operation Accelerator");

    public static final SlimefunItemStack ENTROPY_CONSTRUCTOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ENTROPY_CONSTRUCTOR", Material.COMPOSTER, "Entropy Constructor");
    public static final SlimefunItemStack MATRIX_EXPANDED_CAPACITOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_EXPANDED_CAPACITOR", Material.YELLOW_STAINED_GLASS, "Matrix Expanded Capacitor");
    public static final SlimefunItemStack MATRIX_GENERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_GENERATOR", Material.SEA_LANTERN, "Matrix Generator");
    public static final SlimefunItemStack MATRIX_ITEM_DISMANTLE_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_ITEM_DISMANTLE_TABLE", Material.OXIDIZED_CUT_COPPER, "Matrix Item Dismantle Table");
    public static final SlimefunItemStack MATRIX_STORAGE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_STORAGE", Material.GRAY_WOOL, "Matrix Storage");
    public static final SlimefunItemStack MATRIX_ACCELERATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_ACCELERATOR", Material.TARGET, "Matrix Accelerator");
    public static final SlimefunItemStack MATRIX_COPY_CARD_DUPLICATOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_COPY_CARD_DUPLICATOR", Material.AMETHYST_BLOCK, "Matrix Item Serialization Constructor");
    public static final SlimefunItemStack MATRIX_COPY_CARD_FACTORY = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_COPY_CARD_FACTORY", Material.BUDDING_AMETHYST, "Item Deserialize Parser");
    public static final SlimefunItemStack MATRIX_REACTOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_REACTOR", Material.COAL_BLOCK, "Matrix Reactor");

    // Trophy
    public static final SlimefunItemStack TROPHY_MEAWERFUL = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_TROPHY_MEAWERFUL", "3c8c397e89c92745ef27a43df1636586ed0bbaeca49e2604c1347c2e149ae58d", "§fmeawerful");
    public static final SlimefunItemStack TROPHY_SHIXINZIA = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_TROPHY_SHIXINZIA", "2d461f5c7a9bc81c77910980d821d4a550766c7867e112f040794c4fa949b974", "§fshixinzia");
    
    public static final SlimefunItemStack CARD_OPERATION_TABLE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_CARD_OPERATION_TABLE", Material.CARTOGRAPHY_TABLE, "Card Operation Table");
    public static final SlimefunItemStack ORDERED_DUST_FACTORY_DIRT = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ORDERED_DUST_FACTORY_DIRT", Material.DIRT, "Ordered Dust Factory");
    public static final SlimefunItemStack ORDERED_DUST_FACTORY_STONE = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ORDERED_DUST_FACTORY_STONE", Material.COBBLESTONE, "Ordered Dust Factory");
    public static final SlimefunItemStack ITEM_SERIALIZATION_CONSTRUCTOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ITEM_SERIALIZATION_CONSTRUCTOR", Material.AMETHYST_BLOCK, "Item Seriazilation Constructor");
    public static final SlimefunItemStack MATRIX_ITEM_SERIALIZATION_CONSTRUCTOR = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_ITEM_SERIALIZATION_CONSTRUCTOR", Material.AMETHYST_BLOCK, "Item Seriazilation Constructor");
    public static final SlimefunItemStack ITEM_DESERIALIZE_PARSER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_ITEM_DESERIALIZE_PARSER", Material.BUDDING_AMETHYST, "Item Deserialize Parser");
    public static final SlimefunItemStack MATRIX_ITEM_DESERIALIZE_PARSER = ConfigUtil.getSlimefunItemStack(FinalTech.getLanguageManager(), "FINALTECH_MATRIX_ITEM_DESERIALIZE_PARSER", Material.BUDDING_AMETHYST, "Item Deserialize Parser");
}
