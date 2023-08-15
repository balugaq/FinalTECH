package io.taraxacum.libs.slimefun.dto;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.ld.LocationDataService;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public class LocationDataIconOption extends LocationDataOption {
    private static final ItemStack ERROR_ICON = ItemStackUtil.newItemStack(Material.RED_STAINED_GLASS_PANE, "ERROR");

    private final Map<String, ItemStack> valueIconMap;

    public LocationDataIconOption(@Nonnull String id, @Nonnull String key, @Nonnull Map<String, ItemStack> valueIconMap) {
        super(id, key, valueIconMap.keySet().stream().toList());
        this.valueIconMap = valueIconMap;
    }

    public LocationDataIconOption(@Nonnull SlimefunItem slimefunItem, @Nonnull String key, @Nonnull Map<String, ItemStack> valueIconMap) {
        this(slimefunItem.getId(), key, valueIconMap);
    }

    @Nonnull
    public ItemStack getOrErrorIcon(@Nullable String value) {
        return this.valueIconMap.getOrDefault(value, ERROR_ICON);
    }

    @Nonnull
    public ItemStack defaultIcon() {
        return valueIconMap.get(this.defaultValue());
    }

    public void checkAndUpdateIcon(@Nonnull Inventory inventory, int slot, @Nonnull LocationDataService locationDataService, @Nonnull Location location) {
        String value = this.getOrDefaultValue(locationDataService, location);
        inventory.setItem(slot, this.getOrErrorIcon(value));
    }

    public void checkAndUpdateIcon(@Nonnull Inventory inventory, int slot, @Nonnull LocationDataService locationDataService, @Nonnull LocationData locationData) {
        String value = this.getOrDefaultValue(locationDataService, locationData);
        inventory.setItem(slot, this.getOrErrorIcon(value));
    }

    @Nonnull
    public Consumer<InventoryClickEvent> getHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            Inventory inventory = inventoryClickEvent.getClickedInventory();
            if(inventory != null) {
                String value = this.getOrDefaultValue(locationDataService, location);
                value = inventoryClickEvent.isRightClick() ? this.clickPreviousValue(value, inventoryClickEvent.getClick()) : this.clickNextValue(value, inventoryClickEvent.getClick());
                inventory.setItem(inventoryClickEvent.getSlot(), this.getOrErrorIcon(value));
                this.setOrClearValue(locationDataService, location, value);
            }
        };
    }

    public Consumer<InventoryClickEvent> getUpdateHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            Inventory inventory = inventoryClickEvent.getClickedInventory();
            if(inventory != null) {
                String value = this.getOrDefaultValue(locationDataService, location);
                inventory.setItem(inventoryClickEvent.getSlot(), this.getOrErrorIcon(value));
                this.setOrClearValue(locationDataService, location, value);
            }
        };
    }

    public Consumer<InventoryClickEvent> getNextHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            Inventory inventory = inventoryClickEvent.getClickedInventory();
            if(inventory != null) {
                String value = this.getOrDefaultValue(locationDataService, location);
                value = this.clickNextValue(value, inventoryClickEvent.getClick());
                inventory.setItem(inventoryClickEvent.getSlot(), this.getOrErrorIcon(value));
                this.setOrClearValue(locationDataService, location, value);
            }
        };
    }

    public Consumer<InventoryClickEvent> getPreviousHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            Inventory inventory = inventoryClickEvent.getClickedInventory();
            if(inventory != null) {
                String value = this.getOrDefaultValue(locationDataService, location);
                value = this.clickPreviousValue(value, inventoryClickEvent.getClick());
                inventory.setItem(inventoryClickEvent.getSlot(), this.getOrErrorIcon(value));
                this.setOrClearValue(locationDataService, location, value);
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
