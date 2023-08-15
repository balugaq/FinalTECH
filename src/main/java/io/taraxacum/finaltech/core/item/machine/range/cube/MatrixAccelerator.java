package io.taraxacum.finaltech.core.item.machine.range.cube;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.unit.StatusL2Inventory;
import io.taraxacum.finaltech.core.option.RouteShow;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

/**
 * @author Final_ROOT
 */
public class MatrixAccelerator extends AbstractCubeMachine implements RecipeItem, MenuUpdater {
    private final int range = ConfigUtil.getOrDefaultItemSetting(1, this, "range");
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    private final boolean safeMode = ConfigUtil.getOrDefaultItemSetting(true, this, "safe-mode");
    // System.nanoTime
    // 1,000,000ns = 1ms
    private final int syncThreshold = ConfigUtil.getOrDefaultItemSetting(300_000, this, "threshold-sync");
    private final int asyncThreshold = ConfigUtil.getOrDefaultItemSetting(1_600_000, this, "threshold-async");
    private int statusSlot;

    public MatrixAccelerator(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
        this.notAllowedId.add(this.getId());
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        StatusL2Inventory statusInventory = new StatusL2Inventory(this);
        this.statusSlot = statusInventory.statusSlot;
        return statusInventory;
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
        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if(inventory == null) {
            return;
        }

        boolean hasViewer = !inventory.getViewers().isEmpty();
        boolean drawParticle = hasViewer || RouteShow.VALUE_TRUE.equals(RouteShow.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));
        Location blockLocation = locationData.getLocation();

        int accelerate = 0;
        int range = this.range;

        String machineId = null;

        // parse item
        ItemStack matchItem = inventory.getItem(this.getInputSlot()[0]);
        if (!ItemStackUtil.isItemNull(matchItem)) {
            if (FinalTechItems.ITEM_PHONY.verifyItem(matchItem)) {
                int amount = matchItem.getAmount();
                for (int i = 2, j = amount; j > 0; j /= i) {
                    accelerate++;
                }
                for (int i = 2, j = amount; j > 0; j /= i++) {
                    range++;
                }
            } else {
                SlimefunItem machineItem = SlimefunItem.getByItem(matchItem);
                if (machineItem == null
                        || this.notAllowedId.contains(machineItem.getId())
                        || machineItem.getBlockTicker() == null) {
                    if(hasViewer) {
                        this.updateInv(inventory, this.statusSlot, this,
                                "0", "0", "0");
                    }
                    return;
                }
                machineId = machineItem.getId();
                accelerate = matchItem.getAmount();
            }
        }

        // search around block
        Map<Integer, List<LocationData>> locationDataMap = new HashMap<>(range * 3);
        final String finalMachineId = machineId;
        Function<String, Boolean> availableIdFunction = machineId == null ? id -> !MatrixAccelerator.this.notAllowedId.contains(id) : finalMachineId::equals;
        int count = this.cubeFunction(block, range, location -> {
            if(!location.getChunk().isLoaded()) {
                return -1;
            }
            LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(location);
            if (tempLocationData != null
                    && availableIdFunction.apply(LocationDataUtil.getId(FinalTech.getLocationDataService(), tempLocationData))
                    && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), tempLocationData).getBlockTicker() != null) {
                int distance = Math.abs(location.getBlockX() - blockLocation.getBlockX()) + Math.abs(location.getBlockY() - blockLocation.getBlockY()) + Math.abs(location.getBlockZ() - blockLocation.getBlockZ());
                locationDataMap.computeIfAbsent(distance, d -> new ArrayList<>(d * d * 4 + 2)).add(tempLocationData);
                tempLocationData.cloneLocation();
                return 1;
            }
            return 0;
        });

        if (count == 0) {
            if(hasViewer) {
                this.updateInv(inventory, this.statusSlot, this,
                        "0", "0", "0");
            }
            return;
        }

        int accelerateTimeCount = 0;
        int accelerateMachineCount = 0;

        JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
        final int finalAccelerate = machineId != null ? accelerate /= count : accelerate;
        List<LocationData> locationDataList;
        for (int distance = 1; distance <= range * 3; distance++) {
            locationDataList = locationDataMap.get(distance);
            if (locationDataList != null) {
                Collections.shuffle(locationDataList);
                for (LocationData tempLocationdata : locationDataList) {
                    SlimefunItem sfItem = LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), tempLocationdata);
                    if(sfItem != null) {
                        BlockTicker blockTicker = sfItem.getBlockTicker();
                        if (blockTicker.isSynchronized()) {
                            javaPlugin.getServer().getScheduler().runTask(javaPlugin, () -> {
                                for (int i = 0; i < finalAccelerate; i++) {
                                    long testTime = JavaUtil.testTime(() -> {
                                        try {
                                            FinalTech.getBlockTickerService().run(blockTicker, tempLocationdata);
                                        } catch (Throwable e) {
                                            e.printStackTrace();
                                            throw new RuntimeException(e);
                                        }
                                    });
                                    if (this.safeMode && testTime > MatrixAccelerator.this.syncThreshold) {
                                        FinalTech.logger().warning(this.getId() + " cost " + testTime + "ns to run blockTicker for " + sfItem.getId());
                                        MatrixAccelerator.this.notAllowedId.add(sfItem.getId());
                                        break;
                                    }
                                }
                            });
                        } else if(!this.safeMode || FinalTech.isAsyncSlimefunItem(sfItem.getId()) == FinalTech.isAsyncSlimefunItem(this.getId())) {
                            BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(sfItem.getId()), () -> {
                                for (int i = 0; i < finalAccelerate; i++) {
                                    long testTime = JavaUtil.testTime(() -> {
                                        try {
                                            FinalTech.getBlockTickerService().run(blockTicker, tempLocationdata);
                                        } catch (Throwable e) {
                                            e.printStackTrace();
                                            throw new RuntimeException(e);
                                        }
                                    });
                                    if (this.safeMode && testTime > MatrixAccelerator.this.asyncThreshold) {
                                        FinalTech.logger().warning(this.getId() + " cost " + testTime + "ns to run blockTicker for " + sfItem.getId());
                                        MatrixAccelerator.this.notAllowedId.add(sfItem.getId());
                                        break;
                                    }
                                }
                            }, tempLocationdata.getLocation());
                        }

                        if (drawParticle) {
                            javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, tempLocationdata.getLocation().getBlock()));
                        }
                        accelerateTimeCount += accelerate;
                        accelerateMachineCount++;
                    }
                }
            }
        }

        if(hasViewer) {
            this.updateInv(inventory, this.statusSlot, this,
                    String.valueOf(range),
                    String.valueOf(accelerateTimeCount),
                    String.valueOf(accelerateMachineCount));
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.valueOf(this.range));
    }

    @Override
    public Location[] getLocations(@Nonnull Location sourceLocation) {
        // TODO
        return new Location[0];
    }
}
