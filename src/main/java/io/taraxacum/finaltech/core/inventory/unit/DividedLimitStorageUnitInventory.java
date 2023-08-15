package io.taraxacum.finaltech.core.inventory.unit;

import io.taraxacum.finaltech.core.inventory.AbstractOrdinaryMachineInventory;
import io.taraxacum.finaltech.core.item.machine.AbstractMachine;
import io.taraxacum.libs.plugin.is.ItemWrapper;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * @author Final_ROOT
 */
public class DividedLimitStorageUnitInventory extends AbstractOrdinaryMachineInventory {
    private final int[] border = new int[0];
    private final int[] inputBorder = new int[0];
    private final int[] outputBorder = new int[0];
    private final int[] inputSlot = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
    private final int[] outputSlot = new int[] {27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};

    public DividedLimitStorageUnitInventory(@Nonnull AbstractMachine machine) {
        super(machine);
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

    @Nonnull
    @Override
    public int[] requestSlots(@Nonnull RequestType requestType, @Nonnull ItemStack itemStack, @Nonnull Inventory inventory, @Nonnull Location location) {
        if (RequestType.OUTPUT.equals(requestType)) {
            return this.outputSlot;
        } else if (!RequestType.INPUT.equals(requestType)) {
            return new int[0];
        }

        int full = 0;
        int inputLimit = 1;

        ArrayList<Integer> itemList = new ArrayList<>();
        ArrayList<Integer> nullList = new ArrayList<>();
        ItemWrapper itemWrapper = new ItemWrapper(itemStack);
        for (int slot : this.getInputSlot()) {
            ItemStack existedItem = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(existedItem)) {
                nullList.add(slot);
            } else if (ItemStackUtil.isItemSimilar(itemWrapper, existedItem)) {
                if (existedItem.getAmount() < existedItem.getMaxStackSize()) {
                    itemList.add(slot);
                } else {
                    full++;
                }
                break;
            }
        }

        int[] slots = new int[Math.max(inputLimit - full, 0)];
        int i;
        for (i = 0; i < itemList.size() && i < slots.length; i++) {
            slots[i] = itemList.get(i);
        }
        for (int j = 0; j < nullList.size() && j < slots.length - i; j++) {
            slots[i + j] = nullList.get(j);
        }
        return slots;
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {

    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    protected void initSelf() {

    }
}
