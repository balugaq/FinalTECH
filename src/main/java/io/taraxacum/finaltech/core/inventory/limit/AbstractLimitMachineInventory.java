package io.taraxacum.finaltech.core.inventory.limit;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.option.MachineMaxStack;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.inventory.template.LogicInventory;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class AbstractLimitMachineInventory extends AbstractMachineInventory implements LogicInventory {
    private final int machineMaxStackSlot = 13;

    protected AbstractLimitMachineInventory(@Nonnull SlimefunItem slimefunItem) {
        super(slimefunItem);
    }

    @Nullable
    @Override
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        return slot == this.getMachineMaxStackSlot() ? MachineMaxStack.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem) : super.onClick(location, slot);
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(this.getMachineMaxStackSlot(), MachineMaxStack.ICON);
    }

    @Nonnull
    @Override
    public int[] requestSlots() {
        return new int[0];
    }

    @Nonnull
    @Override
    public int[] requestSlots(@Nonnull RequestType requestType) {
        return switch (requestType) {
            case OUTPUT -> this.getOutputSlot();
            case INPUT -> this.getInputSlot();
            default -> new int[0];
        };
    }

    @Nonnull
    @Override
    public int[] requestSlots(@Nonnull RequestType requestType, @Nonnull ItemStack itemStack, @Nonnull Inventory inventory, @Nonnull Location location) {
        if (RequestType.OUTPUT.equals(requestType)) {
            return this.getOutputSlot();
        } else if(!RequestType.INPUT.equals(requestType)) {
            return new int[0];
        }

        ItemStack machineMaxStackIcon = inventory.getItem(this.getMachineMaxStackSlot());
        if(ItemStackUtil.isItemNull(machineMaxStackIcon)) {
            return new int[0];
        }

        int full = 0;
        if (Material.CHEST.equals(machineMaxStackIcon.getType())) {
            return this.getInputSlot();
        }

        ArrayList<Integer> itemList = new ArrayList<>();
        ArrayList<Integer> nullList = new ArrayList<>();
        ItemStackWrapper itemStackWrapper = ItemStackWrapper.wrap(itemStack);
        int inputLimit = machineMaxStackIcon.getAmount();
        for (int slot : this.getInputSlot()) {
            ItemStack existedItem = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(existedItem)) {
                nullList.add(slot);
            } else if (ItemStackUtil.isItemSimilar(itemStackWrapper, existedItem)) {
                if (existedItem.getAmount() < existedItem.getMaxStackSize()) {
                    itemList.add(slot);
                } else {
                    full++;
                }
                if (itemList.size() + full >= inputLimit) {
                    break;
                }
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
        MachineMaxStack.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), location);
        String quantity = MachineMaxStack.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), location);
        ItemStack itemStack = inventory.getItem(this.getMachineMaxStackSlot());
        if (!ItemStackUtil.isItemNull(itemStack)) {
            MachineMaxStack.OPTION.updateLore(itemStack, quantity);
        }
    }

    public int getMachineMaxStackSlot() {
        return this.machineMaxStackSlot;
    }

    protected abstract int[] getInputSlot();

    protected abstract int[] getOutputSlot();

    public void updateMachineMaxStack(@Nonnull Inventory inventory, @Nonnull LocationData locationData) {
        MachineMaxStack.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
        String quantity = MachineMaxStack.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
        ItemStack itemStack = inventory.getItem(this.getMachineMaxStackSlot());
        if (!ItemStackUtil.isItemNull(itemStack)) {
            MachineMaxStack.OPTION.updateLore(itemStack, quantity);
        }
    }
}
