package de.iani.cubesideutils.sql;

import java.sql.SQLException;

public class MySQLConnection extends SQLConnection {

    public MySQLConnection(String host, String database, String user, String password) throws SQLException {
        super("jdbc:mysql://" + host + "/" + database, database, user, password);
    }

    public MySQLConnection(SQLConfig config) throws SQLException {
        this(config.getHost(), config.getDatabase(), config.getUser(), config.getPassword());
    }

}
