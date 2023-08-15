package io.taraxacum.finaltech.core.group;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.inventory.common.SlimefunItemBigRecipeInventory;
import io.taraxacum.finaltech.core.inventory.common.SlimefunItemSmallRecipeInventory;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.inventory.SimpleVirtualInventory;
import io.taraxacum.libs.plugin.service.InventoryHistoryService;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Yeah it need to be updated.
 * @author Final_ROOT
 */
public class RecipeItemGroup extends FlexItemGroup {
    private static final int SMALL_LIMIT = 9;
    private static final int BIG_LIMIT = 36;

    private final InventoryHistoryService inventoryHistoryService;
    private final ItemStack itemStack;
    private final int page;

    public RecipeItemGroup(@Nonnull NamespacedKey key, @Nonnull SlimefunItem slimefunItem, @Nonnull InventoryHistoryService inventoryHistoryService, int page) {
        super(key, MachineUtil.cloneAsDescriptiveItem(slimefunItem));
        this.inventoryHistoryService = inventoryHistoryService;
        this.itemStack = slimefunItem.getItem();
        this.page = page;
    }

    public RecipeItemGroup(@Nonnull NamespacedKey key, @Nonnull ItemStack itemStack, @Nonnull InventoryHistoryService inventoryHistoryService, int page) {
        super(key, MachineUtil.cloneAsDescriptiveItem(itemStack));
        this.inventoryHistoryService = inventoryHistoryService;
        this.itemStack = itemStack;
        this.page = page;
    }

    @Override
    public boolean isVisible(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode) {
        return false;
    }

    @Override
    public void open(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode) {
        SimpleVirtualInventory simpleVirtualInventory = this.generateMenu(player, playerProfile, slimefunGuideMode);
        if (simpleVirtualInventory != null) {
            this.inventoryHistoryService.tryAddToLast(player, this);
            simpleVirtualInventory.open(player);
        } else {
            this.inventoryHistoryService.openHome(player);
        }
    }

    @Nullable
    private SimpleVirtualInventory generateMenu(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode) {
        SlimefunItem slimefunItem = SlimefunItem.getByItem(this.itemStack);
        if (slimefunItem != null) {
            if (slimefunItem.getRecipe().length <= SMALL_LIMIT) {
                return new SlimefunItemSmallRecipeInventory(player, playerProfile, slimefunGuideMode, this.inventoryHistoryService, slimefunItem, this, this.page);
            } else if (slimefunItem.getRecipe().length <= BIG_LIMIT) {
                return new SlimefunItemBigRecipeInventory(player, playerProfile, slimefunGuideMode, this.inventoryHistoryService, slimefunItem, this, this.page);
            } else {
                // TODO support vary large recipe of slimefunItem
                return null;
            }
        } else {
            // TODO vanilla item recipe
        }
        return null;
    }

    @Nonnull
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Nonnull
    public RecipeItemGroup generateByPage(int page) {
        // TODO remove finaltech
        return new RecipeItemGroup(new NamespacedKey(FinalTech.getInstance(), this.key.getKey() + "_" + page), this.itemStack, this.inventoryHistoryService, page);
    }

    @Nullable
    public static RecipeItemGroup getByItemStack(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode, @Nonnull InventoryHistoryService inventoryHistoryService, @Nullable ItemStack itemStack, int page) {
        SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        if (slimefunItem != null) {
            if (!playerProfile.hasUnlocked(slimefunItem.getResearch())) {
                return null;
            }

            return new RecipeItemGroup(new NamespacedKey(FinalTech.getInstance(), "SLIMEFUN_ITEM_" + slimefunItem.getId().hashCode()), slimefunItem, inventoryHistoryService, page);
        } else if (!ItemStackUtil.isItemNull(itemStack)) {
            if (ItemStackUtil.isRawMaterial(itemStack)) {
                // TODO vanilla item recipe
            } else {
                return null;
            }
        }
        return null;
    }

    @Nullable
    public static RecipeItemGroup getByItemStack(@Nonnull Player player, @Nonnull PlayerProfile playerProfile, @Nonnull SlimefunGuideMode slimefunGuideMode, @Nonnull InventoryHistoryService inventoryHistoryService, @Nullable ItemStack itemStack) {
        return RecipeItemGroup.getByItemStack(player, playerProfile, slimefunGuideMode, inventoryHistoryService, itemStack, 1);
    }
}
