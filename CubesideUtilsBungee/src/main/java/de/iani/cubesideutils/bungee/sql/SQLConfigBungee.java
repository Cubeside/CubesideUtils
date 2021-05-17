package de.iani.cubesideutils.bungee.sql;

import de.iani.cubesideutils.sql.SQLConfig;
import net.md_5.bungee.config.Configuration;

public class SQLConfigBungee extends SQLConfig {

    private static String get(Configuration section, String key) {
        return section == null ? null : section.getString(key, null);
    }

    public SQLConfigBungee(Configuration section) {
        super(get(section, "host"), get(section, "user"), get(section, "password"), get(section, "database"), get(section, "tableprefix"));
    }
}
