package io.taraxacum.finaltech.core.inventory.manual;

import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.item.machine.manual.process.AbstractProcessMachine;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.libs.plugin.interfaces.LogicInventory;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public class ProcessMachineInventory extends AbstractManualMachineInventory implements LogicInventory {
    private final int[] border = new int[] {3, 4, 5, 12, 14, 21, 22, 23, 27, 28, 29, 33, 34, 35, 36, 37, 38, 42, 43, 44, 45, 46, 47, 51, 52, 53};
    private final int[] inputBorder = new int[] {0, 1, 2, 6, 7, 8, 9, 11, 15, 17, 18, 19, 20, 24, 25, 26};
    private final int[] outputBorder = new int[] {30, 31, 32, 39, 41, 48, 49, 50};
    private final int[] inputSlot = new int[] {10, 16};
    private final int[] outputSlot = new int[] {40};

    public final int craftSlot = 13;

    private final ItemStack errorIcon = ItemStackUtil.newItemStack(Material.RED_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", this.getId(), "error-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "error-icon", "lore"));

    private final ItemStack successIcon = ItemStackUtil.newItemStack(Material.GREEN_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", this.getId(), "success-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "success-icon", "lore"));

    private final AbstractProcessMachine abstractProcessMachine;

    public ProcessMachineInventory(@Nonnull AbstractProcessMachine abstractProcessMachine) {
        super(abstractProcessMachine);
        this.abstractProcessMachine = abstractProcessMachine;
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
        return slot == this.craftSlot ? inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            ProcessMachineInventory.this.doFunction(inventoryClickEvent.getInventory(), (Player) inventoryClickEvent.getWhoClicked(), location);
        } : super.onClick(location, slot);
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(this.craftSlot, this.errorIcon);
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
            case INPUT -> this.inputSlot;
            case OUTPUT -> this.outputSlot;
            default -> new int[0];
        };
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        if (!ItemStackUtil.isItemNull(inventory.getItem(this.outputSlot[0]))) {
            inventory.setItem(this.craftSlot, this.errorIcon);
            return;
        }

        ItemStack inputItem1 = inventory.getItem(this.inputSlot[0]);
        ItemStack inputItem2 = inventory.getItem(this.inputSlot[1]);

        if (this.abstractProcessMachine.canCraft(inputItem1, inputItem2)) {
            inventory.setItem(this.craftSlot, this.successIcon);
        } else {
            inventory.setItem(this.craftSlot, this.errorIcon);
        }
    }

    private void doFunction(@Nonnull Inventory inventory, @Nonnull Player player, @Nonnull Location location) {
        if (!ItemStackUtil.isItemNull(inventory.getItem(this.outputSlot[0]))) {
            inventory.setItem(this.craftSlot, this.errorIcon);
            return;
        }

        ItemStack inputItem1 = inventory.getItem(this.inputSlot[0]);
        ItemStack inputItem2 = inventory.getItem(this.inputSlot[1]);
        if (ItemStackUtil.isItemNull(inputItem1) && ItemStackUtil.isItemNull(inputItem2)) {
            return;
        }

        if (this.abstractProcessMachine.canCraft(inputItem1, inputItem2)) {
            if (this.abstractProcessMachine.craft(inputItem1, inputItem2, inventory, this.outputSlot[0], player, location)) {
                inventory.setItem(this.craftSlot, this.successIcon);
            } else {
                inventory.setItem(this.craftSlot, this.errorIcon);
            }
        }
    }
}
