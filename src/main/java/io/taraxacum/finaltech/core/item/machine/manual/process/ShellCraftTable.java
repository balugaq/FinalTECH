package io.taraxacum.finaltech.core.item.machine.manual.process;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enums.LogSourceType;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShellCraftTable extends AbstractProcessMachine {
    public ShellCraftTable(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Override
    public boolean canCraft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2) {
        if (!ItemStackUtil.isItemNull(itemStack1)) {
            return FinalTechItems.SINGULARITY.verifyItem(itemStack1) || FinalTechItems.SPIROCHETE.verifyItem(itemStack1);
        }

        if (!ItemStackUtil.isItemNull(itemStack2)) {
            return FinalTechItems.SINGULARITY.verifyItem(itemStack2) || FinalTechItems.SPIROCHETE.verifyItem(itemStack2);
        }

        return false;
    }

    @Override
    public boolean craft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, @Nonnull Inventory inventory, int outputSlot, @Nonnull Player player, @Nonnull Location location) {
        if (!ItemStackUtil.isItemNull(itemStack1)) {
            if (FinalTechItems.SINGULARITY.verifyItem(itemStack1) || FinalTechItems.SPIROCHETE.verifyItem(itemStack1)) {
                itemStack1.setAmount(itemStack1.getAmount() - 1);
                ItemStack outputItem = FinalTechItems.SHELL.getValidItem();
                inventory.setItem(outputSlot, outputItem);
                FinalTech.getLogService().subItem(SlimefunItem.getByItem(itemStack1).getId(), 1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, this.getAddon().getJavaPlugin());
                FinalTech.getLogService().addItem(FinalTechItems.SHELL.getId(), 1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, this.getAddon().getJavaPlugin());
                return true;
            }
        }
        if (!ItemStackUtil.isItemNull(itemStack2)) {
            if (FinalTechItems.SINGULARITY.verifyItem(itemStack2) || FinalTechItems.SPIROCHETE.verifyItem(itemStack2)) {
                itemStack2.setAmount(itemStack2.getAmount() - 1);
                ItemStack outputItem = FinalTechItems.SHELL.getValidItem();
                inventory.setItem(outputSlot, outputItem);
                FinalTech.getLogService().subItem(SlimefunItem.getByItem(itemStack2).getId(), 1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, this.getAddon().getJavaPlugin());
                FinalTech.getLogService().addItem(FinalTechItems.SHELL.getId(), 1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, this.getAddon().getJavaPlugin());
                return true;
            }
        }
        return false;
    }
}
