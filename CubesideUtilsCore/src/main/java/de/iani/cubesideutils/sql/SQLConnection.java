package de.iani.cubesideutils.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class SQLConnection {
    private String connectURL;

    private Connection connection;

    private String user;

    private String password;

    private String database;

    private int maxTries;

    private HashMap<Object, PreparedStatement> cachedStatements;

    public SQLConnection(String connectURL, String database, String user, String password) throws SQLException {
        this(connectURL, database, user, password, (String) null);
    }

    public SQLConnection(String connectURL, String database, String user, String password, String driverClass) throws SQLException {
        this(connectURL, database, user, password, driverClass == null ? null : new String[] { driverClass });
    }

    public SQLConnection(String connectURL, String database, String user, String password, String... driverClasses) throws SQLException {
        if (driverClasses != null) {
            Exception exception = null;
            for (String driverClass : driverClasses) {
                try {
                    Class.forName(driverClass);
                    exception = null;
                    break;
                } catch (Exception e) {
                    exception = e;
                }
            }
            if (exception != null) {
                throw new SQLException(exception);
            }
        }
        this.connectURL = connectURL;
        this.user = user;
        this.password = password;
        this.database = database;
        this.cachedStatements = new HashMap<>();
        this.maxTries = 3;
        connect();
    }

    public synchronized void disconnect() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                // ignore
            }
            this.connection = null;
        }
        this.cachedStatements.clear();
    }

    private synchronized void connect() throws SQLException {
        disconnect();
        this.connection = user != null ? DriverManager.getConnection(connectURL, user, password) : DriverManager.getConnection(connectURL);
        this.connection.setAutoCommit(false);
    }

    public synchronized <T> T runCommands(SQLRunnable<T> runnable) throws SQLException {
        int fails = 0;
        while (true) {
            try {
                if (connection == null || connection.isClosed()) {
                    connect();
                }
                T rv = runnable.execute(connection, this);
                connection.commit();
                return rv;
            } catch (SQLException e) {
                fails += 1;
                if (connection != null) {
                    try {
                        if (!connection.isClosed()) {
                            connection.rollback();
                        }
                    } catch (SQLException ex) {
                        // ignore
                    }
                    try {
                        connection.close();
                    } catch (SQLException ex) {
                        // ignore
                    }
                    connection = null;
                }
                if (fails >= maxTries) {
                    throw e;
                }
            }
        }
    }

    public PreparedStatement getOrCreateStatement(String statement) throws SQLException {
        PreparedStatement smt = cachedStatements.get(statement);
        if (smt == null || smt.isClosed()) {
            smt = connection.prepareStatement(statement);
            cachedStatements.put(statement, smt);
        }
        return smt;
    }

    public PreparedStatement getOrCreateStatement(String statement, int autoGeneratedKeys) throws SQLException {
        PreparedStatement smt = cachedStatements.get(statement);
        if (smt == null || smt.isClosed()) {
            smt = connection.prepareStatement(statement, autoGeneratedKeys);
            cachedStatements.put(statement, smt);
        }
        return smt;
    }

    public PreparedStatement getOrCreateStatement(String statement, int resultSetType, int resultSetConcurrency) throws SQLException {
        PreparedStatement smt = cachedStatements.get(statement);
        if (smt == null || smt.isClosed()) {
            smt = connection.prepareStatement(statement, resultSetType, resultSetConcurrency);
            cachedStatements.put(statement, smt);
        }
        return smt;
    }

    public boolean hasTable(final String table) throws SQLException {
        return hasTable(this.database, table);
    }

    public boolean hasTable(final String database, final String table) throws SQLException {
        return runCommands(new SQLRunnable<Boolean>() {
            @Override
            public Boolean execute(Connection connection, SQLConnection sqlConnection) throws SQLException {
                PreparedStatement smt = sqlConnection.getOrCreateStatement("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?");
                smt.setString(1, database);
                smt.setString(2, table);
                ResultSet rs = smt.executeQuery();
                boolean rv = false;
                if (rs.next()) {
                    rv = rs.getInt(1) > 0;
                }
                rs.close();
                return rv;
            }
        });
    }

    public boolean hasColumn(final String table, final String column) throws SQLException {
        return hasColumn(this.database, table, column);
    }

    public boolean hasColumn(final String database, final String table, final String column) throws SQLException {
        return runCommands(new SQLRunnable<Boolean>() {
            @Override
            public Boolean execute(Connection connection, SQLConnection sqlConnection) throws SQLException {
                PreparedStatement smt = sqlConnection.getOrCreateStatement("SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = ? AND table_name = ? AND column_name = ?");
                smt.setString(1, database);
                smt.setString(2, table);
                smt.setString(3, column);
                ResultSet rs = smt.executeQuery();
                boolean rv = false;
                if (rs.next()) {
                    rv = rs.getInt(1) > 0;
                }
                rs.close();
                return rv;
            }
        });
    }
}
