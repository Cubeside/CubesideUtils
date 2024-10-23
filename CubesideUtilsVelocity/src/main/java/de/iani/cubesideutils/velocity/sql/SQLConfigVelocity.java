package de.iani.cubesideutils.velocity.sql;

import de.iani.cubesideutils.sql.SQLConfig;
import net.md_5.bungee.config.Configuration;

public class SQLConfigVelocity extends SQLConfig {

    private static String get(Configuration section, String key) {
        return section == null ? null : section.getString(key, null);
    }

    public SQLConfigVelocity(Configuration section) {
        super(get(section, "host"), get(section, "user"), get(section, "password"), get(section, "database"), get(section, "tableprefix"));
    }
}
