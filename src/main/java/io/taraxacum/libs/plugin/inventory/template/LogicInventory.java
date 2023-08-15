package io.taraxacum.libs.plugin.inventory.template;

import io.taraxacum.libs.plugin.inventory.template.InventoryTemplate;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * An inventory template that may do some special functions.
 * @author Final_ROOT
 */
public interface LogicInventory extends InventoryTemplate {

    /**
     * While being requested, it will tell other the slots that can be interacted.
     */
    @Nonnull
    int[] requestSlots();

    @Nonnull
    default int[] requestSlots(@Nonnull RequestType requestType) {
        return this.requestSlots();
    }

    @Nonnull
    default int[] requestSlots(@Nonnull RequestType requestType, @Nonnull ItemStack itemStack, @Nonnull Inventory inventory, @Nonnull Location location) {
        return this.requestSlots(requestType);
    }

    /**
     * This may be used to update the inventory according to the data in the location.
     */
    void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location);

    /**
     * How others request the slot of the inventory template.
     */
    enum RequestType {
        INPUT,
        OUTPUT,
    }
}
