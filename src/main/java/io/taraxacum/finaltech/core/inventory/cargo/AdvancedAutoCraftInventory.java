package io.taraxacum.finaltech.core.inventory.cargo;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.option.Icon;
import io.taraxacum.finaltech.core.option.SlotSearchSize;
import io.taraxacum.libs.plugin.dto.AdvancedMachineRecipe;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

public class AdvancedAutoCraftInventory extends AdvancedAutoCraftFrameInventory {
    private final int[] border = new int[] {36, 38};

    private final int inputSearchSlot = 29;
    private final int outputSearchSlot = 47;

    public final int statusSlot = 27;
    public final int moduleSlot = 28;

    public AdvancedAutoCraftInventory(@Nonnull SlimefunItem slimefunItem, @Nonnull Map<Location, AdvancedMachineRecipe> locationRecipeMap) {
        super(slimefunItem, locationRecipeMap);
    }

    @Nonnull
    @Override
    protected int[] getBorder() {
        return this.border;
    }

    @Override
    @Nullable
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        return switch (slot) {
            case inputSearchSlot -> SlotSearchSize.INPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case outputSearchSlot -> SlotSearchSize.OUTPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            default -> super.onClick(location, slot);
        };
    }

    @Override
    protected void initSelf() {
        super.initSelf();

        this.defaultItemStack.put(this.inputSearchSlot, SlotSearchSize.INPUT_OPTION.defaultIcon());
        this.defaultItemStack.put(this.outputSearchSlot, SlotSearchSize.OUTPUT_OPTION.defaultIcon());
        this.defaultItemStack.put(this.statusSlot, Icon.QUANTITY_MODULE_ICON);
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        super.updateInventory(inventory, location);

        SlotSearchSize.INPUT_OPTION.checkAndUpdateIcon(inventory, this.inputSearchSlot, FinalTech.getLocationDataService(), location);
        SlotSearchSize.OUTPUT_OPTION.checkAndUpdateIcon(inventory, this.outputSearchSlot, FinalTech.getLocationDataService(), location);
    }
}
