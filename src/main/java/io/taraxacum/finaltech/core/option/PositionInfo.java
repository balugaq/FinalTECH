package io.taraxacum.finaltech.core.option;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.libs.plugin.dto.KeyValueStringHelper;
import io.taraxacum.libs.plugin.dto.KeyValueStringOrderHelper;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.interfaces.LocationDataService;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.dto.LocationDataLoreOption;
import io.taraxacum.libs.slimefun.dto.LocationDataOption;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public class PositionInfo {
    public static final String KEY = "pi";

    public static final String VALUE_KEY_NORTH = "n";
    public static final String VALUE_KEY_EAST = "e";
    public static final String VALUE_KEY_SOUTH = "s";
    public static final String VALUE_KEY_WEST = "w";
    public static final String VALUE_KEY_UP = "u";
    public static final String VALUE_KEY_DOWN = "d";

    public static final ItemStack NORTH_ICON = new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, FinalTech.getLanguageString("option", "POSITION_INFO", "north", "name"), FinalTech.getLanguageStringArray("option", "POSITION_INFO", "north", "lore"));
    public static final ItemStack EAST_ICON = new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, FinalTech.getLanguageString("option", "POSITION_INFO", "east", "name"), FinalTech.getLanguageStringArray("option", "POSITION_INFO", "east", "lore"));
    public static final ItemStack SOUTH_ICON = new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, FinalTech.getLanguageString("option", "POSITION_INFO", "south", "name"), FinalTech.getLanguageStringArray("option", "POSITION_INFO", "south", "lore"));
    public static final ItemStack WEST_ICON = new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, FinalTech.getLanguageString("option", "POSITION_INFO", "west", "name"), FinalTech.getLanguageStringArray("option", "POSITION_INFO", "west", "lore"));
    public static final ItemStack UP_ICON = new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, FinalTech.getLanguageString("option", "POSITION_INFO", "up", "name"), FinalTech.getLanguageStringArray("option", "POSITION_INFO", "up", "lore"));
    public static final ItemStack DOWN_ICON = new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, FinalTech.getLanguageString("option", "POSITION_INFO", "down", "name"), FinalTech.getLanguageStringArray("option", "POSITION_INFO", "down", "lore"));

    public static final String VALUE_NULL = "";
    public static final String VALUE_INPUT = "i";
    public static final String VALUE_OUTPUT = "o";
    public static final String VALUE_INPUT_AND_OUTPUT = "io";

    public static final List<String> NULL_LORE = FinalTech.getLanguageStringList("option", "POSITION_INFO", "null", "lore");
    public static final List<String> INPUT_LORE = FinalTech.getLanguageStringList("option", "POSITION_INFO", "input", "lore");
    public static final List<String> OUTPUT_LORE = FinalTech.getLanguageStringList("option", "POSITION_INFO", "output", "lore");
    public static final List<String> INPUT_AND_OUTPUT_LORE = FinalTech.getLanguageStringList("option", "POSITION_INFO", "input-and-output", "lore");

    public static final Material NULL_MATERIAL = Material.GRAY_STAINED_GLASS_PANE;
    public static final Material INPUT_MATERIAL = Material.BLUE_STAINED_GLASS_PANE;
    public static final Material OUTPUT_MATERIAL = Material.ORANGE_STAINED_GLASS_PANE;
    public static final Material INPUT_AND_OUTPUT_MATERIAL = Material.PURPLE_STAINED_GLASS_PANE;

    public static final KeyValueStringHelper MAP_EXAMPLE = new KeyValueStringOrderHelper(Arrays.asList(VALUE_KEY_NORTH, VALUE_KEY_DOWN, VALUE_KEY_SOUTH, VALUE_KEY_WEST, VALUE_KEY_UP, VALUE_KEY_EAST), Arrays.asList(VALUE_INPUT, VALUE_OUTPUT, VALUE_INPUT_AND_OUTPUT));

    public static final Map<String, Material> VALUE_MATERIAL_MAP = new HashMap<>() {{
        this.put(VALUE_NULL, NULL_MATERIAL);
        this.put(VALUE_INPUT, INPUT_MATERIAL);
        this.put(VALUE_OUTPUT, OUTPUT_MATERIAL);
        this.put(VALUE_INPUT_AND_OUTPUT, INPUT_AND_OUTPUT_MATERIAL);
    }};

    public static final LocationDataLoreMaterialOption NORTH_OPTION = new LocationDataLoreMaterialOption(VALUE_KEY_NORTH);
    public static final LocationDataLoreMaterialOption EAST_OPTION = new LocationDataLoreMaterialOption(VALUE_KEY_EAST);
    public static final LocationDataLoreMaterialOption SOUTH_OPTION = new LocationDataLoreMaterialOption(VALUE_KEY_SOUTH);
    public static final LocationDataLoreMaterialOption WEST_OPTION = new LocationDataLoreMaterialOption(VALUE_KEY_WEST);
    public static final LocationDataLoreMaterialOption UP_OPTION = new LocationDataLoreMaterialOption(VALUE_KEY_UP);
    public static final LocationDataLoreMaterialOption DOWN_OPTION = new LocationDataLoreMaterialOption(VALUE_KEY_DOWN);

    public static class LocationDataLoreMaterialOption extends LocationDataLoreOption {
        private final String valueKey;
        LocationDataLoreMaterialOption(@Nonnull String valueKey) {
            super(LocationDataOption.CARGO_ID, KEY, new LinkedHashMap<>() {{
                this.put(VALUE_NULL, NULL_LORE);
                this.put(VALUE_INPUT, INPUT_LORE);
                this.put(VALUE_OUTPUT, OUTPUT_LORE);
                this.put(VALUE_INPUT_AND_OUTPUT, INPUT_AND_OUTPUT_LORE);
            }});
            this.valueKey = valueKey;
        }

        @Nonnull
        @Override
        public String getOrDefaultValue(@Nonnull LocationDataService locationDataService,  @Nonnull Location location) {
            String valueMap = JavaUtil.getFirstNotNull(locationDataService.getLocationData(location, this.key), "");
            KeyValueStringHelper keyValueStringHelper = MAP_EXAMPLE.parseString(valueMap);
            String value = keyValueStringHelper.getValue(this.valueKey);
            if (!this.validValue(value)) {
                value = this.defaultValue();
            }
            return value;
        }

        @Nonnull
        @Override
        public String getOrDefaultValue(@Nonnull LocationDataService locationDataService, @Nonnull LocationData locationData) {
            String valueMap = JavaUtil.getFirstNotNull(locationDataService.getLocationData(locationData, this.key), "");
            KeyValueStringHelper keyValueStringHelper = MAP_EXAMPLE.parseString(valueMap);
            String value = keyValueStringHelper.getValue(this.valueKey);
            if (!this.validValue(value)) {
                value = this.defaultValue();
            }
            return value;
        }

        @Override
        public void updateLore(@Nonnull ItemStack itemStack, @Nullable String value) {
            if (this.validValue(value) && VALUE_MATERIAL_MAP.containsKey(value)) {
                itemStack.setType(VALUE_MATERIAL_MAP.get(value));
            }
            super.updateLore(itemStack, value);
        }
        @Override
        public void updateLore(@Nonnull ItemStack itemStack, @Nullable String value, @Nonnull SlimefunItem slimefunItem) {
            if (this.validValue(value) && VALUE_MATERIAL_MAP.containsKey(value)) {
                itemStack.setType(VALUE_MATERIAL_MAP.get(value));
            }
            super.updateLore(itemStack, value, slimefunItem);
        }

        @Override
        public void checkAndUpdateIcon(@Nonnull ItemStack itemStack, @Nonnull LocationDataService locationDataService, @Nonnull Location location) {
            String valueMap = JavaUtil.getFirstNotNull(locationDataService.getLocationData(location, this.key), "");
            KeyValueStringHelper keyValueStringHelper = MAP_EXAMPLE.parseString(valueMap);
            String value = keyValueStringHelper.getValue(this.valueKey);
            if (!this.validValue(value)) {
                value = this.defaultValue();
                keyValueStringHelper.putEntry(this.valueKey, value);
                this.setOrClearValue(locationDataService, location, keyValueStringHelper.toString());
            }
            itemStack.setAmount(keyValueStringHelper.getKeyIndex(this.valueKey) > 0 ? keyValueStringHelper.getKeyIndex(this.valueKey) + 1 : 1);
            this.updateLore(itemStack, value);
            super.checkAndUpdateIcon(itemStack, locationDataService, location);
        }

        @Nonnull
        @Override
        public Consumer<InventoryClickEvent> getHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
            return inventoryClickEvent -> {
                inventoryClickEvent.setCancelled(true);
                Inventory inventory = inventoryClickEvent.getClickedInventory();
                if(inventory != null) {
                    ItemStack itemStack = inventory.getItem(inventoryClickEvent.getSlot());
                    if(!ItemStackUtil.isItemNull(itemStack)) {
                        String valueMap = JavaUtil.getFirstNotNull(locationDataService.getLocationData(location, this.key), "");
                        KeyValueStringHelper keyValueStringHelper = MAP_EXAMPLE.parseString(valueMap);
                        String value = keyValueStringHelper.getValue(this.valueKey);
                        value = inventoryClickEvent.getClick().isRightClick() ? this.clickPreviousValue(value, inventoryClickEvent.getClick()) : this.clickNextValue(value, inventoryClickEvent.getClick());
                        keyValueStringHelper.putEntry(this.valueKey, value);
                        itemStack.setAmount(keyValueStringHelper.getKeyIndex(this.valueKey) > 0 ? keyValueStringHelper.getKeyIndex(this.valueKey) + 1 : 1);
                        this.updateLore(itemStack, value);
                        this.setOrClearValue(locationDataService, location, keyValueStringHelper.toString());
                    }
                }
            };
        }

        @Nonnull
        @Override
        public Consumer<InventoryClickEvent> getUpdateHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
            return inventoryClickEvent -> {
                inventoryClickEvent.setCancelled(true);
                Inventory inventory = inventoryClickEvent.getClickedInventory();
                if(inventory != null) {
                    ItemStack itemStack = inventory.getItem(inventoryClickEvent.getSlot());
                    if(!ItemStackUtil.isItemNull(itemStack)) {
                        String valueMap = JavaUtil.getFirstNotNull(locationDataService.getLocationData(location, this.key), "");
                        KeyValueStringHelper keyValueStringHelper = MAP_EXAMPLE.parseString(valueMap);
                        String value = keyValueStringHelper.getValue(this.valueKey);
                        if ("".equals(value)) {
                            value = null;
                        }
                        if (!this.validValue(value)) {
                            value = this.defaultValue();
                        }
                        keyValueStringHelper.putEntry(this.valueKey, value);
                        itemStack.setAmount(keyValueStringHelper.getKeyIndex(this.valueKey) > 0 ? keyValueStringHelper.getKeyIndex(this.valueKey) + 1 : 1);
                        this.updateLore(itemStack, value);
                        this.setOrClearValue(locationDataService, location, keyValueStringHelper.toString());
                    }
                }
            };
        }

        @Nonnull
        @Override
        public Consumer<InventoryClickEvent> getNextHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
            return inventoryClickEvent -> {
                inventoryClickEvent.setCancelled(true);
                Inventory inventory = inventoryClickEvent.getClickedInventory();
                if(inventory != null) {
                    ItemStack itemStack = inventory.getItem(inventoryClickEvent.getSlot());
                    if(!ItemStackUtil.isItemNull(itemStack)) {
                        String valueMap = JavaUtil.getFirstNotNull(locationDataService.getLocationData(location, this.key), "");
                        KeyValueStringHelper keyValueStringHelper = MAP_EXAMPLE.parseString(valueMap);
                        String value = keyValueStringHelper.getValue(this.valueKey);
                        value = this.clickNextValue(value, inventoryClickEvent.getClick());
                        keyValueStringHelper.putEntry(this.valueKey, value);
                        itemStack.setAmount(keyValueStringHelper.getKeyIndex(this.valueKey) > 0 ? keyValueStringHelper.getKeyIndex(this.valueKey) + 1 : 1);
                        this.updateLore(itemStack, value);
                        this.setOrClearValue(locationDataService, location, keyValueStringHelper.toString());
                    }
                }
            };
        }

        @Nonnull
        @Override
        public Consumer<InventoryClickEvent> getPreviousHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
            return inventoryClickEvent -> {
                inventoryClickEvent.setCancelled(true);
                Inventory inventory = inventoryClickEvent.getClickedInventory();
                if(inventory != null) {
                    ItemStack itemStack = inventory.getItem(inventoryClickEvent.getSlot());
                    if(!ItemStackUtil.isItemNull(itemStack)) {
                        String valueMap = JavaUtil.getFirstNotNull(locationDataService.getLocationData(location, this.key), "");
                        KeyValueStringHelper keyValueStringHelper = MAP_EXAMPLE.parseString(valueMap);
                        String value = keyValueStringHelper.getValue(this.valueKey);
                        value = this.clickPreviousValue(value, inventoryClickEvent.getClick());
                        keyValueStringHelper.putEntry(this.valueKey, value);
                        itemStack.setAmount(keyValueStringHelper.getKeyIndex(this.valueKey) > 0 ? keyValueStringHelper.getKeyIndex(this.valueKey) + 1 : 1);
                        this.updateLore(itemStack, value);
                        this.setOrClearValue(locationDataService, location, keyValueStringHelper.toString());
                    }
                }
            };
        }
    }

    @Nonnull
    public static BlockFace[] getBlockFaces(@Nonnull String string, @Nonnull String... values) {
        KeyValueStringHelper keyValueStringHelper = MAP_EXAMPLE.parseString(string);
        List<String> keyList = keyValueStringHelper.getAllMatchKey(values);
        BlockFace[] blockFaces = new BlockFace[keyList.size()];
        int i = 0;
        for (String key : keyList) {
            switch (key) {
                case VALUE_KEY_NORTH -> blockFaces[i++] = BlockFace.NORTH;
                case VALUE_KEY_EAST -> blockFaces[i++] = BlockFace.EAST;
                case VALUE_KEY_SOUTH -> blockFaces[i++] = BlockFace.SOUTH;
                case VALUE_KEY_WEST -> blockFaces[i++] = BlockFace.WEST;
                case VALUE_KEY_UP -> blockFaces[i++] = BlockFace.UP;
                case VALUE_KEY_DOWN -> blockFaces[i++] = BlockFace.DOWN;
            }
        }
        return blockFaces;
    }

    @Nonnull
    public static BlockFace[] getBlockFaces(@Nonnull LocationDataService locationDataService, @Nonnull LocationData locationData, @Nonnull String... values) {
        String value = JavaUtil.getFirstNotNull(locationDataService.getLocationData(locationData, KEY), "");
        return PositionInfo.getBlockFaces(value, values);
    }
}
