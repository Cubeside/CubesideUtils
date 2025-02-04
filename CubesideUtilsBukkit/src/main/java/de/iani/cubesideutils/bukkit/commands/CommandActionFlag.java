package de.iani.cubesideutils.bukkit.commands;

public enum CommandActionFlag {
    /**
     * Teleport any player
     */
    TELEPORT,
    /**
     * Send/Receive public or private chat messages
     */
    CHAT,
    /**
     * Any modifications to the players inventory
     */
    MODIFY_PLAYER_INVENTORY_CONTENT,
    /**
     * Opening or closing some inventory
     */
    OPEN_OR_CLOSE_INVENTORY,
    /**
     * Additional player states like attributes, health, gamemode
     */
    MODIFY_MISC_PLAYER_STATE,
    /**
     * Placing blocks, spawning entites, etc.
     */
    MODIFY_WORLD_STATE

}
