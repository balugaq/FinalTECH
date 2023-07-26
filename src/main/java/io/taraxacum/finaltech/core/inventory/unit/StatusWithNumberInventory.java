package io.taraxacum.finaltech.core.inventory.unit;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.option.SimpleNumber;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.interfaces.LogicInventory;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public class StatusWithNumberInventory extends AbstractMachineInventory implements LogicInventory {
    private final int[] border = new int[] {0, 1, 2, 6, 7, 8};
    private final int[] inputBorder = new int[0];
    private final int[] outputBorder = new int[0];
    private final int addSlot = 5;
    private final int subSlot = 3;

    public final int statusSlot = 4;

    public StatusWithNumberInventory(@Nonnull SlimefunItem slimefunItem) {
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
        return switch (slot) {
            case addSlot -> SimpleNumber.OPTION_256.getNextHandler(FinalTech.getLocationDataService(), location, this.statusSlot, this.slimefunItem);
            case subSlot -> SimpleNumber.OPTION_256.getPreviousHandler(FinalTech.getLocationDataService(), location, this.statusSlot, this.slimefunItem);
            default -> super.onClick(location, slot);
        };
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(this.statusSlot, SimpleNumber.SIMPLE_NUMBER_ICON);
        this.defaultItemStack.put(this.addSlot, SimpleNumber.SIMPLE_NUMBER_ADD_ICON);
        this.defaultItemStack.put(this.subSlot, SimpleNumber.SIMPLE_NUMBER_SUB_ICON);
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Nonnull
    @Override
    public int[] requestSlots() {
        return new int[0];
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        SimpleNumber.OPTION_256.checkOrSetDefault(FinalTech.getLocationDataService(), location);
        SimpleNumber.OPTION_256.checkAndUpdateIcon(inventory, this.statusSlot, FinalTech.getLocationDataService(), location);
    }

    public void updateNumber(@Nonnull Inventory inventory, @Nonnull LocationData locationData) {
        SimpleNumber.OPTION_256.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
        SimpleNumber.OPTION_256.checkAndUpdateIcon(inventory, this.statusSlot, FinalTech.getLocationDataService(), locationData);
    }
}
