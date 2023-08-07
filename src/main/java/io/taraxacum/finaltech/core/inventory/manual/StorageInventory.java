package io.taraxacum.finaltech.core.inventory.manual;

import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.item.machine.manual.storage.AbstractStorageMachine;
import io.taraxacum.finaltech.core.option.Icon;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.StringItemUtil;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.interfaces.LogicInventory;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public class StorageInventory extends AbstractManualMachineInventory implements LogicInventory {
    private final int[] border = new int[] {18, 19, 21, 23, 25, 26};
    private final int[] inputBorder = new int[0];
    private final int[] outputBorder = new int[0];
    private final int[] contentSlot = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
    private final int[] infoSlot = new int[] {27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50 ,51, 52, 53};

    private final int previousSlot = 20;
    private final int nextSlot = 24;
    private final int statusSlot = 22;

    private final ItemStack STATUS_ICON = ItemStackUtil.newItemStack(Material.GREEN_STAINED_GLASS_PANE,
            FinalTech.getLanguageManager().getString("items", this.getId(), "status", "name"),
            FinalTech.getLanguageManager().getStringArray("items", this.getId(), "status", "lore"));

    private final AbstractStorageMachine abstractStorageMachine;

    public StorageInventory(@Nonnull AbstractStorageMachine abstractStorageMachine) {
        super(abstractStorageMachine);
        this.abstractStorageMachine = abstractStorageMachine;
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
        Consumer<InventoryClickEvent> eventConsumer = this.onClickContent(location, slot);
        if (eventConsumer != null) {
            return eventConsumer;
        }
        return switch (slot) {
            case previousSlot -> this.onClickPrevious(location);
            case nextSlot -> this.onClickNext(location);
            case statusSlot -> this.onClickStatus(location);
            default -> super.onClick(location, slot);
        };
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(this.statusSlot, this.STATUS_ICON);
        for(int slot : this.border) {
            this.defaultItemStack.put(slot, Icon.SPECIAL_BORDER_ICON);
        }
    }

    @Nonnull
    @Override
    public int[] requestSlots() {
        return this.contentSlot;
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
        if (locationData != null) {
            Map<String, AbstractStorageMachine.ItemWithAmount> itemMap = this.abstractStorageMachine.getItemMap(locationData);
            String pageStr = FinalTech.getLocationDataService().getLocationData(locationData, this.abstractStorageMachine.getKeyPage());
            int maxPage = (itemMap.size() - 1) / this.infoSlot.length + 1;
            int page = pageStr == null ? 1 : Math.min(Integer.parseInt(pageStr), maxPage);
            int indexPage = page - 1;
            List<String> itemStringList = new ArrayList<>(itemMap.keySet());
            int i;
            for (i = 0; i < this.infoSlot.length && i + indexPage * this.infoSlot.length < itemStringList.size(); i++) {
                AbstractStorageMachine.ItemWithAmount itemWithAmount = itemMap.get(itemStringList.get(i + indexPage * this.infoSlot.length));
                ItemStack itemStack = MachineUtil.cloneAsDescriptiveItemWithLore(itemWithAmount.getItemWrapper().getItemStack(),
                        FinalTech.getLanguageManager().replaceStringArray(FinalTech.getLanguageStringArray("items", this.getId(), "show-item", "lore"),
                                itemWithAmount.getItemAmount()));
                inventory.setItem(this.infoSlot[i], itemStack);
            }
            for (; i < this.infoSlot.length; i++) {
                inventory.setItem(this.infoSlot[i], Icon.BORDER_ICON);
            }

            for (HumanEntity humanEntity : inventory.getViewers()) {
                if (humanEntity instanceof Player player) {
                    inventory.setItem(this.previousSlot, ChestMenuUtils.getPreviousButton(player, page, maxPage));
                    inventory.setItem(this.nextSlot, ChestMenuUtils.getNextButton(player, page, maxPage));
                    break;
                }
            }
        }
    }

    @Nonnull
    private Consumer<InventoryClickEvent> onClickPrevious(@Nonnull Location location) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            try {
                LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
                if (locationData != null) {
                    Map<String, AbstractStorageMachine.ItemWithAmount> itemMap = this.abstractStorageMachine.getItemMap(locationData);
                    String pageStr = FinalTech.getLocationDataService().getLocationData(locationData, this.abstractStorageMachine.getKeyPage());
                    int maxPage = (itemMap.size() - 1) / this.infoSlot.length + 1;
                    int page = pageStr == null ? 1 : Math.min(Integer.parseInt(pageStr), maxPage);
                    if(page > 1) {
                        page--;
                        FinalTech.getLocationDataService().setLocationData(locationData, this.abstractStorageMachine.getKeyPage(), String.valueOf(page));
                        this.updateInventory(inventoryClickEvent.getInventory(), location);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    @Nonnull
    private Consumer<InventoryClickEvent> onClickNext(@Nonnull Location location) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            try {
                LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
                if(locationData != null) {
                    Map<String, AbstractStorageMachine.ItemWithAmount> itemMap = this.abstractStorageMachine.getItemMap(locationData);
                    String pageStr = FinalTech.getLocationDataService().getLocationData(locationData, this.abstractStorageMachine.getKeyPage());
                    int maxPage = (itemMap.size() - 1) / this.infoSlot.length + 1;
                    int page = pageStr == null ? 1 : Math.min(Integer.parseInt(pageStr), maxPage);
                    if(page < maxPage) {
                        page++;
                        FinalTech.getLocationDataService().setLocationData(locationData, this.abstractStorageMachine.getKeyPage(), String.valueOf(page));
                        this.updateInventory(inventoryClickEvent.getInventory(), location);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    @Nonnull
    private Consumer<InventoryClickEvent> onClickStatus(@Nonnull Location location) {
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            try {
                LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
                if (locationData != null) {
                    HumanEntity humanEntity = inventoryClickEvent.getWhoClicked();
                    ItemStack itemStack = humanEntity.getItemOnCursor();
                    if (ItemStackUtil.isItemNull(itemStack)) {
                        Map<String, AbstractStorageMachine.ItemWithAmount> itemMap = this.abstractStorageMachine.getItemMap(locationData);
                        this.abstractStorageMachine.input(itemMap, locationData, inventoryClickEvent.getInventory(), this.contentSlot);
                        this.updateInventory(inventoryClickEvent.getInventory(), location);
                    } else {
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if (itemMeta != null && FinalTechItems.STORAGE_CARD.verifyItem(itemMeta)) {
                            ItemStack stringItem = StringItemUtil.parseItemInCard(itemMeta);
                            if (!ItemStackUtil.isItemNull(stringItem)) {
                                stringItem.setAmount(1);
                                String itemStr = ItemStackUtil.itemStackToString(stringItem);
                                String amount = StringItemUtil.parseAmountInCard(itemMeta);
                                StringItemUtil.clearCard(itemMeta);
                                FinalTechItems.STORAGE_CARD.updateLore(itemMeta);
                                itemStack.setItemMeta(itemMeta);
                                Map<String, AbstractStorageMachine.ItemWithAmount> itemMap = this.abstractStorageMachine.getItemMap(locationData);
                                String addedAmount = this.abstractStorageMachine.addItem(itemMap, locationData, itemStr, amount);
                                amount = StringNumberUtil.sub(amount, addedAmount);
                                if (StringNumberUtil.compare(amount, StringNumberUtil.ZERO) > 0) {
                                    StringItemUtil.setItemInCard(itemMeta, stringItem, amount);
                                    FinalTechItems.STORAGE_CARD.updateLore(itemMeta);
                                    itemStack.setItemMeta(itemMeta);
                                }
                                this.updateInventory(inventoryClickEvent.getInventory(), location);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    @Nullable
    private Consumer<InventoryClickEvent> onClickContent(@Nonnull Location location, int slot) {
        int i;
        for (i = 0; i < this.infoSlot.length; i++) {
            if (slot == this.infoSlot[i]) {
                break;
            }
        }
        if (i >= this.infoSlot.length) {
            return null;
        } else {
            final int index = i;
            return inventoryClickEvent -> {
                inventoryClickEvent.setCancelled(true);

                try {
                    LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
                    if (locationData != null) {
                        Map<String, AbstractStorageMachine.ItemWithAmount> itemMap = this.abstractStorageMachine.getItemMap(locationData);
                        String pageStr = FinalTech.getLocationDataService().getLocationData(locationData, this.abstractStorageMachine.getKeyPage());
                        int page = pageStr == null ? 1 : Math.min(Integer.parseInt(pageStr), (itemMap.size() - 1) / this.infoSlot.length + 1);
                        int indexPage = page - 1;
                        List<String> itemStringList = new ArrayList<>(itemMap.keySet());
                        if (!itemStringList.isEmpty()) {
                            String itemString = indexPage * this.infoSlot.length + index < itemStringList.size() ? itemStringList.get(indexPage * this.infoSlot.length + index) : null;
                            if (itemString != null) {
                                HumanEntity humanEntity = inventoryClickEvent.getWhoClicked();
                                AbstractStorageMachine.ItemWithAmount itemWithAmount = itemMap.get(itemString);
                                String amountStr = StringNumberUtil.min(String.valueOf(humanEntity.getInventory().getStorageContents().length * itemWithAmount.getItemWrapper().getItemStack().getMaxStackSize()), itemWithAmount.getItemAmount());
                                ItemStack itemStack = humanEntity.getItemOnCursor();
                                if (ItemStackUtil.isItemNull(itemStack)) {
                                    int validAmount = Integer.parseInt(amountStr);
                                    ClickType clickType = inventoryClickEvent.getClick();
                                    if(clickType.isShiftClick()) {
                                        validAmount = Math.min(ConfigUtil.getOrDefaultItemSetting(576, this.getId(), "shift-click"), validAmount);
                                    } else if(clickType.isRightClick()) {
                                        validAmount = Math.min(ConfigUtil.getOrDefaultItemSetting(64, this.getId(), "right-click"), validAmount);
                                    } else {
                                        validAmount = Math.min(ConfigUtil.getOrDefaultItemSetting(1, this.getId(), "left-click"), validAmount);
                                    }
                                    this.abstractStorageMachine.output(itemMap, locationData, itemString, validAmount, humanEntity.getInventory(), JavaUtil.generateInts(humanEntity.getInventory().getStorageContents().length));
                                } else {
                                    ItemMeta itemMeta = itemStack.getItemMeta();
                                    if (itemMeta != null && FinalTechItems.STORAGE_CARD.verifyItem(itemMeta)) {
                                        ItemStack stringItem = StringItemUtil.parseItemInCard(itemMeta);
                                        String amount = StringItemUtil.parseAmountInCard(itemMeta);
                                        if (ItemStackUtil.isItemNull(stringItem)) {
                                            this.abstractStorageMachine.removeItem(itemMap, locationData, itemString);

                                            StringItemUtil.setItemInCard(itemStack, itemWithAmount.getItemWrapper().getItemStack(), itemWithAmount.getItemAmount());
                                            FinalTechItems.STORAGE_CARD.updateLore(itemStack);
                                        } else if (ItemStackUtil.isItemSimilar(itemWithAmount.getItemWrapper(), stringItem)) {
                                            amount = StringNumberUtil.add(amount, itemWithAmount.getItemAmount());
                                            this.abstractStorageMachine.removeItem(itemMap, locationData, itemString);

                                            StringItemUtil.setAmountInCard(itemStack, amount);
                                            FinalTechItems.STORAGE_CARD.updateLore(itemStack);
                                        }
                                    }
                                }

                                this.updateInventory(inventoryClickEvent.getInventory(), location);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }
    }
}
