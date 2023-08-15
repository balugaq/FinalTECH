package io.taraxacum.finaltech.core.item.usable;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.event.EnergyDepositEvent;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.PermissionUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.util.EnergyUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Final_ROOT
 */
public class EnergyCard extends UsableSlimefunItem implements RecipeItem {
    public static final Map<String, EnergyCard> ENERGY_CARD_MAP = new LinkedHashMap<>();
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    private final String energy;

    public EnergyCard(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item, @Nonnull RecipeType recipeType, @Nonnull String energy) {
        super(itemGroup, item, recipeType, new ItemStack[0]);
        if(ENERGY_CARD_MAP.containsKey(energy)) {
            throw new IllegalArgumentException("duplicated energy while registering " + this.getId());
        }
        ENERGY_CARD_MAP.put(energy, this);

        this.energy = energy;
    }

    @Override
    protected void function(@Nonnull PlayerRightClickEvent playerRightClickEvent) {
        Block block = playerRightClickEvent.getInteractEvent().getClickedBlock();
        if (block == null) {
            return;
        }

        Player player = playerRightClickEvent.getPlayer();
        if (player.isDead()) {
            return;
        }

        Location location = block.getLocation();
        LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);

        if(locationData == null || this.notAllowedId.contains(LocationDataUtil.getId(FinalTech.getLocationDataService(), locationData))) {
            return;
        }

        if (!PermissionUtil.checkPermission(player, location, Interaction.INTERACT_BLOCK, Interaction.BREAK_BLOCK, Interaction.PLACE_BLOCK)) {
            player.sendRawMessage(FinalTech.getLanguageString("message", "no-permission", "location"));
            return;
        }

        if (LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), locationData) instanceof EnergyNetComponent energyNetComponent && energyNetComponent.isChargeable()) {
            JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
            javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(this.getAddon().getJavaPlugin(), Particle.WAX_OFF, 0, block));

            int capacity = energyNetComponent.getCapacity();
            String energyStr = EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData);
            int energy = Integer.parseInt(energyStr);
            if (energy < capacity) {
                EnergyDepositEvent energyDepositEvent = new EnergyDepositEvent(location, this.energy);
                this.getAddon().getJavaPlugin().getServer().getPluginManager().callEvent(energyDepositEvent);
                String transferEnergy = energyDepositEvent.getEnergy();

                energyStr = EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData);
                energyStr = StringNumberUtil.min(StringNumberUtil.add(transferEnergy, energyStr), String.valueOf(capacity));
                EnergyUtil.setCharge(FinalTech.getLocationDataService(), locationData, energyStr);

                ItemStack itemStack = playerRightClickEvent.getItem();
                itemStack.setAmount(itemStack.getAmount() - 1);
            }
        }
    }

    public String getEnergy() {
        return energy;
    }

    @Nullable
    public static EnergyCard getByEnergy(@Nonnull String energy) {
        String targetEnergy = null;
        for(Map.Entry<String, EnergyCard> entry : ENERGY_CARD_MAP.entrySet()) {
            if(StringNumberUtil.compare(energy, entry.getKey()) >= 0) {
                if(targetEnergy == null || StringNumberUtil.compare(targetEnergy, energy) <= 0) {
                    targetEnergy = entry.getKey();
                }
            }
        }

        return ENERGY_CARD_MAP.get(targetEnergy);
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                this.energy);
    }
}
