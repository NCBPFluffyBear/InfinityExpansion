package me.mooy1.infinityexpansion.implementation.machines;

import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import me.mooy1.infinityexpansion.lists.Categories;
import me.mooy1.infinityexpansion.lists.Items;
import me.mooy1.infinityexpansion.lists.InfinityRecipes;
import me.mooy1.infinityexpansion.lists.RecipeTypes;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;

public class PoweredBedrock extends SlimefunItem implements EnergyNetComponent {

    public static final int ENERGY = 60_000;

    public PoweredBedrock() {
        super (Categories.INFINITY_CHEAT, Items.POWERED_BEDROCK, RecipeTypes.INFINITY_WORKBENCH, InfinityRecipes.getRecipe(Items.POWERED_BEDROCK));
    }

    @Override
    public void preRegister() {
        this.addItemHandler(new BlockTicker() {
            public void tick(Block b, SlimefunItem sf, Config data) { PoweredBedrock.this.tick(b); }

            public boolean isSynchronized() { return true; }
        });
    }

    public void tick(Block b) {
        if (b.getType() == Material.AIR) {
            return;
        }
        Location l = b.getLocation();

        if (getCharge(l) >= ENERGY) {
            b.setType(Material.BEDROCK);
        } else {
            b.setType(Material.NETHERITE_BLOCK);
        }
        removeCharge(l, ENERGY);
    }

    @Nonnull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return ENERGY * 2;
    }

}
