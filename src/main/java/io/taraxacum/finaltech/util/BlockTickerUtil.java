package io.taraxacum.finaltech.util;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.taraxacum.common.api.RunnableLockFactory;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.libs.plugin.interfaces.LocationDataService;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.slimefun.dto.SlimefunLocationData;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Final_ROOT
 */
public class BlockTickerUtil {
    private final static String KEY_SLEEP = "finaltech-s";

    private final static String KEY_LIVE_TIME = "finaltech-lt";

    @SafeVarargs
    public static <T> void runTask(@Nonnull RunnableLockFactory<T> serverRunnableLockFactory, boolean async, @Nonnull Runnable runnable, T... locks) {
        if (async) {
            serverRunnableLockFactory.waitThenRun(runnable, locks);
        } else {
            runnable.run();
        }
    }


    public static <T> void runTask(@Nonnull RunnableLockFactory<T> serverRunnableLockFactory, boolean async, @Nonnull Runnable runnable, Supplier<T[]> supplier) {
        if (async) {
            serverRunnableLockFactory.waitThenRun(runnable, supplier.get());
        } else {
            runnable.run();
        }
    }

    @Deprecated
    public static void setSleep(@Nonnull Config config, @Nullable String sleep) {
        config.setValue(ConstantTableUtil.CONFIG_SLEEP, sleep);
    }

    @Deprecated
    public static boolean hasSleep(@Nonnull Config config) {
        return config.contains(ConstantTableUtil.CONFIG_SLEEP);
    }

    @Deprecated
    public static void subSleep(@Nonnull Config config) {
        String sleepStr = config.getString(ConstantTableUtil.CONFIG_SLEEP);
        if (sleepStr != null) {
            double sleep = Double.parseDouble(sleepStr) - 1;
            if (sleep > 0) {
                config.setValue(ConstantTableUtil.CONFIG_SLEEP, String.valueOf(sleep));
            } else {
                config.setValue(ConstantTableUtil.CONFIG_SLEEP, null);
            }
        }
    }

    public static void setSleep(@Nonnull LocationDataService locationDataService, @Nonnull LocationData locationData, @Nonnull Number sleep) {
        locationDataService.setLocationData(locationData, KEY_SLEEP, String.valueOf(sleep));
    }

    /**
     * @return true if something here will not sleep and should work
     */
    public static boolean subSleep(@Nonnull LocationDataService locationDataService, @Nonnull LocationData locationData) {
        String value = locationDataService.getLocationData(locationData, KEY_SLEEP);
        if(value == null) {
            return true;
        }
        double sleep = Double.parseDouble(value);
        sleep--;
        if(sleep > 0) {
            locationDataService.setLocationData(locationData, KEY_SLEEP, String.valueOf(sleep));
            return false;
        } else {
            locationDataService.setLocationData(locationData, KEY_SLEEP, null);
            return true;
        }
    }

    @Nonnull
    public static Function<LocationData, Boolean> functionAntiAcceleration(@Nonnull LocationDataService locationDataService) {
        final String key = "finaltech-aa";
        return locationData -> {
            String value = locationDataService.getLocationData(locationData, key);
            if (value != null && Integer.parseInt(value) == FinalTech.getSlimefunTickCount()) {
                return false;
            }

            locationDataService.setLocationData(locationData, key, String.valueOf(FinalTech.getSlimefunTickCount()));
            return true;
        };
    }

    @Nonnull
    public static Function<LocationData, Boolean> functionPerformanceLimit(@Nonnull LocationDataService locationDataService) {
        final String key = "finaltech-pl";
        return locationData -> {
            String value = locationDataService.getLocationData(locationData, key);
            int charge = value == null ? 0 : Integer.parseInt(value);
            charge += FinalTech.getTps();
            if (charge >= 20) {
                if (charge >= 40) {
                    charge -= 20;
                }
                locationDataService.setLocationData(locationData, key, String.valueOf(charge - 20));
                return true;
            } else {
                locationDataService.setLocationData(locationData, key, String.valueOf(charge));
                return false;
            }
        };
    }

    @Nonnull
    public static Function<LocationData, Boolean> functionGeneralInterval(int interval) {
        return locationData -> FinalTech.getSlimefunTickCount() % interval == 0;
    }

    @Nonnull
    public static Function<LocationData, Boolean> functionIndependentIntervalBefore(@Nonnull LocationDataService locationDataService) {
        return locationData -> BlockTickerUtil.subSleep(locationDataService, locationData);
    }

    @Nonnull
    public static Consumer<LocationData> functionIndependentIntervalAfter(@Nonnull LocationDataService locationDataService, int interval) {
        return locationData -> BlockTickerUtil.setSleep(locationDataService, locationData, interval);
    }

    public static Consumer<LocationData> functionLiveTimeAfter(@Nonnull LocationDataService locationDataService, @Nonnull String id, int time, boolean dropSelf, int... dropSlots) {
        return locationData -> {
            String liveTimeStr = FinalTech.getLocationDataService().getLocationData(locationData, KEY_LIVE_TIME);
            if(liveTimeStr == null) {
                liveTimeStr = String.valueOf(time);
                FinalTech.getLocationDataService().setLocationData(locationData, KEY_LIVE_TIME, liveTimeStr);
            } else {
                int liveTime = Integer.parseInt(liveTimeStr) - 1;
                if(liveTime > 0) {
                    FinalTech.getLocationDataService().setLocationData(locationData, KEY_LIVE_TIME, String.valueOf(liveTime));
                } else {
                    Bukkit.getScheduler().runTaskLater(FinalTech.getInstance(), () -> {
                        LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(locationData.getLocation());
                        if(tempLocationData instanceof SlimefunLocationData slimefunLocationData && slimefunLocationData.getId().equals(id)) {
                            SlimefunItem slimefunItem = slimefunLocationData.getSlimefunItem();
                            Location location = tempLocationData.getLocation();
                            World world = location.getWorld();
                            if(slimefunItem instanceof MachineProcessHolder<?> machineProcessHolder) {
                                machineProcessHolder.getMachineProcessor().endOperation(location);
                            }
                            if(dropSelf) {
                                world.dropItem(locationData.getLocation(), ItemStackUtil.cloneItem(slimefunItem.getItem(), 1));
                            }
                            if(dropSlots.length > 0) {
                                Inventory inventory = FinalTech.getLocationDataService().getInventory(tempLocationData);
                                if(inventory != null) {
                                    InventoryUtil.dropItems(inventory, location, dropSlots);
                                }
                            }
                            locationDataService.clearLocationData(location);
                            location.getBlock().setType(Material.AIR);
                        }
                    }, 0);
                }
            }
        };
    }

    @Nonnull
    public static Function<LocationData, Boolean> functionRangeLimitBefore(@Nonnull LocationDataService locationDataService, @Nonnull RangeLimitHandler rangeLimitHandler) {
        return locationData -> {
            if (rangeLimitHandler.lastLocationList.size() > 1) {
                Location randomLocation = rangeLimitHandler.lastLocationList.get(rangeLimitHandler.random.nextInt(rangeLimitHandler.lastLocationList.size()));
                int limitRange = rangeLimitHandler.range + rangeLimitHandler.mulRange * rangeLimitHandler.lastLocationList.size();
                double distance = LocationUtil.getClosestDistance(locationData.getLocation(), randomLocation);
                if (distance > 0 && distance < limitRange) {
                    FinalTech.getInstance().getServer().getScheduler().runTask(FinalTech.getInstance(), () -> {
                        boolean canBreak = true;
                        World world = locationData.getLocation().getWorld();
                        if (world != null) {
                            List<Player> playerList = new ArrayList<>();
                            for (Entity entity : world.getNearbyEntities(locationData.getLocation(), limitRange, limitRange, limitRange, entity -> entity instanceof Player)) {
                                if (entity instanceof Player player) {
                                    if (canBreak && !PermissionUtil.checkPermission(player, locationData.getLocation(), Interaction.BREAK_BLOCK)) {
                                        canBreak = false;
                                    }
                                    playerList.add(player);
                                }
                            }
                            if (canBreak) {
                                locationDataService.clearLocationData(locationData.getLocation());
                                Block block = locationData.getLocation().getBlock();
                                block.setType(Material.AIR);
                                if(locationData instanceof SlimefunLocationData slimefunLocationData && locationDataService instanceof SlimefunLocationDataService slimefunLocationDataService) {
                                    if(slimefunLocationData.getSlimefunItem() instanceof MachineProcessHolder<?> machineProcessHolder) {
                                        machineProcessHolder.getMachineProcessor().endOperation(locationData.getLocation());
                                    }
                                    SlimefunItem slimefunItem = slimefunLocationDataService.getSlimefunItem((SlimefunLocationData) locationData);
                                    if(rangeLimitHandler.dropSelf && slimefunItem.getId().equals(slimefunLocationData.getId())) {
                                        world.dropItem(locationData.getLocation(), ItemStackUtil.cloneItem(slimefunItem.getItem(), 1));
                                    }
                                }
                                for(Player player : playerList) {
                                    if(locationData instanceof SlimefunLocationData slimefunLocationData) {
                                        player.sendMessage(rangeLimitHandler.message.replace("{1}", slimefunLocationData.getSlimefunItem().getItemName()));
                                    }
                                    player.sendMessage(rangeLimitHandler.message);
                                }
                            }
                        }
                    });
                    return false;
                }
            }
            return true;
        };
    }

    @Nonnull
    public static Consumer<LocationData> functionRangeLimitAfter(@Nonnull RangeLimitHandler rangeLimitHandler) {
        return locationData -> rangeLimitHandler.locationList.add(locationData.getLocation());
    }

    @Nonnull
    public static Runnable functionRangeLimitUnique(@Nonnull RangeLimitHandler rangeLimitHandler) {
        return () -> {
            List<Location> tempLocationList = rangeLimitHandler.lastLocationList;
            rangeLimitHandler.lastLocationList = rangeLimitHandler.locationList;
            rangeLimitHandler.locationList = tempLocationList;
            rangeLimitHandler.locationList.clear();
        };
    }

    public static class RangeLimitHandler {
        private List<Location> lastLocationList = new ArrayList<>();
        private List<Location> locationList = new ArrayList<>();

        private final Random random;

        private final int range;
        private final int mulRange;
        private final boolean dropSelf;
        private final String message;

        public RangeLimitHandler(int range, int mulRange, boolean dropSelf, @Nonnull String message) {
            this.range = range;
            this.mulRange = mulRange;
            this.dropSelf = dropSelf;
            this.message = message;

            this.random = new Random();
        }
    }
}
