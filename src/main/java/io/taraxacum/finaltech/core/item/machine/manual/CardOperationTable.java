package io.taraxacum.finaltech.core.item.machine.manual;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.manual.AbstractManualMachineInventory;
import io.taraxacum.finaltech.core.inventory.manual.CardOperationPortInventory;
import io.taraxacum.libs.plugin.ld.LocationData;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Final_ROOT
 */
@Deprecated
public class CardOperationTable extends AbstractManualMachine implements RecipeItem {
    public List<CardOperationPortInventory.Craft> craftList;

    public CardOperationTable(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nonnull
    @Override
    protected AbstractManualMachineInventory newMachineInventory() {
        CardOperationPortInventory cardOperationPortInventory = new CardOperationPortInventory(this);
        this.craftList = cardOperationPortInventory.craftList;
        return cardOperationPortInventory;
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if (inventory != null && !inventory.getViewers().isEmpty()) {
            this.getMachineInventory().updateInventory(inventory, block.getLocation());
        }
    }

    @Override
    public void registerDefaultRecipes() {
        for (CardOperationPortInventory.Craft craft : this.craftList) {
            if (craft.isEnabled()) {
                String outputItemId = craft.getInfoOutput();
                SlimefunItem slimefunItem = SlimefunItem.getById(outputItemId);
                if (slimefunItem != null) {
                    this.registerDescriptiveRecipe(slimefunItem.getItem(), craft.getInfoName(), craft.getInfoLore());
                } else {
                    this.registerDescriptiveRecipe(craft.getInfoName(), craft.getInfoLore());
                }
            }
        }
    }
}
