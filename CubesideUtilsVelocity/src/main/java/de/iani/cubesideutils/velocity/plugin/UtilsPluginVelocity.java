package de.iani.cubesideutils.velocity.plugin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.nio.file.Path;

//@Plugin(id = "cubesideutils", name = "CubesideUtils", version = "1.16-SNAPSHOT", url = "https://cubeside.de", description = "CubesideUtils", authors = {"Brokkonaut", "Starjon"}, dependencies = {@Dependency(id = "globalconnectionvelocity")})
public class UtilsPluginVelocity {
    private CubesideUtilsVelocity core;

    @Inject
    public UtilsPluginVelocity(ProxyServer server, org.slf4j.Logger logger, @DataDirectory Path dataDirectory) {
        this.core = new CubesideUtilsVelocity(this, server, logger, dataDirectory);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        core.onEnable();
    }
}
