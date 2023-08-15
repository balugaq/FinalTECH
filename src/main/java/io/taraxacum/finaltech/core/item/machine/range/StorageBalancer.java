package io.taraxacum.finaltech.core.item.machine.range;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.unit.VoidInventory;
import io.taraxacum.finaltech.core.item.machine.manual.storage.AbstractStorageMachine;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Final_ROOT
 */
public class StorageBalancer extends AbstractRangeMachine implements RecipeItem {
    private final BlockFace[] blockFaces = new BlockFace[] {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public StorageBalancer(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
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
        return MachineUtil.simpleBlockBreakerHandler();
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Map<BlockFace, MapData> faceDataMap = new HashMap<>(this.blockFaces.length);
        Map<String, String> itemAmountMap = new HashMap<>();
        Set<String> itemStringSet = new HashSet<>();

        int index = 0;
        for (BlockFace blockFace : JavaUtil.shuffle(this.blockFaces)) {
            MapData mapData = this.searchBlock(block, blockFace);
            if (mapData == null) {
                continue;
            }
            mapData.index = index++;
            faceDataMap.put(blockFace, mapData);

            for (Map.Entry<String, AbstractStorageMachine.ItemWithAmount> itemEntry : mapData.itemMap.entrySet()) {
                String amount = itemAmountMap.getOrDefault(itemEntry.getKey(), StringNumberUtil.ZERO);
                amount = StringNumberUtil.add(amount, itemEntry.getValue().getItemAmount());
                itemAmountMap.put(itemEntry.getKey(), amount);
            }
        }

        if (faceDataMap.isEmpty()) {
            return;
        }

        Runnable runnable = () -> {
            for (MapData inputMapData : faceDataMap.values()) {
                Map<String, AbstractStorageMachine.ItemWithAmount> itemMap = inputMapData.itemMap;
                for (String itemStr : itemMap.keySet()) {
                    if (itemStringSet.contains(itemStr)) {
                        break;
                    }
                    itemStringSet.add(itemStr);

                    List<MapData> list = new ArrayList<>();
                    for (MapData outputMapData : faceDataMap.values()) {
                        if (outputMapData.itemMap.containsKey(itemStr)) {
                            list.add(outputMapData);
                        }
                    }

                    if (list.size() <= 1) {
                        break;
                    }

                    list.sort((o1, o2) -> StringNumberUtil.compare(o1.abstractStorageMachine.getAmountLimit(), o2.abstractStorageMachine.getAmountLimit()));

                    String[] amounts = new String[list.size()];
                    String amount = itemAmountMap.get(itemStr);
                    if (StringNumberUtil.compare(amount, StringNumberUtil.VALUE_INFINITY) == 0) {
                        for (int i = 0; i < amounts.length; i++) {
                            amounts[i] = StringNumberUtil.min(StringNumberUtil.VALUE_INFINITY, list.get(i).abstractStorageMachine.getAmountLimit());
                        }
                    } else {
                        BigInteger value = new BigInteger(amount);
                        BigInteger divide = value.divide(new BigInteger(String.valueOf(list.size())));
                        BigInteger mod = value.mod(new BigInteger(String.valueOf(list.size())));
                        String v = divide.toString();
                        for (int i = 0, j = mod.intValue(); i < amounts.length; i++) {
                            amounts[i] = i < j ? StringNumberUtil.add(v) : v;
                            if (StringNumberUtil.compare(list.get(i).abstractStorageMachine.getAmountLimit(), amounts[i]) < 0) {
                                amounts[i] = list.get(i).abstractStorageMachine.getAmountLimit();
                                amount = StringNumberUtil.sub(amount, list.get(i).abstractStorageMachine.getAmountLimit());

                                if (i != list.size() - 1) {
                                    value = new BigInteger(amount);
                                    divide = value.divide(new BigInteger(String.valueOf(list.size() - i - 1)));
                                    mod = value.mod(new BigInteger(String.valueOf(list.size() - i - 1)));
                                    v = divide.toString();
                                    j = mod.intValue();
                                }
                            }
                        }
                    }

                    for (int i = 0; i < list.size(); i++) {
                        MapData mapData = list.get(i);
                        mapData.abstractStorageMachine.setAmount(mapData.itemMap, mapData.locationData, itemStr, amounts[i]);
                    }
                }
            }
        };

        BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(this.getId()), runnable, () -> {
            Location[] locations = new Location[faceDataMap.values().size()];
            int i = 0;
            for (MapData mapData : faceDataMap.values()) {
                locations[i++] = mapData.getLocationData().getLocation();
            }
            return locations;
        });
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Nullable
    protected MapData searchBlock(@Nonnull Block block, @Nonnull BlockFace blockFace) {
        Block targetBlock = block.getRelative(blockFace);
        if (!targetBlock.getChunk().isLoaded()) {
            return null;
        }

        Location location = targetBlock.getLocation();
        Set<Location> locationSet = new HashSet<>();
        LocationData locationData;
        SlimefunItem slimefunItem;
        while (location != null) {
            locationData = FinalTech.getLocationDataService().getLocationData(location);
            if (locationData == null) {
                return null;
            }
            slimefunItem = LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), locationData);
            if (slimefunItem instanceof AbstractStorageMachine abstractStorageMachine) {
                return new MapData(abstractStorageMachine.getItemMap(locationData), locationData, abstractStorageMachine);
//            } else if (slimefunItem instanceof StorageConnector storageConnector) {
//                location = storageConnector.getEnd(locationData, blockFace);
//                if (locationSet.contains(location)) {
//                    return null;
//                } else {
//                    locationSet.add(location);
//                }
            } else {
                return null;
            }
        }
        return null;

    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
    }

    protected static class MapData {
        private Map<String, AbstractStorageMachine.ItemWithAmount> itemMap;

        private LocationData locationData;

        private AbstractStorageMachine abstractStorageMachine;

        private int index;

        @ParametersAreNonnullByDefault
        public MapData(Map<String, AbstractStorageMachine.ItemWithAmount> itemMap, LocationData locationData, AbstractStorageMachine abstractStorageMachine) {
            this.itemMap = itemMap;
            this.locationData = locationData;
            this.abstractStorageMachine = abstractStorageMachine;
        }

        public Map<String, AbstractStorageMachine.ItemWithAmount> getItemMap() {
            return itemMap;
        }

        public void setItemMap(Map<String, AbstractStorageMachine.ItemWithAmount> itemMap) {
            this.itemMap = itemMap;
        }

        public LocationData getLocationData() {
            return locationData;
        }

        public void setLocationData(LocationData locationData) {
            this.locationData = locationData;
        }

        public AbstractStorageMachine getAbstractStorageMachine() {
            return abstractStorageMachine;
        }

        public void setAbstractStorageMachine(AbstractStorageMachine abstractStorageMachine) {
            this.abstractStorageMachine = abstractStorageMachine;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
