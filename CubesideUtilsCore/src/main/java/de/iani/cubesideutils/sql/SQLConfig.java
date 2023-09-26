package de.iani.cubesideutils.sql;

public class SQLConfig {

    private String host = "localhost";

    private String user = "CHANGETHIS";

    private String password = "CHANGETHIS";

    private String database = "CHANGETHIS";

    private String tableprefix = "CHANGETHIS";

    public SQLConfig() {
    }

    public SQLConfig(String host, String user, String password, String database, String tableprefix) {
        this.host = host == null ? this.host : host;
        this.user = user == null ? this.user : user;
        this.password = password == null ? this.password : password;
        this.database = database == null ? this.database : database;
        this.tableprefix = tableprefix == null ? this.tableprefix : tableprefix;
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
