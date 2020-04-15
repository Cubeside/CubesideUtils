package de.iani.cubesideutils.commands;

import de.iani.cubesideutils.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.commands.exceptions.RequiresPlayerException;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

public abstract class SubCommand implements PermissionRequirer {

    // Overwrite these as necessarry

    public boolean requiresPlayer() {
        return false;
    }

    public boolean allowsCommandBlock() {
        return false;
    }

    @Override
    public String getRequiredPermission() {
        return null;
    }

    public boolean isAvailable(CommandSender sender) {
        return true;
    }

    public boolean isVisible(CommandSender sender) {
        return true;
    }

    public abstract boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException;

    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        return isDisplayable(sender) ? null : Collections.emptyList();
    }

    public String getUsage(CommandSender sender) {
        return getUsage();
    }

    public String getUsage() {
        return "";
    }

    // For convenience

    public boolean hasRequiredPermission(CommandSender sender) {
        return getRequiredPermission() == null || sender.hasPermission(getRequiredPermission());
    }

    public boolean isExecutable(CommandSender sender) {
        if (sender instanceof BlockCommandSender || sender instanceof CommandMinecart) {
            if (!allowsCommandBlock()) {
                return false;
            }
        }
        if (!(sender instanceof Player) && requiresPlayer()) {
            return false;
        }
        return hasRequiredPermission(sender) && isAvailable(sender);
    }

    public boolean isDisplayable(CommandSender sender) {
        return isExecutable(sender) && isVisible(sender);
    }
}
