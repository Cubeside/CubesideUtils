package de.iani.cubesideutils.bukkit.commands;

import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.util.StringUtil;

public abstract class HybridCommand extends SubCommand implements CommandExecutor, TabCompleter {

    public static final CommandExceptionHandler SPECIAL_DEFAULT_HANDLER = new CommandExceptionHandler() {

        @Override
        public boolean handleIllegalSyntax(IllegalSyntaxException thrown) {
            CommandSender sender = thrown.getSender();
            String alias = thrown.getAlias();
            SubCommand subCommand = thrown.getSubCommand();
            sender.sendMessage("/" + alias + " " + subCommand.getUsage(sender));
            return true;
        }

    };

    private CommandExceptionHandler handler;

    public HybridCommand(CommandExceptionHandler handler) {
        this.handler = Objects.requireNonNull(handler);
    }

    public HybridCommand() {
        this(HybridCommand.SPECIAL_DEFAULT_HANDLER);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        try {
            if (!allowsCommandBlock() && (sender instanceof BlockCommandSender || sender instanceof CommandMinecart)) {
                throw new DisallowsCommandBlockException(null, sender, command, alias, this, args);
            }
            if (requiresPlayer() && !(sender instanceof Player)) {
                throw new RequiresPlayerException(null, sender, command, alias, this, args);
            }
            if (!hasRequiredPermission(sender) || !isAvailable(sender)) {
                throw new NoPermissionException(null, sender, command, alias, this, args, this.getRequiredPermission());
            }

            if (onCommand(sender, command, alias, "/" + alias + " ", new ArgsParser(args))) {
                return true;
            } else {
                throw new IllegalSyntaxException(null, sender, command, alias, this, args);
            }
        } catch (DisallowsCommandBlockException e) {
            return handler.handleDisallowsCommandBlock(e);
        } catch (RequiresPlayerException e) {
            return handler.handleRequiresPlayer(e);
        } catch (NoPermissionException e) {
            return handler.handleNoPermission(e);
        } catch (IllegalSyntaxException e) {
            return handler.handleIllegalSyntax(e);
        } catch (InternalCommandException e) {
            return handler.handleInternalException(e);
        } catch (Throwable t) {
            return handler.handleInternalException(new InternalCommandException(null, sender, command, alias, this, args, t));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!allowsCommandBlock() && (sender instanceof BlockCommandSender || sender instanceof CommandMinecart)) {
            return Collections.emptyList();
        }
        if (requiresPlayer() && !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (getRequiredPermission() != null && !sender.hasPermission(getRequiredPermission())) {
            return Collections.emptyList();
        }

        Collection<String> options = onTabComplete(sender, command, alias, new ArgsParser(args));
        if (options == null) {
            return null;
        }

        List<String> result = StringUtil.copyPartialMatches(args.length > 0 ? args[args.length - 1] : "", options, new ArrayList<String>());
        Collections.sort(result);
        return result;
    }

}
