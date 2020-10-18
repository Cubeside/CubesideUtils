package de.iani.cubesideutils.plugin.api;

import java.sql.SQLException;
import java.util.UUID;

public interface PasswordHandler {

    public static final int MAX_KEY_LENGTH = 64;

    public boolean test(UUID holderId, String password) throws SQLException;

    public void set(UUID holderId, String password) throws SQLException;

    public void remove(UUID holderId) throws SQLException;

}
