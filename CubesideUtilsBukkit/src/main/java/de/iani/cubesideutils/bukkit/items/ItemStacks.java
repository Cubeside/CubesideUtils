package de.iani.cubesideutils.bukkit.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

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
        Enchantments.addEnchant(meta, enchantment, level, true);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack lore(ItemStack itemStack, String... lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore((lore == null || lore.length == 0) ? null : List.of(lore));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack rename(ItemStack itemStack, String name) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack amount(ItemStack itemStack, int amount) {
        itemStack.setAmount(amount);
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

    public static ItemStack potion(ItemStack itemStack, PotionType potion) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof PotionMeta potionMeta) {
            potionMeta.setBasePotionType(potion);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Deprecated(forRemoval = true)
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
        } else if (type == Material.NETHERITE_SWORD) {
            addDamage = 7;
        }
        if (!Double.isNaN(addDamage)) {
            boolean attackModifierExists = false;
            if (meta.hasAttributeModifiers()) {
                Collection<AttributeModifier> attackSpeedModifiers = meta.getAttributeModifiers(Attribute.GENERIC_ATTACK_SPEED);
                if (attackSpeedModifiers != null) {
                    for (AttributeModifier m : attackSpeedModifiers) {
                        if (m.getUniqueId().equals(attackSpeedUUID)) {
                            attackModifierExists = true;
                        }
                    }
                }
            }
            if (!attackModifierExists) {
                meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(attackSpeedUUID, "1.8-attackspeed", 1.5, Operation.ADD_NUMBER, EquipmentSlot.HAND));
            }
            boolean attackDamageExists = false;
            if (meta.hasAttributeModifiers()) {
                Collection<AttributeModifier> attackDamageModifiers = meta.getAttributeModifiers(Attribute.GENERIC_ATTACK_DAMAGE);
                if (attackDamageModifiers != null) {
                    for (AttributeModifier m : attackDamageModifiers) {
                        if (m.getUniqueId().equals(attackDamageUUID)) {
                            attackDamageExists = true;
                        }
                    }
                }
            }
            if (!attackDamageExists) {
                meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(attackDamageUUID, "1.8-attackdamage", addDamage, Operation.ADD_NUMBER, EquipmentSlot.HAND));
            }
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

    public static Map<ItemStack, Integer> countItems(ItemStack[] items) {
        Map<ItemStack, Integer> counts = new LinkedHashMap<>();
        for (ItemStack item : items) {
            if (item == null || item.getAmount() == 0 || item.getType() == Material.AIR) {
                continue;
            }
            ItemStack key = item.clone();
            key.setAmount(1);
            counts.merge(key, item.getAmount(), (old, add) -> old + add);
        }
        return counts;
    }

    public static int countStacks(Map<ItemStack, Integer> itemCounts) {
        return itemCounts.entrySet().stream().mapToInt(entry -> (int) Math.ceil((double) entry.getValue() / entry.getKey().getMaxStackSize())).sum();
    }

    public static ItemStack[] fromAmounts(Map<ItemStack, Integer> amounts) {
        List<ItemStack> resultList = new ArrayList<>();
        for (Entry<ItemStack, Integer> entry : amounts.entrySet()) {
            ItemStack item = entry.getKey();
            int count = entry.getValue();

            while (count > 0) {
                item = item.clone();
                item.setAmount(Math.min(count, Math.max(1, item.getMaxStackSize())));
                resultList.add(item);
                count -= Math.min(count, Math.max(1, item.getMaxStackSize()));
            }
        }

        return resultList.toArray(new ItemStack[resultList.size()]);
    }

    public static ItemStack[] shrink(ItemStack[] items) {
        return fromAmounts(countItems(items));
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR || stack.getAmount() == 0;
    }

    public static boolean isEmpty(ItemStack[] items) {
        for (ItemStack item : items) {
            if (item != null && item.getAmount() > 0 && item.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSimilar(ItemStack i1, ItemStack i2) {
        if (i1 == null) {
            return i2 == null;
        }

        if (!ItemGroups.isShulkerBox(i1.getType())) {
            return i1.isSimilar(i2);
        }

        if (i1.getType() != i2.getType()) {
            return false;
        }

        BlockStateMeta m1 = (BlockStateMeta) i1.getItemMeta();
        BlockStateMeta m2 = (BlockStateMeta) i2.getItemMeta();

        if (m1.hasDisplayName() != m2.hasDisplayName()) {
            return false;
        }
        if (m1.hasDisplayName() && !Objects.equals(m1.displayName(), m2.displayName())) {
            return false;
        }

        if (m1.hasLore() != m2.hasLore()) {
            return false;
        }
        if (m1.hasLore() && !Objects.equals(m1.lore(), m2.lore())) {
            return false;
        }

        if (!Objects.equals(m1.getEnchants(), m2.getEnchants())) {
            return false;
        }

        if (!Objects.equals(m1.getItemFlags(), m2.getItemFlags())) {
            return false;
        }

        if (!Objects.equals(m1.getAttributeModifiers(), m2.getAttributeModifiers())) {
            return false;
        }

        if (!Objects.equals(m1.getDestroyableKeys(), m2.getDestroyableKeys())) {
            return false;
        }

        if (!Objects.equals(m1.getPlaceableKeys(), m2.getPlaceableKeys())) {
            return false;
        }

        if (m1.hasCustomModelData() != m2.hasCustomModelData()) {
            return false;
        }
        if (m1.hasCustomModelData() && !Objects.equals(m1.getCustomModelData(), m2.getCustomModelData())) {
            return false;
        }

        if (!Objects.equals(m1.getPersistentDataContainer(), m2.getPersistentDataContainer())) {
            return false;
        }

        ShulkerBox s1 = (ShulkerBox) m1.getBlockState();
        ShulkerBox s2 = (ShulkerBox) m2.getBlockState();

        if (s1.isLocked() != s2.isLocked()) {
            return false;
        }

        if (!Objects.equals(s1.getCustomName(), s2.getCustomName())) {
            return false;
        }

        if (!Objects.equals(s1.getColor(), s2.getColor())) {
            return false;
        }

        if (!Arrays.equals(s1.getInventory().getContents(), s2.getInventory().getContents())) {
            return false;
        }

        return true;
    }

    /**
     * Checks whether the given player's inventory contains the given items.
     *
     * Returns all missing items. If removeIfYes is true and all items are present, they are removed.
     * If a non-empty array is returned, or if removeIfYes is false, the player's inventory has been left unchanged.
     *
     * @param player
     *            the player to check
     * @param items
     *            the items to check for
     * @return missing items (empty if all items are present)
     */
    public static ItemStack[] doesHave(Player player, ItemStack[] items, boolean removeIfYes) {
        return doesHave(player, items, removeIfYes, true);
    }

    /**
     * Checks whether the given player's inventory contains the given items.
     *
     * Returns all missing items. If removeIfYes is true and all items are present, they are removed.
     * If a non-empty array is returned, or if removeIfYes is false, the player's inventory has been left unchanged.
     *
     * @param player
     *            the player to check
     * @param items
     *            the items to check for
     * @param includeNonStorage
     *            whether or not to include the special slots like off hand or armor
     * @return missing items (empty if all items are present)
     */
    public static ItemStack[] doesHave(Player player, ItemStack[] items, boolean removeIfYes, boolean includeNonStorage) {
        items = deepCopy(items);

        ItemStack[] oldContents = includeNonStorage ? player.getInventory().getContents() : player.getInventory().getStorageContents();
        ItemStack[] contents = deepCopy(oldContents);

        boolean has = true;
        outer: for (ItemStack toStack : items) {
            for (int i = 0; i < contents.length; i++) {
                ItemStack hisStack = contents[i];
                if (hisStack == null || hisStack.getAmount() <= 0) {
                    continue;
                }
                if (!isSimilar(hisStack, toStack)) {
                    continue;
                }
                if (toStack.getAmount() > hisStack.getAmount()) {
                    toStack.setAmount(toStack.getAmount() - hisStack.getAmount());
                    contents[i] = null;
                    continue;
                } else if (toStack.getAmount() < hisStack.getAmount()) {
                    hisStack.setAmount(hisStack.getAmount() - toStack.getAmount());
                    toStack.setAmount(0);
                    continue outer;
                } else {
                    contents[i] = null;
                    toStack.setAmount(0);
                    continue outer;
                }
            }
            has = false;
        }

        if (!has) {
            ItemStack[] missing = shrink(items);
            if (missing.length == 0) {
                throw new AssertionError();
            }

            return missing;
        }

        if (removeIfYes) {
            if (includeNonStorage) {
                player.getInventory().setContents(contents);
            } else {
                player.getInventory().setStorageContents(contents);
            }
            player.updateInventory();
        }
        return new ItemStack[0];
    }

    public static boolean equals(ItemStack[] i1, ItemStack[] i2) {
        return countItems(i1).equals(countItems(i2));
    }

}
