package io.taraxacum.finaltech.core.option;

import io.taraxacum.libs.slimefun.dto.LocationDataOption;

import java.util.ArrayList;

/**
 * @author Final_ROOT
 */
public class CargoLock {
    public static final String KEY = "cll";

    public static final String VALUE_TRUE = "t";
    public static final String VALUE_FALSE = "f";

    public static final LocationDataOption OPTION = new LocationDataOption(LocationDataOption.CARGO_ID, KEY, new ArrayList<>() {{
        this.add(VALUE_FALSE);
        this.add(VALUE_TRUE);
    }});
}
