package io.taraxacum.finaltech.core.item.machine.range.point.face;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.LocationMachine;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.unit.VoidInventory;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public class MatrixOperationAccelerator extends AbstractFaceMachine implements RecipeItem, LocationMachine {
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));

    public MatrixOperationAccelerator(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return new VoidInventory(this);
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
        this.pointFunction(block, 1, location -> {
            LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(location);
            if(tempLocationData != null
                    && !this.notAllowedId.contains(LocationDataUtil.getId(FinalTech.getLocationDataService(), tempLocationData))
                    && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), tempLocationData) instanceof MachineProcessHolder<?> machineProcessHolder) {
                MachineProcessor<?> machineProcessor = machineProcessHolder.getMachineProcessor();
                Runnable runnable = () -> {
                    MachineOperation operation = machineProcessor.getOperation(location);
                    if (operation != null && operation.getRemainingTicks() > 0) {
                        operation.addProgress(operation.getRemainingTicks());
                    }
                };
                BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(LocationDataUtil.getId(FinalTech.getLocationDataService(), tempLocationData)), runnable, location);
                return 1;
            }
            return 0;
        });
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
        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this);

        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (slimefunItem instanceof MachineProcessHolder) {
                this.registerDescriptiveRecipe(slimefunItem.getItem());
            }
        }
    }

    @Override
    public Location[] getLocations(@Nonnull Location sourceLocation) {
        return new Location[] {this.getTargetLocation(sourceLocation, 1)};
    }
}
