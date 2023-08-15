package io.taraxacum.finaltech.core.inventory.simple;

import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enchantment.NullEnchantment;
import io.taraxacum.finaltech.core.inventory.AbstractOrdinaryMachineInventory;
import io.taraxacum.finaltech.core.item.machine.range.StorageOperator;
import io.taraxacum.finaltech.core.option.EnableOption;
import io.taraxacum.finaltech.util.LocationUtil;
import io.taraxacum.finaltech.util.SqlUtil;
import io.taraxacum.libs.plugin.is.ItemWrapper;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.inventory.template.LogicInventory;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class StorageOperatorInventory extends AbstractOrdinaryMachineInventory implements LogicInventory {
    private final int[] border = new int[] {36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 47, 48, 50, 51, 53};
    private final int[] inputBorder = new int[0];
    private final int[] outputBorder = new int[0];
    private final int[] contentSlot = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};

    private final int locationSlot = 46;
    private final int itemSlot = 49;
    private final int enableSlot = 52;
    private final ItemStack locationIcon = ItemStackUtil.newItemStack(Material.BLUE_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", this.getId(), "location-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "location-icon", "lore"));
    private final ItemStack itemIcon = ItemStackUtil.newItemStack(Material.PURPLE_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", this.getId(), "item-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "item-icon", "lore"));

    protected final StorageOperator storageOperator;

    public StorageOperatorInventory(@Nonnull StorageOperator storageOperator) {
        super(storageOperator);
        this.storageOperator = storageOperator;
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
        return switch (slot) {
            case locationSlot -> this.onClickLocationSlot(location);
            case itemSlot-> this.onClickItemSlot(location);
            case enableSlot -> EnableOption.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            default -> super.onClick(location, slot);
        };
    }

    @Nonnull
    @Override
    public int[] getInputSlot() {
        return this.contentSlot;
    }

    @Nonnull
    @Override
    public int[] getOutputSlot() {
        return this.contentSlot;
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(this.locationSlot, this.locationIcon);
        this.defaultItemStack.put(this.itemSlot, this.itemIcon);
        this.defaultItemStack.put(this.enableSlot, EnableOption.OPTION.defaultIcon());
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        StorageOperator.ItemWithLocation info = this.storageOperator.getInfo(location);
        if(info == null) {
            info = this.storageOperator.getOrGenerateInfo(location);
        }
        ItemWrapper itemWrapper = info.getItemWrapper();
        Location targetLocation = info.getLocation();

        if (itemWrapper != null) {
            ItemStack itemStack = itemWrapper.getItemStack();
            if (!ItemStackUtil.isItemNull(itemStack)) {
                ItemStack icon = inventory.getItem(this.itemSlot);
                if(!ItemStackUtil.isItemNull(icon)) {
                    ItemStackUtil.setLore(icon,
                            FinalTech.getLanguageManager().replaceStringArray(FinalTech.getLanguageStringArray("items", this.getId(), "item-icon", "success-lore"),
                                ItemStackUtil.getItemName(itemStack)));
                    NullEnchantment.addAndHidden(icon);
                }
            }
        }

        if (targetLocation != null) {
            ItemStack icon = inventory.getItem(this.locationSlot);
            if (!ItemStackUtil.isItemNull(icon)) {
                ItemStackUtil.setLore(icon,
                        FinalTech.getLanguageManager().replaceStringArray(FinalTech.getLanguageStringArray("items", this.getId(), "location-icon", "success-lore"),
                                String.valueOf(location.getBlockX() - targetLocation.getBlockX()),
                                String.valueOf(location.getBlockY() - targetLocation.getBlockY()),
                                String.valueOf(location.getBlockZ() - targetLocation.getBlockZ())));
                NullEnchantment.addAndHidden(icon);
            }
        }

        EnableOption.OPTION.checkAndUpdateIcon(inventory, this.enableSlot, FinalTech.getLocationDataService(), location);
    }

    @Nonnull
    protected Consumer<InventoryClickEvent> onClickLocationSlot(@Nonnull Location location) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            if(inventoryClickEvent.getWhoClicked() instanceof Player player) {
                try {
                    ItemStack itemStack = player.getItemOnCursor();
                    if (ItemStackUtil.isItemNull(itemStack)) {
                        String locationStr = FinalTech.getLocationDataService().getLocationData(location, this.storageOperator.getKeyLocation());
                        if (locationStr != null) {
                            Location targetLocation = LocationUtil.stringToLocation(locationStr);
                            if (targetLocation != null
                                    && FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                                BlockMenu targetBlockMenu = slimefunLocationDataService.getBlockMenu(targetLocation);
                                if (targetBlockMenu != null && targetBlockMenu.canOpen(targetLocation.getBlock(), player)) {
                                    targetBlockMenu.open(player);
                                }
                            }
                        }
                    } else {
                        Location targetLocation = LocationUtil.parseLocationInItem(itemStack);
                        if (targetLocation != null) {
                            StorageOperator.ItemWithLocation info = this.storageOperator.getOrGenerateInfo(location);
                            info.setLocation(targetLocation);
                            FinalTech.getLocationDataService().setLocationData(location, this.storageOperator.getKeyLocation(), LocationUtil.locationToString(targetLocation));
                            this.updateInventory(inventoryClickEvent.getInventory(), location);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Nonnull
    protected Consumer<InventoryClickEvent> onClickItemSlot(@Nonnull Location location) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            try {
                ItemStack itemStack = ItemStackUtil.cloneItem(inventoryClickEvent.getWhoClicked().getItemOnCursor());
                if (!ItemStackUtil.isItemNull(itemStack)) {
                    itemStack.setAmount(1);
                    String itemStr = ItemStackUtil.itemStackToString(itemStack);
                    String sql = itemStr;
                    if (FinalTech.safeSql()) {
                        sql = SqlUtil.getSafeSql(itemStr);
                    }
                    if (sql != null) {
                        StorageOperator.ItemWithLocation info = this.storageOperator.getOrGenerateInfo(location);
                        info.setItemStr(itemStr);
                        info.setItemWrapper(new ItemWrapper(itemStack));
                        FinalTech.getLocationDataService().setLocationData(location, this.storageOperator.getKeyItem(), sql);
                        this.updateInventory(inventoryClickEvent.getInventory(), location);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public void updateEnable(@Nonnull Inventory inventory, @Nonnull LocationData locationData) {
        EnableOption.OPTION.checkAndUpdateIcon(inventory, this.enableSlot, FinalTech.getLocationDataService(), locationData);
    }
}
