package de.iani.cubesideutils.velocity.commands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import de.iani.cubesideutils.commands.ArgsParser;
import de.iani.cubesideutils.commands.PermissionRequirer;
import java.util.Collection;
import java.util.Collections;

import de.iani.cubesideutils.velocity.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.velocity.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.velocity.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.velocity.commands.exceptions.RequiresPlayerException;

public abstract class SubCommand implements PermissionRequirer {

    // Overwrite these as necessarry

    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public String getRequiredPermission() {
        return null;
    }

    public boolean isAvailable(CommandSource sender) {
        return true;
    }

    public boolean isVisible(CommandSource sender) {
        return true;
    }

    public abstract boolean onCommand(CommandSource sender, Command command, String alias, String commandString, ArgsParser args) throws RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException;

    public Collection<String> onTabComplete(CommandSource sender, Command command, String alias, ArgsParser args) {
        return isDisplayable(sender) ? null : Collections.emptyList();
    }

    public String getUsage(CommandSource sender) {
        return getUsage();
    }

    public String getUsage() {
        return "";
    }

    // For convenience

    public boolean hasRequiredPermission(CommandSource sender) {
        return getRequiredPermission() == null || sender.hasPermission(getRequiredPermission());
    }

    public boolean isExecutable(CommandSource sender) {
        if (!(sender instanceof Player) && requiresPlayer()) {
            return false;
        }
        return hasRequiredPermission(sender) && isAvailable(sender);
    }

    public boolean isDisplayable(CommandSource sender) {
        return isExecutable(sender) && isVisible(sender);
    }
}
