package de.iani.cubesideutils.bukkit;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class ComponentUtils {
    private ComponentUtils() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static BaseComponent createTranslatableComponentFor(Material m) {
        String key = m.getKey().getKey();
        if (key.startsWith("music_disc_") || key.endsWith("_banner_pattern")) {
            return new TextComponent(new TranslatableComponent("item.minecraft." + key), new TextComponent(": "), new TranslatableComponent("item.minecraft." + key + ".desc"));
        }
        return new TranslatableComponent((m.isBlock() ? "block.minecraft." : "item.minecraft.") + key);
    }

    public static BaseComponent createTranslatableComponentFor(ItemStack stack) {
        Material m = stack.getType();
        if (m == Material.TIPPED_ARROW || m == Material.POTION || m == Material.SPLASH_POTION || m == Material.LINGERING_POTION) {
            ItemMeta meta = stack.getItemMeta();
            if (meta instanceof PotionMeta) {
                String key = m.getKey().getKey();
                PotionType type = ((PotionMeta) meta).getBasePotionData().getType();
                return new TranslatableComponent("item.minecraft." + key + ".effect." + getInternalPotionName(type));
            }
        }
        String key = m.getKey().getKey();
        if (key.startsWith("music_disc_")) {
            return new TextComponent(new TranslatableComponent("item.minecraft." + key), new TextComponent(": "), new TranslatableComponent("item.minecraft." + key + ".desc"));
        }
        return new TranslatableComponent((m.isBlock() ? "block.minecraft." : "item.minecraft.") + key);
    }

    public static BaseComponent createTranslatableComponentFor(EntityType t) {
        return new TranslatableComponent("entity.minecraft." + t.getKey().getKey());
    }

    public static BaseComponent createTranslatableComponentFor(Entity e) {
        return createTranslatableComponentFor(e.getType());
    }

    private static String getInternalPotionName(PotionType t) {
        switch (t) {
            case UNCRAFTABLE:
                return "empty";
            case JUMP:
                return "leaping";
            case SPEED:
                return "swiftness";
            case INSTANT_HEAL:
                return "healing";
            case INSTANT_DAMAGE:
                return "harming";
            case REGEN:
                return "regeneration";
            default:
                return t.name().toLowerCase();
        }
    }
}
