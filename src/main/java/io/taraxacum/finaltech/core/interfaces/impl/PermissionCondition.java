package io.taraxacum.finaltech.core.interfaces.impl;

import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.Condition;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PermissionCondition implements Condition {
    private final String permissionCode;

    private final List<String> rawSuccessLore;
    private final List<String> rawSuccessChat;
    private final List<String> rawFailLore;
    private final List<String> rawFailChat;

    public PermissionCondition(@Nonnull String permissionCode) {
        this.permissionCode = permissionCode;

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
        return FinalTech.getResearchManager().replaceStringList(this.rawSuccessLore, this.permissionCode);
    }

    @Nonnull
    @Override
    public List<String> getSuccessChat(@Nonnull Player player) {
        return FinalTech.getResearchManager().replaceStringList(this.rawSuccessChat, this.permissionCode);
    }

    @Nonnull
    @Override
    public List<String> getFailLore(@Nonnull Player player) {
        return FinalTech.getResearchManager().replaceStringList(this.rawFailLore, this.permissionCode);
    }

    @Nonnull
    @Override
    public List<String> getFailChat(@Nonnull Player player) {
        return FinalTech.getResearchManager().replaceStringList(this.rawFailChat, this.permissionCode);
    }

    @Override
    public boolean match(@Nonnull Player player) {
        return player.hasPermission(this.permissionCode);
    }

    @Override
    public void afterResearch(@Nonnull Player player) {

    }
}
