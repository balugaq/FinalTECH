package io.taraxacum.libs.plugin.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * A virtual inventory will handle an inventory and inventory event.
 * @see SimpleVirtualInventory
 * @see VirtualInventoryManager
 * @author Final_ROOT
 */
public interface VirtualInventory {
    Consumer<InventoryClickEvent> CANCEL_CLICK_CONSUMER = inventoryClickEvent -> inventoryClickEvent.setCancelled(true);

    @Nullable
    Consumer<InventoryClickEvent> onClick(int slot);

    default void open(@Nonnull Player player) {
        InventoryView inventoryView = player.openInventory(this.getInventory());
        if (inventoryView == null) {
            VirtualInventoryManager.getInstance().unbindTo(player, this);
        } else {
            VirtualInventoryManager.getInstance().bindTo(player, this);
            InventoryOpenEvent fakeOpenEvent = new InventoryOpenEvent(inventoryView);
            Consumer<InventoryOpenEvent> eventConsumer = this.onOpen();
            if (eventConsumer != null) {
                eventConsumer.accept(fakeOpenEvent);
                if (fakeOpenEvent.isCancelled() && inventoryView == player.getOpenInventory()) {
                    player.closeInventory();
                }
            }
        }
    }

    @Nullable
    Consumer<InventoryOpenEvent> onOpen();

    @Nullable
    Consumer<InventoryCloseEvent> onClose();

    @Nonnull
    Inventory getInventory();

    boolean allowDrag();

    boolean allowCollect();

    boolean allowMoveToOtherInventory();

    default boolean allowClickPlayerInventory() {
        return true;
    }
}
