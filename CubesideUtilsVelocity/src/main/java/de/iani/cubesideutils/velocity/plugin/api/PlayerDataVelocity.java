package de.iani.cubesideutils.velocity.plugin.api;

import com.velocitypowered.api.proxy.Player;
import de.iani.cubesideutils.plugin.api.PlayerData;

import java.util.Optional;

public interface PlayerDataVelocity extends PlayerData {

    public Optional<Player> getPlayer();

}