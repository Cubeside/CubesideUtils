package de.iani.cubesideutils.bukkit.commands;

import de.iani.cubesideutils.bukkit.events.CommandActionFlagsCheckEvent;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NoChangeCommandActionFlagsCheckListener implements Listener {
    private Command command;

    public NoChangeCommandActionFlagsCheckListener(Command command) {
        this.command = command;
    }

    @EventHandler
    public void onCommandActionFlagsCheck(CommandActionFlagsCheckEvent event) {
        if (event.getCommand() == command) {
            event.setActionFlag(CommandActionFlag.CHAT, false);
            event.setActionFlag(CommandActionFlag.MODIFY_MISC_PLAYER_STATE, false);
            event.setActionFlag(CommandActionFlag.MODIFY_PLAYER_INVENTORY_CONTENT, false);
            event.setActionFlag(CommandActionFlag.MODIFY_WORLD_STATE, false);
            event.setActionFlag(CommandActionFlag.OPEN_OR_CLOSE_INVENTORY, false);
            event.setActionFlag(CommandActionFlag.TELEPORT, false);
        }
    }
}