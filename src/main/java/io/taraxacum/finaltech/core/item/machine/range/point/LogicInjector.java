package io.taraxacum.finaltech.core.item.machine.range.point;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.DigitInjectableItem;
import io.taraxacum.finaltech.core.interfaces.LogicInjectableItem;
import io.taraxacum.finaltech.core.interfaces.LogicItem;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.InjectorInventory;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Can change the setting of a slimefun item as it is marked as {@link LogicInjectableItem}.
 * The effect varies depending on the item.
 * @see LogicInjectableItem
 * @author Final_ROOT
 */
public class LogicInjector extends AbstractPointMachine implements RecipeItem {
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));

    public LogicInjector(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nonnull
    @Override
    public Location getTargetLocation(@Nonnull Location location, int range) {
        Block block = location.getBlock();
        if (block.getState().getBlockData() instanceof Directional directional) {
            return block.getRelative(directional.getFacing()).getLocation();
        } else {
            return location;
        }
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return new InjectorInventory(this);
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
        if (inventory == null) {
            return;
        }

        ItemStack itemStack = inventory.getItem(this.getInputSlot()[0]);
        if (ItemStackUtil.isItemNull(itemStack) || !(SlimefunItem.getByItem(itemStack) instanceof LogicItem logicItem)) {
            return;
        }

        BlockData blockData = block.getState().getBlockData();
        if (!(blockData instanceof Directional directional)) {
            return;
        }

        Block targetBlock = block.getRelative(directional.getFacing().getOppositeFace());
        if (!targetBlock.getChunk().isLoaded()) {
            return;
        }

        LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(targetBlock.getLocation());
        if (tempLocationData == null) {
            return;
        }

        SlimefunItem sfItem = LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), tempLocationData);
        if (sfItem instanceof LogicInjectableItem logicInjectableItem) {
            Runnable runnable = () -> {
                logicInjectableItem.injectLogic(tempLocationData, logicItem.getLogic());
                itemStack.setAmount(itemStack.getAmount() - 1);
            };

            if (sfItem.getBlockTicker() != null) {
                if (sfItem.getBlockTicker().isSynchronized()) {
                    JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
                    javaPlugin.getServer().getScheduler().runTask(javaPlugin, runnable);
                } else {
                    boolean async = FinalTech.isAsyncSlimefunItem(sfItem.getId());
                    BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), async, runnable, tempLocationData.getLocation(), locationData.getLocation());
                }
            } else {
                boolean async = FinalTech.isAsyncSlimefunItem(sfItem.getId());
                if (async) {
                    BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), true, runnable, tempLocationData.getLocation(), locationData.getLocation());
                } else {
                    JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
                    javaPlugin.getServer().getScheduler().runTask(javaPlugin, runnable);
                }
            }
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this);

        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (slimefunItem instanceof LogicInjectableItem) {
                this.registerDescriptiveRecipe(slimefunItem.getItem());
            }
        }
    }
}
