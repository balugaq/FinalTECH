package io.taraxacum.finaltech.core.inventory.unit;

import io.taraxacum.finaltech.core.inventory.AbstractOrdinaryMachineInventory;
import io.taraxacum.finaltech.core.item.machine.AbstractMachine;
import io.taraxacum.libs.plugin.inventory.template.CloseFunctionInventory;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;

public class NullInventory extends AbstractOrdinaryMachineInventory implements CloseFunctionInventory {
    public NullInventory(@Nonnull AbstractMachine machine) {
        super(machine);
    }

    @Nonnull
    @Override
    protected int[] getBorder() {
        return new int[0];
    }

    @Nonnull
    @Override
    protected int[] getInputBorder() {
        return new int[0];
    }

    @Nonnull
    @Override
    protected int[] getOutputBorder() {
        return new int[0];
    }

    @Nonnull
    @Override
    public int[] getInputSlot() {
        return new int[0];
    }

    @Nonnull
    @Override
    public int[] getOutputSlot() {
        return new int[0];
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    protected void initSelf() {

    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {

    }

    @Override
    public void onClose(@Nonnull Player player, @Nonnull Location location, @Nonnull Inventory inventory) {
        player.closeInventory();
    }
}
