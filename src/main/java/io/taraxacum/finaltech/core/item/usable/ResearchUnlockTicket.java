package io.taraxacum.finaltech.core.item.usable;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerPreResearchEvent;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.util.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author Final_ROOT
 */
// TODO update or delete?
public class ResearchUnlockTicket extends UsableSlimefunItem implements RecipeItem {
    public ResearchUnlockTicket(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Override
    protected void function(@Nonnull PlayerRightClickEvent playerRightClickEvent) {
        Player player = playerRightClickEvent.getPlayer();
        List<Research> researchList = new ArrayList<>(Slimefun.getRegistry().getResearches());
        researchList = JavaUtil.shuffle(researchList);
        Optional<PlayerProfile> playerProfile = PlayerProfile.find(player);
        if (playerProfile.isPresent()) {
            if (!playerProfile.get().hasUnlockedEverything()) {
                PlayerProfile profile = playerProfile.get();
                Research research = researchList.get(new Random().nextInt(researchList.size() - 1));
                List<SlimefunItem> slimefunItemList = research.getAffectedItems();
                if (!profile.hasUnlocked(research) && research.canUnlock(player) && !slimefunItemList.isEmpty()) {
                    PlayerPreResearchEvent event = new PlayerPreResearchEvent(player, research, slimefunItemList.get(0));
                    Bukkit.getPluginManager().callEvent(event);
                    if(!event.isCancelled()) {
                        research.unlock(player, true);
                    }
                }
            }
        }
        ItemStack item = playerRightClickEvent.getItem();
        item.setAmount(item.getAmount() - 1);
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
    }
}
