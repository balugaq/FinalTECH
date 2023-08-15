package io.taraxacum.finaltech.core.item.machine.range.point.face;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.LocationMachine;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.util.EnergyUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class OverloadedChargeBase extends AbstractChargeBase implements RecipeItem, MenuUpdater, LocationMachine {
    private final double efficiency = ConfigUtil.getOrDefaultItemSetting(0.5, this, "efficiency");

    public OverloadedChargeBase(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    protected void doCharge(@Nonnull Block block, @Nonnull LocationData locationData) {
        int storedEnergy = 0;
        int chargeEnergy = 0;

        if (LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), locationData) instanceof EnergyNetComponent energyNetComponent
                && !JavaUtil.matchOnce(energyNetComponent.getEnergyComponentType(), EnergyNetComponentType.CAPACITOR, EnergyNetComponentType.GENERATOR)) {
            int capacity = energyNetComponent.getCapacity();
            storedEnergy = Integer.parseInt(EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData));
            chargeEnergy = Math.min(capacity - storedEnergy, (int)(storedEnergy * this.efficiency));
            if (chargeEnergy > 0) {
                storedEnergy += chargeEnergy;
                EnergyUtil.setCharge(FinalTech.getLocationDataService(), locationData, String.valueOf(storedEnergy));
            }
        }

        LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(block.getLocation());
        if(tempLocationData != null) {
            Inventory inventory = FinalTech.getLocationDataService().getRawInventory(tempLocationData);
            if (inventory != null && !inventory.getViewers().isEmpty()) {
                this.updateInv(inventory, this.statusSlot, this,
                        String.valueOf(storedEnergy),
                        String.valueOf(chargeEnergy));
            }
        }
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.valueOf(this.efficiency));
    }
}
