package de.iani.cubesideutils.plugin;

import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.RandomUtil;
import de.iani.cubesideutils.plugin.api.PasswordHandler;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHandlerImpl implements PasswordHandler {

    public static final int SALT_LENGTH = 32;
    public static final int ITERATION_COUNT = 65536;
    public static final int HASH_LENGTH = 128;

    private static final SecretKeyFactory FACTORY;

    static {
        try {
            FACTORY = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String key;

    public PasswordHandlerImpl(String key) {
        if (key.length() > MAX_KEY_LENGTH || key.isEmpty()) {
            throw new IllegalArgumentException("key may neither be empty nor longer than " + MAX_KEY_LENGTH);
        }
        this.key = key;
    }

    @Override
    public boolean test(UUID holderId, String password) throws SQLException {
        Pair<byte[], byte[]> entry = CubesideUtils.getInstance().getDatabase().getPasswordEntry(this.key, holderId);
        if (entry == null) {
            return false;
        }

        byte[] hash = hash(password, entry.first);
        return Arrays.equals(hash, entry.second);
    }

    @Override
    public void set(UUID holderId, String password) throws SQLException {
        byte[] salt = new byte[SALT_LENGTH];
        RandomUtil.SHARED_SECURE_RANDOM.nextBytes(salt);
        byte[] hash = hash(password, salt);

        CubesideUtils.getInstance().getDatabase().setPasswordHash(key, holderId, salt.clone(), hash.clone());
    }

    @Override
    public void remove(UUID holderId) throws SQLException {
        CubesideUtils.getInstance().getDatabase().removePassword(key, holderId);
    }

    private byte[] hash(String password, byte[] salt) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, HASH_LENGTH);
        try {
            return FACTORY.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

}
