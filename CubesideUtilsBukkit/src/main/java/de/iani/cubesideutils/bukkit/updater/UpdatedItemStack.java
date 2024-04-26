package de.iani.cubesideutils.bukkit.updater;

import org.bukkit.inventory.ItemStack;

public record UpdatedItemStack(ItemStack itemStack, boolean isChanged) {
}