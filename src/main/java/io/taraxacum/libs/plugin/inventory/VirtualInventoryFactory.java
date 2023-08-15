package io.taraxacum.libs.plugin.inventory;

import io.taraxacum.libs.plugin.inventory.SimpleVirtualInventory;
import io.taraxacum.libs.plugin.inventory.template.CloseFunctionInventory;
import io.taraxacum.libs.plugin.inventory.template.InventoryTemplate;
import io.taraxacum.libs.plugin.inventory.template.OpenFunctionInventory;
import io.taraxacum.libs.plugin.inventory.VirtualInventory;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

public class VirtualInventoryFactory {

    @Nonnull
    public VirtualInventory generateByTemplate(@Nonnull InventoryTemplate inventoryTemplate, @Nonnull Location location) {
        SimpleVirtualInventory virtualInventory = new SimpleVirtualInventory(inventoryTemplate.getSize(), inventoryTemplate.getName());

        for(Map.Entry<Integer, ItemStack> itemStackEntry : inventoryTemplate.getDefaultItemStacks().entrySet()) {
            virtualInventory.drawBackground(itemStackEntry.getKey(), itemStackEntry.getValue());
        }

        if(inventoryTemplate instanceof OpenFunctionInventory openFunctionInventory) {
            virtualInventory.setOnOpen(inventoryOpenEvent -> {
                HumanEntity humanEntity = inventoryOpenEvent.getPlayer();
                if (humanEntity instanceof Player player) {
                    if(openFunctionInventory.canOpen(player, location)) {
                        openFunctionInventory.onOpen(player, location, virtualInventory.getInventory());
                    } else {
                        inventoryOpenEvent.setCancelled(true);
                    }
                }
            });
        }

        if(inventoryTemplate instanceof CloseFunctionInventory closeFunctionInventory) {
            virtualInventory.setOnClose(inventoryCloseEvent -> {
                HumanEntity humanEntity = inventoryCloseEvent.getPlayer();
                if (humanEntity instanceof Player player) {
                    closeFunctionInventory.onClose(player, location, virtualInventory.getInventory());
                }
            });
        }

        for(int i = 0; i < inventoryTemplate.getSize(); i++) {
            Consumer<InventoryClickEvent> eventConsumer = inventoryTemplate.onClick(location, i);
            if (eventConsumer != null) {
                virtualInventory.setOnClick(i, eventConsumer);
            }
        }

        return virtualInventory;
    }
}
