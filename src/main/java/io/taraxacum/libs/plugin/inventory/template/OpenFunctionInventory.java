package io.taraxacum.libs.plugin.inventory.template;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;

/**
 * This inventory template may do some function while being opened.
 * @author Final_ROOT
 */
public interface OpenFunctionInventory extends InventoryTemplate {

    void onOpen(@Nonnull Player player, @Nonnull Location location, @Nonnull Inventory inventory);
}
