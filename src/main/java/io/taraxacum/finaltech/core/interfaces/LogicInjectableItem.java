package io.taraxacum.finaltech.core.interfaces;

import io.taraxacum.libs.plugin.ld.LocationData;

import javax.annotation.Nonnull;

/**
 * @see io.taraxacum.finaltech.core.item.machine.range.point.LogicInjector
 * @author Final_ROOT
 */
public interface LogicInjectableItem {

    void injectLogic(@Nonnull LocationData locationData, boolean logic);
}
