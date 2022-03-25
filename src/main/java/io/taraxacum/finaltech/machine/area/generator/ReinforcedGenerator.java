package io.taraxacum.finaltech.machine.area.generator;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;

/**
 * @author Final_ROOT
 */
public class ReinforcedGenerator extends AbstractAreaElectricGenerator {
    public final static int ELECTRICITY = 16;
    public final static int RANGE = 6;
    public ReinforcedGenerator(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    protected int getElectricity() {
        return ELECTRICITY;
    }

    @Override
    protected int getRange() {
        return RANGE;
    }
}