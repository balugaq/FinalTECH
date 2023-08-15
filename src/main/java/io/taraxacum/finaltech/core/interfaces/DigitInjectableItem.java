package io.taraxacum.finaltech.core.interfaces;

import io.taraxacum.libs.plugin.ld.LocationData;

import javax.annotation.Nonnull;

/**
 * @see io.taraxacum.finaltech.core.item.machine.range.point.DigitInjector
 * @author Final_ROOT
 */
public interface DigitInjectableItem {

    void injectDigit(@Nonnull LocationData locationData, int digit);
}
