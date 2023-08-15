package io.taraxacum.libs.slimefun.util;

import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.libs.plugin.ld.LocationDataService;
import io.taraxacum.libs.plugin.ld.LocationData;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class EnergyUtil {
    @Nonnull
    public static String getCharge(@Nonnull LocationDataService locationDataService, @Nonnull LocationData locationData) {
        return JavaUtil.getFirstNotNull(locationDataService.getLocationData(locationData, ConstantTableUtil.CONFIG_CHARGE), "0");
    }

    public static void setCharge(@Nonnull LocationDataService locationDataService, @Nonnull LocationData locationData, @Nonnull String energy) {
        locationDataService.setLocationData(locationData, ConstantTableUtil.CONFIG_CHARGE, energy);
    }
}
