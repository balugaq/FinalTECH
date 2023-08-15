package io.taraxacum.finaltech.core.item.usable;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.event.EnergyDepositEvent;
import io.taraxacum.finaltech.core.event.EnergyWithdrawEvent;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.PermissionUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.util.EnergyUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public class PortableEnergyStorage extends UsableSlimefunItem implements RecipeItem {
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    private final NamespacedKey key = new NamespacedKey(FinalTech.getInstance(), this.getId());

    public PortableEnergyStorage(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    /**
     * The function the item will do
     * while a player hold the item and right click.
     *
     * @param playerRightClickEvent
     */
    @Override
    protected void function(@Nonnull PlayerRightClickEvent playerRightClickEvent) {
        ItemStack item = playerRightClickEvent.getItem();
        if(item.getAmount() > 1) {
            return;
        }

        JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();

        Optional<Block> clickedBlock = playerRightClickEvent.getClickedBlock();
        if (clickedBlock.isPresent()) {
            Block block = clickedBlock.get();
            Location location = block.getLocation();
            LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
            if (locationData != null
                    && !this.notAllowedId.contains(LocationDataUtil.getId(FinalTech.getLocationDataService(), locationData))
                    && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), locationData) instanceof EnergyNetComponent energyNetComponent
                    && energyNetComponent.isChargeable()
                    && PermissionUtil.checkPermission(playerRightClickEvent.getPlayer(), location, Interaction.INTERACT_BLOCK, Interaction.PLACE_BLOCK, Interaction.BREAK_BLOCK)) {

                if (!playerRightClickEvent.getPlayer().isSneaking()) {
                    // charge machine

                    String energyInItem = this.getEnergy(item);
                    EnergyDepositEvent energyDepositEvent = new EnergyDepositEvent(location, energyInItem);
                    this.getAddon().getJavaPlugin().getServer().getPluginManager().callEvent(energyDepositEvent);
                    energyInItem = energyDepositEvent.getEnergy();

                    int capacity = energyNetComponent.getCapacity();
                    String energyInMachine = EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData);
                    String charge = StringNumberUtil.min(StringNumberUtil.sub(String.valueOf(capacity), energyInMachine), energyInItem);

                    EnergyUtil.setCharge(FinalTech.getLocationDataService(), locationData, StringNumberUtil.add(energyInMachine, charge));
                    this.setEnergy(item, StringNumberUtil.sub(energyInItem, charge));

                    this.updateLore(item);

                    javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, block));
                } else if (playerRightClickEvent.getPlayer().isSneaking()) {
                    // consume energy in machine, charge item

                    EnergyWithdrawEvent energyWithdrawEvent = new EnergyWithdrawEvent(location);
                    this.getAddon().getJavaPlugin().getServer().getPluginManager().callEvent(energyWithdrawEvent);
                    String energyInMachine = StringNumberUtil.add(EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData), energyWithdrawEvent.getEnergy());

                    this.addEnergy(item, energyInMachine);
                    EnergyUtil.setCharge(FinalTech.getLocationDataService(), locationData, StringNumberUtil.ZERO);

                    this.updateLore(item);

                    javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, block));
                }
            }
        }
    }

    @Nonnull
    public String getEnergy(@Nonnull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            return JavaUtil.getFirstNotNull(persistentDataContainer.get(this.key, PersistentDataType.STRING), StringNumberUtil.ZERO);
        }
        return StringNumberUtil.ZERO;
    }

    public void addEnergy(@Nonnull ItemStack itemStack, @Nonnull String energy) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            String number = JavaUtil.getFirstNotNull(persistentDataContainer.get(this.key, PersistentDataType.STRING), StringNumberUtil.ZERO);
            number = StringNumberUtil.add(number, energy);
            persistentDataContainer.set(this.key, PersistentDataType.STRING, number);
            itemStack.setItemMeta(itemMeta);
        }
    }

    public void subEnergy(@Nonnull ItemStack itemStack, @Nonnull String energy) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            String number = JavaUtil.getFirstNotNull(persistentDataContainer.get(this.key, PersistentDataType.STRING), StringNumberUtil.ZERO);
            number = StringNumberUtil.sub(number, energy);
            persistentDataContainer.set(this.key, PersistentDataType.STRING, number);
            itemStack.setItemMeta(itemMeta);
        }
    }

    public void setEnergy(@Nonnull ItemStack itemStack, @Nonnull String energy) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null) {
            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            persistentDataContainer.set(this.key, PersistentDataType.STRING, energy);
            itemStack.setItemMeta(itemMeta);
        }
    }

    public void updateLore(@Nonnull ItemStack itemStack) {
        ItemStackUtil.setLore(itemStack, ConfigUtil.getStatusMenuLore(FinalTech.getLanguageManager(), this,
                this.getEnergy(itemStack)));
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
    }
}
