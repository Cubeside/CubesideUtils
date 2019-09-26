package de.iani.cubesideutils.commands;

import de.iani.cubesideutils.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class AbstractCommandRouter<T> {

    protected class CommandMap {
        protected String name;

        protected CommandMap parent;

        protected HashMap<String, CommandMap> subCommands;

        protected ArrayList<CommandMap> subcommandsOrdered;

        protected T executor;

        public CommandMap(CommandMap parent, String name) {
            this.parent = parent;
            this.name = name;
        }
    }

    private CommandMap commands;

    public AbstractCommandRouter() {
        commands = new CommandMap(null, null);
    }

    public void addCommandMapping(T command, String... route) {
        if (route.length == 1) {
            if (route[0].isEmpty()) {
                addCommandMapping(command);
                return;
            }
            if (route[0].contains(" ")) {
                addCommandMapping(command, route[0].split(" "));
                return;
            }
        }

        CommandMap current = commands;
        for (int i = 0; i < route.length; i++) {
            if (current.subCommands == null) {
                current.subCommands = new HashMap<>();
                current.subcommandsOrdered = new ArrayList<>();
            }
            String routePart = route[i].toLowerCase();
            CommandMap part = current.subCommands.get(routePart);
            if (part == null) {
                part = new CommandMap(current, routePart);
                current.subCommands.put(routePart, part);
                current.subcommandsOrdered.add(part);
            }
            current = part;
        }
        if (current.executor != null) {
            throw new IllegalArgumentException("Path " + Arrays.toString(route) + " is already mapped!");
        }
        current.executor = command;
    }

    public void addAliases(String aliases[], String... route) {
        for (String alias : aliases) {
            addAlias(alias, route);
        }
    }

    public void addAliases(Iterable<String> aliases, String... route) {
        for (String alias : aliases) {
            addAlias(alias, route);
        }
    }

    public void addAlias(String alias, String... route) {
        if (route.length == 0) {
            throw new IllegalArgumentException("Route may not be empty!");
        }
        if (route.length == 1 && route[0].contains(" ")) {
            addAlias(alias, route[0].split(" "));
            return;
        }

        alias = alias.toLowerCase().trim();
        CommandMap current = commands;
        for (int i = 0; i < route.length - 1; i++) {
            if (current.subCommands == null) {
                throw new IllegalArgumentException("Path " + Arrays.toString(route) + " is not mapped!");
            }
            String routePart = route[i].toLowerCase();
            CommandMap part = current.subCommands.get(routePart);
            if (part == null) {
                throw new IllegalArgumentException("Path " + Arrays.toString(route) + " is not mapped!");
            }
            current = part;
        }
        CommandMap createAliasFor = current.subCommands.get(route[route.length - 1].toLowerCase());
        if (createAliasFor == null) {
            throw new IllegalArgumentException("Path " + Arrays.toString(route) + " is not mapped!");
        }
        if (current.subCommands.get(alias) != null) {
            route = route.clone();
            route[route.length - 1] = alias;
            throw new IllegalArgumentException("Path " + Arrays.toString(route) + " is already mapped!");
        }

        current.subCommands.put(alias, createAliasFor);
        // dont add to current.subcommandsOrdered, because it should not be shown in the help message
    }

    // untested!
    public T getSubCommand(String path) {
        String[] args = path.split(" ");
        CommandMap currentMap = commands;
        int nr = 0;
        while (currentMap != null) {
            String currentCmdPart = args.length > nr ? args[nr] : null;
            if (currentCmdPart != null) {
                currentCmdPart = currentCmdPart.toLowerCase();
            }
            // descend to subcommand?
            if (currentCmdPart != null && currentMap.subCommands != null) {
                CommandMap subMap = currentMap.subCommands.get(currentCmdPart);
                if (subMap != null) {
                    nr += 1;
                    currentMap = subMap;
                    continue;
                }
            }
            // found?
            T toExecute = currentMap.executor;
            if (toExecute != null) {
                return toExecute;
            }
            return null;
        }
        return null;
    }

    protected Pair<CommandMap, Integer> matchCommandMap(String[] args) {
        return matchCommandMap(args, 0);
    }

    protected Pair<CommandMap, Integer> matchCommandMap(String[] args, int ignoredLastArgs) {
        CommandMap currentMap = commands;
        int nr = 0;
        while (true) {
            String currentCmdPart = args.length - ignoredLastArgs > nr ? args[nr] : null;
            if (currentCmdPart != null) {
                currentCmdPart = currentCmdPart.toLowerCase();
            }
            // descend to subcommand?
            if (currentCmdPart != null && currentMap.subCommands != null) {
                CommandMap subMap = currentMap.subCommands.get(currentCmdPart);
                if (subMap != null) {
                    nr += 1;
                    currentMap = subMap;
                    continue;
                }
            }

            return new Pair<>(currentMap, nr);
        }
    }
}
