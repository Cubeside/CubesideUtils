package de.iani.cubesideutils.commands;

import de.iani.cubesideutils.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.commands.exceptions.RequiresPlayerException;
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

    private CommandExceptionHandler handler;

    public HybridCommand(CommandExceptionHandler handler) {
        this.handler = Objects.requireNonNull(handler);
    }

    public HybridCommand() {
        this(CommandExceptionHandler.DEFAULT_HANDLER);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        try {
            if (!allowsCommandBlock() && (sender instanceof BlockCommandSender || sender instanceof CommandMinecart)) {
                throw new DisallowsCommandBlockException(sender, command, alias, this, args);
            }
            if (requiresPlayer() && !(sender instanceof Player)) {
                throw new RequiresPlayerException(sender, command, alias, this, args);
            }
            if (!hasRequiredPermission(sender) || !isAvailable(sender)) {
                throw new NoPermissionException(sender, command, alias, this, args, this.getRequiredPermission());
            }

            if (onCommand(sender, command, alias, "/" + alias, new ArgsParser(args))) {
                return true;
            } else {
                throw new IllegalSyntaxException(sender, command, alias, this, args);
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
            return handler.handleInternalException(new InternalCommandException(sender, command, alias, this, args, t));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (this.getRequiredPermission() != null && !sender.hasPermission(getRequiredPermission())) {
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
