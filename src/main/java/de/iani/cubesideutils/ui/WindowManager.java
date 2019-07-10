package de.iani.cubesideutils.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class WindowManager implements Listener {

    private static WindowManager INSTANCE;

    public static WindowManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new WindowManager();
        return INSTANCE;
    }

    private Set<Window<?>> loadedWindows;
    private Map<Player, Window<?>> openWindows;

    private WindowManager() {
        this.loadedWindows = new HashSet<>();
        this.openWindows = new HashMap<>();
    }

}
