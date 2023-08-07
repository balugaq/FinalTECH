package io.taraxacum.finaltech.core.item.machine.manual.process;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enums.LogSourceType;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.finaltech.util.StringItemUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.dto.ItemValueTableV2;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;

public class AnnularCraftTable extends AbstractProcessMachine {
    private final String baseValue = ConfigUtil.getOrDefaultItemSetting("8", this, "base-value");

    public AnnularCraftTable(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Override
    public boolean canCraft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2) {
        if (!ItemStackUtil.isItemNull(itemStack1) && FinalTechItems.COPY_CARD.verifyItem(itemStack1)) {
            ItemStack itemStack = StringItemUtil.parseItemInCard(itemStack1);
            ItemValueTableV2.Value value = ItemValueTableV2.getInstance().getOrCalItemInputValue(itemStack);
            BigInteger bigInteger = new BigInteger(StringNumberUtil.mul(value.getRealNumber(), String.valueOf(itemStack1.getAmount())));
            return bigInteger.compareTo(new BigInteger(this.baseValue)) >= 0;
        }

        if (!ItemStackUtil.isItemNull(itemStack2) && FinalTechItems.COPY_CARD.verifyItem(itemStack2)) {
            ItemStack itemStack = StringItemUtil.parseItemInCard(itemStack2);
            ItemValueTableV2.Value value = ItemValueTableV2.getInstance().getOrCalItemInputValue(itemStack);
            BigInteger bigInteger = new BigInteger(StringNumberUtil.mul(value.getRealNumber(), String.valueOf(itemStack2.getAmount())));
            return bigInteger.compareTo(new BigInteger(this.baseValue)) >= 0;
        }

        return false;
    }

    @Override
    public boolean craft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, @Nonnull Inventory inventory, int outputSlot, @Nonnull Player player, @Nonnull Location location) {
        if (!ItemStackUtil.isItemNull(itemStack1) && FinalTechItems.COPY_CARD.verifyItem(itemStack1)) {
            ItemStack itemStack = StringItemUtil.parseItemInCard(itemStack1);
            ItemValueTableV2.Value value = ItemValueTableV2.getInstance().getOrCalItemInputValue(itemStack);
            BigInteger totalValue = new BigInteger(StringNumberUtil.mul(value.getRealNumber(), String.valueOf(itemStack1.getAmount())));
            BigInteger baseValue = new BigInteger(this.baseValue);
            int amount = totalValue.divide(baseValue).min(new BigInteger(String.valueOf(FinalTechItems.COPY_CARD.getItem().getMaxStackSize()))).intValue();
            if (amount > 0) {
                int amount1 = itemStack1.getAmount();
                itemStack1.setAmount(0);
                ItemStack validItem = FinalTechItems.ANNULAR.getValidItem();
                validItem.setAmount(amount);
                inventory.setItem(outputSlot, validItem);
                FinalTech.getLogService().subItem(FinalTechItems.COPY_CARD.getId(), amount1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, this.getAddon().getJavaPlugin());
                FinalTech.getLogService().addItem(FinalTechItems.ANNULAR.getId(), amount, this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, this.getAddon().getJavaPlugin());
            }
            return true;
        }

        if (!ItemStackUtil.isItemNull(itemStack2) && FinalTechItems.COPY_CARD.verifyItem(itemStack2)) {
            ItemStack itemStack = StringItemUtil.parseItemInCard(itemStack2);
            ItemValueTableV2.Value value = ItemValueTableV2.getInstance().getOrCalItemInputValue(itemStack);
            BigInteger totalValue = new BigInteger(StringNumberUtil.mul(value.getRealNumber(), String.valueOf(itemStack2.getAmount())));
            BigInteger baseValue = new BigInteger(this.baseValue);
            int amount = totalValue.divide(baseValue).min(new BigInteger(String.valueOf(FinalTechItems.COPY_CARD.getItem().getMaxStackSize()))).intValue();
            if (amount > 0) {
                int amount1 = itemStack2.getAmount();
                itemStack2.setAmount(0);
                ItemStack validItem = FinalTechItems.ANNULAR.getValidItem();
                validItem.setAmount(amount);
                inventory.setItem(outputSlot, validItem);
                FinalTech.getLogService().subItem(FinalTechItems.COPY_CARD.getId(), amount1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, this.getAddon().getJavaPlugin());
                FinalTech.getLogService().addItem(FinalTechItems.ANNULAR.getId(), amount, this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, this.getAddon().getJavaPlugin());
            }
            return true;
        }
        return false;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this, this.baseValue);
    }
}
