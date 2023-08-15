package io.taraxacum.libs.slimefun.dto;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.finaltech.FinalTech;

import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;

public class ItemCodeTable {
    private final static int CODE_LENGTH = 8;
    private final static int CODE_VALUE_LENGTH = 16;
    private final static long POSSIBLE_CODE_LENGTH = (long) Math.pow(CODE_LENGTH, CODE_VALUE_LENGTH);

    private final Map<String, Long> itemCodeMap = new HashMap<>(Slimefun.getRegistry().getEnabledSlimefunItems().size());
    private final Random random = new Random(FinalTech.getConfigManager().getOrDefault((long)(new Random().nextDouble() * Long.MAX_VALUE), "random", "item-code", "value"));
    private final Map<Integer, List<String>> shortestCraftItemMap = new HashMap<>();
    private final Map<Integer, List<String>> longestCraftItemMap = new HashMap<>();
    private final Map<String, Integer> itemShortestCraftMap = new HashMap<>();
    private final Map<String, Integer> itemLongestCraftMap = new HashMap<>();

    private static volatile ItemCodeTable instance;

    private ItemCodeTable() {

    }

    public static void init() {

    }

    public Long convertToLong(@Nonnull String code) {
        return 0L;
    }

    public Long calCode(@Nonnull ItemStack itemStack) {
        SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        if (slimefunItem != null && ItemStackUtil.isItemSimilar(itemStack, slimefunItem.getItem())) {
            return this.getOrCalCode(slimefunItem);
        }

        return null;
    }

    public Long getOrCalCode(@Nonnull String id) {
        if (this.itemCodeMap.containsKey(id)) {
            return this.itemCodeMap.get(id);
        }

        Integer shortestCraft = this.calShortestCraft(id);
        Integer longestCraft = this.calLongestCraft(id);

        return null;
    }

    public Long getOrCalCode(@Nonnull SlimefunItem slimefunItem) {
        if (SlimefunItem.getById(slimefunItem.getId()) != null && ItemStackUtil.isItemSimilar(slimefunItem.getItem(), SlimefunItem.getById(slimefunItem.getId()).getItem())) {
            if (this.itemCodeMap.containsKey(slimefunItem.getId())) {
                return this.itemCodeMap.get(slimefunItem.getId());
            } else {

            }
        } else {
            return this.calCode(slimefunItem.getItem());
        }
        return 0L;
    }

    private Integer calShortestCraft(@Nonnull String id) {
        if(this.itemShortestCraftMap.containsKey(id)) {
            return this.itemShortestCraftMap.get(id);
        }
        int times = 1;
        this.setShortestCraft(id, times);

        SlimefunItem slimefunItem = SlimefunItem.getById(id);
        if(slimefunItem == null) {
            this.setShortestCraft(id, times);
            return times;
        }

        for(ItemStack itemStack : slimefunItem.getRecipe()) {
            SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
            if(sfItem != null) {
                times = times == 1 ? this.calShortestCraft(sfItem.getId()) + 1 : Math.min(times, this.calShortestCraft(sfItem.getId()) + 1);
            }
        }

        this.setShortestCraft(id, times);

        return times;
    }

    private void setShortestCraft(@Nonnull String id, int times) {
        if(this.itemShortestCraftMap.containsKey(id)) {
            Integer oldTimes = this.itemShortestCraftMap.get(id);
            this.itemShortestCraftMap.remove(id);
            List<String> idList = this.shortestCraftItemMap.get(oldTimes);
            if(idList != null) {
                idList.remove(id);
            }
        }

        this.itemShortestCraftMap.put(id, times);
        this.shortestCraftItemMap.computeIfAbsent(times, k -> new ArrayList<>()).add(id);
    }

    private Integer calLongestCraft(@Nonnull String id) {
        if(this.itemLongestCraftMap.containsKey(id)) {
            return this.itemLongestCraftMap.get(id);
        }
        int times = 1;
        this.setLongestCraft(id, times);

        SlimefunItem slimefunItem = SlimefunItem.getById(id);
        if(slimefunItem == null) {
            this.setShortestCraft(id, times);
            return times;
        }

        for(ItemStack itemStack : slimefunItem.getRecipe()) {
            SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
            if(sfItem != null) {
                times = times == 1 ? this.calLongestCraft(sfItem.getId()) + 1 : Math.min(times, this.calLongestCraft(sfItem.getId()) + 1);
            }
        }

        this.setLongestCraft(id, times);

        return times;
    }

    private void setLongestCraft(@Nonnull String id, int times) {
        if(this.itemLongestCraftMap.containsKey(id)) {
            Integer oldTimes = this.itemLongestCraftMap.get(id);
            this.itemLongestCraftMap.remove(id);
            List<String> idList = this.longestCraftItemMap.get(oldTimes);
            if(idList != null) {
                idList.remove(id);
            }
        }

        this.itemLongestCraftMap.put(id, times);
        this.longestCraftItemMap.computeIfAbsent(times, k -> new ArrayList<>()).add(id);
    }

    @Nonnull
    public static ItemCodeTable getInstance() {
        if (instance == null) {
            synchronized (ItemCodeTable.class) {
                if (instance == null) {
                    instance = new ItemCodeTable();
                }
            }
        }
        return instance;
    }

    public static class Code {
        private Long value;

        public Code(Long value) {
            this.value = value;
        }

        public Long getValue() {
            return value;
        }

        public void addValue(Long value) {
            this.value += value;
            this.value %= POSSIBLE_CODE_LENGTH;
        }

        public String getAsString() {
            this.value %= POSSIBLE_CODE_LENGTH;

            long value = this.value;
            StringBuilder stringBuilder = new StringBuilder(CODE_LENGTH);
            for(long i = 0; i < CODE_LENGTH; i++) {
                stringBuilder.append(value % CODE_VALUE_LENGTH);
                value /= CODE_VALUE_LENGTH;
            }

            return stringBuilder.toString();
        }

        public String getS(int i) {
            return switch (i) {
                case 1 -> "1";
                case 2 -> "2";
                case 3 -> "3";
                case 4 -> "4";
                case 5 -> "5";
                case 6 -> "6";
                case 7 -> "7";
                case 8 -> "8";
                case 9 -> "9";
                case 10 -> "10";
                case 11 -> "11";
                case 12 -> "12";
                case 13 -> "13";
                case 14 -> "14";
                case 15 -> "15";
                default -> "0";
            };
        }
    }
}
