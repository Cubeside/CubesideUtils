package de.iani.cubesideutils.bukkit.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    private ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    public static ItemBuilder fromItem(ItemStack item) {
        return new ItemBuilder(item);
    }

    public static ItemBuilder fromMaterial(Material material) {
        return fromItem(new ItemStack(material));
    }

    @Deprecated
    public ItemBuilder displayName(String displayName) {
        meta.setDisplayName(displayName);
        return this;
    }

    public ItemBuilder displayName(Component displayName) {
        meta.displayName(displayName);
        return this;
    }

    @Deprecated
    public ItemBuilder lore(String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder lore(Component... lore) {
        meta.lore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        return enchantment(enchantment, level, true);
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level, boolean ignoreMaxLevel) {
        meta.addEnchant(enchantment, level, ignoreMaxLevel);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        meta.removeEnchant(enchantment);
        return this;
    }

    public ItemBuilder flag(ItemFlag flag) {
        meta.addItemFlags(flag);
        return this;
    }

    public ItemBuilder removeFlag(ItemFlag flag) {
        meta.removeItemFlags(flag);
        return this;
    }

    public ItemBuilder hideTooltip(boolean hide) {
        meta.setHideTooltip(hide);
        return this;
    }

    public ItemBuilder enchantmentGlintOverride(Boolean glintOverride) {
        meta.setEnchantmentGlintOverride(glintOverride);
        return this;
    }

    public ItemBuilder clean() {
        item.removeItemFlags(item.getItemFlags().toArray(new ItemFlag[0]));
        item.getEnchantments().keySet().forEach(item::removeEnchantment);
        meta.lore(null);
        meta.displayName(null);
        return this;
    }

    public ItemBuilder removeItalicFromDisplayName() {
        Component displayName = meta.displayName();
        if (displayName != null) {
            meta.displayName(displayName.decoration(TextDecoration.ITALIC, false));
        }
        return this;
    }

    public ItemBuilder removeItalicFromLore() {
        List<Component> loreList = meta.lore();
        if (loreList != null) {
            List<Component> newLore = new ArrayList<>();
            loreList.forEach(component -> newLore.add(component.decoration(TextDecoration.ITALIC, false)));
            meta.lore(newLore);
        }
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}