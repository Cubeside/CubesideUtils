package de.iani.cubesideutils.bungee.commands;

import de.iani.cubesideutils.bungee.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bungee.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bungee.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bungee.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import de.iani.cubesideutils.commands.PermissionRequirer;
import java.util.Collection;
import java.util.Collections;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

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

    public abstract boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) throws RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException;

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
        if (!(sender instanceof ProxiedPlayer) && requiresPlayer()) {
            return false;
        }
        return hasRequiredPermission(sender) && isAvailable(sender);
    }

    public boolean isDisplayable(CommandSender sender) {
        return isExecutable(sender) && isVisible(sender);
    }
}
