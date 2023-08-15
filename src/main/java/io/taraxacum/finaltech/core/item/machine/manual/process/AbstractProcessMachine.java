package io.taraxacum.finaltech.core.item.machine.manual.process;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.manual.AbstractManualMachineInventory;
import io.taraxacum.finaltech.core.inventory.manual.ProcessMachineInventory;
import io.taraxacum.finaltech.core.item.machine.manual.AbstractManualMachine;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 */
public abstract class AbstractProcessMachine extends AbstractManualMachine implements RecipeItem {
    public AbstractProcessMachine(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nonnull
    @Override
    protected AbstractManualMachineInventory newMachineInventory() {
        return new ProcessMachineInventory(this);
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if (inventory == null) {
            return;
        }

        if (!inventory.getViewers().isEmpty()) {
            this.getMachineInventory().updateInventory(inventory, locationData.getLocation());
        }
    }

    /**
     * @return whether it can craft
     */
    public abstract boolean canCraft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2);

    /**
     * @return whether successful craft
     */
    public abstract boolean craft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, @Nonnull Inventory inventory, int outputSlot, @Nonnull Player player, @Nonnull Location location);

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
    }
}
