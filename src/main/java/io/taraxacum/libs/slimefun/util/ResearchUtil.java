package io.taraxacum.libs.slimefun.util;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class ResearchUtil {

    public static void setResearchBySlimefunItems(@Nonnull SlimefunItemStack slimefunItemStack1, @Nonnull SlimefunItemStack slimefunItemStack2) {
        SlimefunItem slimefunItem1 = SlimefunItem.getByItem(slimefunItemStack1);
        SlimefunItem slimefunItem2 = SlimefunItem.getByItem(slimefunItemStack2);
        if (slimefunItem1 != null && slimefunItem2 != null) {
            slimefunItem1.setResearch(slimefunItem2.getResearch());
        }
    }
    public static void setResearchBySlimefunItemId(@Nonnull SlimefunItemStack slimefunItemStack, @Nonnull String id) {
        SlimefunItem slimefunItem1 = SlimefunItem.getByItem(slimefunItemStack);
        SlimefunItem slimefunItem2 = SlimefunItem.getById(id);
        if (slimefunItem1 != null && slimefunItem2 != null) {
            slimefunItem1.setResearch(slimefunItem2.getResearch());
        }
    }
}
