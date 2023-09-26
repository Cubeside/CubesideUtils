package de.iani.cubesideutils.sql;

import java.sql.SQLException;

public class MariaDBConnection extends SQLConnection {

    public MariaDBConnection(String host, String database, String user, String password) throws SQLException {
        super("jdbc:mariadb://" + host + "/" + database, database, user, password);
    }

    public MariaDBConnection(SQLConfig config) throws SQLException {
        this(config.getHost(), config.getDatabase(), config.getUser(), config.getPassword());
    }

}
