package de.iani.cubesideutils.velocity.sql;

import de.iani.cubesideutils.sql.SQLConfig;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class SQLConfigVelocity extends SQLConfig {

    private static String get(CommentedConfigurationNode section, String key) {
        return section == null ? null : section.node(key).getString();
    }

    public SQLConfigVelocity(CommentedConfigurationNode section) {
        super(get(section, "host"), get(section, "user"), get(section, "password"), get(section, "database"), get(section, "tableprefix"));
    }
}
