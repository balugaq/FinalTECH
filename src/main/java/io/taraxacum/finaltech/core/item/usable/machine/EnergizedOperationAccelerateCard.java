package io.taraxacum.finaltech.core.item.usable.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.util.RecipeUtil;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class EnergizedOperationAccelerateCard extends AbstractOperationAccelerateCard implements RecipeItem {
    private final boolean consumable = true;
    public EnergizedOperationAccelerateCard(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Override
    void addProgress(@Nonnull MachineOperation machineOperation) {
        int progress = Math.min(machineOperation.getProgress() / 2, machineOperation.getRemainingTicks());
        if (progress > 0) {
            machineOperation.addProgress(progress);
        }
    }

    @Override
    boolean consumable() {
        return this.consumable;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this);

        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (slimefunItem instanceof MachineProcessHolder && !this.notAllowedId.contains(slimefunItem.getId())) {
                this.registerDescriptiveRecipe(slimefunItem.getItem());
            }
        }
    }
}
