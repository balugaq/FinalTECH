package io.taraxacum.libs.slimefun.util;

import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.taraxacum.libs.plugin.inventory.template.CloseFunctionInventory;
import io.taraxacum.libs.plugin.inventory.template.InventoryTemplate;
import io.taraxacum.libs.plugin.inventory.template.LogicInventory;
import io.taraxacum.libs.plugin.inventory.template.OpenFunctionInventory;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public class ChestMenuUtil {

    public static InventoryTemplate warpInventoryTemplate(@Nonnull BlockMenuPreset blockMenuPreset) {
        return new InventoryTemplate() {
            @Nonnull
            @Override
            public String getId() {
                return blockMenuPreset.getID();
            }

            @Nonnull
            @Override
            public String getName() {
                return blockMenuPreset.getTitle();
            }

            @Override
            public int getSize() {
                return blockMenuPreset.getSize() != -1 ? blockMenuPreset.getSize() : blockMenuPreset.;
            }

            @Nonnull
            @Override
            public Map<Integer, ItemStack> getDefaultItemStacks() {
                return null;
            }

            @Nullable
            @Override
            public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
                return null;
            }

            @Override
            public boolean canOpen(@Nonnull Player player, @Nonnull Location location) {
                return false;
            }
        };
    }

    @Nonnull
    public static BlockMenuPreset warpBlockMenuPreset(@Nonnull InventoryTemplate inventoryTemplate) {
        return new BlockMenuPreset(inventoryTemplate.getId(), inventoryTemplate.getName()) {
            @Override
            public void init() {
                int maxSlot = -1;
                for (Map.Entry<Integer, ItemStack> entry : inventoryTemplate.getDefaultItemStacks().entrySet()) {
                    this.addItem(entry.getKey(), entry.getValue());
                    this.addMenuClickHandler(entry.getKey(), ChestMenuUtils.getEmptyClickHandler());
                    maxSlot = Math.max(maxSlot, entry.getKey());
                }

                if (maxSlot == -1 || maxSlot / 9 + 1 != inventoryTemplate.getSize() / 9) {
                    this.setSize(inventoryTemplate.getSize());
                }
            }

            @Override
            public void newInstance(@Nonnull BlockMenu blockMenu, @Nonnull Block block) {
                super.newInstance(blockMenu, block);
                Location location = block.getLocation();
                Inventory inventory = blockMenu.toInventory();

                if(inventoryTemplate instanceof LogicInventory logicInventory) {
                    logicInventory.updateInventory(inventory, location);
                }

                if(inventoryTemplate instanceof OpenFunctionInventory openFunctionInventory) {
                    blockMenu.addMenuOpeningHandler(player -> openFunctionInventory.onOpen(player, location, inventory));
                }

                if(inventoryTemplate instanceof CloseFunctionInventory closeFunctionInventory) {
                    blockMenu.addMenuCloseHandler(player -> closeFunctionInventory.onClose(player, location, inventory));
                }

                for (int slot = 0; slot < inventoryTemplate.getSize(); slot++) {
                    Consumer<InventoryClickEvent> eventConsumer = inventoryTemplate.onClick(location, slot);
                    if (eventConsumer != null) {
                        blockMenu.addMenuClickHandler(slot, warpByConsumer(eventConsumer));
                    }
                }
            }

            @Override
            public boolean canOpen(@Nonnull Block block, @Nonnull Player player) {
                return inventoryTemplate.canOpen(player, block.getLocation());
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
                if (inventoryTemplate instanceof LogicInventory logicInventory) {
                    LogicInventory.RequestType requestType = getItemTransportFlow(itemTransportFlow);
                    return requestType == null ? logicInventory.requestSlots() : logicInventory.requestSlots(requestType);
                }
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu chestMenu, ItemTransportFlow itemTransportFlow, ItemStack itemStack) {
                if (inventoryTemplate instanceof LogicInventory logicInventory) {
                    LogicInventory.RequestType requestType = getItemTransportFlow(itemTransportFlow);
                    if (requestType == null) {
                        return logicInventory.requestSlots();
                    } else {
                        if (chestMenu instanceof BlockMenu blockMenu) {
                            return logicInventory.requestSlots(requestType, itemStack, chestMenu.toInventory(), blockMenu.getLocation());
                        } else {
                            return logicInventory.requestSlots(requestType);
                        }
                    }
                }
                return this.getSlotsAccessedByItemTransport(itemTransportFlow);
            }
        };
    }

    @Nonnull
    public static ChestMenu.AdvancedMenuClickHandler warpByConsumer(@Nonnull Consumer<InventoryClickEvent> consumer) {
        return new ChestMenu.AdvancedMenuClickHandler() {
            @Override
            public boolean onClick(InventoryClickEvent event, Player player, int slot, ItemStack cursor, ClickAction action) {
                try {
                    consumer.accept(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onClick(Player p, int slot, ItemStack item, ClickAction action) {
                return false;
            }
        };
    }

    public static ClickType warpClickAction(@Nonnull ClickAction clickAction) {
        if(clickAction.isRightClicked()) {
            if(clickAction.isShiftClicked()) {
                return ClickType.SHIFT_RIGHT;
            } else {
                return ClickType.RIGHT;
            }
        } else {
            if(clickAction.isShiftClicked()) {
                return ClickType.SHIFT_LEFT;
            } else {
                return ClickType.LEFT;
            }
        }
    }

    @Nullable
    public static LogicInventory.RequestType getItemTransportFlow(@Nullable ItemTransportFlow itemTransportFlow) {
        return itemTransportFlow == null ? null : switch (itemTransportFlow) {
            case INSERT -> LogicInventory.RequestType.INPUT;
            case WITHDRAW -> LogicInventory.RequestType.OUTPUT;
        };
    }
}
