package io.taraxacum.libs.plugin.inventory;

import io.taraxacum.libs.plugin.inventory.VirtualInventory;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A simple impl for virtual inventory
 * @author Final_ROOT
 */
public class SimpleVirtualInventory implements VirtualInventory {
    private Consumer<InventoryOpenEvent> openEventConsumer;

    private Consumer<InventoryCloseEvent> closeEventConsumer;

    private final Map<Integer, Consumer<InventoryClickEvent>> clickEventConsumerMap;

    private Inventory inventory;

    private final int size;

    private final String title;

    private boolean allowClickPlayerInventory = true;

    public SimpleVirtualInventory(int size, @Nonnull String title) {
        this.size = size;
        this.title = title;
        this.clickEventConsumerMap = new HashMap<>();
    }

    public int getSize() {
        return size;
    }

    public String getTitle() {
        return title;
    }

    @Nullable
    @Override
    public Consumer<InventoryClickEvent> onClick(int slot) {
        return this.clickEventConsumerMap.get(slot);
    }

    @Nullable
    @Override
    public Consumer<InventoryOpenEvent> onOpen() {
        return this.openEventConsumer;
    }

    @Nullable
    @Override
    public Consumer<InventoryCloseEvent> onClose() {
        return this.closeEventConsumer;
    }

    @Nonnull
    @Override
    public Inventory getInventory() {
        if(this.inventory == null) {
            this.inventory = Bukkit.createInventory(null, this.size, this.title);
        }
        return this.inventory;
    }

    @Override
    public boolean allowDrag() {
        return false;
    }

    @Override
    public boolean allowCollect() {
        return false;
    }

    @Override
    public boolean allowMoveToOtherInventory() {
        return false;
    }

    @Override
    public boolean allowClickPlayerInventory() {
        return this.allowClickPlayerInventory;
    }

    public void setAllowClickPlayerInventory(boolean allowClickPlayerInventory) {
        this.allowClickPlayerInventory = allowClickPlayerInventory;
    }

    public void setOnClick(int slot, @Nullable Consumer<InventoryClickEvent> eventConsumer) {
        this.clickEventConsumerMap.put(slot, eventConsumer);
    }

    public void setOnOpen(@Nullable Consumer<InventoryOpenEvent> eventConsumer) {
        this.openEventConsumer = eventConsumer;
    }

    public void setOnClose(@Nullable Consumer<InventoryCloseEvent> eventConsumer) {
        this.closeEventConsumer = eventConsumer;
    }

    public void drawBackground(int slot, @Nonnull ItemStack itemStack) {
        this.getInventory().setItem(slot, itemStack);
        this.clickEventConsumerMap.put(slot, CANCEL_CLICK_CONSUMER);
    }
}
