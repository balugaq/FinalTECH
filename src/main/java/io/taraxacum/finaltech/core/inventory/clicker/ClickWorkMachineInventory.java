package io.taraxacum.finaltech.core.inventory.clicker;

import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.item.machine.clicker.AbstractClickerMachine;
import io.taraxacum.finaltech.core.option.ForceClose;
import io.taraxacum.libs.plugin.inventory.template.LogicInventory;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ClickWorkMachineInventory extends AbstractClickerInventory implements LogicInventory {
    private final int[] border = new int[] {0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 13, 16, 17};
    private final int[] inputBorder = new int[] {11};
    private final int[] outputBorder = new int[] {15};
    private final int[] inputSlot = new int[] {12};
    private final int[] outputSlot = new int[] {14};

    private final int FORCE_CLOSE_SLOT = 4;

    public ClickWorkMachineInventory(@Nonnull AbstractClickerMachine abstractClickerMachine) {
        super(abstractClickerMachine);
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
            case FORCE_CLOSE_SLOT -> ForceClose.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            default -> super.onClick(location, slot);
        };
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(this.FORCE_CLOSE_SLOT, ForceClose.OPTION.defaultIcon());
    }

    @Override
    protected void openFunction(@Nonnull Player player, @Nonnull Location location, @Nonnull Inventory inventory) {
        ItemStack inputItem = inventory.getItem(inputSlot[0]);
        ItemStack outputItem = inventory.getItem(outputSlot[0]);
        if(ItemStackUtil.isItemNull(inputItem) || !ItemStackUtil.isItemNull(outputItem)) {
            ItemStack itemStack = inventory.getItem(FORCE_CLOSE_SLOT);
            if(ForceClose.TRUE_ICON.getType().equals(itemStack.getType())) {
                InventoryUtil.closeInv(inventory);
            }
        } else {
            outputItem = ItemStackUtil.cloneItem(inputItem);
            int amount = player.isSneaking() ? Math.min(inputItem.getAmount(), 64) : Math.min(inputItem.getAmount(), 1);

            outputItem.setAmount(amount);
            inputItem.setAmount(inputItem.getAmount() - amount);

            inventory.setItem(outputSlot[0], outputItem);
            InventoryUtil.closeInv(inventory);

            Block block = location.getBlock();
            JavaPlugin javaPlugin = this.slimefunItem.getAddon().getJavaPlugin();
            javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, block));
        }
    }

    @Override
    public int getSize() {
        return 18;
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
        ForceClose.OPTION.checkAndUpdateIcon(inventory, this.FORCE_CLOSE_SLOT, FinalTech.getLocationDataService(), location);
    }
}
