package io.taraxacum.finaltech.core.dto;

import io.taraxacum.libs.plugin.dto.InvWithSlots;
import io.taraxacum.finaltech.core.option.CargoFilter;
import io.taraxacum.finaltech.core.option.CargoLimit;
import io.taraxacum.finaltech.core.option.SlotSearchOrder;
import io.taraxacum.finaltech.core.option.SlotSearchSize;
import io.taraxacum.libs.plugin.ld.LocationDataService;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

/**
 * @see io.taraxacum.finaltech.util.CargoUtil#doSimpleCargo(SimpleCargoDTO, String)
 * @author Final_ROOT
 */
public class SimpleCargoDTO {

    private LocationDataService locationDataService;

    private InvWithSlots inputMap;

    /** Source #{@link Location} of #{@link BlockMenu} or #{@link Inventory} */
    private Block inputBlock;

    /** #{@link SlotSearchSize} */
    private String inputSize;

    /** #{@link SlotSearchOrder} */
    private String inputOrder;

    private InvWithSlots outputMap;

    /** Target #{@link Location} of #{@link BlockMenu} or #{@link Inventory} */
    private Block outputBlock;

    /** #{@link SlotSearchSize} */
    private String outputSize;

    /** #{@link SlotSearchOrder} */
    private String outputOrder;

    /** Number limited in one cargo action */
    private int cargoNumber;

    /** #{@link CargoLimit} */
    private String cargoLimit;

    /** #{@link CargoFilter} */
    private String cargoFilter;

    /** #{@link Inventory} for #{@link CargoFilter} to use */
    private Inventory filterInv;

    /** the slots of the filterInv to be used */
    private int[] filterSlots;

    public SimpleCargoDTO() {

    }

    public SimpleCargoDTO(LocationDataService locationDataService, InvWithSlots inputMap, Block inputBlock, String inputSize, String inputOrder, InvWithSlots outputMap, Block outputBlock, String outputSize, String outputOrder, int cargoNumber, String cargoLimit, String cargoFilter, Inventory filterInv, int[] filterSlots) {
        this.locationDataService = locationDataService;
        this.inputMap = inputMap;
        this.inputBlock = inputBlock;
        this.inputSize = inputSize;
        this.inputOrder = inputOrder;
        this.outputMap = outputMap;
        this.outputBlock = outputBlock;
        this.outputSize = outputSize;
        this.outputOrder = outputOrder;
        this.cargoNumber = cargoNumber;
        this.cargoLimit = cargoLimit;
        this.cargoFilter = cargoFilter;
        this.filterInv = filterInv;
        this.filterSlots = filterSlots;
    }

    public SimpleCargoDTO(CargoDTO cargoDTO, InvWithSlots inputMap, InvWithSlots outputMap) {
        this.locationDataService = cargoDTO.getLocationDataService();
        this.inputBlock = cargoDTO.getInputBlock();
        this.inputSize = cargoDTO.getInputSize();
        this.inputOrder = cargoDTO.getInputOrder();
        this.outputBlock = cargoDTO.getOutputBlock();
        this.outputSize = cargoDTO.getOutputSize();
        this.outputOrder = cargoDTO.getOutputOrder();
        this.cargoNumber = cargoDTO.getCargoNumber();
        this.cargoLimit = cargoDTO.getCargoLimit();
        this.cargoFilter = cargoDTO.getCargoFilter();
        this.filterInv = cargoDTO.getFilterInv();
        this.filterSlots = cargoDTO.getFilterSlots();

        this.inputMap = inputMap;
        this.outputMap = outputMap;
    }

    public LocationDataService getLocationDataService() {
        return locationDataService;
    }

    public void setLocationDataService(LocationDataService locationDataService) {
        this.locationDataService = locationDataService;
    }

    public InvWithSlots getInputMap() {
        return inputMap;
    }

    public void setInputMap(InvWithSlots inputMap) {
        this.inputMap = inputMap;
    }

    public Block getInputBlock() {
        return inputBlock;
    }

    public void setInputBlock(Block inputBlock) {
        this.inputBlock = inputBlock;
    }

    public String getInputSize() {
        return inputSize;
    }

    public void setInputSize(String inputSize) {
        this.inputSize = inputSize;
    }

    public String getInputOrder() {
        return inputOrder;
    }

    public void setInputOrder(String inputOrder) {
        this.inputOrder = inputOrder;
    }

    public InvWithSlots getOutputMap() {
        return outputMap;
    }

    public void setOutputMap(InvWithSlots outputMap) {
        this.outputMap = outputMap;
    }

    public Block getOutputBlock() {
        return outputBlock;
    }

    public void setOutputBlock(Block outputBlock) {
        this.outputBlock = outputBlock;
    }

    public String getOutputSize() {
        return outputSize;
    }

    public void setOutputSize(String outputSize) {
        this.outputSize = outputSize;
    }

    public String getOutputOrder() {
        return outputOrder;
    }

    public void setOutputOrder(String outputOrder) {
        this.outputOrder = outputOrder;
    }

    public int getCargoNumber() {
        return cargoNumber;
    }

    public void setCargoNumber(int cargoNumber) {
        this.cargoNumber = cargoNumber;
    }

    public String getCargoLimit() {
        return cargoLimit;
    }

    public void setCargoLimit(String cargoLimit) {
        this.cargoLimit = cargoLimit;
    }

    public String getCargoFilter() {
        return cargoFilter;
    }

    public void setCargoFilter(String cargoFilter) {
        this.cargoFilter = cargoFilter;
    }

    public Inventory getFilterInv() {
        return filterInv;
    }

    public void setFilterInv(Inventory filterInv) {
        this.filterInv = filterInv;
    }

    public int[] getFilterSlots() {
        return filterSlots;
    }

    public void setFilterSlots(int[] filterSlots) {
        this.filterSlots = filterSlots;
    }
}
