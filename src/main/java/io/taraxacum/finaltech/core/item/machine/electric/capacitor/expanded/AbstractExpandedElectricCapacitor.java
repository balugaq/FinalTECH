package io.taraxacum.finaltech.core.item.machine.electric.capacitor.expanded;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.item.machine.electric.capacitor.AbstractElectricCapacitor;
import io.taraxacum.finaltech.core.listener.ExpandedElectricCapacitorEnergyListener;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.slimefun.util.EnergyUtil;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public abstract class AbstractExpandedElectricCapacitor extends AbstractElectricCapacitor implements RecipeItem, MenuUpdater {
    protected static boolean registeredListener = false;
    protected final String key = "s";

    public AbstractExpandedElectricCapacitor(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Override
    public void register(@Nonnull SlimefunAddon addon) {
        super.register(addon);
        if (!this.isDisabled() && !registeredListener) {
            PluginManager pluginManager = addon.getJavaPlugin().getServer().getPluginManager();
            pluginManager.registerEvents(new ExpandedElectricCapacitorEnergyListener(), addon.getJavaPlugin());
            registeredListener = true;
        }
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        String energyStr = EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData);
        String energyStackStr = JavaUtil.getFirstNotNull(FinalTech.getLocationDataService().getLocationData(locationData, this.key), StringNumberUtil.ZERO);
        long energy = Integer.parseInt(energyStr);
        long energyStack = Long.parseLong(energyStackStr);

        long allEnergy = this.calEnergy((int) energy, (int) energyStack);
        allEnergy += energyStack;

        this.setEnergy(locationData, allEnergy);

        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if (inventory != null && !inventory.getViewers().isEmpty()) {
            this.updateInv(inventory, this.statusSlot, this, String.valueOf(energy), energyStackStr);
        }
    }

    @Override
    public abstract int getCapacity();

    public abstract int getMaxStack();

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.valueOf((this.getCapacity() / 2)),
                String.valueOf(this.getMaxStack()),
                ConstantTableUtil.SLIMEFUN_TICK_INTERVAL);
    }

    public int getStack(@Nonnull LocationData locationData) {
        return Integer.parseInt(JavaUtil.getFirstNotNull(FinalTech.getLocationDataService().getLocationData(locationData, this.key), StringNumberUtil.ZERO));
    }

    public long getMaxEnergy() {
        return (long) this.getCapacity() * this.getMaxStack() / 2 + this.getCapacity();
    }

    public long calEnergy(int energy, int stack) {
        return (long) this.getCapacity() * stack / 2 + energy;
    }

    public void setEnergy(@Nonnull LocationData locationData, long energy) {
        long stack = (energy - this.getCapacity() / 4) / (this.getCapacity() / 2);
        stack = Math.min(stack, this.getMaxStack());
        stack = Math.max(stack, 0);

        long lastEnergy = energy - stack * this.getCapacity() / 2;
        lastEnergy = Math.min(lastEnergy, this.getCapacity());
        lastEnergy = Math.max(lastEnergy, 0);

        EnergyUtil.setCharge(FinalTech.getLocationDataService(), locationData, String.valueOf(lastEnergy));
        FinalTech.getLocationDataService().setLocationData(locationData, this.key, String.valueOf(stack));
    }
}
