package io.taraxacum.finaltech.menu;

import io.taraxacum.finaltech.abstractItem.machine.AbstractMachine;
import io.taraxacum.finaltech.abstractItem.menu.AbstractStandardMachineMenu;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class BasicFrameMachineMenu extends AbstractStandardMachineMenu {
    private static final int[] BORDER = new int[]{4, 22, 31, 40, 49};
    private static final int[] INPUT_BORDER = new int[]{3, 12, 21, 30, 39, 48};
    private static final int[] OUTPUT_BORDER = new int[]{5, 14, 23, 32, 41, 50};
    private static final int[] INPUT_SLOTS = new int[]{0, 1, 2, 9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38, 45, 46, 47};
    private static final int[] OUTPUT_SLOTS = new int[]{6, 7, 8, 15, 16, 17, 24, 25, 26, 33, 34, 35, 42, 43, 44, 51, 52, 53};

    public BasicFrameMachineMenu(@Nonnull String id, @Nonnull String title, AbstractMachine abstractMachine) {
        super(id, title, abstractMachine);
    }

    @Override
    public int[] getBorder() {
        return BORDER;
    }

    @Override
    public int[] getInputBorder() {
        return INPUT_BORDER;
    }

    @Override
    public int[] getOutputBorder() {
        return OUTPUT_BORDER;
    }

    @Override
    public int[] getInputSlots() {
        return INPUT_SLOTS;
    }

    @Override
    public int[] getOutputSlots() {
        return OUTPUT_SLOTS;
    }
}