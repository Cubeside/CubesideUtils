package de.iani.cubesideutils.bukkit.items;

import java.util.Arrays;
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

    public ItemBuilder displayName(String displayName) {
        meta.setDisplayName(displayName);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        item.addEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        item.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder flag(ItemFlag flag) {
        item.addItemFlags(flag);
        return this;
    }

    public ItemBuilder removeFlag(ItemFlag flag) {
        item.removeItemFlags(flag);
        return this;
    }

    public ItemBuilder clean() {
        item.removeItemFlags(item.getItemFlags().toArray(new ItemFlag[0]));
        item.getEnchantments().keySet().forEach(item::removeEnchantment);
        meta.setLore(null);
        meta.setDisplayName(null);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}