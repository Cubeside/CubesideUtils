package de.iani.cubesideutils.sql;

import org.bukkit.configuration.ConfigurationSection;

public class SQLConfig {

    private String host = "localhost";

    private String user = "CHANGETHIS";

    private String password = "CHANGETHIS";

    private String database = "CHANGETHIS";

    private String tableprefix = "cubeQuest";

    public SQLConfig(ConfigurationSection section) {
        if (section != null) {
            this.host = section.getString("host", this.host);
            this.user = section.getString("user", this.user);
            this.password = section.getString("password", this.password);
            this.database = section.getString("database", this.database);
            this.tableprefix = section.getString("tableprefix", this.tableprefix);
        }
    }

    public String getHost() {
        return this.host;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getTablePrefix() {
        return this.tableprefix;
    }
}
