package de.iani.cubesideutils.commands;

import de.iani.cubesideutils.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

public abstract class AbstractCommandRouter<ControllerT extends PermissionRequirer, HandlerT> {

    protected class CommandMap {
        public final String name;

        public final CommandMap parent;

        public HashMap<String, CommandMap> subCommands;

        public ArrayList<CommandMap> subcommandsOrdered;

        public ControllerT executor;

        /**
         * Required permissions for executor/subcommands - if null at least one executor/subcommand does not require a permission
         */
        public HashSet<String> requiredPermissions;

        public CommandMap(CommandMap parent, String name) {
            this.parent = parent;
            this.name = name;
        }
    }

    private final CommandMap commands;

    private final boolean caseInsensitive;

    public AbstractCommandRouter(boolean caseInsensitive) {
        commands = new CommandMap(null, null);
        this.caseInsensitive = caseInsensitive;
    }

    protected CommandMap getMainCommandMap() {
        return commands;
    }

    public void addCommandMapping(ControllerT command, String... route) {
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
            String routePart = toLowerCaseIfCaseInsensitive(route[i]);
            CommandMap part = current.subCommands.get(routePart);
            if (part == null) {
                part = new CommandMap(current, routePart);
                current.subCommands.put(routePart, part);
                current.subcommandsOrdered.add(part);
                onSubCommandsModified(current);
            }
            current = part;
        }
        if (current.executor != null) {
            throw new IllegalArgumentException("Path " + Arrays.toString(route) + " is already mapped!");
        }
        current.executor = command;
        onSubCommandsModified(current);
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
        alias = toLowerCaseIfCaseInsensitive(alias);
        CommandMap current = commands;
        for (int i = 0; i < route.length - 1; i++) {
            if (current.subCommands == null) {
                throw new IllegalArgumentException("Path " + Arrays.toString(route) + " is not mapped!");
            }
            String routePart = toLowerCaseIfCaseInsensitive(route[i]);
            CommandMap part = current.subCommands.get(routePart);
            if (part == null) {
                throw new IllegalArgumentException("Path " + Arrays.toString(route) + " is not mapped!");
            }
            current = part;
        }
        CommandMap createAliasFor = current.subCommands.get(toLowerCaseIfCaseInsensitive(route[route.length - 1]));
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
        onSubCommandsModified(current);
    }

    protected Pair<CommandMap, Integer> matchCommandMap(HandlerT handler, String[] args) {
        return matchCommandMap(handler, args, 0);
    }

    protected Pair<CommandMap, Integer> matchCommandMap(HandlerT handler, String[] args, int ignoredLastArgs) {
        CommandMap currentMap = commands;
        int nr = 0;
        while (true) {
            String currentCmdPart = args.length - ignoredLastArgs > nr ? args[nr] : null;
            if (currentCmdPart != null) {
                currentCmdPart = toLowerCaseIfCaseInsensitive(currentCmdPart);
            }
            // descend to subcommand?
            if (currentCmdPart == null || currentMap.subCommands == null) {
                break;
            }
            CommandMap subMap = currentMap.subCommands.get(currentCmdPart);
            if (subMap == null) {
                break;
            }
            nr += 1;
            currentMap = subMap;
        }
        return new Pair<>(currentMap, nr);
    }

    protected void onSubCommandsModified(CommandMap map) {
        HashSet<String> requiredPermissions = null;
        if (map.executor != null) {
            String requiredPermission = map.executor.getRequiredPermission();
            if (requiredPermission == null) {
                if (map.requiredPermissions != null) {
                    map.requiredPermissions = null;
                    if (map.parent != null) {
                        onSubCommandsModified(map.parent);
                    }
                }
                return;
            } else {
                requiredPermissions = new HashSet<>();
                requiredPermissions.add(requiredPermission);
            }
        }
        if (map.subcommandsOrdered != null) {
            for (CommandMap subMap : map.subcommandsOrdered) {
                if (subMap.requiredPermissions == null) {
                    if (map.requiredPermissions != null) {
                        map.requiredPermissions = null;
                        if (map.parent != null) {
                            onSubCommandsModified(map.parent);
                        }
                    }
                    return;
                } else {
                    if (requiredPermissions == null) {
                        requiredPermissions = new HashSet<>();
                    }
                    requiredPermissions.addAll(subMap.requiredPermissions);
                }
            }
        }
        if (!Objects.equals(map.requiredPermissions, requiredPermissions)) {
            map.requiredPermissions = requiredPermissions;
            if (map.parent != null) {
                onSubCommandsModified(map.parent);
            }
        }
    }

    protected String toLowerCaseIfCaseInsensitive(String s) {
        return caseInsensitive ? s.toLowerCase(Locale.US) : s;
    }
}
