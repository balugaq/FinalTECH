package io.taraxacum.finaltech.core.item.machine.range.point.face;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetProvider;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.AbstractEnergyProvider;
import io.github.thebusybiscuit.slimefun4.implementation.operations.FuelOperation;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.unit.VoidInventory;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Final_ROOT
 */
public class FuelOperator extends AbstractFaceMachine implements RecipeItem {
    private final List<String> notAllowedId = ConfigUtil.getItemStringList(this, "not-allowed-id");

    public FuelOperator(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
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
            if (tempLocationData != null) {
                String id = LocationDataUtil.getId(FinalTech.getLocationDataService(), tempLocationData);
                if(id != null && !this.notAllowedId.contains(id)
                    && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), tempLocationData) instanceof EnergyNetProvider
                    && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), tempLocationData) instanceof MachineProcessHolder machineProcessHolder) {
                    BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(id), () -> FuelOperator.this.doCharge((MachineProcessHolder<FuelOperation>) machineProcessHolder, tempLocationData), location);
                }
            }
            return 0;
        });
    }

    private void doCharge(@Nonnull MachineProcessHolder<FuelOperation> MachineProcessHolder, @Nonnull LocationData locationData) {
        MachineOperation machineOperation = MachineProcessHolder.getMachineProcessor().getOperation(locationData.getLocation());
        if (machineOperation == null) {
            MachineProcessHolder.getMachineProcessor().startOperation(locationData.getLocation(), new FuelOperation(new MachineFuel(2, new ItemStack(Material.COBBLESTONE))));
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
        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this);

        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (!this.notAllowedId.contains(slimefunItem.getId()) && slimefunItem instanceof AbstractEnergyProvider && slimefunItem instanceof MachineProcessHolder) {
                this.registerDescriptiveRecipe(slimefunItem.getItem());
            }
        }
    }
}
