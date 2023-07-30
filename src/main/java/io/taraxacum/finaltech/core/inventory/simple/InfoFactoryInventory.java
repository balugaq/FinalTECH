package io.taraxacum.finaltech.core.inventory.simple;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.core.inventory.AbstractOrdinaryMachineInventory;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Final_ROOT
 */
public class InfoFactoryInventory extends AbstractOrdinaryMachineInventory {
    private final int[] border = new int[] {5, 6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26, 32, 33, 34, 35};
    private final int[] inputBorder = new int[] {4, 13, 22, 31};
    private final int[] outputBorder = new int[0];
    private final int[] inputSlot = new int[] {0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30};
    private final int[] outputSlot = new int[] {0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30};

    public InfoFactoryInventory(@Nonnull SlimefunItem slimefunItem) {
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

    @Override
    protected void initSelf() {

    }

    @Nonnull
    @Override
    public int[] getInputSlot() {
        return this.inputSlot;
    }

    @Nonnull
    @Override
    public int[] getOutputSlot() {
        return this.outputSlot;
    }

    @Override
    public int getSize() {
        return 36;
    }

    @Nonnull
    @Override
    public int[] requestSlots(@Nonnull RequestType requestType, @Nonnull ItemStack itemStack, @Nonnull Inventory inventory, @Nonnull Location location) {
        List<Integer> slotList = new ArrayList<>();
        if (requestType == RequestType.INPUT) {
            for (int slot : this.inputSlot) {
                if (!ItemStackUtil.isItemNull(inventory.getItem(slot))) {
                    slotList.add(slot);
                }
            }
        }

        if (requestType == RequestType.OUTPUT) {
            for (int slot : this.inputSlot) {
                if (ItemStackUtil.isItemNull(inventory.getItem(slot))) {
                    slotList.add(slot);
                }
            }
        }

        return JavaUtil.toArray(JavaUtil.shuffle(slotList));
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {

    }
}
