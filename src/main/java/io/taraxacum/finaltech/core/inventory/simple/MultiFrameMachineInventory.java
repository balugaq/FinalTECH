package io.taraxacum.finaltech.core.inventory.simple;

import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.core.inventory.AbstractOrdinaryMachineInventory;
import io.taraxacum.finaltech.core.item.machine.AbstractMachine;
import io.taraxacum.libs.plugin.is.ItemWrapper;
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
public class MultiFrameMachineInventory extends AbstractOrdinaryMachineInventory {
    private final int[] border = new int[0];
    private final int[] inputBorder = new int[] {3, 12, 21, 30, 39, 48};
    private final int[] outputBorder = new int[] {5, 14, 23, 32, 41, 50};
    private final int[] inputSlot = new int[] {0, 1, 2, 9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38, 45, 46, 47};
    private final int[] outputSlot = new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26, 33, 34, 35, 42, 43, 44, 51, 52, 53};

    public final int[] machineSlot = new int[] {4, 13, 22, 31, 40, 49};
    public final int[][][] workInputSlot = new int[][][] {
            new int[][] {new int[] {0, 1, 2}, new int[] {0, 1, 2, 9, 10, 11}, new int[] {0, 1, 2, 9, 10, 11, 18, 19, 20}, new int[] {0, 1, 2, 9, 10, 11, 18, 19, 20, 27, 28, 29}, new int[] {0, 1, 2, 9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38}, new int[] {0, 1, 2, 9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38, 45, 46, 47}},
            new int[][] {new int[] {0, 1, 2, 9, 10, 11}, new int[] {9, 10, 11}, new int[] {9, 10, 11, 18, 19, 20}, new int[] {9, 10, 11, 18, 19, 20, 27, 28, 29}, new int[] {9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38}, new int[] {9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38, 45, 46, 47}},
            new int[][] {new int[] {0, 1, 2, 9, 10, 11, 18, 19, 20}, new int[] {9, 10, 11, 18, 19, 20}, new int[] {18, 19, 20}, new int[] {18, 19, 20, 27, 28, 29}, new int[] {18, 19, 20, 27, 28, 29, 36, 37, 38}, new int[] {18, 19, 20, 27, 28, 29, 36, 37, 38, 45, 46, 47}},
            new int[][] {new int[] {0, 1, 2, 9, 10, 11, 18, 19, 20, 27, 28, 29}, new int[] {9, 10, 11, 18, 19, 20, 27, 28, 29}, new int[] {18, 19, 20, 27, 28, 29}, new int[] {27, 28, 29}, new int[] {27, 28, 29, 36, 37, 38}, new int[] {27, 28, 29, 36, 37, 38, 45, 46, 47}},
            new int[][] {new int[] {0, 1, 2, 9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38}, new int[] {9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38}, new int[] {18, 19, 20, 27, 28, 29, 36, 37, 38}, new int[] {27, 28, 29, 36, 37, 38}, new int[] {36, 37, 38}, new int[] {36, 37, 38, 45, 46, 47}},
            new int[][] {new int[] {0, 1, 2, 9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38, 45, 46, 47}, new int[] {9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38, 45, 46, 47}, new int[] {18, 19, 20, 27, 28, 29, 36, 37, 38, 45, 46, 47}, new int[] {27, 28, 29, 36, 37, 38, 45, 46, 47}, new int[] {36, 37, 38, 45, 46, 47}, new int[] {45, 46, 47}}
    };
    public final int[][][] workOutputSlot = new int[][][] {
            new int[][] {new int[] {6, 7, 8}, new int[] {6, 7, 8, 15, 16, 17}, new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26}, new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26, 33, 34, 35}, new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26, 33, 34, 35, 42, 43, 44}, new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26, 33, 34, 35, 42, 43, 44, 51, 52, 53}},
            new int[][] {new int[] {6, 7, 8, 15, 16, 17}, new int[] {15, 16, 17}, new int[] {15, 16, 17, 24, 25, 26}, new int[] {15, 16, 17, 24, 25, 26, 33, 34, 35}, new int[] {15, 16, 17, 24, 25, 26, 33, 34, 35, 42, 43, 44}, new int[] {15, 16, 17, 24, 25, 26, 33, 34, 35, 42, 43, 44, 51, 52, 53}},
            new int[][] {new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26}, new int[] {15, 16, 17, 24, 25, 26}, new int[] {24, 25, 26}, new int[] {24, 25, 26, 33, 34, 35}, new int[] {24, 25, 26, 33, 34, 35, 42, 43, 44}, new int[] {24, 25, 26, 33, 34, 35, 42, 43, 44, 51, 52, 53}},
            new int[][] {new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26, 33, 34, 35}, new int[] {15, 16, 17, 24, 25, 26, 33, 34, 35}, new int[] {24, 25, 26, 33, 34, 35}, new int[] {33, 34, 35}, new int[] {33, 34, 35, 42, 43, 44}, new int[] {33, 34, 35, 42, 43, 44, 51, 52, 53}},
            new int[][] {new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26, 33, 34, 35, 42, 43, 44}, new int[] {15, 16, 17, 24, 25, 26, 33, 34, 35, 42, 43, 44}, new int[] {24, 25, 26, 33, 34, 35, 42, 43, 44}, new int[] {33, 34, 35, 42, 43, 44}, new int[] {42, 43, 44}, new int[] {42, 43, 44, 51, 52, 53}},
            new int[][] {new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26, 33, 34, 35, 42, 43, 44, 51, 52, 53}, new int[] {15, 16, 17, 24, 25, 26, 33, 34, 35, 42, 43, 44, 51, 52, 53}, new int[] {24, 25, 26, 33, 34, 35, 42, 43, 44, 51, 52, 53}, new int[] {33, 34, 35, 42, 43, 44, 51, 52, 53}, new int[] {42, 43, 44, 51, 52, 53}, new int[] {51, 52, 53}}
    };

    public MultiFrameMachineInventory(@Nonnull AbstractMachine machine) {
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
    public int[] requestSlots(@Nonnull RequestType requestType, @Nonnull ItemStack itemStack, @Nonnull Inventory inventory, @Nonnull Location location) {
        if (ItemStackUtil.isItemNull(itemStack)) {
            return this.requestSlots(requestType);
        }

        if (!JavaUtil.matchOnce(requestType, RequestType.INPUT, RequestType.OUTPUT)) {
           return new int[0];
        }

        List<Integer> result = new ArrayList<>();
        int[][][] workSlot = switch (requestType) {
            case INPUT -> this.workInputSlot;
            case OUTPUT -> this.workOutputSlot;
        };

        int point = 0;
        ItemWrapper itemWrapper = new ItemWrapper(itemStack);

        for(int i = 0; i < this.machineSlot.length; i++) {
            ItemStack machineItem = inventory.getItem(this.machineSlot[i]);
            if(!ItemStackUtil.isItemNull(machineItem)) {
                boolean add = true;
                for(int slot : workSlot[point][i]) {
                    ItemStack existedItem = inventory.getItem(slot);
                    if(ItemStackUtil.isItemSimilar(existedItem, itemWrapper)) {
                        add = false;
                    }
                }
                if(add) {
                    for(int slot : workSlot[point][i]) {
                        result.add(slot);
                    }
                }

                point = i + 1;
            }
        }

        return JavaUtil.toArray(result);
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
        return 54;
    }

    @Override
    protected void initSelf() {

    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {

    }
}
