package de.iani.cubesideutils.bukkit.items;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public enum CustomHeads {
    QUARTZ_ARROW_UP("3554e03b-982d-44f1-8be4-71785ba822f8", "White Arrow Up", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFkNmM4MWY4OTlhNzg1ZWNmMjZiZTFkYzQ4ZWFlMmJjZmU3NzdhODYyMzkwZjU3ODVlOTViZDgzYmQxNGQifX19"),
    QUARTZ_ARROW_DOWN("8f54d1c4-c599-4c54-8993-2cb371649c33", "White Arrow Down", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODgyZmFmOWE1ODRjNGQ2NzZkNzMwYjIzZjg5NDJiYjk5N2ZhM2RhZDQ2ZDRmNjVlMjg4YzM5ZWI0NzFjZTcifX19"),
    QUARTZ_BLOCK_BLANK("3fe6ca71-3da7-4708-a837-0a4211b73df7", "Quartz Block Blank", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzcyMzcwNGE5ZDU5MTBiOWNkNTA1ZGM5OWM3NzliZjUwMzc5Y2I4NDc0NWNjNzE5ZTlmNzg0ZGQ4YyJ9fX0="),

    ;

    private ItemStack head;

    private CustomHeads(String ownerUUIDString, String ownerName, String texturesProperty) {
        head = createHead(UUID.fromString(ownerUUIDString), ownerName, texturesProperty);
    }

    public ItemStack getHead() {
        return new ItemStack(head);
    }

    public ItemStack getHead(String displayName) {
        ItemStack stack = getHead();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(displayName);
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack createHead(UUID ownerUUID, String ownerName, String texturesProperty) {
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(ownerUUID, ownerName);
        profile.setProperty(new ProfileProperty("textures", texturesProperty));
        meta.setPlayerProfile(profile);
        stack.setItemMeta(meta);
        return stack;
    }
}
