package io.taraxacum.libs.plugin.util;

import io.taraxacum.libs.plugin.is.ItemAmountWrapper;
import io.taraxacum.libs.plugin.is.ItemWrapper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author Final_ROOT
 */
public class InventoryUtil {
    /**
     * @return How many slot that has item on it.
     */
    public static int slotCount(@Nonnull Inventory inventory, int[] slots) {
        int count = 0;
        ItemStack itemStack;
        for (int slot : slots) {
            itemStack = inventory.getItem(slot);
            if (!ItemStackUtil.isItemNull(itemStack)) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return Whether all item on the specified slots is full.
     */
    public static boolean isFull(@Nonnull Inventory inventory, int[] slots) {
        ItemStack itemStack;
        for (int slot : slots) {
            itemStack = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(itemStack) || itemStack.getAmount() < itemStack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return Whether all item on the specified slots is null.
     */
    public static boolean isEmpty(@Nonnull Inventory inventory, int[] slots) {
        ItemStack itemStack;
        for (int slot : slots) {
            itemStack = inventory.getItem(slot);
            if (!ItemStackUtil.isItemNull(itemStack)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Stock same items in the specified area of slots.
     */
    public static void stockSlots(@Nonnull Inventory inventory, int[] slots) {
        List<ItemWrapper> items = new ArrayList<>(slots.length);
        ItemWrapper itemWrapper = new ItemWrapper();
        for (int slot : slots) {
            ItemStack stockingItem = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(stockingItem)) {
                continue;
            }
            itemWrapper.newWrap(stockingItem);
            for (ItemWrapper stockedItem : items) {
                ItemStackUtil.stack(itemWrapper, stockedItem);
            }
            if (stockingItem.getAmount() > 0 && stockingItem.getAmount() < stockingItem.getMaxStackSize()) {
                items.add(itemWrapper.shallowClone());
            }
        }
    }

    /**
     * @return Get the List of ItemWrapper by specified slots.
     */
    public static List<ItemWrapper> getItemList(@Nonnull Inventory inventory, int[] slots) {
        List<ItemWrapper> itemWrapperList = new ArrayList<>();
        for (int filterSlot : slots) {
            if (!ItemStackUtil.isItemNull(inventory.getItem(filterSlot))) {
                itemWrapperList.add(new ItemWrapper(inventory.getItem(filterSlot)));
            }
        }
        return itemWrapperList;
    }

    /**
     * @return Get the Map of ItemWrapper by specified slots.
     */
    public static Map<Integer, ItemWrapper> getSlotItemWrapperMap(@Nonnull Inventory inventory, int[] slots) {
        Map<Integer, ItemWrapper> itemMap = new LinkedHashMap<>(slots.length);
        for (int slot : slots) {
            ItemStack item = inventory.getItem(slot);
            if (!ItemStackUtil.isItemNull(item)) {
                itemMap.put(slot, new ItemWrapper(item));
            }
        }
        return itemMap;
    }

    /**
     * @return Get the List of ItemWrapper and its amount by specified slots. The ItemStack in return list is not the same of ItemStack in the Inventory.
     */
    public static List<ItemAmountWrapper> calItemListWithAmount(@Nonnull Inventory inventory, int[] slots) {
        List<ItemAmountWrapper> itemAmountWrapperList = new ArrayList<>(slots.length);
        ItemAmountWrapper itemAmountWrapper = new ItemAmountWrapper();
        for (int slot : slots) {
            ItemStack item = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(item)) {
                continue;
            }
            itemAmountWrapper.newWrap(item);
            boolean find = false;
            for (ItemAmountWrapper existedItemWrapper : itemAmountWrapperList) {
                if (ItemStackUtil.isItemSimilar(itemAmountWrapper, existedItemWrapper)) {
                    existedItemWrapper.addAmount(item.getAmount());
                    find = true;
                    break;
                }
            }
            if (!find) {
                itemAmountWrapperList.add(itemAmountWrapper.shallowClone());
            }
        }
        return itemAmountWrapperList;
    }

    public static int calMaxMatch(@Nonnull Inventory inventory, int[] slots, @Nonnull List<ItemAmountWrapper> itemAmountWrapperList) {
        List<Integer> countList = new ArrayList<>(itemAmountWrapperList.size());
        List<Integer> stackList = new ArrayList<>(itemAmountWrapperList.size());
        int[] counts = new int[itemAmountWrapperList.size()];
        int[] stacks = new int[itemAmountWrapperList.size()];
        for (int i = 0; i < itemAmountWrapperList.size(); i++) {
            countList.add(0);
            stackList.add(0);
        }

        int emptySlot = 0;
        ItemWrapper itemWrapper = new ItemWrapper();
        for (int slot : slots) {
            ItemStack item = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(item)) {
                emptySlot++;
                continue;
            } else if (item.getAmount() >= item.getMaxStackSize()) {
                continue;
            }
            itemWrapper.newWrap(item);
            for (int i = 0; i < itemAmountWrapperList.size(); i++) {
                if (ItemStackUtil.isItemSimilar(itemWrapper, itemAmountWrapperList.get(i))) {
                    counts[i] = counts[i] + item.getMaxStackSize() - item.getAmount();
                    stacks[i] = counts[i] / itemAmountWrapperList.get(i).getAmount();
                    break;
                }
            }
        }

        while (emptySlot > 0) {
            int minStackP = 0;
            int minStack = stackList.get(0);
            for (int i = 1; i < itemAmountWrapperList.size(); i++) {
                if (minStack > stackList.get(i)) {
                    minStack = stackList.get(i);
                    minStackP = i;
                }
            }
            counts[minStackP] = counts[minStackP] + itemAmountWrapperList.get(minStackP).getItemStack().getMaxStackSize();
            countList.set(minStackP, countList.get(minStackP) + itemAmountWrapperList.get(minStackP).getItemStack().getMaxStackSize());
            stacks[minStackP] = counts[minStackP] / itemAmountWrapperList.get(minStackP).getAmount();
            stackList.set(minStackP, countList.get(minStackP) / itemAmountWrapperList.get(minStackP).getAmount());
            emptySlot--;
        }

        int min = stackList.get(0);
        for (int stack : stackList) {
            min = Math.min(min, stack);
        }
        return min;
    }

    public static int calMaxMatch(@Nonnull Inventory inventory, int[] slots, @Nonnull ItemAmountWrapper[] itemAmountWrapperList) {
        List<Integer> countList = new ArrayList<>(itemAmountWrapperList.length);
        List<Integer> stackList = new ArrayList<>(itemAmountWrapperList.length);
        for (int i = 0; i < itemAmountWrapperList.length; i++) {
            countList.add(0);
            stackList.add(0);
        }

        int emptySlot = 0;
        ItemWrapper itemWrapper = new ItemWrapper();
        for (int slot : slots) {
            ItemStack item = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(item)) {
                emptySlot++;
                continue;
            } else if (item.getAmount() >= item.getMaxStackSize()) {
                continue;
            }
            itemWrapper.newWrap(item);
            for (int i = 0; i < itemAmountWrapperList.length; i++) {
                if (ItemStackUtil.isItemSimilar(itemWrapper, itemAmountWrapperList[i])) {
                    countList.set(i, countList.get(i) + (item.getMaxStackSize() - item.getAmount()));
                    stackList.set(i, countList.get(i) / itemAmountWrapperList[i].getAmount());
                    break;
                }
            }
        }

        while (emptySlot > 0) {
            int minStackP = 0;
            int minStack = stackList.get(0);
            for (int i = 1; i < itemAmountWrapperList.length; i++) {
                if (minStack > stackList.get(i)) {
                    minStack = stackList.get(i);
                    minStackP = i;
                }
            }
            countList.set(minStackP, countList.get(minStackP) + itemAmountWrapperList[minStackP].getItemStack().getMaxStackSize());
            stackList.set(minStackP, countList.get(minStackP) / itemAmountWrapperList[minStackP].getAmount());
            emptySlot--;
        }

        int min = stackList.get(0);
        for (int stack : stackList) {
            min = Math.min(min, stack);
        }
        return min;
    }

    public static int calMaxMatch(@Nonnull Inventory inventory, int[] slots, @Nonnull ItemAmountWrapper itemAmountWrapper) {
        int count = 0;
        int maxStack = itemAmountWrapper.getItemStack().getMaxStackSize();
        for (int slot : slots) {
            ItemStack item = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(item)) {
                count += maxStack;
            } else if (item.getAmount() < maxStack && ItemStackUtil.isItemSimilar(itemAmountWrapper, item)) {
                count += maxStack - item.getAmount();
            }
        }

        return count / itemAmountWrapper.getAmount();
    }

    public static int calMaxMatch(@Nonnull Inventory inventory, int[] slots, @Nonnull ItemStack[] item) {
        return calMaxMatch(inventory, slots, ItemStackUtil.calItemArrayWithAmount(item));
    }

    public static int calMaxMatch(@Nonnull Inventory inventory, int[] slots, @Nonnull ItemStack item) {
        return calMaxMatch(inventory, slots, new ItemAmountWrapper(item));
    }

    public static void pushItem(@Nonnull Inventory inventory, int[] slots, @Nonnull ItemStack... itemStacks) {
        pushItem(inventory, slots, 1, itemStacks);
    }

    public static void pushItem(@Nonnull Inventory inventory, int[] slots, int amount, @Nonnull ItemStack... itemStacks) {
        ItemAmountWrapper[] itemAmountWrappers = ItemStackUtil.calItemArrayWithAmount(itemStacks);
        pushItem(inventory, slots, amount, itemAmountWrappers);
    }

    public static void pushItem(@Nonnull Inventory inventory, int[] slots, @Nonnull ItemAmountWrapper... itemAmountWrappers) {
        pushItem(inventory, slots, 1, itemAmountWrappers);

    }

    /**
     * Use {@link #calMaxMatch} to make sure that it is available to do this
     */
    public static void pushItem(@Nonnull Inventory inventory, int[] slots, int amount, @Nonnull ItemAmountWrapper... itemAmountWrappers) {
        if (itemAmountWrappers.length == 0 || amount == 0) {
            return;
        }
        int[] totalAmount = new int[itemAmountWrappers.length];
        for (int i = 0; i < totalAmount.length; i++) {
            totalAmount[i] = amount * itemAmountWrappers[i].getAmount();
        }
        int finish = 0;
        List<Integer> emptySlotList = new ArrayList<>();
        ItemWrapper itemWrapper = new ItemWrapper();
        int count;
        Map<Integer, Integer> slotAmountMap = new HashMap<>();
        for (int slot : slots) {
            ItemStack itemStack = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(itemStack)) {
                emptySlotList.add(slot);
            } else if (itemStack.getAmount() < itemStack.getMaxStackSize()) {
                slotAmountMap.put(slot, itemStack.getAmount());
                itemWrapper.newWrap(itemStack);
                for (int i = 0; i < itemAmountWrappers.length; i++) {
                    if (totalAmount[i] > 0 && ItemStackUtil.isItemSimilar(itemWrapper, itemAmountWrappers[i])) {
                        count = Math.min(totalAmount[i], itemStack.getMaxStackSize() - itemStack.getAmount());
                        itemStack.setAmount(itemStack.getAmount() + count);
                        totalAmount[i] -= count;
                        if (totalAmount[i] == 0) {
                            finish++;
                        }
                        break;
                    }
                }
            }
        }

        if (finish == totalAmount.length) {
            return;
        }

        List<Integer> emptySlotListCopy = new ArrayList<>(emptySlotList);
        Iterator<Integer> iterator;
        int i;
        for (i = 0; i < itemAmountWrappers.length; i++) {
            if (totalAmount[i] > 0) {
                iterator = emptySlotList.iterator();
                while (iterator.hasNext()) {
                    Integer slot = iterator.next();
                    if (totalAmount[i] > 0) {
                        count = Math.min(totalAmount[i], itemAmountWrappers[i].getItemStack().getMaxStackSize());
                        inventory.setItem(slot, itemAmountWrappers[i].getItemStack());
                        inventory.getItem(slot).setAmount(count);
                        totalAmount[i] -= count;
                        iterator.remove();
                        if (totalAmount[i] == 0) {
                            finish++;
                            break;
                        }
                    }
                }
            }
        }

        // This should not happen as if the items could not be put in the inventory
        // If it happened, just rollback
        if (finish != totalAmount.length) {
            for (int slot : emptySlotListCopy) {
                inventory.setItem(slot, null);
            }
            for (Map.Entry<Integer, Integer> entry : slotAmountMap.entrySet()) {
                inventory.getItem(entry.getKey()).setAmount(entry.getValue());
            }
        }
    }

    public static boolean tryPushAllItem(@Nonnull Inventory inventory, int[] slots, @Nonnull ItemStack... itemStacks) {
        return tryPushAllItem(inventory, slots, 1, ItemStackUtil.calItemArrayWithAmount(itemStacks));
    }

    public static boolean tryPushAllItem(@Nonnull Inventory inventory, int[] slots, int amount, @Nonnull ItemStack... itemStacks) {
        return tryPushAllItem(inventory, slots, amount, ItemStackUtil.calItemArrayWithAmount(itemStacks));
    }

    public static boolean tryPushAllItem(@Nonnull Inventory inventory, int[] slots, @Nonnull ItemAmountWrapper... itemAmountWrappers) {
        return tryPushAllItem(inventory, slots, 1, itemAmountWrappers);
    }

    /**
     * Push items to the inventory with given amount
     * @param amount how many items should be pushed
     * @return true if item can be pushed and pushed successfully
     * for example:
     *      amount = 4
     *      itemAmountWrappers = [cobblestone with 12 amount]
     *      in this case, 4 * 12 = 48 cobblestones will be pushed to the inventory, and it will return true
     */
    public static boolean tryPushAllItem(@Nonnull Inventory inventory, int[] slots, int amount, @Nonnull ItemAmountWrapper... itemAmountWrappers) {
        if (itemAmountWrappers.length == 0) {
            return false;
        }
        int[] totalAmount = new int[itemAmountWrappers.length];
        List<Integer>[] pushSlot = new List[itemAmountWrappers.length];
        List<Integer>[] emptyUseSlot = new List[itemAmountWrappers.length];
        for (int i = 0; i < totalAmount.length; i++) {
            totalAmount[i] = amount * itemAmountWrappers[i].getAmount();
            pushSlot[i] = new ArrayList<>();
            emptyUseSlot[i] = new ArrayList<>();
        }

        ItemWrapper itemWrapper = new ItemWrapper();
        List<Integer> emptySlotList = new ArrayList<>();
        for (int slot : slots) {
            ItemStack itemStack = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(itemStack)) {
                emptySlotList.add(slot);
            } else if (itemStack.getAmount() < itemStack.getMaxStackSize()) {
                itemWrapper.newWrap(itemStack);
                for (int i = 0; i < itemAmountWrappers.length; i++) {
                    if (totalAmount[i] > 0 && ItemStackUtil.isItemSimilar(itemWrapper, itemAmountWrappers[i])) {
                        pushSlot[i].add(slot);
                        totalAmount[i] -= Math.min(totalAmount[i], itemStack.getMaxStackSize() - itemStack.getAmount());
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < totalAmount.length; i++) {
            if (totalAmount[i] > 0) {
                Iterator<Integer> iterator = emptySlotList.iterator();
                while (iterator.hasNext()) {
                    Integer slot = iterator.next();
                    iterator.remove();
                    emptyUseSlot[i].add(slot);
                    totalAmount[i] -= Math.min(totalAmount[i], itemAmountWrappers[i].getItemStack().getMaxStackSize());
                    if (totalAmount[i] == 0) {
                        break;
                    }
                }
                if (totalAmount[i] > 0) {
                    return false;
                }
            }
        }

        int count;
        for (int i = 0; i < totalAmount.length; i++) {
            count =  amount * itemAmountWrappers[i].getAmount();
            for (int slot : pushSlot[i]) {
                ItemStack itemStack = inventory.getItem(slot);
                int n = Math.min(count, itemStack.getMaxStackSize() - itemStack.getAmount());
                itemStack.setAmount(itemStack.getAmount() + n);
                count -= n;
            }

            for (int slot : emptyUseSlot[i]) {
                int n = Math.min(count, itemAmountWrappers[i].getItemStack().getMaxStackSize());
                inventory.setItem(slot, itemAmountWrappers[i].getItemStack());
                inventory.getItem(slot).setAmount(n);
                count -= n;
            }
        }

        return true;
    }

    public static int tryPushItem(@Nonnull Inventory inventory, int[] slots, @Nonnull ItemStack... itemStacks) {
        return tryPushItem(inventory, slots, 1, ItemStackUtil.calItemArrayWithAmount(itemStacks));
    }

    public static int tryPushItem(@Nonnull Inventory inventory, int[] slots, int amount, @Nonnull ItemStack... itemStacks) {
        return tryPushItem(inventory, slots, amount, ItemStackUtil.calItemArrayWithAmount(itemStacks));
    }

    public static int tryPushItem(@Nonnull Inventory inventory, int[] slots, @Nonnull ItemAmountWrapper... itemAmountWrappers) {
        return tryPushItem(inventory, slots, 1, itemAmountWrappers);
    }

    /**
     * Push items to the inventory with given amount
     * @param amount how many items should be pushed
     * @return amount that is actually can be pushed and successfully pushed
     * for example:
     *      amount = 4
     *      itemAmountWrappers = [cobblestone with 12 amount]
     *      in this case, 4 * 12 = 48 cobblestones will be pushed to the inventory, and it will return 4
     *      or maybe 2 * 12 = 24 cobblestones will be pushed to the inventory, and it will return 2
     */
    public static int tryPushItem(@Nonnull Inventory inventory, int[] slots, int amount, @Nonnull ItemAmountWrapper... itemAmountWrappers) {
        if (itemAmountWrappers.length == 0) {
            return 0;
        }
        int[] totalAmount = new int[itemAmountWrappers.length];
        int[] maxMatchAmount = new int[itemAmountWrappers.length];
        int[] matchAmount = new int[itemAmountWrappers.length];
        List<Integer>[] pushSlot = new List[itemAmountWrappers.length];
        List<Integer>[] emptyUseSlot = new List[itemAmountWrappers.length];
        for (int i = 0; i < totalAmount.length; i++) {
            totalAmount[i] = 0;
            maxMatchAmount[i] = amount * itemAmountWrappers[i].getAmount();
            matchAmount[i] = 0;
            pushSlot[i] = new ArrayList<>();
            emptyUseSlot[i] = new ArrayList<>();
        }

        ItemWrapper itemWrapper = new ItemWrapper();
        List<Integer> emptySlotList = new ArrayList<>();
        for (int slot : slots) {
            ItemStack itemStack = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(itemStack)) {
                emptySlotList.add(slot);
            } else if (itemStack.getAmount() < itemStack.getMaxStackSize()) {
                itemWrapper.newWrap(itemStack);
                for (int i = 0; i < itemAmountWrappers.length; i++) {
                    if (totalAmount[i] < maxMatchAmount[i] && ItemStackUtil.isItemSimilar(itemWrapper, itemAmountWrappers[i])) {
                        pushSlot[i].add(slot);
                        totalAmount[i] = Math.min(totalAmount[i] + itemStack.getMaxStackSize() - itemStack.getAmount(), maxMatchAmount[i]);
                        matchAmount[i] = totalAmount[i] / itemAmountWrappers[i].getAmount();
                        break;
                    }
                }
            }
        }

        int minMatch;
        int minMatchP;
        boolean allFull;
        for (int slot : emptySlotList) {
            minMatchP = 0;
            minMatch = matchAmount[0];
            allFull = true;

            for (int i = 0; i < totalAmount.length; i++) {
                if (matchAmount[i] < amount) {
                    allFull = false;
                    if (minMatch > matchAmount[i]) {
                        minMatchP = i;
                        minMatch = matchAmount[i];
                    }
                }
            }

            if (allFull) {
                break;
            } else {
                emptyUseSlot[minMatchP].add(slot);
                totalAmount[minMatchP] = Math.min(totalAmount[minMatchP] + itemAmountWrappers[minMatchP].getItemStack().getMaxStackSize(), maxMatchAmount[minMatchP]);
                matchAmount[minMatchP] = totalAmount[minMatchP] / itemAmountWrappers[minMatchP].getAmount();
            }
        }

        minMatch = matchAmount[0];
        for (int i = 1; i < totalAmount.length; i++) {
            minMatch = Math.min(minMatch, matchAmount[i]);
        }

        int count;
        for (int i = 0; i < totalAmount.length; i++) {
            count =  minMatch * itemAmountWrappers[i].getAmount();
            for (int slot : pushSlot[i]) {
                ItemStack itemStack = inventory.getItem(slot);
                int n = Math.min(count, itemStack.getMaxStackSize() - itemStack.getAmount());
                itemStack.setAmount(itemStack.getAmount() + n);
                count -= n;
            }

            for (int slot : emptyUseSlot[i]) {
                int n = Math.min(count, itemAmountWrappers[i].getItemStack().getMaxStackSize());
                inventory.setItem(slot, itemAmountWrappers[i].getItemStack());
                inventory.getItem(slot).setAmount(n);
                count -= n;
            }
        }

        return minMatch;
    }

    /**
     * @return how many items truly dropped in stack
     */
    public static int dropItems(@Nonnull Inventory inventory, @Nonnull Location location, int... slots) {
        return InventoryUtil.dropItems(inventory, location.getWorld(), location, slots);
    }

    /**
     * @return how many items truly dropped in stack
     */
    public static int dropItems(@Nonnull Inventory inventory, @Nonnull World world, @Nonnull Location location, int... slots) {
        int amount = 0;
        for (int slot : slots) {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack != null) {
                inventory.clear(slot);
                world.dropItemNaturally(location, itemStack);
                amount++;
            }
        }
        return amount;
    }

    public static int closeInv(@Nonnull Inventory inventory) {
        List<HumanEntity> humanEntityList = new ArrayList<>(inventory.getViewers());
        for (HumanEntity humanEntity : humanEntityList) {
            humanEntity.closeInventory();
        }
        return humanEntityList.size();
    }
}
