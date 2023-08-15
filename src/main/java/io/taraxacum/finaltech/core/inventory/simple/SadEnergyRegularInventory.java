package io.taraxacum.finaltech.core.inventory.simple;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.libs.plugin.inventory.template.OpenFunctionInventory;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public class SadEnergyRegularInventory extends AbstractMachineInventory implements OpenFunctionInventory {
    private final int[] border = new int[] {0, 1, 2, 3, 5 ,6 , 7, 8};
    private final int[] inputBorder = new int[0];
    private final int[] outputBorder = new int[0];

    public final int statusSlot = 4;

    public SadEnergyRegularInventory(@Nonnull SlimefunItem slimefunItem) {
        super(slimefunItem);
    }

    @Nonnull
    @Override
    protected int[] getBorder() {
        return this.border;
    }

    @Nonnull
    @Override
    protected int[] getInputBorder() {
        return this.inputBorder;
    }

    @Nonnull
    @Override
    protected int[] getOutputBorder() {
        return this.outputBorder;
    }

    @Nullable
    @Override
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        return super.onClick(location, slot);
    }

    @Override
    protected void initSelf() {
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public void onOpen(@Nonnull Player player, @Nonnull Location location, @Nonnull Inventory inventory) {
        if (player.isSneaking()) {
            new SadEnergyRegularDetailInventory(location, this.getName()).open(player);
        }
    }
}
