package io.taraxacum.finaltech.core.inventory.manual;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enums.LogSourceType;
import io.taraxacum.finaltech.core.item.machine.manual.ItemDismantleTable;
import io.taraxacum.finaltech.core.item.unusable.ReplaceableCard;
import io.taraxacum.finaltech.core.option.Icon;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.file.LanguageManager;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.inventory.template.LogicInventory;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.interfaces.ValidItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public class ItemDismantleTableInventory extends AbstractManualMachineInventory implements LogicInventory {
    private final int[] border = new int[] {3, 4, 12, 21, 22};
    private final int[] inputBorder = new int[] {0, 1, 2, 9, 11, 18, 19, 20};
    private final int[] outputBorder = new int[] {5, 14, 23};
    private final int[] inputSlot = new int[] {10};
    private final int[] outputSlot = new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26};

    private final int statusSlot = 13;

    private final ItemDismantleTable itemDismantleTable;

    public ItemDismantleTableInventory(@Nonnull ItemDismantleTable itemDismantleTable) {
        super(itemDismantleTable);
        this.itemDismantleTable = itemDismantleTable;
    }

    @Nonnull
    @Override
    protected int[] getBorder() {
        return this.border;
    }

    @Nonnull
    @Override
    protected int[] getInputBorder() {
        return this.inputBorder;
    }

    @Nonnull
    @Override
    protected int[] getOutputBorder() {
        return this.outputBorder;
    }

    @Nullable
    @Override
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        return slot == this.statusSlot ? inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
            if (locationData == null) {
                return;
            }

            String countStr = FinalTech.getLocationDataService().getLocationData(locationData, this.itemDismantleTable.getKey());
            if (countStr == null) {
                countStr = StringNumberUtil.ZERO;
            }
            Inventory inventory = inventoryClickEvent.getInventory();
            if(StringNumberUtil.compare(countStr, this.itemDismantleTable.getCount()) >= 0
                    && InventoryUtil.isEmpty(inventory, this.outputSlot)) {
                ItemStack item = inventory.getItem(this.inputSlot[0]);
                SlimefunItem slimefunItem = SlimefunItem.getByItem(item);
                if (slimefunItem != null
                        && this.itemDismantleTable.calAllowed(slimefunItem)
                        && item.getAmount() >= slimefunItem.getRecipeOutput().getAmount()) {
                    boolean verify;
                    if (slimefunItem instanceof ValidItem validItem) {
                        verify = validItem.verifyItem(item);
                    } else {
                        verify = ItemStackUtil.isItemSimilar(item, slimefunItem.getRecipeOutput()) && ItemStackUtil.isEnchantmentSame(item, slimefunItem.getRecipeOutput());
                    }
                    if (verify) {
                        int amount = item.getAmount() / slimefunItem.getRecipeOutput().getAmount();
                        for (ItemStack outputItem : slimefunItem.getRecipe()) {
                            if (!ItemStackUtil.isItemNull(outputItem)) {
                                amount = Math.min(amount, outputItem.getMaxStackSize() / outputItem.getAmount());
                            }
                        }
                        item.setAmount(item.getAmount() - slimefunItem.getRecipeOutput().getAmount() * amount);
                        if (slimefunItem instanceof ValidItem) {
                            FinalTech.getLogService().subItem(slimefunItem.getId(), slimefunItem.getRecipeOutput().getAmount() * amount, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, location, this.slimefunItem.getAddon().getJavaPlugin());
                        }
                        for (int i = 0; i < ItemDismantleTableInventory.this.outputSlot.length && i < slimefunItem.getRecipe().length; i++) {
                            if (!ItemStackUtil.isItemNull(slimefunItem.getRecipe()[i])) {
                                ItemStack outputItem;
                                ReplaceableCard replaceableCard = RecipeUtil.getReplaceableCard(slimefunItem.getRecipe()[i]);
                                if (replaceableCard != null && replaceableCard.getExtraSourceMaterial() != null) {
                                    outputItem = replaceableCard.getItem();
                                } else {
                                    outputItem = slimefunItem.getRecipe()[i];
                                }
                                inventory.setItem(this.outputSlot[i], outputItem);
                                outputItem = inventory.getItem(this.outputSlot[i]);
                                outputItem.setAmount(outputItem.getAmount() * amount);
                            }
                        }

                        FinalTech.getLocationDataService().setLocationData(locationData, this.itemDismantleTable.getKey(), StringNumberUtil.sub(countStr, this.itemDismantleTable.getCount()));
                    }
                }
            }
        } : super.onClick(location, slot);
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(this.statusSlot, Icon.STATUS_ICON);
    }

    @Nonnull
    @Override
    public int[] requestSlots() {
        return new int[0];
    }

    @Nonnull
    @Override
    public int[] requestSlots(@Nonnull RequestType requestType) {
        return switch (requestType) {
            case INPUT -> this.inputSlot;
            case OUTPUT -> this.outputSlot;
            default -> new int[0];
        };
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
        if (locationData == null) {
            return;
        }

        ItemStack itemStack = inventory.getItem(statusSlot);
        if(!ItemStackUtil.isItemNull(itemStack)) {
            String countStr = FinalTech.getLocationDataService().getLocationData(locationData, this.itemDismantleTable.getKey());
            if (countStr == null) {
                countStr = StringNumberUtil.ZERO;
            }

            LanguageManager languageManager = FinalTech.getLanguageManager();
            ItemStackUtil.setLore(itemStack,
                    languageManager.replaceStringList(languageManager.getStringList("items", this.getId(), "status-icon", "lore"),
                            countStr,
                            this.itemDismantleTable.getCount()));

            if(StringNumberUtil.compare(countStr, this.itemDismantleTable.getCount()) >= 0) {
                itemStack.setType(Material.GREEN_STAINED_GLASS_PANE);
            } else {
                itemStack.setType(Material.RED_STAINED_GLASS_PANE);
            }
        }

    }
}
