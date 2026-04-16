package de.iani.cubesideutils.bukkit.commands;

import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.Collection;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PlayerSubCommand extends SubCommand {
    @Override
    public final boolean requiresPlayer() {
        return true;
    }

    @Override
    public final boolean hasRequiredPermission(CommandSender sender) {
        return sender instanceof Player player ? hasRequiredPermission(player) : false;
    }

    public boolean hasRequiredPermission(Player sender) {
        return getRequiredPermission() == null || sender.hasPermission(getRequiredPermission());
    }

    @Override
    public final boolean isAvailable(CommandSender sender) {
        return sender instanceof Player player ? isAvailable(player) : false;
    }

    public boolean isAvailable(Player sender) {
        return true;
    }

    @Override
    public final boolean isVisible(CommandSender sender) {
        return sender instanceof Player player ? isVisible(player) : false;
    }

    public boolean isVisible(Player sender) {
        return true;
    }

    @Override
    public final String getUsage(CommandSender sender) {
        return sender instanceof Player player ? getUsage(player) : "";
    }

    public String getUsage(Player sender) {
        return getUsage();
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        if (sender instanceof Player player) {
            return onCommand(player, command, alias, commandString, args);
        }
        throw new IllegalArgumentException("expecting a player!");
    }

    public abstract boolean onCommand(Player sender, Command command, String alias, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException;

    @Override
    public final Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        if (sender instanceof Player player) {
            return onTabComplete(player, command, alias, args);
        }
        return List.of();
    }

    public Collection<String> onTabComplete(Player sender, Command command, String alias, ArgsParser args) {
        return super.onTabComplete(sender, command, alias, args);
    }
}
