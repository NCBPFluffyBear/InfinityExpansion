package me.mooy1.infinityexpansion.machines;

import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mooy1.infinityexpansion.setup.Categories;
import me.mooy1.infinityexpansion.InfinityExpansion;
import me.mooy1.infinityexpansion.Items;
import me.mooy1.infinityexpansion.setup.InfinityRecipes;
import me.mooy1.infinityexpansion.utils.IDUtils;
import me.mooy1.infinityexpansion.utils.MessageUtils;
import me.mooy1.infinityexpansion.utils.PresetUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.protection.ProtectableAction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InfinityWorkbench extends SlimefunItem implements InventoryBlock, EnergyNetComponent, RecipeDisplayItem {

    public static int ENERGY = 10_000_000;

    private final int[] INPUT_SLOTS = {
        0, 1, 2, 3, 4, 5,
        9, 10, 11, 12, 13, 14,
        18, 19, 20, 21, 22, 23,
        27, 28, 29, 30, 31, 32,
        36, 37, 38, 39, 40, 41,
        45, 46, 47, 48, 49, 50
    };
    private final int[] OUTPUT_SLOTS = {
        PresetUtils.slot3 + 27
    };
    private final int STATUS_SLOT = PresetUtils.slot3;

    public InfinityWorkbench() {
        super(Categories.ADVANCED_MACHINES, Items.INFINITY_FORGE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            Items.VOID_INGOT, Items.MACHINE_PLATE, Items.VOID_INGOT,
                SlimefunItems.ENERGIZED_CAPACITOR, new ItemStack(Material.CRAFTING_TABLE), SlimefunItems.ENERGIZED_CAPACITOR,
                Items.VOID_INGOT, Items.MACHINE_PLATE, Items.VOID_INGOT
        });

        new BlockMenuPreset(getID(), Objects.requireNonNull(Items.INFINITY_FORGE.getDisplayName())) {

            @Override
            public void init() {
                setupInv(this);
            }

            @Override
            public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {
                menu.addMenuClickHandler(STATUS_SLOT, (p, slot, item, action) -> {
                    craft(b, p);
                    return false;
                });
            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
                return (p.hasPermission("slimefun.inventory.bypass")
                        || SlimefunPlugin.getProtectionManager().hasPermission(
                        p, b.getLocation(), ProtectableAction.ACCESS_INVENTORIES));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow == ItemTransportFlow.INSERT) {
                    return new int[0];
                } else if (flow == ItemTransportFlow.WITHDRAW) {
                    return OUTPUT_SLOTS;
                } else {
                    return new int[0];
                }
            }
        };

        registerBlockHandler(getID(), (p, b, stack, reason) -> {
            BlockMenu inv = BlockStorage.getInventory(b);
            Location l = b.getLocation();

            if (inv != null) {
                inv.dropItems(l, OUTPUT_SLOTS);
                inv.dropItems(l, INPUT_SLOTS);
            }

            return true;
        });
    }

    private void setupInv(BlockMenuPreset blockMenuPreset) {
        for (int i : PresetUtils.slotChunk3) {
            blockMenuPreset.addItem(i + 27, PresetUtils.borderItemOutput, ChestMenuUtils.getEmptyClickHandler());
        }
        for (int i : PresetUtils.slotChunk3) {
            blockMenuPreset.addItem(i, PresetUtils.borderItemStatus, ChestMenuUtils.getEmptyClickHandler());
        }
        blockMenuPreset.addItem(STATUS_SLOT, PresetUtils.loadingItemBarrier,
                ChestMenuUtils.getEmptyClickHandler());
    }

    @Override
    public void preRegister() {
        this.addItemHandler(new BlockTicker() {
            public void tick(Block b, SlimefunItem sf, Config data) { InfinityWorkbench.this.tick(b); }

            public boolean isSynchronized() { return true; }
        });
    }

    public void tick(Block b) {
        @Nullable final BlockMenu inv = BlockStorage.getInventory(b.getLocation());
        if (inv == null) return;

        if (inv.toInventory() != null && !inv.toInventory().getViewers().isEmpty()) { //only active when player watching
            int charge = getCharge(b.getLocation());

            if (charge < ENERGY) { //not enough energy

                inv.replaceExistingItem(STATUS_SLOT, new CustomItem(
                        Material.RED_STAINED_GLASS_PANE,
                        "&cNot enough energy!",
                        "",
                        "&aCharge: " + charge + "/" + ENERGY + " J",
                        ""
                ));

            } else { //enough energy

                ItemStack output = getOutput(inv);

                if (output == null) { //invalid

                    inv.replaceExistingItem(STATUS_SLOT, PresetUtils.invalidRecipe);

                } else { //correct recipe
                    inv.replaceExistingItem(STATUS_SLOT, getDisplayItem(output));
                }
            }
        }
    }

    public void craft(Block b, Player p) {
        @Nullable final BlockMenu inv = BlockStorage.getInventory(b.getLocation());
        if (inv == null) return;

        int charge = getCharge(b.getLocation());

        if (charge < ENERGY) { //not enough energy

            inv.replaceExistingItem(STATUS_SLOT, new CustomItem(
                    Material.RED_STAINED_GLASS_PANE,
                    "&cNot enough energy!",
                    "",
                    "&aCharge: &c" + charge + "&a/" + ENERGY + " J",
                    ""
            ));
            MessageUtils.message(p, ChatColor.RED + "Not enough energy!");
            MessageUtils.message(p, ChatColor.GREEN + "Charge: " + ChatColor.RED + charge + ChatColor.GREEN + "/" + ENERGY + " J");

        } else { //enough energy

            ItemStack output = getOutput(inv);

            if (output == null) { //invalid

                inv.replaceExistingItem(STATUS_SLOT, PresetUtils.invalidRecipe);
                MessageUtils.message(p, ChatColor.RED + "Invalid Recipe!");

            } else { //correct recipe

                if (!inv.fits(output, OUTPUT_SLOTS)) { //not enough room

                    inv.replaceExistingItem(STATUS_SLOT, PresetUtils.notEnoughRoom);
                    MessageUtils.message(p, ChatColor.GOLD + "Not enough room!");

                } else { //enough room

                    for (int slot : INPUT_SLOTS) {
                        if (inv.getItemInSlot(slot) != null) {
                            inv.consumeItem(slot);
                        }
                    }
                    MessageUtils.message(p, ChatColor.GREEN + "Successfully crafted: " + ChatColor.WHITE + ItemUtils.getItemName(output));

                    inv.pushItem(output, OUTPUT_SLOTS);
                    setCharge(b.getLocation(), 0);
                }
            }
        }
    }

    public ItemStack getDisplayItem(ItemStack output) {
        if (output.getItemMeta() != null) {
            ItemMeta meta = output.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (meta.getLore() != null) {
                lore = meta.getLore();
            }
            lore.add(ChatColor.GREEN + "-------------------");
            lore.add(ChatColor.GREEN + "\u21E8 Click to craft");
            lore.add(ChatColor.GREEN + "-------------------");
            meta.setLore(lore);
            output.setItemMeta(meta);
        }
        return output;
    }

    public ItemStack getOutput(BlockMenu inv) {

        String[] input = new String[36];

        for (int i = 0 ; i < 36 ; i++) {
            input[i] = IDUtils.getIDFromItem(inv.getItemInSlot(INPUT_SLOTS[i]));
        }

        for (int ii = 0; ii < InfinityRecipes.RECIPES.length ; ii++) {
            int matches = 0;
            for (int i = 0 ; i < input.length ; i++) {
                if (input[i].equals(IDUtils.getIDFromItem(InfinityRecipes.RECIPES[ii][i]))) {
                    matches++;
                }
            }
            if (matches == 36) {
                return IDUtils.getItemFromID(InfinityRecipes.OUTPUTS[ii], 1);
            }
        }

        return null;
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

    @Override
    public int[] getInputSlots() {
        return new int[0];
    }

    @Override
    public int[] getOutputSlots() {
        return OUTPUT_SLOTS;
    }

    @Nonnull
    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> items = new ArrayList<>();

        for (String id : InfinityRecipes.OUTPUTS) {
            items.add(IDUtils.getItemFromID(id, 1));
        }

        return items;
    }

    @Nonnull
    @Override
    public String getRecipeSectionLabel(@Nonnull Player p) {
        return "&7Crafts:";
    }
}