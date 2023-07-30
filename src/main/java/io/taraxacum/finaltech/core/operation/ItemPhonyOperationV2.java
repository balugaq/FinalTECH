package io.taraxacum.finaltech.core.operation;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.dto.ItemValueTableV2;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public class ItemPhonyOperationV2 implements MachineOperation {
    private long singularityDifficulty;
    private long spirocheteDifficulty;
    private long itemTotalAmount;
    private long itemValueSum;
    private long differentValueSum;
    private final Set<ItemValueTableV2.Value> differentValueSet = new HashSet<>();
    private final ItemStack showItem;

    public ItemPhonyOperationV2() {
        this.singularityDifficulty = ConstantTableUtil.ITEM_SPIROCHETE_AMOUNT;
        this.spirocheteDifficulty = ConstantTableUtil.ITEM_SINGULARITY_AMOUNT;
        this.showItem = ItemStackUtil.newItemStack(FinalTechItems.ITEM_PHONY.getItem().getType(),
                FinalTech.getLanguageString("items", FinalTechItems.PHONY_FACTORY.getId(), "phony", "name"));
    }

    @Nonnull
    public ItemStack getShowItem() {
        return this.showItem;
    }

    public void updateShowItem() {
        ItemStackUtil.setLore(this.showItem,
                FinalTech.getLanguageManager().replaceStringArray(FinalTech.getLanguageStringArray("items", FinalTechItems.PHONY_FACTORY.getId(), "phony", "lore"),
                        String.valueOf(this.itemValueSum),
                        String.valueOf(this.differentValueSet.size()),
                        String.valueOf(this.singularityDifficulty),
                        String.valueOf(this.itemTotalAmount),
                        String.valueOf(this.differentValueSum),
                        String.valueOf(this.spirocheteDifficulty),
                        String.valueOf(this.itemValueSum * this.differentValueSet.size()),
                        String.valueOf(this.itemTotalAmount * this.differentValueSum)));
    }

    public int addItem(@Nonnull ItemStack itemStack) {
        if (!FinalTechItems.COPY_CARD.verifyItem(itemStack)) {
            return 0;
        }

        ItemValueTableV2.Value value = ItemValueTableV2.getInstance().getOrCalItemInputValue(itemStack);
        this.itemValueSum += Long.parseLong(value.getRealNumber());
        this.itemTotalAmount += 1;
        if (!this.differentValueSet.contains(value)) {
            this.differentValueSet.add(value);
            this.differentValueSum += Long.parseLong(value.getRealNumber());
        }

        return 1;
    }

    @Override
    public boolean isFinished() {
        return this.itemValueSum * this.differentValueSet.size() >= this.singularityDifficulty || this.itemTotalAmount * this.differentValueSum >= this.spirocheteDifficulty;
    }

    @Nonnull
    public ItemStack getResult() {
        if (this.itemValueSum * this.differentValueSet.size() >= this.singularityDifficulty && this.itemTotalAmount * this.differentValueSum >= this.spirocheteDifficulty) {
            return FinalTechItems.ITEM_PHONY.getValidItem();
        }
        if (this.itemValueSum * this.differentValueSet.size() >= this.singularityDifficulty) {
            return FinalTechItems.SINGULARITY.getValidItem();
        }
        if (this.itemTotalAmount * this.differentValueSum >= this.spirocheteDifficulty) {
            return FinalTechItems.SPIROCHETE.getValidItem();
        }
        return ItemStackUtil.AIR;
    }

    @Deprecated
    @Override
    public void addProgress(int i) {

    }

    @Deprecated
    @Override
    public int getProgress() {
        return 0;
    }

    @Deprecated
    @Override
    public int getTotalTicks() {
        return 0;
    }
}
