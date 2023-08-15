package io.taraxacum.finaltech.core.item.machine.range.line.pile;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.util.EnergyUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class NormalElectricityShootPile extends AbstractElectricityShootPile {
    private final int range = ConfigUtil.getOrDefaultItemSetting(16, this, "range");

    public NormalElectricityShootPile(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Override
    public int getRange() {
        return this.range;
    }

    @Nonnull
    @Override
    protected RangeFunction doFunction(@Nonnull Summary summary) {
        return location -> {
            if (summary.getCapacitorEnergy() <= 0 || !location.getChunk().isLoaded()) {
                return -1;
            }

            LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
            if (locationData != null
                    && !this.notAllowedId.contains(LocationDataUtil.getId(FinalTech.getLocationDataService(), locationData))
                    && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), locationData) instanceof EnergyNetComponent energyNetComponent
                    && !EnergyNetComponentType.NONE.equals(energyNetComponent.getEnergyComponentType())) {
                int componentCapacity = energyNetComponent.getCapacity();
                if(componentCapacity > 0) {
                    int componentEnergy = Integer.parseInt(EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData));
                    if (componentEnergy < componentCapacity) {
                        int transferEnergy = Math.min(summary.getCapacitorEnergy(), componentCapacity - componentEnergy);
                        if(transferEnergy > 0) {
                            EnergyUtil.setCharge(FinalTech.getLocationDataService(), locationData, String.valueOf(componentEnergy + transferEnergy));
                            summary.setCapacitorEnergy(summary.getCapacitorEnergy() - transferEnergy);
                            summary.setEnergyCharge(StringNumberUtil.add(summary.getEnergyCharge(), String.valueOf(transferEnergy)));
                            if(summary.isDrawParticle()) {
                                final Location finalLocation = location.clone();
                                JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
                                javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, finalLocation.getBlock()));
                            }
                            return -1;
                        }
                    }
                    return 1;
                }
            }
            return 0;
        };
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.valueOf(this.range));
    }
}
