package de.iani.cubesideutils.commands;

import java.lang.reflect.Method;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public class CommandUtil {
    private CommandUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    /**
     * This method has to be called after commands are added / removed in the servers command map.
     * It recyncs the tab completions for the commands.
     */
    public static void resyncCommandTabCompletions() {
        Server server = Bukkit.getServer();
        try {
            Method syncCommandsMethod = server.getClass().getDeclaredMethod("syncCommands");
            syncCommandsMethod.setAccessible(true);
            syncCommandsMethod.invoke(server);
        } catch (Exception e) {
            server.getLogger().log(Level.SEVERE, "Could not resync commands", e);
        }
    }
}
