package io.taraxacum.finaltech.core.networks;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.network.NetworkComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetProvider;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNet;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

/**
 * @author Final_ROOT
 */
public class AlteredEnergyNet extends EnergyNet {
    private final int range = ConfigUtil.getOrDefaultConfigSetting(6, "network", "energy", "range");

    private final Map<Location, EnergyNetProvider> generators = new HashMap<>();
    private final Map<Location, EnergyNetComponent> capacitors = new HashMap<>();
    private final Map<Location, EnergyNetComponent> consumers = new HashMap<>();

    private final static Map<Location, Long> GENERATED_ENERGY_MAP = new HashMap<>();
    private final static Map<Location, Long> CONSUMED_ENERGY_MAP = new HashMap<>();

    protected AlteredEnergyNet(@Nonnull Location location) {
        super(location);
    }

    @Override
    public int getRange() {
        return this.range;
    }

    @Nonnull
    @Override
    public String getId() {
        return "ALTERED_ENERGY_NETWORK";
    }

    @Nullable
    @Override
    public NetworkComponent classifyLocation(@Nonnull Location location) {
        LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
        if(locationData != null) {
            if(LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), locationData) instanceof EnergyNetComponent energyNetComponent) {
                return switch (energyNetComponent.getEnergyComponentType()) {
                    case GENERATOR, CONSUMER -> NetworkComponent.TERMINUS;
                    case CONNECTOR, CAPACITOR -> NetworkComponent.CONNECTOR;
                    default -> this.regulator.equals(location) ? NetworkComponent.REGULATOR : null;
                };
            } else {
                return this.regulator.equals(location) ? NetworkComponent.REGULATOR : null;
            }
        }

        return null;
    }

    @Override
    public void onClassificationChange(Location location, NetworkComponent from, NetworkComponent to) {
        // TODO
        this.generators.remove(location);
        this.capacitors.remove(location);
        this.consumers.remove(location);

        LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
        if(locationData != null
                && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), locationData) instanceof EnergyNetComponent energyNetComponent) {
            switch (energyNetComponent.getEnergyComponentType()) {
                case GENERATOR -> {
                    if(LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), locationData) instanceof EnergyNetProvider energyNetProvider) {
                        this.generators.put(location, energyNetProvider);
                    } else {
                        LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), locationData).warn("This Item is marked as a GENERATOR but does not implement the interface EnergyNetProvider!");
                    }
                }
                case CAPACITOR -> this.capacitors.put(location, energyNetComponent);
                case CONSUMER -> this.consumers.put(location, energyNetComponent);
            }
        }
    }

    public void tick(@Nonnull LocationData locationData, @Nonnull BiConsumer<Inventory, Summary> inventoryUpdater, @Nonnull Block block, @Nonnull SlimefunItem slimefunItem) {
        Location location = block.getLocation();

        if (!this.regulator.equals(location)) {
            this.updateHologram(block, FinalTech.getLanguageString("items", slimefunItem.getId(), "hologram", "multi-regulator"));
            return;
        }

        super.tick();

        if (this.connectorNodes.isEmpty() && this.terminusNodes.isEmpty()) {
            this.updateHologram(block, FinalTech.getLanguageString("items", slimefunItem.getId(), "hologram", "no-nodes"));
            return;
        }

        Summary summary = new Summary();

        BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(this.getId()), () -> {
            try {
                // Used to cal output energy
                Iterator<Map.Entry<Location, EnergyNetProvider>> generatorIterator1 = this.generators.entrySet().iterator();
                // Used to cal stored energy
                Iterator<Map.Entry<Location, EnergyNetProvider>> generatorIterator2 = this.generators.entrySet().iterator();
                Iterator<Map.Entry<Location, EnergyNetComponent>> capacitorIterator = this.capacitors.entrySet().iterator();
                Iterator<Map.Entry<Location, EnergyNetComponent>> consumerIterator = this.consumers.entrySet().iterator();
                List<Location> removeList = new ArrayList<>();

                AtomicLong timestamp = new AtomicLong(Slimefun.getProfiler().newEntry());

                long outputEnergy = 0;
                while (consumerIterator.hasNext()) {
                    Map.Entry<Location, EnergyNetComponent> consumerEntry = consumerIterator.next();
                    if(!consumerEntry.getKey().getChunk().isLoaded()) {
                        continue;
                    }
                    EnergyNetComponent consumer = consumerEntry.getValue();
                    int consumerEnergy = consumer.getCharge(consumerEntry.getKey());
                    int consumerCapacity = consumer.getCapacity();

                    int leftEnergy = consumerCapacity - consumerEnergy;
                    if(leftEnergy > 0) {
                        summary.consumableEnergy += leftEnergy;

                        long transferEnergy;
                        int sourceEnergy;
                        if(outputEnergy > 0) {
                            transferEnergy = Math.min(leftEnergy, outputEnergy);
                            leftEnergy -= transferEnergy;
                            outputEnergy -= transferEnergy;
                        }

                        while (leftEnergy > 0 && generatorIterator1.hasNext()) {
                            Map.Entry<Location, EnergyNetProvider> generatorEntry = generatorIterator1.next();
                            if(!generatorEntry.getKey().getChunk().isLoaded()) {
                                continue;
                            }
                            if(GENERATED_ENERGY_MAP.containsKey(generatorEntry.getKey())) {
                                continue;
                            }
                            long t1 = Slimefun.getProfiler().newEntry();
                            sourceEnergy = FinalTech.getBlockTickerService().getGeneratedOutput(generatorEntry.getValue(), generatorEntry.getKey());
                            long t2 = Slimefun.getProfiler().closeEntry(generatorEntry.getKey(), (SlimefunItem) generatorEntry.getValue(), t1);
                            timestamp.getAndAdd(t2);
                            if(FinalTech.getBlockTickerService().willExplode(generatorEntry.getValue(), generatorEntry.getKey())) {
                                FinalTech.getLocationDataService().clearLocationData(generatorEntry.getKey());
                                Location generatorLocation = generatorEntry.getKey();
                                Slimefun.runSync(() -> {
                                    generatorLocation.getBlock().setType(Material.LAVA);
                                    generatorLocation.getWorld().createExplosion(generatorLocation, 0F, false);
                                });
                                removeList.add(generatorLocation);
                                continue;
                            }

                            if(sourceEnergy > 0) {
                                summary.generatedEnergy += sourceEnergy;
                                outputEnergy += sourceEnergy;
                                transferEnergy = Math.min(leftEnergy, outputEnergy);
                                leftEnergy -= transferEnergy;
                                outputEnergy -= transferEnergy;
                                GENERATED_ENERGY_MAP.put(generatorEntry.getKey(), (long) sourceEnergy);
                            }
                        }

                        while (leftEnergy > 0 && generatorIterator2.hasNext()) {
                            Map.Entry<Location, EnergyNetProvider> generatorEntry = generatorIterator2.next();
                            if(!generatorEntry.getKey().getChunk().isLoaded()) {
                                continue;
                            }
                            sourceEnergy = generatorEntry.getValue().getCharge(generatorEntry.getKey());
                            summary.generatorCapacity += generatorEntry.getValue().getCapacity();
                            if(sourceEnergy > 0) {
                                transferEnergy = Math.min(leftEnergy, sourceEnergy);
                                leftEnergy -= transferEnergy;
                                sourceEnergy -= transferEnergy;
                                summary.generatorEnergy += sourceEnergy;
                                generatorEntry.getValue().setCharge(generatorEntry.getKey(), sourceEnergy);
                            }
                        }

                        while (leftEnergy > 0 && capacitorIterator.hasNext()) {
                            Map.Entry<Location, EnergyNetComponent> capacitorEntry = capacitorIterator.next();
                            if(!capacitorEntry.getKey().getChunk().isLoaded()) {
                                continue;
                            }
                            sourceEnergy = capacitorEntry.getValue().getCharge(capacitorEntry.getKey());
                            summary.capacitorCapacity += capacitorEntry.getValue().getCapacity();
                            if(sourceEnergy > 0) {
                                transferEnergy = Math.min(leftEnergy, sourceEnergy);
                                leftEnergy -= transferEnergy;
                                sourceEnergy -= transferEnergy;
                                summary.capacitorEnergy += sourceEnergy;
                                capacitorEntry.getValue().setCharge(capacitorEntry.getKey(), sourceEnergy);
                            }
                        }

                        consumerEntry.getValue().setCharge(consumerEntry.getKey(), consumerCapacity - leftEnergy);
                    }

                    int consumed = consumerCapacity - leftEnergy - consumerEnergy;
                    if(consumed > 0) {
                        summary.transferredEnergy += consumed;
                        CONSUMED_ENERGY_MAP.put(consumerEntry.getKey(), CONSUMED_ENERGY_MAP.getOrDefault(consumerEntry.getKey(), 0L) + consumed);
                    }
                    summary.consumerEnergy += consumerCapacity - leftEnergy;
                    summary.consumerCapacity += consumerCapacity;
                }

                int energy;
                int capacity;

                while (capacitorIterator.hasNext()) {
                    Map.Entry<Location, EnergyNetComponent> capacitorEntry = capacitorIterator.next();
                    if(!capacitorEntry.getKey().getChunk().isLoaded()) {
                        continue;
                    }
                    energy = capacitorEntry.getValue().getCharge(capacitorEntry.getKey());
                    capacity = capacitorEntry.getValue().getCapacity();
                    summary.capacitorCapacity += capacitorEntry.getValue().getCapacity();

                    int leftEnergy = capacity - energy;
                    if(leftEnergy > 0) {
                        long transferEnergy;
                        int sourceEnergy;
                        if(outputEnergy > 0) {
                            transferEnergy = Math.min(leftEnergy, outputEnergy);
                            leftEnergy -= transferEnergy;
                            outputEnergy -= transferEnergy;
                        }

                        while (leftEnergy > 0 && generatorIterator1.hasNext()) {
                            Map.Entry<Location, EnergyNetProvider> generatorOutputEntry = generatorIterator1.next();
                            if(!generatorOutputEntry.getKey().getChunk().isLoaded() || GENERATED_ENERGY_MAP.containsKey(generatorOutputEntry.getKey())) {
                                continue;
                            }
                            long t1 = Slimefun.getProfiler().newEntry();
                            sourceEnergy = FinalTech.getBlockTickerService().getGeneratedOutput(generatorOutputEntry.getValue(), generatorOutputEntry.getKey());
                            long t2 = Slimefun.getProfiler().closeEntry(generatorOutputEntry.getKey(), (SlimefunItem) generatorOutputEntry.getValue(), t1);
                            timestamp.getAndAdd(t2);
                            if(FinalTech.getBlockTickerService().willExplode(generatorOutputEntry.getValue(), generatorOutputEntry.getKey())) {
                                FinalTech.getLocationDataService().clearLocationData(generatorOutputEntry.getKey());
                                Location generatorLocation = generatorOutputEntry.getKey();
                                Slimefun.runSync(() -> {
                                    generatorLocation.getBlock().setType(Material.LAVA);
                                    generatorLocation.getWorld().createExplosion(generatorLocation, 0F, false);
                                });
                                removeList.add(generatorLocation);
                                continue;
                            }

                            if(sourceEnergy > 0) {
                                summary.generatedEnergy += sourceEnergy;
                                outputEnergy += sourceEnergy;
                                transferEnergy = Math.min(leftEnergy, outputEnergy);
                                leftEnergy -= transferEnergy;
                                outputEnergy -= transferEnergy;
                                GENERATED_ENERGY_MAP.put(generatorOutputEntry.getKey(), (long) sourceEnergy);
                            }
                        }

                        summary.capacitorEnergy += capacity - leftEnergy;
                        capacitorEntry.getValue().setCharge(capacitorEntry.getKey(), capacity - leftEnergy);
                    } else {
                        summary.capacitorEnergy += energy;
                    }
                }

                while (generatorIterator2.hasNext()) {
                    Map.Entry<Location, EnergyNetProvider> generatorEntry = generatorIterator2.next();
                    if(!generatorEntry.getValue().isChargeable() || !generatorEntry.getKey().getChunk().isLoaded()) {
                        continue;
                    }

                    energy = generatorEntry.getValue().getCharge(generatorEntry.getKey());
                    capacity = generatorEntry.getValue().getCapacity();
                    summary.generatorCapacity += capacity;

                    int leftEnergy = capacity - energy;
                    if(leftEnergy > 0) {
                        long transferEnergy;
                        int sourceEnergy;
                        if(outputEnergy > 0) {
                            transferEnergy = Math.min(leftEnergy, outputEnergy);
                            leftEnergy -= transferEnergy;
                            outputEnergy -= transferEnergy;
                        }

                        while (leftEnergy > 0 && generatorIterator1.hasNext()) {
                            Map.Entry<Location, EnergyNetProvider> generatorOutputEntry = generatorIterator1.next();
                            if(GENERATED_ENERGY_MAP.containsKey(generatorOutputEntry.getKey())) {
                                continue;
                            }
                            long t1 = Slimefun.getProfiler().newEntry();
                            sourceEnergy = FinalTech.getBlockTickerService().getGeneratedOutput(generatorOutputEntry.getValue(), generatorOutputEntry.getKey());
                            long t2 = Slimefun.getProfiler().closeEntry(generatorOutputEntry.getKey(), (SlimefunItem) generatorOutputEntry.getValue(), t1);
                            timestamp.getAndAdd(t2);
                            if(FinalTech.getBlockTickerService().willExplode(generatorOutputEntry.getValue(), generatorOutputEntry.getKey())) {
                                FinalTech.getLocationDataService().clearLocationData(generatorOutputEntry.getKey());
                                Location generatorLocation = generatorOutputEntry.getKey();
                                Slimefun.runSync(() -> {
                                    generatorLocation.getBlock().setType(Material.LAVA);
                                    generatorLocation.getWorld().createExplosion(generatorLocation, 0F, false);
                                });
                                removeList.add(generatorLocation);
                                continue;
                            }

                            if(sourceEnergy > 0) {
                                summary.generatedEnergy += sourceEnergy;
                                outputEnergy += sourceEnergy;
                                transferEnergy = Math.min(leftEnergy, outputEnergy);
                                leftEnergy -= transferEnergy;
                                outputEnergy -= transferEnergy;
                                GENERATED_ENERGY_MAP.put(generatorOutputEntry.getKey(), (long) sourceEnergy);
                            }
                        }

                        summary.generatorEnergy += capacity - leftEnergy;
                        generatorEntry.getValue().setCharge(generatorEntry.getKey(), capacity - leftEnergy);
                    } else {
                        summary.generatorEnergy += energy;
                    }
                }

                for(Location l : removeList) {
                    this.generators.remove(l);
                }

                long showEnergy = summary.generatorEnergy + summary.capacitorEnergy - summary.consumableEnergy;
                String showStr = NumberUtils.getCompactDouble(showEnergy);
                showStr = showEnergy < 0 ? "&4&l- &c" + showStr + " &7J &e\u26A1" : "&2&l+ &a" + showStr + " &7J &e\u26A1";

                this.updateHologram(block, showStr);

                Slimefun.getProfiler().closeEntry(block.getLocation(), slimefunItem, timestamp.get());

                summary.consumerAmount = this.consumers.size();
                summary.generatorAmount = this.generators.size();
                summary.capacitorAmount = this.capacitors.size();

                Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
                if (inventory != null && !inventory.getViewers().isEmpty()) {
                    inventoryUpdater.accept(inventory, summary);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, () -> {
            Set<Location> locationSet = new HashSet<>();
            locationSet.addAll(this.generators.keySet());
            locationSet.addAll(this.capacitors.keySet());
            locationSet.addAll(this.consumers.keySet());
            return locationSet.toArray(new Location[0]);
        });
    }

    public static void uniqueTick() {
        GENERATED_ENERGY_MAP.clear();
        CONSUMED_ENERGY_MAP.clear();
    }

    @Nonnull
    public static EnergyNet getNetworkFromLocationOrCreate(@Nonnull Location l) {
        Optional<EnergyNet> energyNetwork = Slimefun.getNetworkManager().getNetworkFromLocation(l, EnergyNet.class);

        if (energyNetwork.isPresent()) {
            return energyNetwork.get();
        } else {
            EnergyNet network = new AlteredEnergyNet(l);
            Slimefun.getNetworkManager().registerNetwork(network);
            return network;
        }
    }

    public static Map<Location, Long> getGeneratedEnergyMap() {
        return GENERATED_ENERGY_MAP;
    }

    public static Map<Location, Long> getConsumedEnergyMap() {
        return CONSUMED_ENERGY_MAP;
    }

    public static class Summary {
        private int generatorAmount;
        private int capacitorAmount;
        private int consumerAmount;
        private long generatorEnergy;
        private long capacitorEnergy;
        private long consumerEnergy;
        private long generatorCapacity;
        private long capacitorCapacity;
        private long consumerCapacity;
        private long generatedEnergy;
        private long transferredEnergy;
        private long consumableEnergy;

        public Summary() {

        }

        public Summary(int generatorAmount, int capacitorAmount, int consumerAmount, long generatorEnergy, long capacitorEnergy, long consumerEnergy, long generatorCapacity, long capacitorCapacity, long consumerCapacity, long generatedEnergy, long transferredEnergy, long consumableEnergy) {
            this.generatorAmount = generatorAmount;
            this.capacitorAmount = capacitorAmount;
            this.consumerAmount = consumerAmount;
            this.generatorEnergy = generatorEnergy;
            this.capacitorEnergy = capacitorEnergy;
            this.consumerEnergy = consumerEnergy;
            this.generatorCapacity = generatorCapacity;
            this.capacitorCapacity = capacitorCapacity;
            this.consumerCapacity = consumerCapacity;
            this.generatedEnergy = generatedEnergy;
            this.transferredEnergy = transferredEnergy;
            this.consumableEnergy = consumableEnergy;
        }

        public int getGeneratorAmount() {
            return generatorAmount;
        }

        public void setGeneratorAmount(int generatorAmount) {
            this.generatorAmount = generatorAmount;
        }

        public int getCapacitorAmount() {
            return capacitorAmount;
        }

        public void setCapacitorAmount(int capacitorAmount) {
            this.capacitorAmount = capacitorAmount;
        }

        public int getConsumerAmount() {
            return consumerAmount;
        }

        public void setConsumerAmount(int consumerAmount) {
            this.consumerAmount = consumerAmount;
        }

        public long getGeneratorEnergy() {
            return generatorEnergy;
        }

        public void setGeneratorEnergy(long generatorEnergy) {
            this.generatorEnergy = generatorEnergy;
        }

        public long getCapacitorEnergy() {
            return capacitorEnergy;
        }

        public void setCapacitorEnergy(long capacitorEnergy) {
            this.capacitorEnergy = capacitorEnergy;
        }

        public long getConsumerEnergy() {
            return consumerEnergy;
        }

        public void setConsumerEnergy(long consumerEnergy) {
            this.consumerEnergy = consumerEnergy;
        }

        public long getGeneratorCapacity() {
            return generatorCapacity;
        }

        public void setGeneratorCapacity(long generatorCapacity) {
            this.generatorCapacity = generatorCapacity;
        }

        public long getCapacitorCapacity() {
            return capacitorCapacity;
        }

        public void setCapacitorCapacity(long capacitorCapacity) {
            this.capacitorCapacity = capacitorCapacity;
        }

        public long getConsumerCapacity() {
            return consumerCapacity;
        }

        public void setConsumerCapacity(long consumerCapacity) {
            this.consumerCapacity = consumerCapacity;
        }

        public long getGeneratedEnergy() {
            return generatedEnergy;
        }

        public void setGeneratedEnergy(long generatedEnergy) {
            this.generatedEnergy = generatedEnergy;
        }

        public long getTransferredEnergy() {
            return transferredEnergy;
        }

        public void setTransferredEnergy(long transferredEnergy) {
            this.transferredEnergy = transferredEnergy;
        }

        public long getConsumableEnergy() {
            return consumableEnergy;
        }

        public void setConsumableEnergy(long consumableEnergy) {
            this.consumableEnergy = consumableEnergy;
        }
    }
}
