package io.taraxacum.finaltech.core.option;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.item.machine.AbstractMachine;
import io.taraxacum.libs.plugin.ld.LocationDataService;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.dto.LocationDataLoreOption;
import io.taraxacum.libs.slimefun.dto.LocationDataOption;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public final class MachineMaxStack {
    public static final String KEY = "mms";

    public static final ItemStack ICON = new CustomItemStack(Material.CHEST, FinalTech.getLanguageString("option", "MACHINE_MAX_STACK", "icon", "name"));

    public static final LocationDataLoreOption OPTION = new LocationDataLoreOption(LocationDataOption.CARGO_ID, KEY, new LinkedHashMap<>() {{
        this.put("0", FinalTech.getLanguageStringList("option", "MACHINE_MAX_STACK", "0", "lore"));
        for (int i = 1; i <= 54; i++) {
            this.put(String.valueOf(i), FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("option", "MACHINE_MAX_STACK", "value", "lore"), String.valueOf(i)));
        }
    }}) {
        @Override
        public void updateLore(@Nonnull ItemStack itemStack, @Nullable String value) {
            if (Objects.equals(this.defaultValue(), value)) {
                itemStack.setType(Material.CHEST);
                itemStack.setAmount(1);
            } else if (value != null) {
                itemStack.setType(Material.HOPPER);
                itemStack.setAmount(Integer.parseInt(value));
            } else {
                return;
            }
            super.updateLore(itemStack, value);
        }

        @Nonnull
        @Override
        public String nextOrDefaultValue(@Nullable String value) {
            return this.defaultValue();
        }

        @Nonnull
        @Override
        public String previousOrDefaultValue(@Nullable String value) {
            return this.defaultValue();
        }

        @Nonnull
        @Override
        public Consumer<InventoryClickEvent> getHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
            if (slimefunItem instanceof AbstractMachine abstractMachine) {
                return inventoryClickEvent -> {
                    inventoryClickEvent.setCancelled(true);
                    Inventory inventory = inventoryClickEvent.getClickedInventory();
                    if (inventory != null) {
                        ItemStack itemStack = inventory.getItem(inventoryClickEvent.getSlot());
                        if (!ItemStackUtil.isItemNull(itemStack)) {
                            int quantity = Integer.parseInt(this.getOrDefaultValue(locationDataService, location));
                            if (inventoryClickEvent.getClick().isShiftClick()) {
                                quantity = 0;
                            } else {
                                if (inventoryClickEvent.getClick().isRightClick()) {
                                    quantity = (quantity + abstractMachine.getInputSlot().length) % (abstractMachine.getInputSlot().length + 1);
                                } else {
                                    quantity = (quantity + 1) % (abstractMachine.getInputSlot().length + 1);
                                }
                            }
                            this.setOrClearValue(FinalTech.getLocationDataService(), location, String.valueOf(quantity));
                            this.updateLore(itemStack, String.valueOf(quantity));
                        }
                    }
                };
            } else {
                return super.getHandler(locationDataService, location, slimefunItem);
            }
        }
    };
}
