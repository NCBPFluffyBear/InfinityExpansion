package me.mooy1.infinityexpansion.setup;

import me.mooy1.infinityexpansion.InfinityExpansion;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CheatCategory extends Category {
    public CheatCategory() {
        super(new NamespacedKey(InfinityExpansion.getInstance(), "INFINITY_CHEAT"),
                new CustomItem(Material.SMITHING_TABLE, "&bInfinity &cCheat ( ͡° ͜ʖ ͡°)"), 2
        );
    }

    @Override
    public boolean isHidden(@Nonnull Player p) {
        return true;
    }
}
