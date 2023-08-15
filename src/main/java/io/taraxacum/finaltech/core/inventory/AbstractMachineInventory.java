package io.taraxacum.finaltech.core.inventory;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.taraxacum.finaltech.core.option.Icon;
import io.taraxacum.libs.plugin.inventory.template.InventoryTemplate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public abstract class AbstractMachineInventory implements InventoryTemplate {
    protected SlimefunItem slimefunItem;

    protected Map<Integer, ItemStack> defaultItemStack = new HashMap<>(this.getSize());

    private boolean init = false;

    protected AbstractMachineInventory(@Nonnull SlimefunItem slimefunItem) {
        this.slimefunItem = slimefunItem;
    }

    @Nonnull
    @Override
    public String getId() {
        return this.slimefunItem.getId();
    }

    @Nonnull
    @Override
    public String getName() {
        return this.slimefunItem.getItemName();
    }

    @Nonnull
    protected abstract int[] getBorder();

    @Nonnull
    protected abstract int[] getInputBorder();

    @Nonnull
    protected abstract int[] getOutputBorder();

    @Nonnull
    @Override
    public Map<Integer, ItemStack> getDefaultItemStacks() {
        return this.defaultItemStack;
    }

    @Override
    @Nullable
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        return this.defaultItemStack.containsKey(slot) ? CANCEL_CLICK_CONSUMER : null;
    }

    @Override
    public boolean canOpen(@Nonnull Player player, @Nonnull Location location) {
        return player.hasPermission("slimefun.inventory.bypass") || this.slimefunItem.canUse(player, false) && Slimefun.getProtectionManager().hasPermission(player, location, Interaction.INTERACT_BLOCK);
    }

    @Override
    public final @Nonnull InventoryTemplate init() {
        if(!this.init) {
            for(int slot : this.getBorder()) {
                this.defaultItemStack.put(slot, Icon.BORDER_ICON);
            }

            for(int slot : this.getInputBorder()) {
                this.defaultItemStack.put(slot, Icon.INPUT_BORDER_ICON);
            }

            for(int slot : this.getOutputBorder()) {
                this.defaultItemStack.put(slot, Icon.OUTPUT_BORDER_ICON);
            }

            this.initSelf();

            this.init = true;
        }

        return this;
    }

    protected abstract void initSelf();
}
