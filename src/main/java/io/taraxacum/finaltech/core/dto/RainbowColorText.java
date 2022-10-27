package io.taraxacum.finaltech.core.dto;

import io.taraxacum.finaltech.api.factory.RandomColorText;
import io.taraxacum.finaltech.util.slimefun.TextUtil;
import org.bukkit.Color;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Final_ROOT
 * @since 2.0
 */
public class RainbowColorText extends RandomColorText {
    private int step;
    private int index = 0;
    private final Color[] circulateColor;

    public RainbowColorText(@Nonnull List<Color> colorList, @Nonnull String... text) {
        this(1, colorList, text);
    }

    public RainbowColorText(int step, @Nonnull List<Color> colorList, @Nonnull String... text) {
        this(step, text.length, colorList, text);
    }

    public RainbowColorText(int step, int disperse, @Nonnull List<Color> colorList, @Nonnull String... text) {
        super(text);
        this.step = step;
        this.circulateColor = TextUtil.disperse(disperse * colorList.size(), colorList);
        this.calNextColorList();
    }

    @Nonnull
    @Override
    protected ColorString[] initColorList(@Nonnull String... text) {
        ColorString[] colorStrings = new ColorString[text.length];
        for (int i = 0; i < text.length; i++) {
            ColorString colorString = new ColorString(text[i], TextUtil.WHITE_COLOR);
            colorStrings[i] = colorString;
        }
        return colorStrings;
    }

    @Override
    public void calNextColorList() {
        this.index += this.step;
        this.index = this.index % this.circulateColor.length;
        for (int i = 0; i < this.getSize(); i++) {
            this.setColor(this.circulateColor[(this.index + i) % this.circulateColor.length], i);
        }
    }
}
