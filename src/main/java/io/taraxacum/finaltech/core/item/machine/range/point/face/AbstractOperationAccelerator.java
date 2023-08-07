package io.taraxacum.finaltech.core.item.machine.range.point.face;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.LocationMachine;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.unit.StatusL2Inventory;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.util.EnergyUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public abstract class AbstractOperationAccelerator extends AbstractFaceMachine implements RecipeItem, EnergyNetComponent, MenuUpdater, LocationMachine {
    private int statusSlot;

    public AbstractOperationAccelerator(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        StatusL2Inventory statusL2Inventory = new StatusL2Inventory(this);
        this.statusSlot = statusL2Inventory.statusSlot;
        return statusL2Inventory;
    }

    @Nonnull
    @Override
    protected BlockPlaceHandler onBlockPlace() {
        return MachineUtil.BLOCK_PLACE_HANDLER_PLACER_DENY;
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return MachineUtil.simpleBlockBreakerHandler(FinalTech.getLocationDataService(), this);
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if(inventory == null) {
            return;
        }

        boolean hasViewer = !inventory.getViewers().isEmpty();

        int amount;
        ItemStack itemStack = inventory.getItem(this.getInputSlot()[0]);
        if (ItemStackUtil.isItemSimilar(itemStack, this.getItem())) {
            amount = itemStack.getAmount() + 1;
        } else {
            amount = 1;
        }

        int count = this.pointFunction(block, 1, location -> {
            LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(location);
            if(tempLocationData != null
                    && !this.getNotAllowedId().contains(LocationDataUtil.getId(FinalTech.getLocationDataService(), tempLocationData))
                    && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), tempLocationData) instanceof MachineProcessHolder<?> machineProcessHolder
                    && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), tempLocationData) instanceof EnergyNetComponent energyNetComponent) {
                MachineProcessor<?> machineProcessor = machineProcessHolder.getMachineProcessor();

                Runnable runnable = () -> {
                    int energy = Integer.parseInt(EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData));
                    int time = 0;
                    MachineOperation operation = machineProcessor.getOperation(location);
                    if (operation != null) {
                        time = Math.min(Math.min(amount * this.getBaseEfficiency() + FinalTech.getRandom().nextInt(1 + amount * this.getRandomEfficiency()), energy / energyNetComponent.getCapacity()), operation.getRemainingTicks());
                        if(time > 0 && operation.getRemainingTicks() > 0) {
                            operation.addProgress(Math.min(time, operation.getRemainingTicks()));
                            energy = Math.max(0, energy - time * energyNetComponent.getCapacity());
                            EnergyUtil.setCharge(FinalTech.getLocationDataService(), locationData, String.valueOf(energy));
                        }
                    }

                    if(hasViewer) {
                        this.updateInv(inventory, this.statusSlot, this,
                                String.valueOf(energy),
                                String.valueOf(time));
                    }
                };
                BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(LocationDataUtil.getId(FinalTech.getLocationDataService(), tempLocationData)), runnable, () -> new Location[]{location, block.getLocation()});
                return 1;
            }
            return 0;
        });

        if(count == 0 && hasViewer) {
            this.updateInv(inventory, this.statusSlot, this,
                    EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData),
                    "0");
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Nonnull
    @Override
    protected BlockFace getBlockFace() {
        return BlockFace.UP;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this,
                String.valueOf(this.getBaseEfficiency()),
                String.valueOf(this.getRandomEfficiency()));

        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (slimefunItem instanceof MachineProcessHolder && !this.getNotAllowedId().contains(slimefunItem.getId())) {
                this.registerDescriptiveRecipe(slimefunItem.getItem());
            }
        }
    }

    @Nonnull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public Location[] getLocations(@Nonnull Location sourceLocation) {
        return new Location[] {this.getTargetLocation(sourceLocation, 1)};
    }

    @Nonnull
    abstract Set<String> getNotAllowedId();

    abstract int getBaseEfficiency();

    abstract int getRandomEfficiency();
}
