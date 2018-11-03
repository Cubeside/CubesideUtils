package de.iani.cubesideutils.items;

import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

public class ItemStacks {
    private ItemStacks() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    private static final UUID attackSpeedUUID = UUID.fromString("34f5963d-3fc6-474b-a576-c4f05c2af419");
    private static final UUID attackDamageUUID = UUID.fromString("ea434c08-d745-4b5c-858e-1db76cc70088");

    public static ItemStack unbreakable(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setUnbreakable(true);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack hideProperties(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack enchant(ItemStack itemStack, Enchantment enchantment, int level) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack rename(ItemStack itemStack, String name) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack colorize(ItemStack itemStack, Color color) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(color);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack potion(ItemStack itemStack, PotionData potion) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof PotionMeta) {
            ((PotionMeta) meta).setBasePotionData(potion);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack fastPvP(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        Material type = itemStack.getType();
        double addDamage = Double.NaN;
        if (type == Material.WOODEN_SWORD) {
            addDamage = 3;
        } else if (type == Material.GOLDEN_SWORD) {
            addDamage = 3;
        } else if (type == Material.STONE_SWORD) {
            addDamage = 4;
        } else if (type == Material.IRON_SWORD) {
            addDamage = 5;
        } else if (type == Material.DIAMOND_SWORD) {
            addDamage = 6;
        }
        if (!Double.isNaN(addDamage)) {
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(attackSpeedUUID, "1.8-attackspeed", 1.5, Operation.ADD_NUMBER, EquipmentSlot.HAND));
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(attackDamageUUID, "1.8-attackdamage", addDamage, Operation.ADD_NUMBER, EquipmentSlot.HAND));
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack[] deepCopy(ItemStack[] of) {
        ItemStack[] result = new ItemStack[of.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = of[i] == null ? null : new ItemStack(of[i]);
        }
        return result;
    }

    public static boolean addToInventoryIfFits(Inventory inventory, ItemStack... items) {
        ItemStack[] contents = deepCopy(inventory.getContents());
        if (!inventory.addItem(items).isEmpty()) {
            inventory.setContents(contents);
            return false;
        }
        return true;
    }
}
