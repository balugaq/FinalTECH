package io.taraxacum.finaltech.core.item.machine.range;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.DigitalItem;
import io.taraxacum.libs.plugin.ld.LocationData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class NormalControllableElectricityShootPile extends AbstractDigitElectricityShootPile {

    public NormalControllableElectricityShootPile(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Override
    protected int getDigit(@Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if (inventory == null) {
            return -1;
        }

        ItemStack itemStack = inventory.getItem(this.getInputSlot()[0]);
        SlimefunItem digitalSlimefunItem = SlimefunItem.getByItem(itemStack);
        if (digitalSlimefunItem instanceof DigitalItem digitalItem) {
            return digitalItem.getDigit();
        } else {
            return -1;
        }
    }
}
