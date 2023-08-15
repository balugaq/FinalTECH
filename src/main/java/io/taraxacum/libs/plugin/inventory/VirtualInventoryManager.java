package io.taraxacum.libs.plugin.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * It will handle the event while player is using virtual inventory.
 * @see VirtualInventory
 * @author Final_ROOT
 */
public class VirtualInventoryManager implements Listener {
    private static volatile VirtualInventoryManager instance;

    private final Map<UUID, VirtualInventory> playerIdInventoryMap;

    private volatile boolean init = false;

    private VirtualInventoryManager() {
        this.playerIdInventoryMap = new HashMap<>();
    }

    public void init(JavaPlugin javaPlugin) {
        if(!this.init) {
            this.init = true;
            PluginManager pluginManager = javaPlugin.getServer().getPluginManager();
            pluginManager.registerEvents(this, javaPlugin);
        }
    }

    public void bindTo(@Nonnull Player player, @Nonnull VirtualInventory virtualInventory) {
        this.playerIdInventoryMap.put(player.getUniqueId(), virtualInventory);
    }

    public boolean unbindTo(@Nonnull Player player, @Nonnull VirtualInventory virtualInventory) {
        return this.playerIdInventoryMap.remove(player.getUniqueId(), virtualInventory);
    }

    public void unbind(@Nonnull Player player) {
        this.playerIdInventoryMap.remove(player.getUniqueId());
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent inventoryOpenEvent) {
        VirtualInventory virtualInventory = this.playerIdInventoryMap.get(inventoryOpenEvent.getPlayer().getUniqueId());

        if (virtualInventory != null) {
            Consumer<InventoryOpenEvent> eventConsumer = virtualInventory.onOpen();
            if (eventConsumer != null) {
                eventConsumer.accept(inventoryOpenEvent);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {
        VirtualInventory virtualInventory = this.playerIdInventoryMap.remove(inventoryCloseEvent.getPlayer().getUniqueId());

        if (virtualInventory != null) {
            Consumer<InventoryCloseEvent> eventConsumer = virtualInventory.onClose();
            if (eventConsumer != null) {
                eventConsumer.accept(inventoryCloseEvent);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent inventoryClickEvent) {
        VirtualInventory virtualInventory = this.playerIdInventoryMap.get(inventoryClickEvent.getWhoClicked().getUniqueId());

        if (virtualInventory != null) {
            InventoryAction action = inventoryClickEvent.getAction();
            if (InventoryAction.COLLECT_TO_CURSOR.equals(action) && !virtualInventory.allowCollect()) {
                inventoryClickEvent.setCancelled(true);
            }
            if (InventoryAction.MOVE_TO_OTHER_INVENTORY.equals(action) && !virtualInventory.allowMoveToOtherInventory()) {
                inventoryClickEvent.setCancelled(true);
            }
            if (inventoryClickEvent.getSlot() != inventoryClickEvent.getRawSlot() && !virtualInventory.allowClickPlayerInventory()) {
                inventoryClickEvent.setCancelled(true);
            }
            Consumer<InventoryClickEvent> eventConsumer = virtualInventory.onClick(inventoryClickEvent.getRawSlot());
            if (eventConsumer != null) {
                eventConsumer.accept(inventoryClickEvent);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent inventoryDragEvent) {
        VirtualInventory virtualInventory = this.playerIdInventoryMap.get(inventoryDragEvent.getWhoClicked().getUniqueId());

        if(virtualInventory != null && !virtualInventory.allowDrag()) {
            inventoryDragEvent.setCancelled(true);
        }
    }

    @Nonnull
    public static VirtualInventoryManager getInstance() {
        if (instance == null) {
            synchronized (VirtualInventoryManager.class) {
                if (instance == null) {
                    instance = new VirtualInventoryManager();
                }
            }
        }
        return instance;
    }
}
