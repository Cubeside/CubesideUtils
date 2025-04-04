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
