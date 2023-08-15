package io.taraxacum.finaltech.core.inventory.common;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.core.group.CraftItemGroup;
import io.taraxacum.finaltech.core.group.RecipeItemGroup;
import io.taraxacum.finaltech.core.group.TypeItemGroup;
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
import java.util.List;

/**
 * @author Final_ROOT
 */
public class SlimefunItemSmallRecipeInventory extends SimpleVirtualInventory {
    private final int backSlot = 1;
    private final int recipeTypeSlot = 10;
    private final int recipeResultSlot = 16;
    private final int[] recipeContentSlot = new int[] {3, 4, 5, 12, 13, 14, 21, 22, 23};
    private final int infoSlot = 9;

    private final int[] border = new int[] {27, 29, 30, 31, 32, 33, 35};
    private final int previousSlot =  28;
    private final int nextSlot = 34;
    private final int[] workContent = new int[] {36, 45, 37, 46, 38, 47, 39, 48, 40, 49, 41, 50, 42, 51, 43, 52, 44, 53};

    private final Player player;
    private final PlayerProfile playerProfile;
    private final SlimefunGuideMode slimefunGuideMode;
    private final InventoryHistoryService inventoryHistoryService;
    private final SlimefunItem slimefunItem;

    private int page;

    @Nullable
    private RecipeItemGroup recipeItemGroup;

    public SlimefunItemSmallRecipeInventory(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode, @Nonnull InventoryHistoryService inventoryHistoryService, @Nonnull SlimefunItem slimefunItem, @Nonnull RecipeItemGroup recipeItemGroup, int page) {
        super(SlimefunItemSmallRecipeInventory.calSize(slimefunItem), ItemStackUtil.getItemName(recipeItemGroup.getItemStack()));

        this.player = player;
        this.playerProfile = playerProfile;
        this.slimefunGuideMode = slimefunGuideMode;
        this.inventoryHistoryService = inventoryHistoryService;
        this.slimefunItem = slimefunItem;

        this.page = page;

        this.recipeItemGroup = recipeItemGroup;

        this.init();
    }

    public SlimefunItemSmallRecipeInventory(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode, @Nonnull InventoryHistoryService inventoryHistoryService, @Nonnull SlimefunItem slimefunItem, int page) {
        super(SlimefunItemSmallRecipeInventory.calSize(slimefunItem), slimefunItem.getItemName());

        this.player = player;
        this.playerProfile = playerProfile;
        this.slimefunGuideMode = slimefunGuideMode;
        this.inventoryHistoryService = inventoryHistoryService;
        this.slimefunItem = slimefunItem;

        this.page = page;

        this.init();
    }

    @Nonnull
    protected SlimefunItemSmallRecipeInventory generateByPage(int page) {
        if (this.recipeItemGroup != null) {
            return new SlimefunItemSmallRecipeInventory(this.player, this.playerProfile, this.slimefunGuideMode, this.inventoryHistoryService, this.slimefunItem, this.recipeItemGroup, page);
        } else {
            return new SlimefunItemSmallRecipeInventory(this.player, this.playerProfile, this.slimefunGuideMode, this.inventoryHistoryService, this.slimefunItem, page);
        }
    }

    protected void init() {
        this.setAllowClickPlayerInventory(false);

        for (int slot : JavaUtil.generateInts(this.getSize())) {
            this.setOnClick(slot, CANCEL_CLICK_CONSUMER);
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

        this.getInventory().setItem(this.recipeTypeSlot, MachineUtil.cloneAsDescriptiveItem(this.slimefunItem.getRecipeType().toItem()));
        this.setOnClick(this.recipeTypeSlot, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            TypeItemGroup typeItemGroup = TypeItemGroup.getByRecipeType(this.slimefunItem.getRecipeType(), this.inventoryHistoryService);
            typeItemGroup.open(this.player, this.playerProfile, this.slimefunGuideMode);
        });

        this.getInventory().setItem(this.recipeResultSlot, MachineUtil.cloneAsDescriptiveItem(this.slimefunItem.getRecipeOutput()));
        this.setOnClick(this.recipeResultSlot, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            CraftItemGroup craftItemGroup = CraftItemGroup.getBySlimefunItem(this.slimefunItem, this.inventoryHistoryService);
            craftItemGroup.open(this.player, this.playerProfile, this.slimefunGuideMode);
        });

        for (int i = 0; i < this.slimefunItem.getRecipe().length; i++) {
            ItemStack itemStack = this.slimefunItem.getRecipe()[i];
            ItemStack icon = itemStack;
            SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
            if(sfItem != null && !this.playerProfile.hasUnlocked(sfItem.getResearch())) {
                icon = ChestMenuUtils.getNotResearchedItem();
            }
            if (!ItemStackUtil.isItemNull(icon)) {
                this.getInventory().setItem(this.recipeContentSlot[i], MachineUtil.cloneAsDescriptiveItem(icon));
            }
            this.setOnClick(this.recipeContentSlot[i], inventoryClickEvent -> {
                inventoryClickEvent.setCancelled(true);

                if (sfItem != null && this.playerProfile.hasUnlocked(sfItem.getResearch())) {
                    RecipeItemGroup recipeItemGroup = RecipeItemGroup.getByItemStack(this.player, this.playerProfile, this.slimefunGuideMode, this.inventoryHistoryService, itemStack);
                    if (recipeItemGroup != null) {
                        recipeItemGroup.open(this.player, this.playerProfile, this.slimefunGuideMode);
                    }
                }
            });
        }

        this.getInventory().setItem(this.infoSlot, Icon.generateInfoIcon(this.slimefunItem, this.player));

        this.setupWorkContent();
    }

    private void setupWorkContent() {
        if (this.slimefunItem instanceof RecipeDisplayItem recipeDisplayItem && !recipeDisplayItem.getDisplayRecipes().isEmpty()) {
            List<ItemStack> displayRecipeItemList = recipeDisplayItem.getDisplayRecipes();
            if (!displayRecipeItemList.isEmpty()) {
                for (int slot : this.border) {
                    this.getInventory().setItem(slot, ChestMenuUtils.getBackground());
                }
            }

            List<ItemStack> displayRecipes = recipeDisplayItem.getDisplayRecipes();

            this.getInventory().setItem(this.previousSlot, ChestMenuUtils.getPreviousButton(this.player, this.page, (displayRecipes.size() - 1) / this.workContent.length + 1));
            this.setOnClick(this.previousSlot, inventoryClickEvent -> {
                inventoryClickEvent.setCancelled(true);

                if (this.recipeItemGroup != null && this.inventoryHistoryService.canBeAddToLast(this.recipeItemGroup)) {
                    this.inventoryHistoryService.removeLast(this.player);
                    this.recipeItemGroup.generateByPage(Math.max(this.page - 1, 1)).open(this.player, this.playerProfile, this.slimefunGuideMode);
                } else {
                    SlimefunItemSmallRecipeInventory slimefunItemSmallRecipeInventory = this.generateByPage(Math.max(this.page - 1, 1));
                    this.inventoryHistoryService.tryReplaceLast(this.player, slimefunItemSmallRecipeInventory);
                    slimefunItemSmallRecipeInventory.open(this.player);
                }
            });

            this.getInventory().setItem(this.nextSlot, ChestMenuUtils.getNextButton(this.player, this.page, (displayRecipes.size() - 1) / this.workContent.length + 1));
            this.setOnClick(this.nextSlot, inventoryClickEvent -> {
                inventoryClickEvent.setCancelled(true);

                if (this.recipeItemGroup != null && this.inventoryHistoryService.canBeAddToLast(this.recipeItemGroup)) {
                    this.inventoryHistoryService.removeLast(this.player);
                    this.recipeItemGroup.generateByPage(Math.min(this.page + 1, (displayRecipes.size() - 1) / this.workContent.length + 1)).open(this.player, this.playerProfile, this.slimefunGuideMode);
                } else {
                    SlimefunItemSmallRecipeInventory slimefunItemSmallRecipeInventory = this.generateByPage(Math.min(this.page + 1, (displayRecipes.size() - 1) / this.workContent.length + 1));
                    this.inventoryHistoryService.tryReplaceLast(this.player, slimefunItemSmallRecipeInventory);
                    slimefunItemSmallRecipeInventory.open(this.player);
                }
            });

            int i;
            for (i = 0; i < this.workContent.length; i++) {
                int index = i + this.page * this.workContent.length - this.workContent.length;
                if (index < displayRecipes.size()) {
                    ItemStack itemStack = displayRecipes.get(index);
                    ItemStack icon = itemStack;
                    SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
                    if(slimefunItem != null && !this.playerProfile.hasUnlocked(slimefunItem.getResearch())) {
                        icon = ChestMenuUtils.getNotResearchedItem();
                    }
                    this.getInventory().setItem(this.workContent[i], MachineUtil.cloneAsDescriptiveItem(icon));
                    this.setOnClick(this.workContent[i], inventoryClickEvent -> {
                        inventoryClickEvent.setCancelled(true);

                        if (slimefunItem != null && this.playerProfile.hasUnlocked(slimefunItem.getResearch())) {
                            RecipeItemGroup recipeItemGroup = RecipeItemGroup.getByItemStack(this.player, this.playerProfile, this.slimefunGuideMode, this.inventoryHistoryService, itemStack);
                            if (recipeItemGroup != null) {
                                recipeItemGroup.open(this.player, this.playerProfile, this.slimefunGuideMode);
                            }
                        }
                    });
                } else {
                    this.getInventory().setItem(this.workContent[i], null);
                    this.setOnClick(this.workContent[i], CANCEL_CLICK_CONSUMER);
                }
            }
        }
    }

    protected static int calSize(@Nonnull SlimefunItem slimefunItem) {
        if (slimefunItem instanceof RecipeDisplayItem recipeDisplayItem && recipeDisplayItem.getDisplayRecipes().size() > 0) {
            return 54;
        } else {
            return 27;
        }
    }
}
