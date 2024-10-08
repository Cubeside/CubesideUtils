package de.iani.cubesideutils.bukkit.sql;

import de.iani.cubesideutils.sql.SQLConfig;
import org.bukkit.configuration.ConfigurationSection;

public class SQLConfigBukkit extends SQLConfig {

    private static String get(ConfigurationSection section, String key) {
        return section == null ? null : section.getString(key, null);
    }

    public SQLConfigBukkit(ConfigurationSection section) {
        super(get(section, "host"), get(section, "user"), get(section, "password"), get(section, "database"), get(section, "tableprefix"));
    }
}
