package io.taraxacum.libs.plugin.recipe;

import io.taraxacum.common.util.CompareUtil;
import io.taraxacum.libs.plugin.is.ItemAmountWrapper;
import io.taraxacum.libs.plugin.is.SimpleItemComparator;
import org.bukkit.NamespacedKey;

import javax.annotation.Nonnull;

public class SimpleCraftRandomRecipe implements CraftRandomRecipe {
    private final NamespacedKey id;

    private final SimpleItemComparator[] imports;

    private final RandomExport[] randomExports;

    private final ItemAmountWrapper[][] allRandomExports;

    private final int[] weightBeginValues;

    private int weightSum = 0;

    public SimpleCraftRandomRecipe(@Nonnull NamespacedKey id, @Nonnull SimpleItemComparator[] imports, @Nonnull RandomExport[] randomExports) {
        this.id = id;
        this.imports = imports;
        this.randomExports = randomExports;
        this.weightBeginValues = new int[randomExports.length];
        for (int i = 0; i < this.randomExports.length; i++) {
            this.weightBeginValues[i] = this.weightSum;
            this.weightSum += this.randomExports[i].getWeight();
        }
        this.allRandomExports = new ItemAmountWrapper[this.randomExports.length][];
        for (int i = 0, j = 0; i < this.randomExports.length; i++) {
            for (int k = 0; k < this.randomExports[i].exports.length; k++) {
                this.allRandomExports[j++] = this.randomExports[i].exports;
            }
        }
    }

    @Nonnull
    @Override
    public NamespacedKey getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public SimpleItemComparator[] getImports() {
        return this.imports;
    }

    @Nonnull
    @Override
    public ItemAmountWrapper[] getExports() {
        int r = (int)(Math.random() * this.weightSum);
        return this.randomExports[CompareUtil.getIntSmallFuzzyIndex(this.weightBeginValues, r)].exports;
    }

    @Nonnull
    @Override
    public ItemAmountWrapper[][] getAllExports() {
        return this.allRandomExports;
    }

    public record RandomExport(@Nonnull ItemAmountWrapper[] exports, int weight) {
        public RandomExport(@Nonnull ItemAmountWrapper[] exports, int weight) {
            this.exports = exports;
            this.weight = weight;
        }

        @Nonnull
        public ItemAmountWrapper[] getExports() {
            return exports;
        }

        public int getWeight() {
            return weight;
        }
    }
}
