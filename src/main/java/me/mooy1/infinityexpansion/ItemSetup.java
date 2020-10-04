package me.mooy1.infinityexpansion;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.Capacitor;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.AutoEnchanter;
import me.mooy1.infinityexpansion.gear.InfinityGear;
import me.mooy1.infinityexpansion.gear.MagnoniumGear;
import me.mooy1.infinityexpansion.gear.VoidFlame;
import me.mooy1.infinityexpansion.machines.*;
import me.mooy1.infinityexpansion.materials.*;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public final class ItemSetup {

    private ItemSetup() { }

    public static void setup(@Nonnull InfinityExpansion plugin) {

        //add machines

        for (MachineMaterials.Type type : MachineMaterials.Type.values()) {
            new MachineMaterials(type).register(plugin);
        }

        for (Quarry.Type tier : Quarry.Type.values()) {
            new Quarry(tier).register(plugin);
        }

        new AdvancedEnchanter(AdvancedEnchanter.Type.BASIC).register(plugin);
        new AdvancedDisenchanter(AdvancedDisenchanter.Type.BASIC).register(plugin);
        new AdvancedEnchanter(AdvancedEnchanter.Type.INFINITY).register(plugin);
        new AdvancedDisenchanter(AdvancedDisenchanter.Type.INFINITY).register(plugin);

        new InfinityForge().register(plugin);

        for (InfinityPanel.Type type : InfinityPanel.Type.values()) {
            new InfinityPanel(type).register(plugin);
        }

        for (VoidHarvester.Type type : VoidHarvester.Type.values()) {
            new VoidHarvester(type).register(plugin);
        }

        for (StorageUnit.Tier tier : StorageUnit.Tier.values()) {
            new StorageUnit(tier).register(plugin);
        }

        //add materials

        for (CompressedCobblestone.Type type : CompressedCobblestone.Type.values()) {
            new CompressedCobblestone(type).register(plugin);
        }

        for (Cores.Type type : Cores.Type.values()) {
            new Cores(type).register(plugin);
        }

        for (Ingots.Type type : Ingots.Type.values()) {
            new Ingots(type).register(plugin);
        }

        for (SFIngotBlocks.Type type: SFIngotBlocks.Type.values()) {
            new SFIngotBlocks(type).register(plugin);
        }

        //add gear

        for (InfinityGear.Type type : InfinityGear.Type.values()) {
            new InfinityGear(type).register(plugin);
        }

        new VoidFlame().register(plugin);

        for (MagnoniumGear.Type type : MagnoniumGear.Type.values()) {
            new MagnoniumGear(type).register(plugin);
        }

        //add geominer recipe

        new VoidDust().register(plugin);

        //Slimefun constructors

        new Capacitor(Categories.INFINITY_MACHINES, 1_600_000_000, Items.INFINITY_CAPACITOR,
            RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            Items.INFINITE_INGOT, Items.INFINITE_INGOT, Items.INFINITE_INGOT,
            Items.INFINITE_INGOT, SlimefunItems.ENERGIZED_CAPACITOR, Items.INFINITE_INGOT,
            Items.INFINITE_INGOT, Items.INFINITE_INGOT, Items.INFINITE_INGOT
        }).register(plugin);



    }
}