package io.taraxacum.finaltech.core.item.machine.manual.storage;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.item.machine.manual.AbstractManualMachine;
import io.taraxacum.finaltech.util.SqlUtil;
import io.taraxacum.libs.plugin.dto.ItemWrapper;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractStorageMachine extends AbstractManualMachine {
    protected final String keyItem = "item";
    protected final String keyAmount = "amount";
    protected final String keyPage = "p";

    public AbstractStorageMachine(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    /**
     * Get the map in the target location.
     */
    @Nonnull
    public abstract Map<String, ItemWithAmount> getItemMap(@Nonnull LocationData locationData);

    /**
     * All the items in the slots will be added to the item map. Then update the location data.
     * The item map and the location data should be in the same location.
     * @return how many item inputted.
     */
    public int input(@Nonnull Map<String, ItemWithAmount> itemMap, @Nonnull LocationData locationData, @Nonnull Inventory inventory, int... slots) {
        int result = 0;
        for (int slot : slots) {
            ItemStack itemStack = inventory.getItem(slot);
            if (!ItemStackUtil.isItemNull(itemStack)) {
                int amount = itemStack.getAmount();
                result += amount;
                itemStack.setAmount(1);
                String itemStr = ItemStackUtil.itemStackToString(itemStack);
                String amountStr = String.valueOf(amount);
                String addAmount = this.addItem(itemMap, locationData, itemStr, amountStr);
                if (StringNumberUtil.compare(amountStr, addAmount) > 0) {
                    itemStack.setAmount(Integer.parseInt(StringNumberUtil.sub(amountStr, addAmount)));
                    result -= itemStack.getAmount();
                } else {
                    inventory.clear(slot);
                }
            }
        }
        return result;
    }

    /**
     * All the items in the slots will be added to the item map. Then update the location data.
     * The item map and the location data should be in the same location.
     * @return how many item inputted.
     */
    public int input(@Nonnull Map<String, ItemWithAmount> itemMap, @Nonnull LocationData locationData, @Nonnull String itemString, @Nonnull Inventory inventory, int... slots) {
        ItemWithAmount itemWithAmount = itemMap.get(itemString);
        String sql = null;
        if (itemWithAmount == null) {
            if (this.getTypeLimit() == 0 || itemMap.size() < this.getTypeLimit()) {
                ItemStack itemStack = ItemStackUtil.stringToItemStack(itemString);
                if (ItemStackUtil.isItemNull(itemStack)) {
                    return 0;
                }
                itemWithAmount = new ItemWithAmount(new ItemWrapper(itemStack), itemString, "0", this.getIndex(itemMap));
                sql = FinalTech.safeSql() ? SqlUtil.getSafeSql(itemString) : itemString;
            } else {
                return 0;
            }
        }

        int availableAmount = StringNumberUtil.compare(this.getAmountLimit(), StringNumberUtil.VALUE_INFINITY) == 0
                ? inventory.getMaxStackSize() * slots.length
                : Integer.parseInt(StringNumberUtil.min(StringNumberUtil.sub(this.getAmountLimit(), itemWithAmount.itemAmount), String.valueOf(inventory.getMaxStackSize() * slots.length)));
        if (availableAmount == 0) {
            return 0;
        }

        int validAmount = availableAmount;

        for (int slot : slots) {
            ItemStack itemStack = inventory.getItem(slot);
            if (!ItemStackUtil.isItemNull(itemStack) && ItemStackUtil.isItemSimilar(itemStack, itemWithAmount.itemWrapper)) {
                int amount = Math.min(itemStack.getAmount(), validAmount);
                validAmount -= amount;
                inventory.clear(slot);
                if (validAmount == 0) {
                    break;
                }
            }
        }
        if (availableAmount != validAmount) {
            itemWithAmount.itemAmount = StringNumberUtil.add(itemWithAmount.itemAmount, String.valueOf(availableAmount - validAmount));
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyAmount + itemWithAmount.index, itemWithAmount.itemAmount);
            if (sql != null) {
                FinalTech.getLocationDataService().setLocationData(locationData, this.keyItem + itemWithAmount.index, sql);
                itemMap.put(itemString, itemWithAmount);
            }
        }

        return availableAmount - validAmount;
    }

    /**
     * Output items to the given slots from the item map. Then update the location data
     * The item map and the location data should be in the same location.
     * @param inventory the inventory that items will be output here. It may not be in same location with location data.
     */
    public int output(@Nonnull Map<String, ItemWithAmount> itemMap, @Nonnull LocationData locationData, @Nonnull String itemStr, @Nonnull Inventory inventory, int... slots) {
        return this.output(itemMap, locationData, itemStr, inventory.getMaxStackSize() * slots.length, inventory, slots);
    }

    /**
     * Output items to the given slots from the item map. Then update the location data
     * The item map and the location data should be in the same location.
     * @param inventory the inventory that items will be output here. It may not be in same location with location data.
     */
    public int output(@Nonnull Map<String, ItemWithAmount> itemMap, @Nonnull LocationData locationData, @Nonnull String itemStr, int maxAmount, @Nonnull Inventory inventory, int... slots) {
        ItemWithAmount itemWithAmount = itemMap.get(itemStr);
        if (itemWithAmount == null) {
            return 0;
        }

        int validAmount = Integer.parseInt(StringNumberUtil.min(String.valueOf(slots.length * itemWithAmount.itemWrapper.getItemStack().getMaxStackSize()), itemWithAmount.itemAmount));
        if (validAmount <= 0) {
            return 0;
        }

        validAmount = Math.min(validAmount, maxAmount);
        int availableAmount = validAmount;
        ItemStack templateItemStack = ItemStackUtil.cloneItem(itemWithAmount.itemWrapper.getItemStack());
        for (int slot : slots) {
            ItemStack itemStack = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(itemStack)) {
                int amount = Math.min(availableAmount, itemWithAmount.itemWrapper.getItemStack().getMaxStackSize());
                availableAmount -= amount;
                templateItemStack.setAmount(amount);
                inventory.setItem(slot, templateItemStack);
                if (availableAmount == 0) {
                    break;
                }
            } else if (itemStack.getAmount() < itemStack.getMaxStackSize() && ItemStackUtil.isItemSimilar(itemStack, itemWithAmount.itemWrapper)) {
                int amount = Math.min(availableAmount, itemStack.getMaxStackSize() - itemStack.getAmount());
                availableAmount -= amount;
                itemStack.setAmount(itemStack.getAmount() + amount);
                if (availableAmount == 0) {
                    break;
                }
            }
        }
        if (availableAmount != validAmount) {
            this.subItem(itemMap, locationData, itemStr, String.valueOf(validAmount - availableAmount));
        }
        return validAmount - availableAmount;
    }

    /**
     * Get the index for incoming new item.
     * So that different item should have different index.
     */
    @Nonnull
    public String getIndex(@Nonnull Map<String, ItemWithAmount> itemMap) {
        Set<String> indexSet = new HashSet<>();
        for (Map.Entry<String, ItemWithAmount> entry : itemMap.entrySet()) {
            indexSet.add(entry.getValue().getIndex());
        }
        for (int i = itemMap.size(); i >= 0; i--) {
            if (!indexSet.contains(String.valueOf(i))) {
                return String.valueOf(i);
            }
        }
        return String.valueOf(FinalTech.getRandom().nextLong(Long.MAX_VALUE));
    }

    /**
     * Simply add items in given amount to the item map. Then update the location data.
     * The item map and the location data should be in the same location.
     * @return The amount that truly add to the item map.
     */
    @Nonnull
    public String addItem(@Nonnull Map<String, ItemWithAmount> itemMap, @Nonnull LocationData locationData, @Nonnull String itemStr, @Nonnull String amount) {
        ItemWithAmount itemWithAmount = itemMap.get(itemStr);
        if (itemWithAmount != null) {
            String result = StringNumberUtil.min(StringNumberUtil.sub(this.getAmountLimit(), itemWithAmount.itemAmount), amount);
            itemWithAmount.itemAmount = StringNumberUtil.add(itemWithAmount.itemAmount, result);
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyAmount + itemWithAmount.index, itemWithAmount.itemAmount);
            return result;
        } else {
            ItemStack itemStack = ItemStackUtil.stringToItemStack(itemStr);
            if (!ItemStackUtil.isItemNull(itemStack) && this.getTypeLimit() == 0 || itemMap.size() < this.getTypeLimit()) {
                String result = StringNumberUtil.ZERO;
                String sql = itemStr;
                if (FinalTech.safeSql()) {
                    sql = SqlUtil.getSafeSql(sql);
                }
                if (sql != null) {
                    result = StringNumberUtil.min(amount, this.getAmountLimit());
                    itemWithAmount = new ItemWithAmount(new ItemWrapper(itemStack), itemStr, result, this.getIndex(itemMap));
                    FinalTech.getLocationDataService().setLocationData(locationData, this.keyAmount + itemWithAmount.index, itemWithAmount.itemAmount);
                    FinalTech.getLocationDataService().setLocationData(locationData, this.keyItem + itemWithAmount.index, sql);
                    itemMap.put(itemStr, itemWithAmount);
                }
                return result;
            } else {
                return StringNumberUtil.ZERO;
            }
        }
    }

    /**
     * Simply sub items in given amount to the item map. Then update the location data
     * The item map and the location data should be in the same location.
     */
    public void subItem(@Nonnull Map<String, ItemWithAmount> itemMap, @Nonnull LocationData locationData, @Nonnull String itemStr, @Nonnull String amount) {
        ItemWithAmount itemWithAmount = itemMap.get(itemStr);
        if (itemWithAmount != null) {
            itemWithAmount.itemAmount = StringNumberUtil.sub(itemWithAmount.itemAmount, amount);
            if (StringNumberUtil.compare(itemWithAmount.itemAmount, StringNumberUtil.ZERO) > 0) {
                FinalTech.getLocationDataService().setLocationData(locationData, this.keyAmount + itemWithAmount.index, itemWithAmount.itemAmount);
            } else {
                this.removeItem(itemMap, locationData, itemStr);
            }
        }
    }

    /**
     * Simply sub items in given amount to the item map. Then update the location data
     * The item map and the location data should be in the same location.
     */
    public void removeItem(@Nonnull Map<String, ItemWithAmount> itemMap, @Nonnull LocationData locationData, @Nonnull String itemStr) {
        ItemWithAmount itemWithAmount = itemMap.get(itemStr);
        if (itemWithAmount != null) {
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyAmount + itemWithAmount.index, null);
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyItem + itemWithAmount.index, null);
            itemMap.remove(itemStr);
        }
    }

    /**
     * @return if successful set amount.
     */
    public boolean setAmount(@Nonnull Map<String, ItemWithAmount> itemMap, @Nonnull LocationData locationData, @Nonnull String itemStr, @Nonnull String amount) {
        ItemWithAmount itemWithAmount = itemMap.get(itemStr);
        if (StringNumberUtil.compare(amount, this.getAmountLimit()) > 0) {
            return false;
        }
        if (itemWithAmount != null) {
            if (StringNumberUtil.compare(amount, StringNumberUtil.ZERO) > 0) {
                itemWithAmount.itemAmount = amount;
                FinalTech.getLocationDataService().setLocationData(locationData, this.keyAmount + itemWithAmount.index, amount);
            } else {
                FinalTech.getLocationDataService().setLocationData(locationData, this.keyAmount + itemWithAmount.index, null);
                FinalTech.getLocationDataService().setLocationData(locationData, this.keyItem + itemWithAmount.index, null);
                itemMap.remove(itemStr);
            }
        } else if (StringNumberUtil.compare(amount, StringNumberUtil.ZERO) > 0
                && (this.getTypeLimit() == 0 || itemMap.size() < this.getTypeLimit())) {
            String sql = FinalTech.safeSql() ? SqlUtil.getSafeSql(itemStr) : itemStr;
            ItemStack itemStack = ItemStackUtil.stringToItemStack(itemStr);
            if (sql != null && itemStack != null) {
                itemWithAmount = new ItemWithAmount(new ItemWrapper(itemStack), itemStr, amount, this.getIndex(itemMap));
                FinalTech.getLocationDataService().setLocationData(locationData, this.keyAmount + itemWithAmount.index, itemWithAmount.itemAmount);
                FinalTech.getLocationDataService().setLocationData(locationData, this.keyItem + itemWithAmount.index, sql);
                itemMap.put(itemStr, itemWithAmount);
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * @return how many amount can be stored per item. Return "INFINITY" if there is no limit.
     */
    @Nonnull
    public abstract String getAmountLimit();

    /**
     * @return how many kind of items can be stored. Return 0 if there is no limit.
     */
    public abstract int getTypeLimit();

    @Nonnull
    public String getKeyItem() {
        return this.keyItem;
    }

    @Nonnull
    public String getKeyAmount() {
        return this.keyAmount;
    }

    @Nonnull
    public String getKeyPage() {
        return this.keyPage;
    }

    public static class ItemWithAmount {
        private final ItemWrapper itemWrapper;
        private final String itemString;
        private String itemAmount;
        private final String index;

        protected ItemWithAmount(@Nonnull ItemWrapper itemWrapper, @Nonnull String itemString, @Nonnull String itemAmount, @Nonnull String index) {
            this.itemWrapper = itemWrapper;
            this.itemString = itemString;
            this.itemAmount = itemAmount;
            this.index = index;
        }

        public ItemWrapper getItemWrapper() {
            return itemWrapper;
        }

        public String getItemString() {
            return itemString;
        }

        public String getItemAmount() {
            return itemAmount;
        }

        public void setItemAmount(String itemAmount) {
            this.itemAmount = itemAmount;
        }

        public String getIndex() {
            return index;
        }
    }
}
