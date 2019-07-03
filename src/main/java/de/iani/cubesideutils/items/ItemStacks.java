package de.iani.cubesideutils.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
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
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return itemStack;
        }
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
        ItemStack[] contents = deepCopy(inventory.getStorageContents());
        for (ItemStack item : items) {
            if (item != null && !ItemGroups.isAir(item.getType())) {
                int remaining = item.getAmount();
                // fill partial stacks
                int firstPartial = -1;
                while (remaining > 0) {
                    firstPartial = getFirstPartial(item, contents, firstPartial + 1);
                    if (firstPartial < 0) {
                        break;
                    }
                    ItemStack content = contents[firstPartial];
                    int add = Math.min(content.getMaxStackSize() - content.getAmount(), remaining);
                    content.setAmount(content.getAmount() + add);
                    remaining -= add;
                }
                // create new stacks
                int firstFree = -1;
                while (remaining > 0) {
                    firstFree = getFirstFree(contents, firstFree + 1);
                    if (firstFree < 0) {
                        return false; // no free place found
                    }
                    ItemStack content = new ItemStack(item);
                    contents[firstFree] = content;
                    // max stack size might return -1, in this case assume 1
                    int add = Math.min(Math.max(content.getMaxStackSize(), 1), remaining);
                    content.setAmount(add);
                    remaining -= add;
                }
            }
        }
        inventory.setStorageContents(contents);
        return true;
    }

    private static int getFirstPartial(ItemStack item, ItemStack[] contents, int start) {
        for (int i = start; i < contents.length; i++) {
            ItemStack content = contents[i];
            if (content != null && content.isSimilar(item) && content.getAmount() < content.getMaxStackSize()) {
                return i;
            }
        }
        return -1;
    }

    private static int getFirstFree(ItemStack[] contents, int start) {
        for (int i = start; i < contents.length; i++) {
            ItemStack content = contents[i];
            if (content == null || content.getAmount() == 0 || ItemGroups.isAir(content.getType())) {
                return i;
            }
        }
        return -1;
    }

    public static ItemStack[] shrink(ItemStack[] items) {
        List<ItemStack> stackList = new ArrayList<>(Arrays.asList(items));
        stackList.removeIf(item -> item == null || item.getAmount() == 0 || item.getType() == Material.AIR);
        items = stackList.toArray(new ItemStack[stackList.size()]);
        return items;
    }

    public static boolean isEmpty(ItemStack[] items) {
        for (ItemStack item : items) {
            if (item != null && item.getAmount() > 0 && item.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes the given items from the given player's inventory, if he has them. Returns all missing items.
     *
     * If a non-empty array is returned, the player's inventory has been left unchanged.
     *
     * @param player
     *            the player to remove the items from
     * @param items
     *            the items to remove
     * @return missing items (empty if they could be removed)
     */
    public static ItemStack[] removeIfHas(Player player, ItemStack[] items) {
        items = deepCopy(items);

        ItemStack[] oldHis = player.getInventory().getStorageContents();
        ItemStack[] his = deepCopy(oldHis);

        boolean has = true;
        outer: for (ItemStack toStack : items) {
            for (int i = 0; i < his.length; i++) {
                ItemStack hisStack = his[i];
                if (hisStack == null || hisStack.getAmount() <= 0) {
                    continue;
                }
                if (!hisStack.isSimilar(toStack)) {
                    continue;
                }
                if (toStack.getAmount() > hisStack.getAmount()) {
                    toStack.setAmount(toStack.getAmount() - hisStack.getAmount());
                    his[i] = null;
                    continue;
                } else if (toStack.getAmount() < hisStack.getAmount()) {
                    hisStack.setAmount(hisStack.getAmount() - toStack.getAmount());
                    toStack.setAmount(0);
                    continue outer;
                } else {
                    his[i] = null;
                    toStack.setAmount(0);
                    continue outer;
                }
            }
            has = false;
        }

        if (!has) {
            ItemStack[] missing = shrink(items);
            if (missing.length > 0) {
                throw new AssertionError();
            }

            return missing;
        }

        player.getInventory().setStorageContents(his);
        player.updateInventory();
        return new ItemStack[0];
    }

}
