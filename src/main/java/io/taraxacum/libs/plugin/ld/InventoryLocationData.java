package io.taraxacum.libs.plugin.ld;

import io.taraxacum.libs.plugin.inventory.VirtualInventory;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;

public interface InventoryLocationData extends LocationData {

    @Nonnull
    Inventory getRawInventory();

    @Nonnull
    VirtualInventory getInventory();
}
