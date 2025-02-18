package de.iani.cubesideutils.bukkit.items;

import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.bukkit.StringUtilBukkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public class ItemsAndStrings {
    private ItemsAndStrings() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    @Deprecated
    public static final String LORE_COLOR = "" + ChatColor.ITALIC + ChatColor.DARK_PURPLE;

    public static String toNiceString(ItemStack[] items) {
        return toNiceString(items, ChatColor.RESET.toString());
    }

    public static String toNiceString(ItemStack[] items, String colorPrefix) {
        TreeMap<ItemStack, Integer> itemMap = new TreeMap<>((item1, item2) -> {
            if (item1.isSimilar(item2)) {
                return 0;
            }

            int result = item1.getType().compareTo(item2.getType());
            if (result != 0) {
                return result;
            }

            if (item1.getItemMeta().hasDisplayName()) {
                if (item2.getItemMeta().hasDisplayName()) {
                    result = item1.getItemMeta().getDisplayName().compareTo(item2.getItemMeta().getDisplayName());
                } else {
                    return 1;
                }
            } else {
                if (item2.getItemMeta().hasDisplayName()) {
                    return -1;
                }
            }
            if (result != 0) {
                return result;
            }

            return item1.getItemMeta().toString().compareTo(item2.getItemMeta().toString());
        });

        Arrays.stream(items).filter(item -> item != null && item.getType() != Material.AIR && item.getAmount() > 0).forEach(item -> itemMap.merge(item, item.getAmount(), (v1, v2) -> v1 + v2));
        return toNiceString(itemMap, colorPrefix);
    }

    public static String toNiceString(Map<ItemStack, Integer> amounts) {
        return toNiceString(amounts, ChatColor.RESET.toString());
    }

    public static String toNiceString(Map<ItemStack, Integer> amounts, String colorPrefix) {
        StringBuilder builder = new StringBuilder();
        int index = 0;

        for (ItemStack item : amounts.keySet()) {
            int amount = amounts.get(item);

            builder.append(toNiceString(item, amount, colorPrefix));
            if (index + 1 < amounts.size()) {
                if (index + 2 < amounts.size()) {
                    builder.append(", ");
                } else {
                    builder.append(" und ");
                }
            }
            index++;
        }

        return builder.toString();
    }

    public static String toNiceString(ItemStack item) {
        return toNiceString(item, item.getAmount(), ChatColor.RESET.toString());
    }

    public static String toNiceString(ItemStack item, int amount) {
        return toNiceString(item, amount, ChatColor.RESET.toString());
    }

    public static String toNiceString(ItemStack item, String colorPrefix) {
        return toNiceString(item, item.getAmount(), colorPrefix);
    }

    public static String toNiceString(ItemStack item, int amount, String colorPrefix) {
        StringBuilder builder = new StringBuilder(colorPrefix);
        builder.append(amount).append(" ");
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            // there used to be a warnign logged here, but cannot log without a logger... maybe make a util logger at some point?
        }

        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
            Color color = armorMeta.getColor();
            // ignore "default" color:
            if (color.asRGB() != 0xA06540) {
                builder.append(StringUtilBukkit.toNiceString(color)).append(" ");
            }
        }

        builder.append(StringUtil.capitalizeFirstLetter(item.getType().name(), true));

        if (meta instanceof PotionMeta potionMeta) {
            PotionType data = potionMeta.getBasePotionType();
            if (data != null) {
                builder.append(" of ").append(StringUtil.capitalizeFirstLetter(data.name(), true));
            }
            // builder.append(data.isUpgraded() ? " II" : " I");
            // if (data.isExtended()) {
            // builder.append(" (verlängert)");
            // }

            int index = 0;
            for (PotionEffect effect : potionMeta.getCustomEffects()) {
                builder.append((index + 1 < potionMeta.getCustomEffects().size()) ? ", " : " and ");
                builder.append(StringUtil.capitalizeFirstLetter(effect.getType().getName(), true)).append(" ").append(StringUtil.toRomanNumber(effect.getAmplifier()));
                if (!effect.getType().isInstant()) {
                    builder.append(" (").append(StringUtil.formatTimespan(50 * effect.getDuration(), "", "", "", "", ":", ":", false, true)).append(")");
                }
                index++;
            }
        }

        if (meta instanceof Damageable) {
            Damageable damageableMeta = (Damageable) meta;
            if (damageableMeta.hasDamage()) {
                builder.append(':').append(damageableMeta.getDamage());
            }
        }

        if (meta instanceof BookMeta) {
            BookMeta bookMeta = (BookMeta) meta;
            boolean appended = false;

            if (meta.hasDisplayName()) {
                builder.append(" (\"").append(meta.getDisplayName()).append(colorPrefix).append('"'); // PaperComponents.legacySectionSerializer().serialize(meta.displayName())
                appended = true;
            } else if (bookMeta.hasTitle()) {
                builder.append(" (\"").append(bookMeta.getTitle()).append(colorPrefix).append('"');
                appended = true;
            }

            if (appended && bookMeta.hasAuthor()) {
                builder.append(" von ").append(bookMeta.getAuthor()).append(colorPrefix);
            }

            if (appended) {
                builder.append(")");
            }
        } else if (meta != null && meta.hasDisplayName()) {
            builder.append(" (\"").append(meta.getDisplayName()).append(colorPrefix).append("\")");
        }

        Map<Enchantment, Integer> enchantments = meta == null ? Collections.emptyMap() : new HashMap<>(meta.getEnchants());
        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta enchMeta = (EnchantmentStorageMeta) meta;
            enchantments.putAll(enchMeta.getStoredEnchants());
        }

        if (!enchantments.isEmpty()) {
            builder.append(", verzaubert mit ");

            List<Enchantment> enchList = new ArrayList<>(enchantments.keySet());
            enchList.sort((e1, e2) -> EnchantmentNames.getName(e1).compareTo(EnchantmentNames.getName(e2)));

            int index = 0;
            for (Enchantment ench : enchList) {
                builder.append(StringUtil.capitalizeFirstLetter(EnchantmentNames.getName(ench), true));
                if (ench.getMaxLevel() > 1 || enchantments.get(ench) > 1) {
                    builder.append(" ").append(StringUtil.toRomanNumber(enchantments.get(ench)));
                }
                if (index + 1 < enchantments.size()) {
                    if (index + 2 < enchantments.size()) {
                        builder.append(", ");
                    } else {
                        builder.append(" und ");
                    }
                }
                index++;
            }
        }

        return builder.toString();
    }

    public static String toNiceString(Material m) {
        return StringUtil.capitalizeFirstLetter(m.name(), true);
    }

}
