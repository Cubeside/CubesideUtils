package de.iani.cubesideutils.bukkit.items;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.Component.translatable;

import de.iani.cubesideutils.ComponentUtilAdventure;
import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.bukkit.StringUtilBukkit;
import io.papermc.paper.datacomponent.DataComponentType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public class ItemStacks {
    private ItemStacks() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    private static final NamespacedKey attackSpeedKey = NamespacedKey.fromString("cubesideutils:attackspeed");
    private static final NamespacedKey attackDamageKey = NamespacedKey.fromString("cubesideutils:attackdamage");

    public static ItemStack unbreakable(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setUnbreakable(true);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack hideProperties(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_UNBREAKABLE);
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
        if (lore == null || lore.length == 0) {
            meta.lore(null);
        } else {
            meta.lore(List.of(lore).stream().map(s -> LegacyComponentSerializer.legacySection().deserialize(s)).toList());
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack lore(ItemStack itemStack, Component... lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.lore((lore == null || lore.length == 0) ? null : List.of(lore));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack rename(ItemStack itemStack, String name) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(LegacyComponentSerializer.legacySection().deserialize(name));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack rename(ItemStack itemStack, Component name) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(name);
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
            meta.removeAttributeModifier(Attribute.ATTACK_SPEED);
            meta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(attackSpeedKey, 1.5, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));

            meta.removeAttributeModifier(Attribute.ATTACK_DAMAGE);
            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(attackDamageKey, addDamage, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack[] deepCopy(ItemStack[] of) {
        ItemStack[] result = new ItemStack[of.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = of[i] == null ? null : of[i].clone();
        }
        return result;
    }

    public static boolean removeFromInventoryIfContainsAll(Inventory inventory, ItemStackAndAmount... items) {
        ItemStack[] contents = deepCopy(inventory.getStorageContents());
        for (ItemStackAndAmount item : items) {
            if (item != null && item.stack() != null && !ItemGroups.isAir(item.stack().getType())) {
                int remaining = item.amount();
                int firstSimilar = -1;
                while (remaining > 0) {
                    firstSimilar = getFirstSimilar(item.stack(), contents, firstSimilar + 1);
                    if (firstSimilar < 0) {
                        return false;
                    }
                    ItemStack content = contents[firstSimilar];
                    int here = content.getAmount();
                    if (here > remaining) {
                        content.setAmount(here - remaining);
                        remaining = 0;
                    } else {
                        contents[firstSimilar] = null;
                        remaining -= here;
                    }
                }
            }
        }
        inventory.setStorageContents(contents);
        return true;
    }

    public static boolean addToInventoryIfFits(Inventory inventory, ItemStackAndAmount... items) {
        ItemStack[] contents = deepCopy(inventory.getStorageContents());
        for (ItemStackAndAmount item : items) {
            if (item != null && item.stack() != null && !ItemGroups.isAir(item.stack().getType())) {
                int remaining = item.amount();
                // fill partial stacks
                int firstPartial = -1;
                while (remaining > 0) {
                    firstPartial = getFirstPartial(item.stack(), contents, firstPartial + 1);
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
                    ItemStack content = new ItemStack(item.stack());
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

    private static int getFirstSimilar(ItemStack item, ItemStack[] contents, int start) {
        for (int i = start; i < contents.length; i++) {
            ItemStack content = contents[i];
            if (content != null && content.isSimilar(item)) {
                return i;
            }
        }
        return -1;
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

        return i1.isSimilar(i2);
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
        return doesHave(player, items, removeIfYes, includeNonStorage, null);
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
     * @param ignoredComponents
     *            components on the itemstacks that will be ignored when comparing them
     * @return missing items (empty if all items are present)
     */
    public static ItemStack[] doesHave(Player player, ItemStack[] items, boolean removeIfYes, boolean includeNonStorage, DataComponentType[] ignoredComponents) {
        items = deepCopy(items);

        ItemStack[] oldContents = includeNonStorage ? player.getInventory().getContents() : player.getInventory().getStorageContents();
        ItemStack[] contents = deepCopy(oldContents);

        ItemStack[] filteredItems = null;
        ItemStack[] filteredContents = null;
        boolean anyRealFilteredComponents = false;
        if (ignoredComponents != null && ignoredComponents.length > 0) {
            filteredItems = new ItemStack[items.length];
            for (int j = 0; j < items.length; j++) {
                ItemStack stack = items[j];
                if (stack != null) {
                    boolean cloned = false;
                    for (DataComponentType ignored : ignoredComponents) {
                        if (stack.isDataOverridden(ignored)) {
                            if (!cloned) {
                                stack = stack.clone();
                                cloned = true;
                            }
                            stack.resetData(ignored);
                            anyRealFilteredComponents = true;
                        }
                    }
                    filteredItems[j] = stack;
                }
            }

            filteredContents = new ItemStack[contents.length];
            for (int j = 0; j < contents.length; j++) {
                ItemStack stack = contents[j];
                if (stack != null) {
                    boolean cloned = false;
                    for (DataComponentType ignored : ignoredComponents) {
                        if (stack.isDataOverridden(ignored)) {
                            if (!cloned) {
                                stack = stack.clone();
                                cloned = true;
                            }
                            stack.resetData(ignored);
                            anyRealFilteredComponents = true;
                        }
                    }
                    filteredContents[j] = stack;
                }
            }
        }

        boolean has = true;

        outer: for (int j = 0; j < items.length; j++) {
            ItemStack toStack = items[j];
            ItemStack toStackCheck = anyRealFilteredComponents ? filteredItems[j] : toStack;
            for (int i = 0; i < contents.length; i++) {
                ItemStack hisStack = contents[i];
                ItemStack hisStackCheck = anyRealFilteredComponents ? filteredContents[i] : hisStack;
                if (hisStack == null || hisStack.getAmount() <= 0) {
                    continue;
                }
                if (!isSimilar(hisStackCheck, toStackCheck)) {
                    continue;
                }
                if (toStack.getAmount() > hisStack.getAmount()) {
                    toStack.setAmount(toStack.getAmount() - hisStack.getAmount());
                    contents[i] = null;
                    if (anyRealFilteredComponents) {
                        toStackCheck.setAmount(toStack.getAmount());
                        filteredContents[i] = null;
                    }
                    continue;
                } else if (toStack.getAmount() < hisStack.getAmount()) {
                    hisStack.setAmount(hisStack.getAmount() - toStack.getAmount());
                    toStack.setAmount(0);
                    if (anyRealFilteredComponents) {
                        hisStackCheck.setAmount(hisStack.getAmount());
                        toStackCheck.setAmount(0);
                    }
                    continue outer;
                } else {
                    contents[i] = null;
                    toStack.setAmount(0);
                    if (anyRealFilteredComponents) {
                        filteredContents[i] = null;
                        toStackCheck.setAmount(0);
                    }
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

    public static ItemStack prepareForSerialization(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        ItemStackPrepareForSerializationEvent event = new ItemStackPrepareForSerializationEvent(stack);
        event.callEvent();
        stack = event.getStack();

        ItemMeta meta = stack.getItemMeta();
        if (meta instanceof BlockStateMeta blockStateMeta) {
            if (blockStateMeta.hasBlockState()) {
                BlockState blockState = blockStateMeta.getBlockState();
                if (blockState != null) {
                    if (blockState instanceof Container container) {
                        Inventory inv = container.getSnapshotInventory();
                        ItemStack[] contents = inv.getContents();
                        boolean modified = false;
                        for (int i = 0; i < contents.length; i++) {
                            ItemStack oldContent = contents[i];
                            if (oldContent != null) {
                                ItemStack newContent = prepareForSerialization(oldContent);
                                if (!Objects.equals(oldContent, newContent)) {
                                    contents[i] = newContent;
                                    modified = true;
                                }
                            }
                        }
                        if (modified) {
                            inv.setContents(contents);
                            blockStateMeta.setBlockState(blockState);
                            stack = stack.clone();
                            stack.setItemMeta(blockStateMeta);
                        }
                    }
                }
            }
        } else if (meta instanceof BundleMeta bundleMeta) {
            ItemStack[] contents = bundleMeta.getItems().toArray(ItemStack[]::new);
            boolean modified = false;
            for (int i = 0; i < contents.length; i++) {
                ItemStack oldContent = contents[i];
                if (oldContent != null) {
                    ItemStack newContent = prepareForSerialization(oldContent);
                    if (!Objects.equals(oldContent, newContent)) {
                        contents[i] = newContent;
                        modified = true;
                    }
                }
            }
            if (modified) {
                bundleMeta.setItems(Arrays.asList(contents));
                stack = stack.clone();
                stack.setItemMeta(bundleMeta);
            }
        }
        return stack;
    }

    public static ItemStack restoreAfterDeserialization(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        ItemStackRestoreAfterDeserializationEvent event = new ItemStackRestoreAfterDeserializationEvent(stack);
        event.callEvent();
        stack = event.getStack();

        ItemMeta meta = stack.getItemMeta();
        if (meta instanceof BlockStateMeta blockStateMeta) {
            if (blockStateMeta.hasBlockState()) {
                BlockState blockState = blockStateMeta.getBlockState();
                if (blockState != null) {
                    if (blockState instanceof Container container) {
                        Inventory inv = container.getSnapshotInventory();
                        ItemStack[] contents = inv.getContents();
                        boolean modified = false;
                        for (int i = 0; i < contents.length; i++) {
                            ItemStack oldContent = contents[i];
                            if (oldContent != null) {
                                ItemStack newContent = restoreAfterDeserialization(oldContent);
                                if (!Objects.equals(oldContent, newContent)) {
                                    contents[i] = newContent;
                                    modified = true;
                                }
                            }
                        }
                        if (modified) {
                            inv.setContents(contents);
                            blockStateMeta.setBlockState(blockState);
                            stack = stack.clone();
                            stack.setItemMeta(blockStateMeta);
                        }
                    }
                }
            }
        } else if (meta instanceof BundleMeta bundleMeta) {
            ItemStack[] contents = bundleMeta.getItems().toArray(ItemStack[]::new);
            boolean modified = false;
            for (int i = 0; i < contents.length; i++) {
                ItemStack oldContent = contents[i];
                if (oldContent != null) {
                    ItemStack newContent = restoreAfterDeserialization(oldContent);
                    if (!Objects.equals(oldContent, newContent)) {
                        contents[i] = newContent;
                        modified = true;
                    }
                }
            }
            if (modified) {
                bundleMeta.setItems(Arrays.asList(contents));
                stack = stack.clone();
                stack.setItemMeta(bundleMeta);
            }
        }
        return stack;
    }

    public static String getBase64StringFromItemStack(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(stack.serializeAsBytes());
    }

    public static ItemStack getItemStackFromBase64(String itemString) {
        if (itemString == null) {
            return null;
        }
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(itemString));
    }

    public static Component toComponent(ItemStack[] items) {
        return toComponent(items, Style.empty());
    }

    public static Component toComponent(ItemStack[] items, Style style) {
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
                    result = ComponentUtilAdventure.TEXT_ONLY_ORDER.compare(item1.getItemMeta().displayName(), item2.getItemMeta().displayName());
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
        return toComponent(itemMap, style);
    }

    public static Component toComponent(Map<ItemStack, Integer> amounts) {
        return toComponent(amounts, Style.empty());
    }

    public static Component toComponent(Map<ItemStack, Integer> amounts, Style style) {
        Component[] components = new Component[amounts.size()];
        int index = 0;

        for (ItemStack item : amounts.keySet()) {
            int amount = amounts.get(item);

            Component component = toComponent(item, amount, style);
            if (index + 1 < amounts.size()) {
                if (index + 2 < amounts.size()) {
                    component = textOfChildren(component, text(", "));
                } else {
                    component = textOfChildren(component, text(" und "));
                }
            }
            components[index] = component;
            index++;
        }

        return textOfChildren(components).style(style);
    }

    public static Component toComponent(ItemStack item) {
        return toComponent(item, item.getAmount(), Style.empty());
    }

    public static Component toComponent(ItemStack item, int amount) {
        return toComponent(item, amount, Style.empty());
    }

    public static Component toComponent(ItemStack item, Style style) {
        return toComponent(item, item.getAmount(), style);
    }

    public static Component toComponent(ItemStack item, int amount, Style style) {
        Component result = text(amount + " ");
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            // there used to be a warnign logged here, but cannot log without a logger... maybe make a util logger at some point?
        }

        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
            Color color = armorMeta.getColor();
            // ignore "default" color:
            if (color.asRGB() != 0xA06540) {
                result = result.append(text(StringUtilBukkit.toNiceString(color)), text(" "));
            }
        }

        result = result.append(text(StringUtil.capitalizeFirstLetter(item.getType().name(), true)));

        if (meta instanceof PotionMeta potionMeta) {
            PotionType data = potionMeta.getBasePotionType();
            if (data != null) {
                result = result.append(text(" of "), text(StringUtil.capitalizeFirstLetter(data.name(), true)));
            }
            // builder.append(data.isUpgraded() ? " II" : " I");
            // if (data.isExtended()) {
            // builder.append(" (verlängert)");
            // }

            int index = 0;
            for (PotionEffect effect : potionMeta.getCustomEffects()) {
                result = result.append(text((index + 1 < potionMeta.getCustomEffects().size()) ? ", " : " and "));
                result = result.append(text(StringUtil.capitalizeFirstLetter(effect.getType().key().value(), true)), text(" "), text(StringUtil.toRomanNumber(effect.getAmplifier())));
                if (!effect.getType().isInstant()) {
                    result = result.append(text(" ("), text(StringUtil.formatTimespan(50 * effect.getDuration(), "", "", "", "", ":", ":", false, true)), text(")"));
                }
                index++;
            }
        }

        if (meta instanceof Damageable) {
            Damageable damageableMeta = (Damageable) meta;
            if (damageableMeta.hasDamage()) {
                result = result.append(text(':'), text(damageableMeta.getDamage()));
            }
        }

        if (meta instanceof BookMeta) {
            BookMeta bookMeta = (BookMeta) meta;
            boolean appended = false;

            if (meta.hasDisplayName()) {
                result = result.append(text(" (\""), meta.displayName(), text('"')); // PaperComponents.legacySectionSerializer().serialize(meta.displayName())
                appended = true;
            } else if (bookMeta.hasTitle()) {
                result = result.append(text(" (\""), bookMeta.title(), text('"'));
                appended = true;
            }

            if (appended && bookMeta.hasAuthor()) {
                result = result.append(text(" von "), bookMeta.author());
            }

            if (appended) {
                result = result.append(text(")"));
            }
        } else if (meta != null && meta.hasDisplayName()) {
            result = result.append(text(" (\""), meta.displayName(), text("\")"));
        }

        Map<Enchantment, Integer> enchantments = meta == null ? Collections.emptyMap() : new HashMap<>(meta.getEnchants());
        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta enchMeta = (EnchantmentStorageMeta) meta;
            enchantments.putAll(enchMeta.getStoredEnchants());
        }

        if (!enchantments.isEmpty()) {
            result = result.append(text(", verzaubert mit "));

            List<Enchantment> enchList = new ArrayList<>(enchantments.keySet());
            enchList.sort(Comparator.comparing(Enchantment::description, ComponentUtilAdventure.TEXT_ONLY_ORDER));

            int index = 0;
            for (Enchantment ench : enchList) {
                result = result.append(ench.description());
                if (ench.getMaxLevel() > 1 || enchantments.get(ench) > 1) {
                    result = result.append(space(), text(StringUtil.toRomanNumber(enchantments.get(ench))));
                }
                if (index + 1 < enchantments.size()) {
                    if (index + 2 < enchantments.size()) {
                        result = result.append(text(", "));
                    } else {
                        result = result.append(text(" und "));
                    }
                }
                index++;
            }
        }

        return result.style(style);
    }

    public static Component toComponent(Material m) {
        return translatable(m);
    }

    public static String toNiceString(Material m) {
        return StringUtil.capitalizeFirstLetter(m.name(), true);
    }

}
