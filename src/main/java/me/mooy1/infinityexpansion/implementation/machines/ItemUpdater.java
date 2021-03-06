package me.mooy1.infinityexpansion.implementation.machines;

import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mooy1.infinityexpansion.implementation.storage.StorageForge;
import me.mooy1.infinityexpansion.lists.Categories;
import me.mooy1.infinityexpansion.lists.Items;
import me.mooy1.infinityexpansion.utils.MessageUtils;
import me.mooy1.infinityexpansion.utils.PresetUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ItemUpdater extends SlimefunItem implements EnergyNetComponent {

    public static final int ENERGY = 200;

    private static final int[] OUTPUT_SLOTS= {
            PresetUtils.slot3
    };
    private static final int[] INPUT_SLOTS = {
            PresetUtils.slot1
    };
    private static final int INPUT_SLOT = INPUT_SLOTS[0];
    private static final int STATUS_SLOT = PresetUtils.slot2;

    public ItemUpdater() {
        super(Categories.BASIC_MACHINES, Items.ITEM_UPDATER, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                Items.MAGSTEEL, Items.MAGSTEEL, Items.MAGSTEEL,
                Items.MAGSTEEL, Items.MACHINE_CIRCUIT, Items.MAGSTEEL,
                Items.MAGSTEEL, Items.MAGSTEEL, Items.MAGSTEEL,
        });

        new BlockMenuPreset(getId(), Objects.requireNonNull(Items.ITEM_UPDATER.getDisplayName())) {
            @Override
            public void init() {
                setupInv(this);
            }

            @Override
            public boolean canOpen(@Nonnull Block block, @Nonnull Player player) {
                return (player.hasPermission("slimefun.inventory.bypass")
                        || SlimefunPlugin.getProtectionManager().hasPermission(
                        player, block.getLocation(), ProtectableAction.ACCESS_INVENTORIES));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow == ItemTransportFlow.INSERT) {
                    return INPUT_SLOTS;
                } else if (flow == ItemTransportFlow.WITHDRAW) {
                    return OUTPUT_SLOTS;
                } else {
                    return new int[0];
                }
            }
        };

        registerBlockHandler(getId(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);

            if (inv != null) {
                inv.dropItems(b.getLocation(), OUTPUT_SLOTS);
                inv.dropItems(b.getLocation(), INPUT_SLOTS);
            }

            return true;
        });
    }

    private void setupInv(BlockMenuPreset blockMenuPreset) {
                    for (int i : PresetUtils.slotChunk3) {
                        blockMenuPreset.addItem(i, PresetUtils.borderItemOutput, ChestMenuUtils.getEmptyClickHandler());
                    }
                    for (int i : PresetUtils.slotChunk2) {
                        blockMenuPreset.addItem(i, PresetUtils.borderItemStatus, ChestMenuUtils.getEmptyClickHandler());
                    }
                    for (int i : PresetUtils.slotChunk1) {
                        blockMenuPreset.addItem(i, PresetUtils.borderItemInput, ChestMenuUtils.getEmptyClickHandler());
                    }
                    blockMenuPreset.addItem(STATUS_SLOT, PresetUtils.loadingItemRed,
                            ChestMenuUtils.getEmptyClickHandler());
    }

    @Override
    public void preRegister() {
        this.addItemHandler(new BlockTicker() {
            public void tick(Block b, SlimefunItem sf, Config data) { ItemUpdater.this.tick(b); }

            public boolean isSynchronized() { return false; }
        });
    }

    public void tick(Block b) {
        @Nullable final BlockMenu inv = BlockStorage.getInventory(b.getLocation());
        if (inv == null) return;

        boolean playerWatching = inv.toInventory() != null && !inv.toInventory().getViewers().isEmpty();

        ItemStack input = inv.getItemInSlot(INPUT_SLOT);

        if (getCharge(b.getLocation()) < ENERGY) { //not enough energy

            if (playerWatching) {
                inv.replaceExistingItem(STATUS_SLOT, PresetUtils.notEnoughEnergy);
            }

        } else if (input == null) { //check input

            if (playerWatching) {
                inv.replaceExistingItem(STATUS_SLOT, PresetUtils.inputAnItem);
            }

        } else {

            ItemStack output = null;
            int inputAmount = input.getAmount();
            if (SlimefunItem.getByItem(input) != null) {
                SlimefunItem slimefunItem = SlimefunItem.getByItem(input);
                if (slimefunItem != null) {
                    output = slimefunItem.getItem().clone();
                    output.setAmount(inputAmount);

                    if (slimefunItem.getId().endsWith("_STORAGE") && !Objects.equals(output, input)) {

                        MessageUtils.messagePlayersInInv(inv, ChatColor.GREEN + "Stored items transferred!");
                        StorageForge.transferItems(output, input);

                    }
                }
            }

            if (output == null) { //not sf item
                if (playerWatching) {
                    inv.replaceExistingItem(STATUS_SLOT, new CustomItem(
                            Material.RED_STAINED_GLASS_PANE,
                            "&9Input a &aSlimefun &9item!")
                    );
                }

            } else if (!inv.fits(output, OUTPUT_SLOTS)) { //update

                if (playerWatching) {
                    inv.replaceExistingItem(STATUS_SLOT, PresetUtils.notEnoughRoom);
                }

            } else {
                if (!output.getEnchantments().isEmpty()) {
                    Map<Enchantment, Integer> enchantments = new HashMap<>();
                    int amount = 0;
                    for (Map.Entry<Enchantment, Integer> entry : output.getEnchantments().entrySet()) {
                        enchantments.put(entry.getKey(), entry.getValue());
                        amount++;
                    }
                    if (amount > 0 ) {
                        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                            output.removeEnchantment(entry.getKey());

                        }
                    }
                }

                output.addUnsafeEnchantments(input.getEnchantments());

                if (!output.getEnchantments().isEmpty()) {
                    MessageUtils.messagePlayersInInv(inv, ChatColor.GREEN + "Enchantments transferred!");
                }

                removeCharge(b.getLocation(), ENERGY);
                inv.consumeItem(INPUT_SLOT, inputAmount);
                inv.pushItem(output, OUTPUT_SLOTS);

                if (playerWatching) {
                    inv.replaceExistingItem(STATUS_SLOT, new CustomItem(
                            Material.LIME_STAINED_GLASS_PANE,
                            "&aItem Updated!")
                    );
                }
            }
        }
    }

    @Nonnull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return ENERGY;
    }
}
