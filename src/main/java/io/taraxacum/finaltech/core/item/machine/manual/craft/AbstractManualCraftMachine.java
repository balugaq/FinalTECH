package io.taraxacum.finaltech.core.item.machine.manual.craft;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.manual.AbstractManualMachineInventory;
import io.taraxacum.finaltech.core.inventory.manual.ManualCraftMachineInventory;
import io.taraxacum.finaltech.core.item.machine.manual.AbstractManualMachine;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.util.EnergyUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Final_ROOT
 */
public abstract class AbstractManualCraftMachine extends AbstractManualMachine implements RecipeItem, EnergyNetComponent {
    private final Map<Location, Integer> locationCountMap = new HashMap<>();
    private int countThreshold = ConfigUtil.getOrDefaultItemSetting(Slimefun.getTickerTask().getTickRate() * 2, this, "threshold");
    private int leftClickAmount = ConfigUtil.getOrDefaultItemSetting(1, this, "left-click-amount");
    private int rightClickAmount = ConfigUtil.getOrDefaultItemSetting(64, this, "right-click-amount");
    private int leftShiftClickAmount = ConfigUtil.getOrDefaultItemSetting(576, this, "left-shift-click-amount");
    private int rightShiftClickAmount = ConfigUtil.getOrDefaultItemSetting(2304, this, "right-shift-click-amount");

    private int capacity = ConfigUtil.getOrDefaultItemSetting(18432, this, "capacity");
    private int charge = ConfigUtil.getOrDefaultItemSetting(1, this, "charge");
    private int consume = ConfigUtil.getOrDefaultItemSetting(1, this, "consume");

    public AbstractManualCraftMachine(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nonnull
    @Override
    protected AbstractManualMachineInventory newMachineInventory() {
        return new ManualCraftMachineInventory(this);
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        String chargeStr = EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData);
        int charge = Integer.parseInt(chargeStr) + this.charge;
        while (charge >= this.capacity / 2) {
            charge /= 2;
        }

        EnergyUtil.setCharge(FinalTech.getLocationDataService(), locationData, String.valueOf(Math.min(charge, this.capacity)));

        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if (inventory != null && !inventory.getViewers().isEmpty()) {
            this.getMachineInventory().updateInventory(inventory, block.getLocation());
        }
    }

    @Override
    protected void uniqueTick() {
        super.uniqueTick();
        this.locationCountMap.clear();
    }

    @Nonnull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    public Map<Location, Integer> getLocationCountMap() {
        return locationCountMap;
    }

    public int getConsume() {
        return consume;
    }

    public int getCountThreshold() {
        return countThreshold;
    }

    public int getLeftClickAmount() {
        return leftClickAmount;
    }

    public int getRightClickAmount() {
        return rightClickAmount;
    }

    public int getLeftShiftClickAmount() {
        return leftShiftClickAmount;
    }

    public int getRightShiftClickAmount() {
        return rightShiftClickAmount;
    }
}
