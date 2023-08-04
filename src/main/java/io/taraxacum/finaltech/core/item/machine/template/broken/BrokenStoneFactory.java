package io.taraxacum.finaltech.core.item.machine.template.broken;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class BrokenStoneFactory extends AbstractBrokenMachine {
    protected BrokenStoneFactory(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Override
    public void registerDefaultRecipes() {
        this.registerRecipe(new ItemStack(Material.COBBLESTONE, 12), new ItemStack(Material.COBBLESTONE, 20));
    }
}
