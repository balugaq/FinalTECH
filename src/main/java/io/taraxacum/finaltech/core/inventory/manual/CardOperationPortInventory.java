package io.taraxacum.finaltech.core.inventory.manual;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enums.LogSourceType;
import io.taraxacum.finaltech.core.item.machine.AbstractMachine;
import io.taraxacum.finaltech.core.item.unusable.CopyCard;
import io.taraxacum.finaltech.core.item.unusable.StorageCard;
import io.taraxacum.finaltech.setup.FinalTechItemStacks;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.StringItemUtil;
import io.taraxacum.libs.plugin.inventory.template.LogicInventory;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
@Deprecated
public class CardOperationPortInventory extends AbstractManualMachineInventory implements LogicInventory {
    private final int[] border = new int[] {3, 4, 5, 12, 14, 21, 22, 23, 27, 28, 29, 33, 34, 35, 36, 37, 38, 42, 43, 44, 45, 46, 47, 51, 52, 53};
    private final int[] inputBorder = new int[] {0, 1, 2, 6, 7, 8, 9, 11, 15, 17, 18, 19, 20, 24, 25, 26};
    private final int[] outputBorder = new int[] {30, 31, 32, 39, 41, 48, 49, 50};
    private final int[] inputSlot = new int[] {10, 16};
    private final int[] outputSlot = new int[] {40};

    private final int craftSlot = 13;
    private final ItemStack craftIcon = new CustomItemStack(Material.RED_STAINED_GLASS_PANE,
            ConfigUtil.getStatusMenuName(FinalTech.getLanguageManager(), this.getId()),
            ConfigUtil.getStatusMenuLore(FinalTech.getLanguageManager(), this.getId()));

    public final List<Craft> craftList = new ArrayList<>();

    public CardOperationPortInventory(@Nonnull AbstractMachine machine) {
        super(machine);
        // entropy
        this.craftList.add(new Craft() {
            private final boolean enable = ConfigUtil.getOrDefaultItemSetting(true, CardOperationPortInventory.this.getId(), this.getId(), "enable");
            private final String infoName = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "name");
            private final String[] infoLore = FinalTech.getLanguageStringArray("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "lore");
            private final String infoOutput = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "output");

            @Nonnull
            @Override
            public String getId() {
                return "entropy";
            }

            @Override
            public boolean isEnabled() {
                return this.enable;
            }

            @Override
            public String getInfoName() {
                return this.infoName;
            }

            @Override
            public String[] getInfoLore() {
                return this.infoLore;
            }

            @Override
            public String getInfoOutput() {
                return this.infoOutput;
            }

            @Override
            public boolean canCraft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2) {
                if(ItemStackUtil.isItemNull(itemStack1) || ItemStackUtil.isItemNull(itemStack2)) {
                    return false;
                }

                ItemStack itemEntropy = null;
                ItemStack itemCard = null;
                if(ItemStackUtil.isItemSimilar(itemStack1, FinalTechItemStacks.ENTROPY)) {
                    itemEntropy = itemStack1;
                } else if(FinalTechItems.COPY_CARD.verifyItem(itemStack1) || FinalTechItems.STORAGE_CARD.verifyItem(itemStack1)) {
                    itemCard = itemStack1;
                }

                if(itemEntropy == null && itemCard == null) {
                    return false;
                }

                if(itemEntropy == null && ItemStackUtil.isItemSimilar(itemStack2, FinalTechItemStacks.ENTROPY)) {
                    itemEntropy = itemStack2;
                } else if(itemCard == null && (FinalTechItems.COPY_CARD.verifyItem(itemStack2) || FinalTechItems.STORAGE_CARD.verifyItem(itemStack2))) {
                    itemCard = itemStack2;
                }

                return itemEntropy != null && itemCard != null && !ItemStackUtil.isItemNull(StringItemUtil.parseItemInCard(itemCard));
            }

            @Override
            public boolean craft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, @Nonnull Inventory inventory, int outputSlot, @Nonnull Player player, @Nonnull Location location) {
                if(ItemStackUtil.isItemNull(itemStack1) || ItemStackUtil.isItemNull(itemStack2)) {
                    return false;
                }

                ItemStack itemEntropy = null;
                ItemStack itemCard = null;
                if(ItemStackUtil.isItemSimilar(itemStack1, FinalTechItemStacks.ENTROPY)) {
                    itemEntropy = itemStack1;
                } else if(FinalTechItems.COPY_CARD.verifyItem(itemStack1) || FinalTechItems.STORAGE_CARD.verifyItem(itemStack1)) {
                    itemCard = itemStack1;
                }

                if(itemEntropy == null && itemCard == null) {
                    return false;
                }

                if(itemEntropy == null && ItemStackUtil.isItemSimilar(itemStack2, FinalTechItemStacks.ENTROPY)) {
                    itemEntropy = itemStack2;
                } else if(itemCard == null && (FinalTechItems.COPY_CARD.verifyItem(itemStack2) || FinalTechItems.STORAGE_CARD.verifyItem(itemStack2))) {
                    itemCard = itemStack2;
                }

                if(itemEntropy == null || itemCard == null) {
                    return false;
                }

                ItemMeta itemMeta = itemCard.getItemMeta();
                String amount = StringItemUtil.parseAmountInCard(itemMeta);

                ItemStack outputItem;
                SlimefunItem slimefunItem = SlimefunItem.getByItem(itemCard);
                if(slimefunItem instanceof StorageCard storageCard) {
                    BigInteger bigInteger = new BigDecimal(amount).sqrt(MathContext.DECIMAL32).toBigInteger();
                    outputItem = storageCard.getValidItem(new ItemStack(FinalTechItemStacks.ENTROPY), bigInteger.toString());
                } else if(slimefunItem instanceof CopyCard copyCard) {
                    outputItem = copyCard.getValidItem(new ItemStack(FinalTechItemStacks.ENTROPY), amount);
                } else {
                    return false;
                }

                inventory.setItem(outputSlot, outputItem);
                itemCard.setAmount(itemCard.getAmount() - 1);
                itemEntropy.setAmount(itemEntropy.getAmount() - 1);

                return true;
            }
        });
        // craft-infinity-storage-card
        this.craftList.add(new Craft() {
            private final boolean enable = ConfigUtil.getOrDefaultItemSetting(true, CardOperationPortInventory.this.getId(), this.getId(), "enable");
            private final String infoName = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "name");
            private final String[] infoLore = FinalTech.getLanguageStringArray("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "lore");
            private final String infoOutput = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "output");

            @Nonnull
            @Override
            public String getId() {
                return "craft-infinity-storage-card";
            }

            @Override
            public boolean isEnabled() {
                return this.enable;
            }

            @Override
            public String getInfoName() {
                return this.infoName;
            }

            @Override
            public String[] getInfoLore() {
                return this.infoLore;
            }

            @Override
            public String getInfoOutput() {
                return this.infoOutput;
            }

            @Override
            public boolean canCraft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2) {
                if(ItemStackUtil.isItemNull(itemStack1) || ItemStackUtil.isItemNull(itemStack2)) {
                    return false;
                }

                ItemStack itemPhony = null;
                ItemStack itemCopyCard = null;
                if(FinalTechItems.ITEM_PHONY.verifyItem(itemStack1)) {
                    itemPhony = itemStack1;
                } else if(FinalTechItems.COPY_CARD.verifyItem(itemStack1)) {
                    itemCopyCard = itemStack1;
                }

                if(itemPhony == null && itemCopyCard == null) {
                    return false;
                }

                if(itemPhony == null && FinalTechItems.ITEM_PHONY.verifyItem(itemStack2)) {
                    itemPhony = itemStack2;
                } else if(itemCopyCard == null && FinalTechItems.COPY_CARD.verifyItem(itemStack2)) {
                    itemCopyCard = itemStack2;
                }

                return itemPhony != null && itemCopyCard != null && !ItemStackUtil.isItemNull(StringItemUtil.parseItemInCard(itemCopyCard));
            }

            @Override
            public boolean craft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, @Nonnull Inventory inventory, int outputSlot, @Nonnull Player player, @Nonnull Location location) {
                if(ItemStackUtil.isItemNull(itemStack1) || ItemStackUtil.isItemNull(itemStack2)) {
                    return false;
                }

                ItemStack itemPhony = null;
                ItemStack itemCopyCard = null;
                if(FinalTechItems.ITEM_PHONY.verifyItem(itemStack1)) {
                    itemPhony = itemStack1;
                } else if(FinalTechItems.COPY_CARD.verifyItem(itemStack1)) {
                    itemCopyCard = itemStack1;
                }

                if(itemPhony == null && itemCopyCard == null) {
                    return false;
                }

                if(itemPhony == null && FinalTechItems.ITEM_PHONY.verifyItem(itemStack2)) {
                    itemPhony = itemStack2;
                } else if(itemCopyCard == null && FinalTechItems.COPY_CARD.verifyItem(itemStack2)) {
                    itemCopyCard = itemStack2;
                }

                if(itemPhony == null || itemCopyCard == null) {
                    return false;
                }

                ItemStack stringItem = StringItemUtil.parseItemInCard(itemCopyCard);
                if(ItemStackUtil.isItemNull(stringItem)) {
                    return false;
                }

                ItemStack outputItem = FinalTechItems.STORAGE_CARD.getValidItem(stringItem, StringNumberUtil.VALUE_INFINITY);

                itemPhony.setAmount(itemPhony.getAmount() - 1);
                itemCopyCard.setAmount(itemCopyCard.getAmount() - 1);
                inventory.setItem(outputSlot, outputItem);

                FinalTech.getLogService().subItem(FinalTechItems.COPY_CARD.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                FinalTech.getLogService().subItem(FinalTechItems.ITEM_PHONY.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());

                return true;
            }
        });
        // distribute-storage-card
        this.craftList.add(new Craft() {
            private final boolean enable = ConfigUtil.getOrDefaultItemSetting(true, CardOperationPortInventory.this.getId(), this.getId(), "enable");
            private final String infoName = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "name");
            private final String[] infoLore = FinalTech.getLanguageStringArray("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "lore");
            private final String infoOutput = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "output");

            @Nonnull
            @Override
            public String getId() {
                return "distribute-storage-card";
            }

            @Override
            public boolean isEnabled() {
                return this.enable;
            }

            @Override
            public String getInfoName() {
                return this.infoName;
            }

            @Override
            public String[] getInfoLore() {
                return this.infoLore;
            }

            @Override
            public String getInfoOutput() {
                return this.infoOutput;
            }

            @Override
            public boolean canCraft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2) {
                if(ItemStackUtil.isItemNull(itemStack1) || ItemStackUtil.isItemNull(itemStack2)) {
                    return false;
                }

                if(itemStack1.getAmount() > 1 || itemStack2.getAmount() > 1) {
                    return false;
                }

                if(!FinalTechItems.STORAGE_CARD.verifyItem(itemStack1) || !FinalTechItems.STORAGE_CARD.verifyItem(itemStack2)) {
                    return false;
                }

                ItemStack stringItem1 = StringItemUtil.parseItemInCard(itemStack1);
                ItemStack stringItem2 = StringItemUtil.parseItemInCard(itemStack2);

                if(ItemStackUtil.isItemNull(stringItem1) && ItemStackUtil.isItemNull(stringItem2)) {
                    return false;
                }

                return ItemStackUtil.isItemNull(stringItem1) || ItemStackUtil.isItemNull(stringItem2);
            }

            @Override
            public boolean craft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, @Nonnull Inventory inventory, int outputSlot, @Nonnull Player player, @Nonnull Location location) {
                if(ItemStackUtil.isItemNull(itemStack1) || ItemStackUtil.isItemNull(itemStack2)) {
                    return false;
                }

                if(itemStack1.getAmount() > 1 || itemStack2.getAmount() > 1) {
                    return false;
                }

                if(!FinalTechItems.STORAGE_CARD.verifyItem(itemStack1) || !FinalTechItems.STORAGE_CARD.verifyItem(itemStack2)) {
                    return false;
                }

                ItemMeta itemMeta1 = itemStack1.getItemMeta();
                ItemMeta itemMeta2 = itemStack2.getItemMeta();

                ItemStack stringItem1 = StringItemUtil.parseItemInCard(itemMeta1);
                ItemStack stringItem2 = StringItemUtil.parseItemInCard(itemMeta2);

                if(ItemStackUtil.isItemNull(stringItem1) && ItemStackUtil.isItemNull(stringItem2)) {
                    return false;
                }

                ItemStack stringItem;
                if(ItemStackUtil.isItemNull(stringItem1)) {
                    stringItem = stringItem2;
                } else if(ItemStackUtil.isItemNull(stringItem2)) {
                    stringItem = stringItem1;
                } else {
                    return false;
                }

                String amount1 = StringItemUtil.parseAmountInCard(itemMeta1);
                String amount2 = StringItemUtil.parseAmountInCard(itemMeta2);

                String amount = StringNumberUtil.add(amount1, amount2);
                String resultAmount1;
                String resultAmount2;
                if(StringNumberUtil.VALUE_INFINITY.equals(amount)) {
                    resultAmount1 = StringNumberUtil.VALUE_INFINITY;
                    resultAmount2 = StringNumberUtil.VALUE_INFINITY;
                } else {
                    resultAmount1 = new BigInteger(amount).divide(new BigInteger("2")).toString();
                    resultAmount2 = StringNumberUtil.sub(amount, resultAmount1);
                }

                StringItemUtil.setItemInCard(itemMeta1, stringItem, resultAmount1);
                StringItemUtil.setItemInCard(itemMeta2, stringItem, resultAmount2);


                FinalTechItems.STORAGE_CARD.updateLore(itemMeta1);
                FinalTechItems.STORAGE_CARD.updateLore(itemMeta2);

                itemStack1.setItemMeta(itemMeta1);
                itemStack2.setItemMeta(itemMeta2);

                if(FinalTech.getRandom().nextBoolean()) {
                    ItemStack outputItem = ItemStackUtil.cloneItem(itemStack1);
                    itemStack1.setAmount(itemStack1.getAmount() - 1);
                    inventory.setItem(outputSlot, outputItem);
                } else {
                    ItemStack outputItem = ItemStackUtil.cloneItem(itemStack2);
                    itemStack2.setAmount(itemStack2.getAmount() - 1);
                    inventory.setItem(outputSlot, outputItem);
                }

                return true;
            }
        });
        // merge-storage-card
        this.craftList.add(new Craft() {
            private final boolean enable = ConfigUtil.getOrDefaultItemSetting(true, CardOperationPortInventory.this.getId(), this.getId(), "enable");
            private final String infoName = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "name");
            private final String[] infoLore = FinalTech.getLanguageStringArray("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "lore");
            private final String infoOutput = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "output");

            @Nonnull
            @Override
            public String getId() {
                return "merge-storage-card";
            }

            @Override
            public boolean isEnabled() {
                return this.enable;
            }

            @Override
            public String getInfoName() {
                return this.infoName;
            }

            @Override
            public String[] getInfoLore() {
                return this.infoLore;
            }

            @Override
            public String getInfoOutput() {
                return this.infoOutput;
            }

            @Override
            public boolean canCraft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2) {
                if (!ItemStackUtil.isItemNull(itemStack1) && !ItemStackUtil.isItemNull(itemStack2) && FinalTechItems.STORAGE_CARD.verifyItem(itemStack1) && FinalTechItems.STORAGE_CARD.verifyItem(itemStack2)) {
                    ItemStack stringItem1 = StringItemUtil.parseItemInCard(itemStack1);
                    ItemStack stringItem2 = StringItemUtil.parseItemInCard(itemStack2);
                    return !ItemStackUtil.isItemNull(stringItem1) && !ItemStackUtil.isItemNull(stringItem2) && ItemStackUtil.isItemSimilar(stringItem1, stringItem2);
                }
                return false;
            }

            @Override
            public boolean craft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, @Nonnull Inventory inventory, int outputSlot, @Nonnull Player player, @Nonnull Location location) {
                if (!ItemStackUtil.isItemNull(itemStack1) && !ItemStackUtil.isItemNull(itemStack2) && itemStack1.hasItemMeta() && itemStack2.hasItemMeta()) {
                    ItemMeta itemMeta1 = itemStack1.getItemMeta();
                    ItemMeta itemMeta2 = itemStack2.getItemMeta();
                    if (FinalTechItems.STORAGE_CARD.verifyItem(itemStack1) && FinalTechItems.STORAGE_CARD.verifyItem(itemStack2)) {
                        ItemStack stringItem1 = StringItemUtil.parseItemInCard(itemMeta1);
                        ItemStack stringItem2 = StringItemUtil.parseItemInCard(itemMeta2);
                        if (!ItemStackUtil.isItemNull(stringItem1) && !ItemStackUtil.isItemNull(stringItem2) && ItemStackUtil.isItemSimilar(stringItem1, stringItem2)) {
                            String amount1 = StringItemUtil.parseAmountInCard(itemMeta1);
                            String amount2 = StringItemUtil.parseAmountInCard(itemMeta2);
                            ItemStack outputItem = FinalTechItems.STORAGE_CARD.getValidItem(stringItem1, StringNumberUtil.add(amount1, amount2));
                            FinalTechItems.STORAGE_CARD.updateLore(outputItem);
                            itemStack1.setAmount(itemStack1.getAmount() - 1);
                            itemStack2.setAmount(itemStack2.getAmount() - 1);
                            inventory.setItem(outputSlot, outputItem);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        // copy-copy-card
        this.craftList.add(new Craft() {
            private final boolean enable = ConfigUtil.getOrDefaultItemSetting(true, CardOperationPortInventory.this.getId(), this.getId(), "enable");
            private final String infoName = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "name");
            private final String[] infoLore = FinalTech.getLanguageStringArray("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "lore");
            private final String infoOutput = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "output");

            @Nonnull
            @Override
            public String getId() {
                return "copy-copy-card";
            }

            @Override
            public boolean isEnabled() {
                return this.enable;
            }

            @Override
            public String getInfoName() {
                return infoName;
            }

            @Override
            public String[] getInfoLore() {
                return this.infoLore;
            }

            @Override
            public String getInfoOutput() {
                return this.infoOutput;
            }

            @Override
            public boolean canCraft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2) {
                if (!ItemStackUtil.isItemNull(itemStack1) && FinalTechItems.COPY_CARD.verifyItem(itemStack1) && FinalTechItems.SHELL.verifyItem(itemStack2) && !ItemStackUtil.isItemNull(StringItemUtil.parseItemInCard(itemStack1))) {
                    return true;
                } else return !ItemStackUtil.isItemNull(itemStack2) && FinalTechItems.COPY_CARD.verifyItem(itemStack2) && FinalTechItems.SHELL.verifyItem(itemStack1) && !ItemStackUtil.isItemNull(StringItemUtil.parseItemInCard(itemStack2));
            }

            @Override
            public boolean craft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, @Nonnull Inventory inventory, int outputSlot, @Nonnull Player player, @Nonnull Location location) {
                if (!ItemStackUtil.isItemNull(itemStack1) && FinalTechItems.COPY_CARD.verifyItem(itemStack1) && FinalTechItems.SHELL.verifyItem(itemStack2) && !ItemStackUtil.isItemNull(StringItemUtil.parseItemInCard(itemStack1))) {
                    itemStack2.setAmount(itemStack2.getAmount() - 1);
                    ItemStack outputItem = ItemStackUtil.cloneItem(itemStack1);
                    outputItem.setAmount(1);
                    inventory.setItem(outputSlot, outputItem);
                    FinalTech.getLogService().subItem(FinalTechItems.SHELL.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    FinalTech.getLogService().addItem(FinalTechItems.COPY_CARD.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    return true;
                } else if (!ItemStackUtil.isItemNull(itemStack2) && FinalTechItems.COPY_CARD.verifyItem(itemStack2) && FinalTechItems.SHELL.verifyItem(itemStack1) && !ItemStackUtil.isItemNull(StringItemUtil.parseItemInCard(itemStack2))) {
                    itemStack1.setAmount(itemStack1.getAmount() - 1);
                    ItemStack outputItem = ItemStackUtil.cloneItem(itemStack2);
                    outputItem.setAmount(1);
                    inventory.setItem(outputSlot, outputItem);
                    FinalTech.getLogService().subItem(FinalTechItems.SHELL.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    FinalTech.getLogService().addItem(FinalTechItems.COPY_CARD.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    return true;
                }
                return false;
            }
        });
        // craft-item-phony
        this.craftList.add(new Craft() {
            private final boolean enable = ConfigUtil.getOrDefaultItemSetting(true, CardOperationPortInventory.this.getId(), this.getId(), "enable");
            private final String infoName = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "name");
            private final String[] infoLore = FinalTech.getLanguageStringArray("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "lore");
            private final String infoOutput = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "output");

            @Nonnull
            @Override
            public String getId() {
                return "craft-item-phony";
            }

            @Override
            public boolean isEnabled() {
                return this.enable;
            }

            @Override
            public String getInfoName() {
                return infoName;
            }

            @Override
            public String[] getInfoLore() {
                return this.infoLore;
            }

            @Override
            public String getInfoOutput() {
                return this.infoOutput;
            }

            @Override
            public boolean canCraft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2) {
                if (FinalTechItems.SINGULARITY.verifyItem(itemStack1) && FinalTechItems.SPIROCHETE.verifyItem(itemStack2)) {
                    return true;
                } else return FinalTechItems.SPIROCHETE.verifyItem(itemStack1) && FinalTechItems.SINGULARITY.verifyItem(itemStack2);
            }

            @Override
            public boolean craft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, @Nonnull Inventory inventory, int outputSlot, @Nonnull Player player, @Nonnull Location location) {
                if (this.canCraft(itemStack1, itemStack2)) {
                    itemStack1.setAmount(itemStack1.getAmount() - 1);
                    itemStack2.setAmount(itemStack2.getAmount() - 1);
                    inventory.setItem(outputSlot, FinalTechItems.ITEM_PHONY.getValidItem());
                    FinalTech.getLogService().subItem(FinalTechItems.SINGULARITY.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    FinalTech.getLogService().subItem(FinalTechItems.SPIROCHETE.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    FinalTech.getLogService().addItem(FinalTechItems.ITEM_PHONY.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    return true;
                }
                return false;
            }
        });
        // craft-item-shell
        this.craftList.add(new Craft() {
            private final boolean enable = ConfigUtil.getOrDefaultItemSetting(true, CardOperationPortInventory.this.getId(), this.getId(), "enable");
            private final String infoName = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "name");
            private final String[] infoLore = FinalTech.getLanguageStringArray("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "lore");
            private final String infoOutput = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "output");

            @Nonnull
            @Override
            public String getId() {
                return "craft-item-shell";
            }

            @Override
            public boolean isEnabled() {
                return this.enable;
            }

            @Override
            public String getInfoName() {
                return infoName;
            }

            @Override
            public String[] getInfoLore() {
                return this.infoLore;
            }

            @Override
            public String getInfoOutput() {
                return this.infoOutput;
            }

            @Override
            public boolean canCraft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2) {
                return FinalTechItems.SINGULARITY.verifyItem(itemStack1) || FinalTechItems.SINGULARITY.verifyItem(itemStack2) || FinalTechItems.SPIROCHETE.verifyItem(itemStack1) || FinalTechItems.SPIROCHETE.verifyItem(itemStack2);
            }

            @Override
            public boolean craft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, @Nonnull Inventory inventory, int outputSlot, @Nonnull Player player, @Nonnull Location location) {
                if (FinalTechItems.SINGULARITY.verifyItem(itemStack1) || FinalTechItems.SPIROCHETE.verifyItem(itemStack1)) {
                    itemStack1.setAmount(itemStack1.getAmount() - 1);
                    ItemStack outputItem = FinalTechItems.SHELL.getValidItem();
                    inventory.setItem(outputSlot, outputItem);
                    FinalTech.getLogService().subItem(SlimefunItem.getByItem(itemStack1).getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    FinalTech.getLogService().addItem(FinalTechItems.SHELL.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    return true;
                } else if (FinalTechItems.SINGULARITY.verifyItem(itemStack2) || FinalTechItems.SPIROCHETE.verifyItem(itemStack2)) {
                    itemStack2.setAmount(itemStack2.getAmount() - 1);
                    ItemStack outputItem = FinalTechItems.SHELL.getValidItem();
                    inventory.setItem(outputSlot, outputItem);
                    FinalTech.getLogService().subItem(SlimefunItem.getByItem(itemStack2).getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    FinalTech.getLogService().addItem(FinalTechItems.SHELL.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    return true;
                }
                return false;
            }
        });
        // craft-annular
        this.craftList.add(new Craft() {
            private final boolean enable = ConfigUtil.getOrDefaultItemSetting(true, CardOperationPortInventory.this.getId(), this.getId(), "enable");
            private final String infoName = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "name");
            private final String[] infoLore = FinalTech.getLanguageStringArray("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "lore");
            private final String infoOutput = FinalTech.getLanguageString("items", CardOperationPortInventory.this.getId(), this.getId(), "info-icon", "output");

            @Nonnull
            @Override
            public String getId() {
                return "craft-annular";
            }

            @Override
            public boolean isEnabled() {
                return this.enable;
            }

            @Override
            public String getInfoName() {
                return infoName;
            }

            @Override
            public String[] getInfoLore() {
                return this.infoLore;
            }

            @Override
            public String getInfoOutput() {
                return this.infoOutput;
            }

            @Override
            public boolean canCraft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2) {
                return (FinalTechItems.COPY_CARD.verifyItem(itemStack1) && !ItemStackUtil.isItemNull(StringItemUtil.parseItemInCard(itemStack1))) || (FinalTechItems.COPY_CARD.verifyItem(itemStack2) && !ItemStackUtil.isItemNull(StringItemUtil.parseItemInCard(itemStack2)));
            }

            @Override
            public boolean craft(@Nullable ItemStack itemStack1, @Nullable ItemStack itemStack2, @Nonnull Inventory inventory, int outputSlot, @Nonnull Player player, @Nonnull Location location) {
                if (FinalTechItems.COPY_CARD.verifyItem(itemStack1) && !ItemStackUtil.isItemNull(StringItemUtil.parseItemInCard(itemStack1))) {
                    itemStack1.setAmount(itemStack1.getAmount() - 1);
                    inventory.setItem(outputSlot, FinalTechItems.ANNULAR.getValidItem());
                    FinalTech.getLogService().subItem(FinalTechItems.COPY_CARD.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    FinalTech.getLogService().addItem(FinalTechItems.ANNULAR.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    return true;
                } else if (FinalTechItems.COPY_CARD.verifyItem(itemStack2) && !ItemStackUtil.isItemNull(StringItemUtil.parseItemInCard(itemStack2))) {
                    itemStack2.setAmount(itemStack2.getAmount() - 1);
                    inventory.setItem(outputSlot, FinalTechItems.ANNULAR.getValidItem());
                    FinalTech.getLogService().subItem(FinalTechItems.COPY_CARD.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    FinalTech.getLogService().addItem(FinalTechItems.ANNULAR.getId(), 1, CardOperationPortInventory.this.getId(), LogSourceType.SLIMEFUN_MACHINE, player, location, CardOperationPortInventory.this.slimefunItem.getAddon().getJavaPlugin());
                    return true;
                }
                return false;
            }
        });
    }

    @Nonnull
    @Override
    protected int[] getBorder() {
        return this.border;
    }

    @Nonnull
    @Override
    protected int[] getInputBorder() {
        return this.inputBorder;
    }

    @Nonnull
    @Override
    protected int[] getOutputBorder() {
        return this.outputBorder;
    }

    @Nullable
    @Override
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        return slot == this.craftSlot ? inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            CardOperationPortInventory.this.doFunction(inventoryClickEvent.getInventory(), (Player) inventoryClickEvent.getWhoClicked(), location);
        } : super.onClick(location, slot);
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(this.craftSlot, this.craftIcon);
    }

    @Nonnull
    @Override
    public int[] requestSlots() {
        return new int[0];
    }

    @Nonnull
    @Override
    public int[] requestSlots(@Nonnull RequestType requestType) {
        return switch (requestType) {
            case INPUT -> this.inputSlot;
            case OUTPUT -> this.outputSlot;
            default -> new int[0];
        };
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        if (!ItemStackUtil.isItemNull(inventory.getItem(this.outputSlot[0]))) {
            inventory.setItem(this.craftSlot, this.craftIcon);
            return;
        }

        ItemStack inputItem1 = inventory.getItem(this.inputSlot[0]);
        ItemStack inputItem2 = inventory.getItem(this.inputSlot[1]);
        ItemStack iconItem = inventory.getItem(this.craftSlot);
        boolean work = false;
        for (Craft craft : this.craftList) {
            if (craft.isEnabled() && craft.canCraft(inputItem1, inputItem2)) {
                if(ItemStackUtil.isItemNull(iconItem)) {
                    iconItem = new ItemStack(this.craftIcon);
                }
                craft.doUpdateIcon(iconItem);
                work = true;
                break;
            }
        }
        if (!work) {
            inventory.setItem(this.craftSlot, this.craftIcon);
        }
    }

    private void doFunction(@Nonnull Inventory inventory, @Nonnull Player player, @Nonnull Location location) {
        if (!ItemStackUtil.isItemNull(inventory.getItem(this.outputSlot[0]))) {
            inventory.setItem(this.craftSlot, this.craftIcon);
            return;
        }
        ItemStack inputItem1 = inventory.getItem(this.inputSlot[0]);
        ItemStack inputItem2 = inventory.getItem(this.inputSlot[1]);
        if (ItemStackUtil.isItemNull(inputItem1) && ItemStackUtil.isItemNull(inputItem2)) {
            return;
        }
        for (Craft craft : this.craftList) {
            if (craft.isEnabled() && craft.craft(inputItem1, inputItem2, inventory, this.outputSlot[0], player, location)) {
                break;
            }
        }
    }

    public interface Craft {
        @Nonnull
        String getId();

        boolean isEnabled();

        String getInfoName();

        String[] getInfoLore();

        String getInfoOutput();

        boolean canCraft(@Nullable ItemStack item1, @Nullable ItemStack item2);

        default void doUpdateIcon(@Nonnull ItemStack iconItem) {
            iconItem.setType(Material.GREEN_STAINED_GLASS_PANE);
            ItemStackUtil.setItemName(iconItem, this.getInfoName());
            ItemStackUtil.setLore(iconItem, this.getInfoLore());
        }

        boolean craft(@Nullable ItemStack item1, @Nullable ItemStack item2, @Nonnull Inventory inventory, int outputSlot, @Nonnull Player player, @Nonnull Location location);
    }
}
