package de.iani.cubesideutils.bukkit.plugin;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import de.iani.cubesideutils.bukkit.items.CustomHeads;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.scheduler.BukkitRunnable;

public class AnvilGUI {
    private CubesideUtilsBukkit plugin;
    private Player player;
    private AnvilInventory inventory;

    private ItemStack firstItem;
    private ItemStack secondItem;
    private ItemStack resultItem;
    private AnvilView openInventory;
    private String searchForName;
    private boolean confirmed = false;
    private Function<AnvilGUI, Boolean> confirmHandler;
    private Consumer<AnvilGUI> cancelHandler;

    AnvilGUI(CubesideUtilsBukkit plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        firstItem = CustomHeads.QUARTZ_QUESTION_MARK.getHead(Component.empty(), Component.text("Suchen", NamedTextColor.GRAY, TextDecoration.BOLD), Component.text("Gib deine Suchanfrage ein."));
        resultItem = CustomHeads.QUARTZ_ARROW_RIGHT.getHead(Component.text("Suche zurücksetzen!", NamedTextColor.GREEN));
    }

    public AnvilGUI setFirstItem(ItemStack stack) {
        firstItem = stack == null ? null : stack.clone();
        if (openInventory != null) {
            openInventory.getTopInventory().setFirstItem(firstItem);
        }
        return this;
    }

    public AnvilGUI setSecondItem(ItemStack stack) {
        secondItem = stack == null ? null : stack.clone();
        if (openInventory != null) {
            openInventory.getTopInventory().setSecondItem(secondItem);
        }
        return this;
    }

    public AnvilGUI setResultItem(ItemStack stack) {
        resultItem = stack == null ? null : stack.clone();
        if (openInventory != null) {
            openInventory.getTopInventory().setResult(resultItem);
        }
        return this;
    }

    AnvilView getOpenInventory() {
        return openInventory;
    }

    public boolean isOpen() {
        return openInventory != null;
    }

    public boolean open() {
        // player.sendMessage("open");
        if (openInventory != null) {
            throw new IllegalStateException("this inventory is already open");
        }
        confirmed = false;
        InventoryView view = player.openAnvil(null, true);
        if (view == null) {
            return false;
        }
        if (!(view instanceof AnvilView anvilView)) {
            throw new RuntimeException("expected AnvilView");
        }
        openInventory = anvilView;
        openInventory.setRepairCost(0);
        inventory = openInventory.getTopInventory();
        inventory.setFirstItem(firstItem);
        inventory.setSecondItem(secondItem);
        inventory.setResult(resultItem);
        OnlinePlayerDataImpl onlinePlayerData = plugin.getPlayerData(player);
        onlinePlayerData.setOpenAnvilGUI(this);

        return true;
    }

    public void close(boolean fromCloseEvent) {
        // player.sendMessage("close");
        OnlinePlayerDataImpl onlinePlayerData = plugin.getPlayerData(player);
        if (onlinePlayerData.getOpenAnvilGUI() == this) {
            onlinePlayerData.setOpenAnvilGUI(null);
        }
        if (openInventory == null) {
            return;
        }
        if (!fromCloseEvent && Objects.equals(player.getOpenInventory(), openInventory)) {
            openInventory.close();
        }
        openInventory = null;
        if (!confirmed) {
            if (cancelHandler != null) {
                if (fromCloseEvent) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            cancelHandler.accept(AnvilGUI.this);
                        }
                    }.runTask(plugin.getPlugin());
                } else {
                    cancelHandler.accept(this);
                }
            }
        }
    }

    public void setConfirmHandler(Function<AnvilGUI, Boolean> handler) {
        confirmHandler = handler;
    }

    public void setCancelHandler(Consumer<AnvilGUI> handler) {
        cancelHandler = handler;
    }

    public void setUpdateTextHandler(Consumer<AnvilGUI> handler) {

    }

    void onInventoryClick(int slot, ItemStack currentItem, boolean shiftClick) {
        // player.sendMessage("onInventoryClick: " + slot + " " + shiftClick);
        openInventory.setRepairCost(0);
        if (slot == 2) {
            // player.sendMessage("Search: " + searchForName);
            confirmed = true;
            if (confirmHandler == null || confirmHandler.apply(this) == Boolean.TRUE) {
                close(false);
            }
        }
    }

    void onUpdateAnvil(PrepareResultEvent event) {
        // player.sendMessage("update anvil");
        openInventory.setRepairCost(0);
        AnvilInventory inv = openInventory.getTopInventory();
        searchForName = null;
        if (inv.getResult() != null && inv.getResult().hasItemMeta()) {
            Component displayName = inv.getResult().getItemMeta().displayName();
            if (displayName != null) {
                searchForName = PlainTextComponentSerializer.plainText().serialize(displayName);
            }
        }
        if (searchForName != null) {
            searchForName = searchForName.trim();
            if (searchForName.isEmpty()) {
                searchForName = null;
            }
        }
        // player.sendMessage("update anvil search: " + searchForName);
        if (searchForName == null) {
            event.setResult(CustomHeads.QUARTZ_ARROW_RIGHT.getHead(Component.text("Suche zurücksetzen!", NamedTextColor.GREEN)));
        } else {
            event.setResult(CustomHeads.QUARTZ_ARROW_RIGHT.getHead(Component.text("Nach '" + searchForName + "' suchen!", NamedTextColor.GREEN)));
        }
    }

    void onInventoryClose() {
        // player.sendMessage("onInventoryClose");
        close(true);
    }

    public String getInputString() {
        return searchForName;
    }
}
