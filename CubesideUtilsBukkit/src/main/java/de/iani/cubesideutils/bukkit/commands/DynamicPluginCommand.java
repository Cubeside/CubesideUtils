package de.iani.cubesideutils.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

public abstract class DynamicPluginCommand extends Command implements PluginIdentifiableCommand {
    private final Plugin plugin;

    protected DynamicPluginCommand(Plugin plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}