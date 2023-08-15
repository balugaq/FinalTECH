package io.taraxacum.finaltech.core.inventory.clicker;

import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.item.machine.clicker.AbstractClickerMachine;
import io.taraxacum.libs.plugin.inventory.template.OpenFunctionInventory;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;

public abstract class AbstractClickerInventory extends AbstractMachineInventory implements OpenFunctionInventory {
    protected final AbstractClickerMachine clickerMachine;

    protected AbstractClickerInventory(@Nonnull AbstractClickerMachine abstractClickerMachine) {
        super(abstractClickerMachine);
        this.clickerMachine = abstractClickerMachine;
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

    @Override
    public final void onOpen(@Nonnull Player player, @Nonnull Location location, @Nonnull Inventory inventory) {
        Integer count = this.clickerMachine.getLocationCountMap().getOrDefault(location, 0);
        if (count < this.clickerMachine.getCountThreshold()) {
            this.clickerMachine.getLocationCountMap().put(location, ++count);
        } else {
            player.closeInventory();
            // TODO: waring info in console
            return;
        }

        this.openFunction(player, location, inventory);
    }

    protected abstract void openFunction(@Nonnull Player player, @Nonnull Location location, @Nonnull Inventory inventory);
}
