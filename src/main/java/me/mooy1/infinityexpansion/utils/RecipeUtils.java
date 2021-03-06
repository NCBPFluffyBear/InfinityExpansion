package me.mooy1.infinityexpansion.utils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class RecipeUtils {

    private RecipeUtils() {}

    @Nonnull
    public static ItemStack[] Compress(@Nonnull ItemStack item) {
        return new ItemStack[]{
                item, item, item, item, item, item, item, item, item,
        };
    }

    @Nonnull
    public static ItemStack[] MiddleItem(@Nonnull ItemStack item) {
        return new ItemStack[] {
                null, null, null, null , item, null, null, null, null
        };
    }

    @Nonnull
    public static ItemStack getDisplayItem(@Nonnull ItemStack output) {
        if (output.getItemMeta() != null) {
            ItemMeta meta = output.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (meta.getLore() != null) {
                lore = meta.getLore();
            }
            lore.add(ChatColor.GREEN + "");
            lore.add(ChatColor.GREEN + "-------------------");
            lore.add(ChatColor.GREEN + "\u21E8 Click to craft");
            lore.add(ChatColor.GREEN + "-------------------");
            meta.setLore(lore);
            output.setItemMeta(meta);
        }
        return output;
    }
}
