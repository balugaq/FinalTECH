package io.taraxacum.finaltech.core.listener;

import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.event.EnergyDepositEvent;
import io.taraxacum.finaltech.core.event.EnergyWithdrawEvent;
import io.taraxacum.finaltech.core.item.machine.electric.capacitor.expanded.AbstractExpandedElectricCapacitor;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Final_ROOT
 */
public class ExpandedElectricCapacitorEnergyListener implements Listener {

    @EventHandler
    public void onEnergyDeposit(EnergyDepositEvent energyDepositEvent) {
        Location location = energyDepositEvent.getLocation();
        LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
        if (locationData != null
                && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), locationData) instanceof AbstractExpandedElectricCapacitor expandedElectricCapacitor) {
            int energy = expandedElectricCapacitor.getCharge(location);
            int stack = expandedElectricCapacitor.getStack(locationData);

            long nowEnergy = expandedElectricCapacitor.calEnergy(energy, stack);
            long availableEnergy = expandedElectricCapacitor.getMaxEnergy() - nowEnergy;

            if (availableEnergy > 0) {
                String depositEnergy = energyDepositEvent.getEnergy();
                String transferEnergy = StringNumberUtil.min(depositEnergy, String.valueOf(availableEnergy));

                energyDepositEvent.setEnergy(StringNumberUtil.sub(depositEnergy, transferEnergy));
                nowEnergy += Long.parseLong(transferEnergy);
                expandedElectricCapacitor.setEnergy(locationData, nowEnergy);
            }
        }
    }

    @EventHandler
    public void onEnergyWithdraw(EnergyWithdrawEvent energyWithdrawEvent) {
        Location location = energyWithdrawEvent.getLocation();
        LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
        if(locationData != null && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), locationData) instanceof AbstractExpandedElectricCapacitor expandedElectricCapacitor) {
            int energy = expandedElectricCapacitor.getCharge(location);
            int stack = expandedElectricCapacitor.getStack(locationData);

            long nowEnergy = expandedElectricCapacitor.calEnergy(energy, stack);
            expandedElectricCapacitor.setEnergy(locationData, 0);

            energyWithdrawEvent.setEnergy(StringNumberUtil.add(energyWithdrawEvent.getEnergy(), String.valueOf(nowEnergy)));
        }
    }
}
