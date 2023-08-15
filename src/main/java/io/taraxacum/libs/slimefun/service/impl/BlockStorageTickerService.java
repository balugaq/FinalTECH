package io.taraxacum.libs.slimefun.service.impl;

import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetProvider;
import io.taraxacum.libs.slimefun.dto.LocationBlockStorageData;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.service.BlockTickerService;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Location;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Final_ROOT
 */
public class BlockStorageTickerService extends BlockTickerService {
    private final MethodHandle methodHandle;

    private BlockStorageTickerService(@Nonnull MethodHandle methodHandle) {
        this.methodHandle = methodHandle;
    }

    @Override
    public void run(@Nonnull BlockTicker blockTicker, @Nonnull LocationData locationData) throws Throwable {
        if(locationData instanceof LocationBlockStorageData locationBlockStorageData) {
            methodHandle.invokeExact(blockTicker, locationData.getLocation().getBlock(), locationBlockStorageData.getSlimefunItem(), locationBlockStorageData.getConfig());
        }
    }

    @Override
    public final BlockTicker warp(@Nonnull BlockTicker blockTicker, @Nonnull BiConsumer<Runnable, LocationData> ticker, @Nonnull Function<LocationData, Boolean>[] beforeTicks, @Nonnull Consumer<LocationData>[] afterTicks, @Nonnull Runnable[] extraUniqueTicks) {
        return new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return blockTicker.isSynchronized();
            }

            public void tick(Block block, SlimefunItem slimefunItem, Config data) {
                LocationData locationData = new LocationBlockStorageData(block.getLocation(), data, slimefunItem.getId(), slimefunItem);
                ticker.accept(() -> {
                    for (Function<LocationData, Boolean> function : beforeTicks) {
                        if(!function.apply(locationData)) {
                            return;
                        }
                    }
                    blockTicker.tick(block, slimefunItem, data);
                    for (Consumer<LocationData> consumer : afterTicks) {
                        consumer.accept(locationData);
                    }
                }, locationData);
            }

            @Override
            public void update() {
                blockTicker.update();
            }

            @Override
            public Optional<IncompatibleItemHandlerException> validate(SlimefunItem item) {
                return blockTicker.validate(item);
            }

            @Override
            public void uniqueTick() {
                for(Runnable runnable : extraUniqueTicks) {
                    runnable.run();
                }
                blockTicker.uniqueTick();
            }

            @Override
            public Class<? extends ItemHandler> getIdentifier() {
                return blockTicker.getIdentifier();
            }

            @Override
            public void startNewTick() {
                blockTicker.startNewTick();
            }
        };
    }

    @Override
    public int getGeneratedOutput(@Nonnull EnergyNetProvider energyNetProvider, @Nonnull Location location) {
        Config config = BlockStorage.getLocationInfo(location);
        return energyNetProvider.getGeneratedOutput(location, config);
    }

    @Override
    public boolean willExplode(@Nonnull EnergyNetProvider energyNetProvider, @Nonnull Location location) {
        Config config = BlockStorage.getLocationInfo(location);
        return energyNetProvider.willExplode(location, config);
    }

    @Nullable
    public static BlockStorageTickerService newInstance() {
        try {
            Method tickerClassMethod = BlockTicker.class.getMethod("tick", Block.class, SlimefunItem.class, Config.class);
            MethodHandle methodHandle = MethodHandles.lookup().unreflect(tickerClassMethod);
            return new BlockStorageTickerService(methodHandle);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
