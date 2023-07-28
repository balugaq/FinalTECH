package io.taraxacum.finaltech.core.interfaces.impl;

import io.taraxacum.finaltech.core.interfaces.Condition;
import io.taraxacum.finaltech.core.dto.SpecialResearch;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SimpleSpecialResearch extends SpecialResearch {
    private final List<Condition> conditionList = new ArrayList<>();
    private final List<String> lore = new ArrayList<>();

    public SimpleSpecialResearch(@Nonnull NamespacedKey key, int id, @Nonnull String defaultName, int defaultCost) {
        super(key, id, defaultName, defaultCost);
    }

    @Override
    public String[] getShowText(@Nonnull Player player) {
        List<String> result = new ArrayList<>(this.lore);
        for (Condition condition : this.conditionList) {
            if (condition.match(player)) {
                for (String lore : condition.getSuccessLore(player)) {
                    if (!lore.isBlank()) {
                        result.add(lore);
                    }
                }
            } else {
                for (String lore : condition.getFailLore(player)) {
                    if (!lore.isBlank()) {
                        result.add(lore);
                    }
                }
            }
        }
        return result.toArray(new String[0]);
    }

    @Override
    public String[] getChatText(@Nonnull Player player) {
        List<String> result = new ArrayList<>();
        for (Condition condition : this.conditionList) {
            if (condition.match(player)) {
                for (String s : condition.getSuccessChat(player)) {
                    if (!s.isBlank()) {
                        result.add(s);
                    }
                }
            } else {
                for (String s : condition.getFailChat(player)) {
                    if (!s.isBlank()) {
                        result.add(s);
                    }
                }
            }
        }

        return result.toArray(new String[0]);
    }

    @Override
    public boolean canResearch(@Nonnull Player player) {
        for (Condition condition : this.conditionList) {
            if (!condition.match(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterResearch(@Nonnull Player player) {
        for (Condition condition : this.conditionList) {
            condition.afterResearch(player);
        }
    }

    public void addCondition(@Nonnull Condition condition) {
        this.conditionList.add(condition);
    }

    public int conditionSize() {
        return this.conditionList.size();
    }

    @Nonnull
    public List<String> getBaseLore() {
        return this.lore;
    }
}
