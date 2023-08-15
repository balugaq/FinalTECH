package io.taraxacum.finaltech.core.inventory.limit.lock;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.inventory.limit.AbstractLimitMachineInventory;
import io.taraxacum.finaltech.core.option.MachineRecipeLock;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class AbstractLockMachineInventory extends AbstractLimitMachineInventory {
    public final int recipeLockSlot = 4;

    protected AbstractLockMachineInventory(@Nonnull SlimefunItem slimefunItem) {
        super(slimefunItem);
    }

    @Nullable
    @Override
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        return slot == this.getRecipeLockSlot() ? MachineRecipeLock.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem) : super.onClick(location, slot);
    }

    @Override
    protected void initSelf() {
        super.initSelf();
        this.defaultItemStack.put(this.getRecipeLockSlot(), MachineRecipeLock.ICON);
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        super.updateInventory(inventory, location);

        MachineRecipeLock.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), location);
        String recipeLock = MachineRecipeLock.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), location);
        ItemStack itemStack = inventory.getItem(this.getRecipeLockSlot());
        if (!ItemStackUtil.isItemNull(itemStack)) {
            MachineRecipeLock.OPTION.updateLore(itemStack, recipeLock, this.slimefunItem);
        }
    }

    public int getRecipeLockSlot() {
        return this.recipeLockSlot;
    }

    public void updateMachineRecipeLock(@Nonnull Inventory inventory, @Nonnull LocationData locationData) {
        MachineRecipeLock.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
        String recipeLock = MachineRecipeLock.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
        ItemStack itemStack = inventory.getItem(this.getRecipeLockSlot());
        if (!ItemStackUtil.isItemNull(itemStack)) {
            MachineRecipeLock.OPTION.updateLore(itemStack, recipeLock, this.slimefunItem);
        }
    }
}
