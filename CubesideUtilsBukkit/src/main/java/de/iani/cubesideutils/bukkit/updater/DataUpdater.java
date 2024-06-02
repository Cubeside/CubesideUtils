package de.iani.cubesideutils.bukkit.updater;

import de.iani.cubesideutils.bukkit.MinecraftVersion;
import java.util.Objects;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class DataUpdater {
    private DataUpdater() {
        throw new UnsupportedOperationException("No instance for you, Sir!"); // prevents instances
    }

    /**
     * Performs updates to the given ItemStack. The given stack is not changed but a new ItemStack is creatied if something has to be updated.
     * Plugins can register to update ItemStacks by using the {@link ItemStackUpdateEvent}.
     * Use the result to get the updated stack.
     *
     * @param stack
     *            a ItemStack that should be updated if required
     * @return the new created updated ItemStack or the unmodified old ItemStack if nothing changed.
     */
    public static ItemStack updateItemStack(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        ItemStackUpdateEvent event = new ItemStackUpdateEvent(stack);
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
                                ItemStack newContent = updateItemStack(oldContent);
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
        }
        return stack;
    }

    /**
     * Update the names for enchantments in the bukkit enums
     *
     * @param name
     *            the old name
     * @return the updated name
     */
    public static String updateEnchantmentName(String name) {
        if (!MinecraftVersion.isAboveOrEqual(1, 20, 5)) {
            return name;
        }
        // renames from 1.20.5
        return switch (name) {
            case "PROTECTION_ENVIRONMENTAL" -> "PROTECTION";
            case "PROTECTION_FIRE" -> "FIRE_PROTECTION";
            case "PROTECTION_FALL" -> "FEATHER_FALLING";
            case "PROTECTION_EXPLOSIONS" -> "BLAST_PROTECTION";
            case "PROTECTION_PROJECTILE" -> "PROJECTILE_PROTECTION";
            case "OXYGEN" -> "RESPIRATION";
            case "WATER_WORKER" -> "AQUA_AFFINITY";
            case "DAMAGE_ALL" -> "SHARPNESS";
            case "DAMAGE_UNDEAD" -> "SMITE";
            case "DAMAGE_ARTHROPODS" -> "BANE_OF_ARTHROPODS";
            case "LOOT_BONUS_MOBS" -> "LOOTING";
            case "DIG_SPEED" -> "EFFICIENCY";
            case "DURABILITY" -> "UNBREAKING";
            case "LOOT_BONUS_BLOCKS" -> "FORTUNE";
            case "ARROW_DAMAGE" -> "POWER";
            case "ARROW_KNOCKBACK" -> "PUNCH";
            case "ARROW_FIRE" -> "FLAME";
            case "ARROW_INFINITE" -> "INFINITY";
            case "LUCK" -> "LUCK_OF_THE_SEA";
            case "SWEEPING" -> "SWEEPING_EDGE";
            default -> name;
        };
    }

    /**
     * Update the names for entity types in the bukkit enums
     *
     * @param name
     *            the old name
     * @return the updated name
     */
    public static String updateEntityTypeName(String name) {
        if (!MinecraftVersion.isAboveOrEqual(1, 20, 5)) {
            return name;
        }
        // renames from 1.20.5
        return switch (name) {
            case "DROPPED_ITEM" -> "ITEM";
            case "LEASH_HITCH" -> "LEASH_KNOT";
            case "ENDER_SIGNAL" -> "EYE_OF_ENDER";
            case "SPLASH_POTION" -> "POTION";
            case "THROWN_EXP_BOTTLE" -> "EXPERIENCE_BOTTLE";
            case "PRIMED_TNT" -> "TNT";
            case "FIREWORK" -> "FIREWORK_ROCKET";
            case "MINECART_COMMAND" -> "COMMAND_BLOCK_MINECART";
            case "MINECART_CHEST" -> "CHEST_MINECART";
            case "MINECART_FURNACE" -> "FURNACE_MINECART";
            case "MINECART_TNT" -> "TNT_MINECART";
            case "MINECART_HOPPER" -> "HOPPER_MINECART";
            case "MINECART_MOB_SPAWNER" -> "SPAWNER_MINECART";
            case "MUSHROOM_COW" -> "MOOSHROOM";
            case "SNOWMAN" -> "SNOW_GOLEM";
            case "ENDER_CRYSTAL" -> "END_CRYSTAL";
            case "FISHING_HOOK" -> "FISHING_BOBBER";
            case "LIGHTNING" -> "LIGHTNING_BOLT";
            default -> name;
        };
    }

    /**
     * Update the names for potion effect types in the bukkit enums
     *
     * @param name
     *            the old name
     * @return the updated name
     */
    public static String updatePotionEffectTypeName(String name) {
        if (!MinecraftVersion.isAboveOrEqual(1, 20, 5)) {
            return name;
        }
        // renames from 1.20.5
        return switch (name) {
            case "SLOW" -> "SLOWNESS";
            case "FAST_DIGGING" -> "HASTE";
            case "SLOW_DIGGING" -> "MINING_FATIGUE";
            case "INCREASE_DAMAGE" -> "STRENGTH";
            case "HEAL" -> "INSTANT_HEALTH";
            case "HARM" -> "INSTANT_DAMAGE";
            case "JUMP" -> "JUMP_BOOST";
            case "CONFUSION" -> "NAUSEA";
            case "DAMAGE_RESISTANCE" -> "RESISTANCE";
            default -> name;
        };
    }

    /**
     * Update the names for potion effects in the bukkit enums
     *
     * @param name
     *            the old name
     * @return the updated name
     */
    public static String updatePotionEffectName(String name) {
        if (!MinecraftVersion.isAboveOrEqual(1, 20, 5)) {
            return name;
        }
        // renames from 1.20.5
        return switch (name) {
            case "JUMP" -> "LEAPING";
            case "SPEED" -> "SWIFTNESS";
            case "INSTANT_HEAL" -> "HEALING";
            case "INSTANT_DAMAGE" -> "HARMING";
            case "REGEN" -> "REGENERATION";
            default -> name;
        };
    }

    /**
     * Update the names for particles in the bukkit enums
     *
     * @param name
     *            the old name
     * @return the updated name
     */
    public static String updateParticleName(String name) {
        if (!MinecraftVersion.isAboveOrEqual(1, 20, 5)) {
            return name;
        }
        // renames from 1.20.5
        return switch (name) {
            case "EXPLOSION_NORMAL" -> "POOF";
            case "EXPLOSION_LARGE" -> "EXPLOSION";
            case "EXPLOSION_HUGE" -> "EXPLOSION_EMITTER";
            case "FIREWORKS_SPARK" -> "FIREWORK";
            case "WATER_BUBBLE" -> "BUBBLE";
            case "WATER_SPLASH" -> "SPLASH";
            case "WATER_WAKE" -> "FISHING";
            case "SUSPENDED" -> "UNDERWATER";
            case "SUSPENDED_DEPTH" -> "UNDERWATER";
            case "CRIT_MAGIC" -> "ENCHANTED_HIT";
            case "SMOKE_NORMAL" -> "SMOKE";
            case "SMOKE_LARGE" -> "LARGE_SMOKE";
            case "SPELL" -> "EFFECT";
            case "SPELL_INSTANT" -> "INSTANT_EFFECT";
            case "SPELL_MOB" -> "ENTITY_EFFECT";
            case "SPELL_MOB_AMBIENT" -> "ENTITY_EFFECT";
            case "SPELL_WITCH" -> "WITCH";
            case "DRIP_WATER" -> "DRIPPING_WATER";
            case "DRIP_LAVA" -> "DRIPPING_LAVA";
            case "VILLAGER_ANGRY" -> "ANGRY_VILLAGER";
            case "VILLAGER_HAPPY" -> "HAPPY_VILLAGER";
            case "TOWN_AURA" -> "MYCELIUM";
            case "ENCHANTMENT_TABLE" -> "ENCHANT";
            case "REDSTONE" -> "DUST";
            case "SNOWBALL" -> "ITEM_SNOWBALL";
            case "SNOW_SHOVEL" -> "ITEM_SNOWBALL";
            case "SLIME" -> "ITEM_SLIME";
            case "ITEM_CRACK" -> "ITEM";
            case "BLOCK_CRACK" -> "BLOCK";
            case "BLOCK_DUST" -> "BLOCK";
            case "WATER_DROP" -> "RAIN";
            case "MOB_APPEARANCE" -> "ELDER_GUARDIAN";
            case "TOTEM" -> "TOTEM_OF_UNDYING";
            default -> name;
        };
    }
}
