package io.taraxacum.finaltech.core.interfaces.impl;

import io.taraxacum.common.util.StringUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.Condition;
import io.taraxacum.libs.plugin.dto.ItemAmountWrapper;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.plugin.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemCondition implements Condition {
    private final ItemAmountWrapper itemAmountWrapper;

    private final boolean consume;

    private final List<String> rawSuccessLore;
    private final List<String> rawSuccessChat;
    private final List<String> rawFailLore;
    private final List<String> rawFailChat;

    public ItemCondition(@Nonnull ItemStack itemStack, int amount, boolean consume) {
        this.itemAmountWrapper = new ItemAmountWrapper(itemStack, amount);
        this.consume = consume;

        this.rawSuccessLore = new ArrayList<>();
        this.rawSuccessChat = new ArrayList<>();
        this.rawFailLore = new ArrayList<>();
        this.rawFailChat = new ArrayList<>();
    }

    @Nonnull
    public List<String> getRawSuccessLore() {
        return rawSuccessLore;
    }

    @Nonnull
    public List<String> getRawSuccessChat() {
        return rawSuccessChat;
    }

    @Nonnull
    public List<String> getRawFailLore() {
        return rawFailLore;
    }

    @Nonnull
    public List<String> getRawFailChat() {
        return rawFailChat;
    }

    @Nonnull
    @Override
    public List<String> getSuccessLore(@Nonnull Player player) {
        int amount = this.getAmount(player);
        String itemName = ItemStackUtil.getItemName(this.itemAmountWrapper.getItemStack());
        String[] split = StringUtil.split(itemName, "[", "]");
        if (split.length == 3 && split[0].isBlank() && split[2].isBlank()) {
            itemName = TextUtil.COLOR_WHITE + split[1];
        }
        return FinalTech.getResearchManager().replaceStringList(this.rawSuccessLore,
                itemName,
                String.valueOf(this.itemAmountWrapper.getAmount()),
                String.valueOf(amount),
                String.valueOf(Math.max(0, this.itemAmountWrapper.getAmount() - amount))
        );
    }

    @Nonnull
    @Override
    public List<String> getSuccessChat(@Nonnull Player player) {
        int amount = this.getAmount(player);
        String itemName = ItemStackUtil.getItemName(this.itemAmountWrapper.getItemStack());
        String[] split = StringUtil.split(itemName, "[", "]");
        if (split.length == 3 && split[0].isBlank() && split[2].isBlank()) {
            itemName = TextUtil.COLOR_WHITE + split[1];
        }
        return FinalTech.getResearchManager().replaceStringList(this.rawSuccessChat,
                itemName,
                String.valueOf(this.itemAmountWrapper.getAmount()),
                String.valueOf(amount),
                String.valueOf(Math.max(0, this.itemAmountWrapper.getAmount() - amount))
        );
    }

    @Nonnull
    @Override
    public List<String> getFailLore(@Nonnull Player player) {
        int amount = this.getAmount(player);
        String itemName = ItemStackUtil.getItemName(this.itemAmountWrapper.getItemStack());
        String[] split = StringUtil.split(itemName, "[", "]");
        if (split.length == 3 && split[0].isBlank() && split[2].isBlank()) {
            itemName = TextUtil.COLOR_WHITE + split[1];
        }
        return FinalTech.getResearchManager().replaceStringList(this.rawFailLore,
                itemName,
                String.valueOf(this.itemAmountWrapper.getAmount()),
                String.valueOf(amount),
                String.valueOf(Math.max(0, this.itemAmountWrapper.getAmount() - amount))
        );
    }

    @Nonnull
    @Override
    public List<String> getFailChat(@Nonnull Player player) {
        int amount = this.getAmount(player);
        String itemName = ItemStackUtil.getItemName(this.itemAmountWrapper.getItemStack());
        String[] split = StringUtil.split(itemName, "[", "]");
        if (split.length == 3 && split[0].isBlank() && split[2].isBlank()) {
            itemName = TextUtil.COLOR_WHITE + split[1];
        }
        return FinalTech.getResearchManager().replaceStringList(this.rawFailChat,
                itemName,
                String.valueOf(this.itemAmountWrapper.getAmount()),
                String.valueOf(amount),
                String.valueOf(Math.max(0, this.itemAmountWrapper.getAmount() - amount))
        );
    }

    @Override
    public boolean match(@Nonnull Player player) {
        return this.getAmount(player) >= this.itemAmountWrapper.getAmount();
    }

    @Override
    public void afterResearch(@Nonnull Player player) {
        if (this.consume) {
            int amount = this.itemAmountWrapper.getAmount();
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (ItemStackUtil.isItemSimilar(itemStack, this.itemAmountWrapper)) {
                    int count = Math.min(amount, itemStack.getAmount());
                    itemStack.setAmount(itemStack.getAmount() - count);
                    amount -= count;
                    if (count == 0) {
                        break;
                    }
                }
            }
        }
    }

    public int getAmount(@Nonnull Player player) {
        int amount = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (ItemStackUtil.isItemSimilar(itemStack, this.itemAmountWrapper)) {
                amount += itemStack.getAmount();
            }
        }
        return amount;
    }
}
