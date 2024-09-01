package de.iani.cubesideutils.bukkit.plugin.api;

import de.iani.cubesideutils.bukkit.plugin.CubesideUtilsBukkit;
import de.iani.cubesideutils.bukkit.sound.SoundSequence;
import de.iani.cubesideutils.conditions.Condition;
import de.iani.cubesideutils.plugin.api.UtilsApi;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface UtilsApiBukkit extends UtilsApi {

    public static UtilsApiBukkit getInstance() {
        return CubesideUtilsBukkit.getInstance();
    }

    public abstract InventoryInputManager getInventoryInputManager();

    public abstract OnlinePlayerData getPlayerData(Player player);

    public abstract PlayerDataBukkit getPlayerData(OfflinePlayer player);

    @Override
    public abstract PlayerDataBukkit getPlayerData(UUID playerId);

    public abstract List<OfflinePlayer> searchPlayersByPartialName(String partialName);

    public abstract String getWorldDisplayName(LivingEntity entity);

    public abstract String getWorldDisplayName(World world);

    public abstract String getWorldDisplayName(String worldName);

    public abstract Set<String> getWorldDisplayNames(OfflinePlayer player);

    public abstract Set<String> getWorldDisplayNames(UUID playerId);

    public abstract void sendMessageToPlayersAllServers(Condition<? super Player> seeMsgCondition, String message);

    public abstract void sendMessageToPlayersAllServers(Condition<? super Player> seeMsgCondition, BaseComponent... message);

    public abstract void sendMessageToPlayersAllServers(Condition<? super Player> seeMsgCondition, Component message);

    public abstract void sendPlayerOptions(CommandSender sender, OfflinePlayer player);

    public abstract void doAfterReconfigurationPhase(Player player, List<Consumer<? super Player>> actions);

    public abstract void doAfterReconfigurationPhase(Player player, Consumer<? super Player> action);

    public abstract void sendSoundSequenceToPlayersAllServers(Condition<? super Player> hearSoundCondition, SoundSequence soundSequence);
}