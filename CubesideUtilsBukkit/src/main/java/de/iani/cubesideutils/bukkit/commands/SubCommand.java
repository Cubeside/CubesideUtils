package de.iani.cubesideutils.bukkit.commands;

import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import de.iani.cubesideutils.commands.PermissionRequirer;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletionStage;
import net.kyori.adventure.text.Component;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.jetbrains.annotations.Nullable;

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

    public abstract boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException;

    /**
     * Completes arguments synchronously. For potentially blocking work, prefer
     * {@link #onTabCompleteAsync(CommandSender, Command, String, ArgsParser)}
     * to keep the server's main thread responsive.
     */
    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        return isDisplayable(sender) ? null : Collections.emptyList();
    }

    /**
     * Prepares an asynchronous tab completion on the server's main thread.
     * Prefer this hook for potentially blocking work such as database queries,
     * file access or network requests, so that it does not block the main thread.
     * Return immediately, reading any required Bukkit state before starting work
     * on your own executor or using an already asynchronous service. For example:
     * <pre>{@code
     * String prefix = args.getNext("");
     * return CompletableFuture.supplyAsync(() -> repository.findNames(prefix), executor);
     * }</pre>
     * The returned stage must complete reliably and quickly: there is no timeout,
     * and a pending stage occupies one of Paper's limited tab completion threads.
     * Do not access unsafe Bukkit state in the asynchronous work.
     * <p>
     * Returning {@code null} selects the existing synchronous
     * {@link #onTabComplete(CommandSender, Command, String, ArgsParser)} instead.
     * Otherwise, the stage must supply a non-null collection without null entries.
     * An empty collection supplies no argument suggestions; displayable subcommands
     * are still added by the router. Results are filtered and sorted by the router.
     * Do not modify the result collection after completing the stage.
     * <p>
     * Only Paper's async completion event uses this hook. Other completion calls
     * continue to use the synchronous method. Disabling the plugin releases the
     * router's waiting requests without cancelling stages supplied by this method.
     *
     * @return the completion stage, or {@code null} for synchronous completion
     */
    public @Nullable CompletionStage<? extends Collection<String>> onTabCompleteAsync(CommandSender sender, Command command, String alias, ArgsParser args) {
        return null;
    }

    public String getUsage(CommandSender sender) {
        return getUsage();
    }

    public String getUsage() {
        return "";
    }

    public Component getDescription(CommandSender sender) {
        return getDescription();
    }

    public Component getDescription() {
        return null;
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
