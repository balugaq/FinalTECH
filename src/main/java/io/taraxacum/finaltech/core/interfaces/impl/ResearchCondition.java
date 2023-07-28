package io.taraxacum.finaltech.core.interfaces.impl;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.Condition;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResearchCondition implements Condition {
    private final Research research;

    private final List<String> rawSuccessLore;
    private final List<String> rawSuccessChat;
    private final List<String> rawFailLore;
    private final List<String> rawFailChat;
    public ResearchCondition(@Nonnull Research research) {
        this.research = research;

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
        return FinalTech.getResearchManager().replaceStringList(this.rawSuccessLore, this.research.getName(player));
    }

    @Nonnull
    @Override
    public List<String> getSuccessChat(@Nonnull Player player) {
        return FinalTech.getResearchManager().replaceStringList(this.rawSuccessChat, this.research.getName(player));
    }

    @Nonnull
    @Override
    public List<String> getFailLore(@Nonnull Player player) {
        return FinalTech.getResearchManager().replaceStringList(this.rawFailLore, this.research.getName(player));
    }

    @Nonnull
    @Override
    public List<String> getFailChat(@Nonnull Player player) {
        return FinalTech.getResearchManager().replaceStringList(this.rawFailChat, this.research.getName(player));
    }

    @Override
    public boolean match(@Nonnull Player player) {
        Optional<PlayerProfile> playerProfile = PlayerProfile.find(player);
        return playerProfile.map(profile -> profile.hasUnlocked(this.research)).orElse(false);

    }

    @Override
    public void afterResearch(@Nonnull Player player) {

    }
}
