package io.taraxacum.finaltech.core.item.machine.electric;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.unit.StatusInventory;
import io.taraxacum.finaltech.setup.FinalTechItemStacks;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import io.taraxacum.libs.slimefun.util.EnergyUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 */
public class VariableWireCapacitor extends AbstractElectricMachine implements RecipeItem, MenuUpdater {
    private final int capacity = ConfigUtil.getOrDefaultItemSetting(65536, this, "capacity");
    private int statusSlot;

    public VariableWireCapacitor(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        StatusInventory statusInventory = new StatusInventory(this);
        this.statusSlot = statusInventory.statusSlot;
        return statusInventory;
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if (inventory == null) {
            return;
        }

        Location location = block.getLocation();
        String charge = EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData);
        if (inventory.getViewers().isEmpty()) {
            if (StringNumberUtil.ZERO.equals(charge)) {
                JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
                FinalTech.getLocationDataService().clearLocationData(location);
                Runnable runnable = () -> {
                    if(FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                        slimefunLocationDataService.getOrCreateEmptyLocationData(location, FinalTechItems.VARIABLE_WIRE_RESISTANCE.getId());
                        Slimefun.getNetworkManager().updateAllNetworks(location);
                        javaPlugin.getServer().getScheduler().runTaskLater(javaPlugin, () -> {
                            LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(location);
                            if(!location.getBlock().getType().isAir()
                                    && tempLocationData != null
                                    && FinalTechItems.VARIABLE_WIRE_RESISTANCE.getId().equals(LocationDataUtil.getId(FinalTech.getLocationDataService(), tempLocationData))) {
                                block.setType(FinalTechItemStacks.VARIABLE_WIRE_RESISTANCE.getType());
                            }
                        }, 0);
                    }
                };

                javaPlugin.getServer().getScheduler().runTask(javaPlugin, () -> BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(this.getId()), runnable, location));
            }
        } else {
            this.updateInv(inventory, this.statusSlot, this, charge);
        }
    }

    @Nonnull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CAPACITOR;
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.valueOf(this.capacity));
    }
}
