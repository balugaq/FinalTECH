package io.taraxacum.finaltech.core.inventory.common;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerPreResearchEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemState;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.group.RecipeItemGroup;
import io.taraxacum.finaltech.core.group.SubFlexItemGroup;
import io.taraxacum.finaltech.core.interfaces.VisibleItem;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.dto.SimpleVirtualInventory;
import io.taraxacum.libs.plugin.service.InventoryHistoryService;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SubFlexItemGroupInventory extends SimpleVirtualInventory {
    private final int backSlot = 1;
    private final int previousSlot = 3;
    private final int nextSlot = 5;
    private final int iconSlot = 7;
    private final int[] border = new int[] {0, 2, 4, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
    private final int[][] mainContentSlot = new int[][] {
            new int[] {18, 19, 20, 21, 22, 23, 24, 25, 26},
            new int[] {27, 28, 29, 30, 31, 32, 33, 34, 35},
            new int[] {36, 37, 38, 39, 40, 41, 42, 43, 44},
            new int[] {45, 46, 47, 48, 49, 50, 51, 52, 53}};

    private final int size = 54;

    private final Player player;
    private final PlayerProfile playerProfile;
    private final SlimefunGuideMode slimefunGuideMode;
    private final InventoryHistoryService inventoryHistoryService;
    private final ItemStack itemStack;
    private final List<List<SlimefunItem>> slimefunItemList;
    private final int page;

    @Nullable
    private SubFlexItemGroup subFlexItemGroup;

    public SubFlexItemGroupInventory(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode, @Nonnull InventoryHistoryService inventoryHistoryService, @Nonnull SubFlexItemGroup subFlexItemGroup, int page) {
        super(54, ItemStackUtil.getItemName(subFlexItemGroup.getItem(player)));
        this.player = player;
        this.playerProfile = playerProfile;
        this.slimefunGuideMode = slimefunGuideMode;
        this.inventoryHistoryService = inventoryHistoryService;
        this.itemStack = subFlexItemGroup.getItem(player);
        this.slimefunItemList = subFlexItemGroup.getSlimefunItemList();
        this.page = page;

        this.subFlexItemGroup = subFlexItemGroup;

        this.init();;
    }

    public SubFlexItemGroupInventory(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode, @Nonnull InventoryHistoryService inventoryHistoryService, @Nonnull ItemStack itemStack, @Nonnull List<List<SlimefunItem>> slimefunItemList, int page) {
        super(54, ItemStackUtil.getItemName(itemStack));
        this.player = player;
        this.playerProfile = playerProfile;
        this.slimefunGuideMode = slimefunGuideMode;
        this.inventoryHistoryService = inventoryHistoryService;
        this.itemStack = itemStack;
        this.slimefunItemList = slimefunItemList;
        this.page = page;

        this.init();
    }

    @Nonnull
    protected SubFlexItemGroupInventory generateByPage(int page) {
        if (this.subFlexItemGroup != null) {
            return new SubFlexItemGroupInventory(this.player, this.playerProfile, this.slimefunGuideMode, this.inventoryHistoryService, this.subFlexItemGroup, page);
        } else {
            return new SubFlexItemGroupInventory(this.player, this.playerProfile, this.slimefunGuideMode, this.inventoryHistoryService, this.itemStack, this.slimefunItemList, page);
        }
    }

    protected void init() {
        this.init(this.page);
    }

    protected void init(int page) {
        List<List<SlimefunItem>> slimefunItemList = new ArrayList<>();
        for (List<SlimefunItem> list : this.slimefunItemList) {
            List<SlimefunItem> temp = new ArrayList<>();
            for (SlimefunItem slimefunItem : list) {
                if (slimefunItem instanceof VisibleItem visibleItem) {
                    if (visibleItem.isVisible(this.player)) {
                        temp.add(slimefunItem);
                    }
                    continue;
                }

                if (slimefunItem.getState() != ItemState.ENABLED) {
                    continue;
                }

                temp.add(slimefunItem);
            }
            if (!temp.isEmpty()) {
                slimefunItemList.add(temp);
            }
        }

        this.setAllowClickPlayerInventory(false);

        for (int slot : JavaUtil.generateInts(this.size)) {
            this.setOnClick(slot, this.CANCEL_CLICK_CONSUMER);
        }

        this.setOnOpen(inventoryOpenEvent -> {
            this.inventoryHistoryService.tryAddToLast(this.player, this);
            this.player.playSound(this.player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
        });

        page = Math.min(page, (slimefunItemList.size() - 1) / this.mainContentSlot.length + 1);
        page = Math.max(page, 1);
        final int finalPage = page;

        this.getInventory().setItem(this.backSlot, ChestMenuUtils.getBackButton(this.player));
        this.setOnClick(this.backSlot, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            if (inventoryClickEvent.getClick().isShiftClick()) {
                this.inventoryHistoryService.openHome(this.player);
            } else {
                this.inventoryHistoryService.removeThenOpenLast(this.player);
            }
        });

        this.getInventory().setItem(this.previousSlot, ChestMenuUtils.getPreviousButton(this.player, finalPage, (slimefunItemList.size() - 1) / this.mainContentSlot.length + 1));
        this.setOnClick(this.previousSlot, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            if (this.subFlexItemGroup != null && this.inventoryHistoryService.canBeAddToLast(this.subFlexItemGroup)) {
                this.inventoryHistoryService.removeLast(this.player);
                this.subFlexItemGroup.generateByPage(Math.max(finalPage - 1, 1)).open(this.player, this.playerProfile, this.slimefunGuideMode);
            } else {
                SubFlexItemGroupInventory subFlexItemGroupInventory = this.generateByPage(Math.max(finalPage - 1, 1));
                this.inventoryHistoryService.tryReplaceLast(this.player, subFlexItemGroupInventory);
                subFlexItemGroupInventory.open(this.player);
            }
        });

        this.getInventory().setItem(this.nextSlot, ChestMenuUtils.getNextButton(this.player, finalPage, (slimefunItemList.size() - 1) / this.mainContentSlot.length + 1));
        this.setOnClick(this.nextSlot, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            if (this.subFlexItemGroup != null && this.inventoryHistoryService.canBeAddToLast(this.subFlexItemGroup)) {
                this.inventoryHistoryService.removeLast(this.player);
                this.subFlexItemGroup.generateByPage(Math.min(finalPage + 1, (slimefunItemList.size() - 1) / this.mainContentSlot.length + 1)).open(this.player, this.playerProfile, this.slimefunGuideMode);
            } else {
                SubFlexItemGroupInventory subFlexItemGroupInventory = this.generateByPage(Math.min(finalPage + 1, (slimefunItemList.size() - 1) / this.mainContentSlot.length + 1));
                this.inventoryHistoryService.tryReplaceLast(this.player, subFlexItemGroupInventory);
                subFlexItemGroupInventory.open(this.player);
            }
        });

        this.getInventory().setItem(this.iconSlot, this.itemStack);

        for (int slot : this.border) {
            this.getInventory().setItem(slot, ChestMenuUtils.getBackground());
        }

        for (int i = 0; i < this.mainContentSlot.length; i++) {
            int index = i + finalPage * this.mainContentSlot.length - this.mainContentSlot.length;
            if (index < slimefunItemList.size()) {
                List<SlimefunItem> slimefunItems = slimefunItemList.get(index);
                for (int j = 0; j < slimefunItems.size(); j++) {
                    SlimefunItem slimefunItem = slimefunItems.get(j);
                    Research research = slimefunItem.getResearch();
                    if (research == null || this.playerProfile.hasUnlocked(research)) {
                        ItemStack itemStack = MachineUtil.cloneAsDescriptiveItem(slimefunItem);
                        ItemStackUtil.addLoreToFirst(itemStack, "§7" + slimefunItem.getId());
                        this.getInventory().setItem(this.mainContentSlot[i][j], itemStack);
                        this.setOnClick(this.mainContentSlot[i][j], inventoryClickEvent -> {
                            inventoryClickEvent.setCancelled(true);

                            RecipeItemGroup recipeItemGroup = RecipeItemGroup.getByItemStack(this.player, this.playerProfile, this.slimefunGuideMode, this.inventoryHistoryService, slimefunItem.getItem());
                            if (recipeItemGroup != null) {
                                Bukkit.getScheduler().runTask(FinalTech.getInstance(), () -> recipeItemGroup.open(this.player, this.playerProfile, this.slimefunGuideMode));
                            }
                        });
                    } else {
                        ItemStack icon = MachineUtil.getLockedItem(this.player, research);
                        this.getInventory().setItem(this.mainContentSlot[i][j], icon);
                        this.setOnClick(this.mainContentSlot[i][j], inventoryClickEvent -> {
                            inventoryClickEvent.setCancelled(true);

                            PlayerPreResearchEvent event = new PlayerPreResearchEvent(this.player, research, slimefunItem);
                            Bukkit.getPluginManager().callEvent(event);

                            if (!event.isCancelled() && !this.playerProfile.hasUnlocked(research)) {
                                if (research.canUnlock(this.player)) {
                                    Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE).unlockItem(this.player, slimefunItem, player -> this.init());
                                } else {
                                    this.init();
                                    Slimefun.getLocalization().sendMessage(this.player, "messages.not-enough-xp", true);
                                }
                            } else {
                                this.init();
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void open(@Nonnull Player player) {
        this.init();
        super.open(player);
    }
}
