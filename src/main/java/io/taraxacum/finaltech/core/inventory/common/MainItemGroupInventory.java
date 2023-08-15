package io.taraxacum.finaltech.core.inventory.common;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.core.group.MainItemGroup;
import io.taraxacum.finaltech.core.group.SubFlexItemGroup;
import io.taraxacum.finaltech.core.option.Icon;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.inventory.SimpleVirtualInventory;
import io.taraxacum.libs.plugin.service.InventoryHistoryService;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainItemGroupInventory extends SimpleVirtualInventory {
    private final int backSlot = 1;
    private final int previousSlot = 3;
    private final int nextSlot = 5;
    private final int iconSlot = 7;
    private final int[] border = new int[] {0, 2, 4, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};

    private final int[] fatherItemGroupSlot = new int[] {18, 27, 36, 45};
    private final int[][] sonItemGroupSlot = new int[][] {
            new int[] {19, 20, 21, 22, 23, 24, 25, 26},
            new int[] {28, 29, 30, 31, 32, 33, 34, 35},
            new int[] {37, 38, 39, 40, 41, 42, 43, 44},
            new int[] {46, 47, 48, 49, 50, 51, 52, 53}
    };

    private final int size = 54;

    private final Player player;
    private final PlayerProfile playerProfile;
    private final SlimefunGuideMode slimefunGuideMode;
    private final InventoryHistoryService inventoryHistoryService;
    private final ItemStack icon;
    private final List<MainItemGroup.FatherSonItemGroup> fatherSonItemGroupList;
    private final int page;

    @Nullable
    private MainItemGroup mainItemGroup;

    public MainItemGroupInventory(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode, @Nonnull InventoryHistoryService inventoryHistoryService, @Nonnull ItemStack icon, @Nonnull MainItemGroup mainItemGroup, int page) {
        super(54, ItemStackUtil.getItemName(icon));

        this.player = player;
        this.playerProfile = playerProfile;
        this.slimefunGuideMode = slimefunGuideMode;
        this.inventoryHistoryService = inventoryHistoryService;
        this.icon = icon;
        this.fatherSonItemGroupList = mainItemGroup.getFatherSonItemGroupList();
        this.page = page;

        this.mainItemGroup = mainItemGroup;

        this.init();
    }

    /**
     * @param page start from 1
     */
    public MainItemGroupInventory(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode, @Nonnull InventoryHistoryService inventoryHistoryService, @Nonnull ItemStack icon, @Nonnull List<MainItemGroup.FatherSonItemGroup> fatherSonItemGroupList, int page) {
        super(54, ItemStackUtil.getItemName(icon));

        this.player = player;
        this.playerProfile = playerProfile;
        this.slimefunGuideMode = slimefunGuideMode;
        this.inventoryHistoryService = inventoryHistoryService;
        this.icon = icon;
        this.fatherSonItemGroupList = fatherSonItemGroupList;
        this.page = page;

        this.init();
    }

    @Nonnull
    protected MainItemGroupInventory generateByPage(int page) {
        if (this.mainItemGroup != null) {
            return new MainItemGroupInventory(this.player, this.playerProfile, this.slimefunGuideMode, this.inventoryHistoryService, this.icon, this.mainItemGroup, page);
        } else {
            return new MainItemGroupInventory(this.player, this.playerProfile, this.slimefunGuideMode, this.inventoryHistoryService, this.icon, this.fatherSonItemGroupList, page);
        }
    }

    protected void init() {
        this.init(this.page);
    }

    protected void init(int page) {
        LinkedHashMap<Integer, MainItemGroup.FatherSonItemGroup> itemGroupMap = new LinkedHashMap<>();
        int newStartLine = 1;
        for (MainItemGroup.FatherSonItemGroup fatherSonItemGroup : this.fatherSonItemGroupList) {
            List<ItemGroup> sonItemGoupList = new ArrayList<>();
            for (ItemGroup itemGroup : fatherSonItemGroup.getSonItemGoupList()) {
                if (itemGroup instanceof SubFlexItemGroup subFlexItemGroup) {
                    if (subFlexItemGroup.isTrulyVisible(this.player)) {
                        sonItemGoupList.add(subFlexItemGroup);
                    }
                } else if (itemGroup instanceof MainItemGroup mainItemGroup) {
                    if (mainItemGroup.isTrulyVisible(this.player)) {
                        sonItemGoupList.add(mainItemGroup);
                    }
                }else if (itemGroup instanceof FlexItemGroup flexItemGroup ? flexItemGroup.isVisible(this.player, this.playerProfile, this.slimefunGuideMode) : itemGroup.isVisible(this.player)) {
                    sonItemGoupList.add(itemGroup);
                }
            }

            if (sonItemGoupList.size() == 0) {
                continue;
            }

            itemGroupMap.put(newStartLine, new MainItemGroup.FatherSonItemGroup(fatherSonItemGroup.getFatherItemGroup(), sonItemGoupList));
            newStartLine += (sonItemGoupList.size() - 1) / 8 + 1;
        }

        int maxPage = (newStartLine - 2) / 4 + 1;
        page = Math.min(page, maxPage);
        page = Math.max(1, page);
        int pageStartLine = page * 4 - 3;
        int pageEndLine = page * 4;

        ItemGroup[] fatherItemGroup = new ItemGroup[4];
        ItemGroup[][] sonItemGroups = new ItemGroup[4][];
        for (Map.Entry<Integer, MainItemGroup.FatherSonItemGroup> itemGroupEntry : itemGroupMap.entrySet()) {
            Integer startLine = itemGroupEntry.getKey();
            List<ItemGroup> sonItemGoupList = itemGroupEntry.getValue().getSonItemGoupList();
            int lineSize = (sonItemGoupList.size() - 1) / 8 + 1;
            if (startLine >= pageStartLine && startLine <= pageEndLine) {
                fatherItemGroup[startLine - pageStartLine] = itemGroupEntry.getValue().getFatherItemGroup();
                for (int i = 0, limit = Math.min(pageEndLine - startLine, lineSize - 1); i <= limit; i++) {
                    ItemGroup[] itemGroups = new ItemGroup[Math.min(sonItemGoupList.size() - i * 8, 8)];
                    sonItemGroups[startLine - pageStartLine + i] = itemGroups;
                    for (int j = 0; j < itemGroups.length; j++) {
                        itemGroups[j] = sonItemGoupList.get(i * 8 + j);
                    }
                }
            } else {
                if (startLine + lineSize - 1 >= pageStartLine && startLine + lineSize - 1 <= pageEndLine) {
                    for (int i = 0, limit = startLine + lineSize - 1 - pageStartLine; i <= limit; i++) {
                        ItemGroup[] itemGroups = new ItemGroup[Math.min(sonItemGoupList.size() - (lineSize - i - 1) * 8, 8)];
                        sonItemGroups[startLine + lineSize - 1 - pageStartLine - i] = itemGroups;
                        for (int j = 0; j < itemGroups.length; j++) {
                            itemGroups[j] = itemGroupEntry.getValue().getSonItemGoupList().get((lineSize - i - 1) * 8 + j);
                        }
                    }
                }
            }
        }

        final int finalPage = page;

        this.setAllowClickPlayerInventory(false);

        for (int slot : JavaUtil.generateInts(this.size)) {
            this.setOnClick(slot, this.CANCEL_CLICK_CONSUMER);
            this.getInventory().clear(slot);
        }

        this.setOnOpen(inventoryOpenEvent -> {
            this.inventoryHistoryService.tryAddToLast(this.player, this);
            this.player.playSound(this.player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
        });

        this.getInventory().setItem(this.backSlot, ChestMenuUtils.getBackButton(this.player));
        this.setOnClick(this.backSlot, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            if (inventoryClickEvent.getClick().isShiftClick()) {
                this.inventoryHistoryService.openHome(this.player);
            } else {
                this.inventoryHistoryService.removeThenOpenLast(this.player);
            }
        });

        this.getInventory().setItem(this.previousSlot, ChestMenuUtils.getPreviousButton(this.player, page, maxPage));
        this.setOnClick(this.previousSlot, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            if (Math.max(finalPage - 1, 1) == this.page) {
                return;
            }

            if (this.mainItemGroup != null && this.inventoryHistoryService.canBeAddToLast(this.mainItemGroup)) {
                this.inventoryHistoryService.removeLast(this.player);
                this.mainItemGroup.generate(Math.max(finalPage - 1, 1), this.inventoryHistoryService).open(this.player, this.playerProfile, this.slimefunGuideMode);
            } else {
                MainItemGroupInventory mainItemGroupInventory = this.generateByPage(Math.max(finalPage - 1, 1));
                this.inventoryHistoryService.tryReplaceLast(this.player, mainItemGroupInventory);
                mainItemGroupInventory.open(this.player);
            }
        });

        this.getInventory().setItem(this.nextSlot, ChestMenuUtils.getNextButton(this.player, page, maxPage));
        this.setOnClick(this.nextSlot, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            if (Math.min(finalPage + 1, maxPage) == this.page) {
                return;
            }

            if (this.mainItemGroup != null && this.inventoryHistoryService.canBeAddToLast(this.mainItemGroup)) {
                this.inventoryHistoryService.removeLast(this.player);
                this.mainItemGroup.generate(Math.min(finalPage + 1, maxPage), this.inventoryHistoryService).open(this.player, this.playerProfile, this.slimefunGuideMode);
            } else {
                MainItemGroupInventory mainItemGroupInventory = this.generateByPage(Math.min(finalPage + 1, maxPage));
                this.inventoryHistoryService.tryReplaceLast(this.player, mainItemGroupInventory);
                mainItemGroupInventory.open(this.player);
            }
        });

        this.getInventory().setItem(this.iconSlot, MachineUtil.cloneAsDescriptiveItem(this.icon));

        for (int slot : this.border) {
            this.getInventory().setItem(slot, ChestMenuUtils.getBackground());
        }

        for (int i = 0; i < this.sonItemGroupSlot.length; i++) {
            ItemGroup itemGroup = fatherItemGroup[i];
            if (itemGroup != null) {
                this.getInventory().setItem(this.fatherItemGroupSlot[i], fatherItemGroup[i].getItem(this.player));
                this.setOnClick(this.fatherItemGroupSlot[i], inventoryClickEvent -> {
                    inventoryClickEvent.setCancelled(true);

                    if (itemGroup instanceof SubFlexItemGroup subFlexItemGroup) {
                        subFlexItemGroup.open(this.player, this.playerProfile, this.slimefunGuideMode, this.inventoryHistoryService);
                    } else if (itemGroup instanceof MainItemGroup mainItemGroup1) {
                        mainItemGroup1.open(this.player, this.playerProfile, this.slimefunGuideMode, this.inventoryHistoryService);
                    } else if (itemGroup instanceof FlexItemGroup flexItemGroup) {
                        flexItemGroup.open(this.player, this.playerProfile, this.slimefunGuideMode);
                    }
                });
            }

            ItemGroup[] itemGroups = sonItemGroups[i];
            if (itemGroups != null) {
                for (int j = 0; j < itemGroups.length; j++) {
                    ItemGroup sonItemGroup = itemGroups[j];
                    this.getInventory().setItem(this.sonItemGroupSlot[i][j], itemGroups[j].getItem(this.player));
                    this.setOnClick(this.sonItemGroupSlot[i][j], inventoryClickEvent -> {
                        inventoryClickEvent.setCancelled(true);

                        if (sonItemGroup instanceof SubFlexItemGroup subFlexItemGroup) {
                            subFlexItemGroup.open(this.player, this.playerProfile, this.slimefunGuideMode, this.inventoryHistoryService);
                        } else if (sonItemGroup instanceof MainItemGroup mainItemGroup1) {
                            mainItemGroup1.open(this.player, this.playerProfile, this.slimefunGuideMode, this.inventoryHistoryService);
                        } else if (sonItemGroup instanceof FlexItemGroup flexItemGroup) {
                            flexItemGroup.open(this.player, this.playerProfile, this.slimefunGuideMode);
                        }
                    });
                }
            }
        }
    }

    public void drawBackAsBorder() {
        this.getInventory().setItem(this.backSlot, Icon.BORDER_ICON);
        this.setOnClick(this.backSlot, CANCEL_CLICK_CONSUMER);
    }

    @Override
    public void open(@Nonnull Player player) {
        this.init();
        super.open(player);
    }
}
