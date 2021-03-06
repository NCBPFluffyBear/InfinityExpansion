package me.mooy1.infinityexpansion.implementation.materials;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mooy1.infinityexpansion.lists.Categories;
import me.mooy1.infinityexpansion.lists.Items;
import me.mooy1.infinityexpansion.lists.InfinityRecipes;
import me.mooy1.infinityexpansion.lists.RecipeTypes;
import me.mooy1.infinityexpansion.utils.RecipeUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class MachineMaterial extends SlimefunItem {

    public MachineMaterial(Type type) {
        super(type.getCategory(), type.getItem(), type.getRecipeType(), type.getRecipe());
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Type {

        MAGSTEEL_PLATE(Categories.INFINITY_MAIN, Items.MAGSTEEL_PLATE, RecipeType.ENHANCED_CRAFTING_TABLE, RecipeUtils.Compress(Items.MAGSTEEL)
        ),
        PLATE(Categories.INFINITY_MAIN, Items.MACHINE_PLATE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                Items.MAGSTEEL_PLATE, SlimefunItems.REINFORCED_PLATE, Items.MAGSTEEL_PLATE,
            SlimefunItems.REINFORCED_PLATE, Items.TITANIUM, SlimefunItems.REINFORCED_PLATE,
                Items.MAGSTEEL_PLATE, SlimefunItems.REINFORCED_PLATE, Items.MAGSTEEL_PLATE
        }),
        CIRCUIT(Categories.INFINITY_MAIN, Items.MACHINE_CIRCUIT, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
            SlimefunItems.COPPER_INGOT, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.COPPER_INGOT,
            SlimefunItems.COPPER_INGOT, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.COPPER_INGOT,
            SlimefunItems.COPPER_INGOT, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.COPPER_INGOT
        }),
        CORE(Categories.INFINITY_MAIN, Items.MACHINE_CORE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                Items.TITANIUM, Items.MACHINE_PLATE, Items.TITANIUM,
            Items.MACHINE_CIRCUIT, new ItemStack(Material.IRON_BLOCK), Items.MACHINE_CIRCUIT,
                Items.TITANIUM, Items.MACHINE_PLATE, Items.TITANIUM,
        }),
        I_CIRCUIT(Categories.INFINITY_CHEAT, Items.INFINITE_MACHINE_CIRCUIT, RecipeTypes.INFINITY_WORKBENCH, InfinityRecipes.getRecipe(Items.INFINITE_MACHINE_CIRCUIT)),
        I_CORE(Categories.INFINITY_CHEAT, Items.INFINITE_MACHINE_CORE, RecipeTypes.INFINITY_WORKBENCH, InfinityRecipes.getRecipe(Items.INFINITE_MACHINE_CORE));

        @Nonnull
        private final Category category;
        private final SlimefunItemStack item;
        private final RecipeType recipeType;
        private final ItemStack[] recipe;
    }
}