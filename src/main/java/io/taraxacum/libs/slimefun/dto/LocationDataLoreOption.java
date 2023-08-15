package io.taraxacum.libs.slimefun.dto;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.ld.LocationDataService;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Location;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public class LocationDataLoreOption extends LocationDataOption {

    /**
     * -1: replace all the lore
     * others: replace the lore from the specified line.
     */
    private final int loreOffset;
    private final Map<String, List<String>> valueLoreMap;
    private static final List<String> ERROR_LORE = List.of("§cERROR");

    public LocationDataLoreOption(@Nonnull String id, @Nonnull String key, int loreOffset, @Nonnull Map<String, List<String>> valueLoreMap) {
        super(id, key, valueLoreMap.keySet().stream().toList());
        this.valueLoreMap = valueLoreMap;
        this.loreOffset = loreOffset;
    }

    public LocationDataLoreOption(@Nonnull SlimefunItem slimefunItem, int loreOffset, @Nonnull String key, @Nonnull Map<String, List<String>> valueLoreMap) {
        this(slimefunItem.getId(), key, loreOffset, valueLoreMap);
    }

    public LocationDataLoreOption(@Nonnull String id, @Nonnull String key, @Nonnull Map<String, List<String>> valueLoreMap) {
        this(id, key, -1, valueLoreMap);
    }

    public LocationDataLoreOption(@Nonnull SlimefunItem slimefunItem, @Nonnull String key, @Nonnull Map<String, List<String>> valueLoreMap) {
        this(slimefunItem.getId(), key, -1, valueLoreMap);
    }

    public void updateLore(@Nonnull ItemStack itemStack, @Nullable String value) {
        List<String> loreList = this.valueLoreMap.get(this.validValue(value) ? value : this.defaultValue());
        if(this.loreOffset < 0) {
            ItemStackUtil.setLore(itemStack, loreList);
        } else {
            ItemStackUtil.replaceLore(itemStack, this.loreOffset, loreList);
        }
    }

    public void updateLore(@Nonnull ItemStack itemStack, @Nullable String value, @Nonnull SlimefunItem slimefunItem) {
        this.updateLore(itemStack, value);
    }

    public void checkAndUpdateIcon(@Nonnull ItemStack itemStack, @Nonnull LocationDataService locationDataService, @Nonnull Location location) {
        String value = this.getOrDefaultValue(locationDataService, location);
        this.updateLore(itemStack, value);
    }

    public void checkAndUpdateIcon(@Nonnull Inventory inventory, int slot, @Nonnull LocationDataService locationDataService, @Nonnull Location location) {
        ItemStack itemStack = inventory.getItem(slot);
        if(!ItemStackUtil.isItemNull(itemStack)) {
            String value = this.getOrDefaultValue(locationDataService, location);
            this.updateLore(itemStack, value);
        }
    }

    public void checkAndUpdateIcon(@Nonnull Inventory inventory, int slot, @Nonnull LocationDataService locationDataService, @Nonnull LocationData locationData) {
        ItemStack itemStack = inventory.getItem(slot);
        if(!ItemStackUtil.isItemNull(itemStack)) {
            String value = this.getOrDefaultValue(locationDataService, locationData);
            this.updateLore(itemStack, value);
        }
    }

    @Nonnull
    public Consumer<InventoryClickEvent> getHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            Inventory inventory = inventoryClickEvent.getClickedInventory();
            if(inventory != null) {
                ItemStack itemStack = inventory.getItem(inventoryClickEvent.getSlot());
                if(!ItemStackUtil.isItemNull(itemStack)) {
                    String value = this.getOrDefaultValue(locationDataService, location);
                    value = inventoryClickEvent.isRightClick() ? this.clickPreviousValue(value, inventoryClickEvent.getClick()) : this.clickNextValue(value, inventoryClickEvent.getClick());
                    this.updateLore(itemStack, value);
                    this.setOrClearValue(locationDataService, location, value);
                }
            }
        };
    }

    @Nonnull
    public Consumer<InventoryClickEvent> getUpdateHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            Inventory inventory = inventoryClickEvent.getClickedInventory();
            if(inventory != null) {
                ItemStack itemStack = inventory.getItem(inventoryClickEvent.getSlot());
                if(!ItemStackUtil.isItemNull(itemStack)) {
                    String value = this.getOrDefaultValue(locationDataService, location);
                    this.updateLore(itemStack, value);
                    this.setOrClearValue(locationDataService, location, value);
                }
            }
        };
    }

    @Nonnull
    public Consumer<InventoryClickEvent> getNextHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            Inventory inventory = inventoryClickEvent.getClickedInventory();
            if(inventory != null) {
                ItemStack itemStack = inventory.getItem(inventoryClickEvent.getSlot());
                if(!ItemStackUtil.isItemNull(itemStack)) {
                    String value = this.getOrDefaultValue(locationDataService, location);
                    value = this.clickNextValue(value, inventoryClickEvent.getClick());
                    this.updateLore(itemStack, value);
                    this.setOrClearValue(locationDataService, location, value);
                }
            }
        };
    }

    @Nonnull
    public Consumer<InventoryClickEvent> getNextHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, int slot, @Nonnull SlimefunItem slimefunItem) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            Inventory inventory = inventoryClickEvent.getClickedInventory();
            if(inventory != null) {
                ItemStack itemStack = inventory.getItem(slot);
                if(!ItemStackUtil.isItemNull(itemStack)) {
                    String value = this.getOrDefaultValue(locationDataService, location);
                    value = this.clickNextValue(value, inventoryClickEvent.getClick());
                    this.updateLore(itemStack, value);
                    this.setOrClearValue(locationDataService, location, value);
                }
            }
        };
    }

    @Nonnull
    public Consumer<InventoryClickEvent> getPreviousHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            Inventory inventory = inventoryClickEvent.getClickedInventory();
            if(inventory != null) {
                ItemStack itemStack = inventory.getItem(inventoryClickEvent.getSlot());
                if(!ItemStackUtil.isItemNull(itemStack)) {
                    String value = this.getOrDefaultValue(locationDataService, location);
                    value = this.clickPreviousValue(value, inventoryClickEvent.getClick());
                    this.updateLore(itemStack, value);
                    this.setOrClearValue(locationDataService, location, value);
                }
            }
        };
    }

    @Nonnull
    public Consumer<InventoryClickEvent> getPreviousHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, int slot, @Nonnull SlimefunItem slimefunItem) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            Inventory inventory = inventoryClickEvent.getClickedInventory();
            if(inventory != null) {
                ItemStack itemStack = inventory.getItem(slot);
                if(!ItemStackUtil.isItemNull(itemStack)) {
                    String value = this.getOrDefaultValue(locationDataService, location);
                    value = this.clickPreviousValue(value, inventoryClickEvent.getClick());
                    this.updateLore(itemStack, value);
                    this.setOrClearValue(locationDataService, location, value);
                }
            }
        };
    }

    @Nonnull
    public String clickNextValue(@Nonnull String value, @Nonnull ClickType clickType) {
        return this.nextOrDefaultValue(value);
    }

    @Nonnull
    public String clickPreviousValue(@Nonnull String value, @Nonnull ClickType clickType) {
        return this.previousOrDefaultValue(value);
    }
}
