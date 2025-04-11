package de.iani.cubesideutils.plugin;

import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.RandomUtil;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper.MessageType;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class OtpHandlerImpl implements OtpHandler {

    public static final int ITERATION_COUNT = 10000;
    public static final int HASH_LENGTH = 64;

    private static final SecretKeyFactory FACTORY;

    static {
        try {
            FACTORY = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String key;

    private Map<UUID, byte[]> currentOtps;

    public OtpHandlerImpl(String key) {
        if (key.length() > MAX_KEY_LENGTH || key.isEmpty()) {
            throw new IllegalArgumentException("key may neither be empty nor longer than " + MAX_KEY_LENGTH);
        }
        this.key = key;
        this.currentOtps = new ConcurrentHashMap<>();

        CubesideUtils.getInstance().getGlobalDataHelper().registerHandler(MessageType.OTP_CHANGED, this::handleMessage);
    }

    private void handleMessage(GlobalServer source, DataInputStream data) {
        try {
            String messageKey = data.readUTF();
            if (!key.equals(messageKey)) {
                return;
            }

            boolean created = data.readBoolean();
            UUID holderId = GlobalDataHelperBaseImpl.readUUID(data);
            if (created) {
                currentOtps.put(holderId, data.readAllBytes());
            } else {
                currentOtps.remove(holderId);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean testAndReset(UUID holderId, String password) {
        byte[] otp = currentOtps.get(holderId);
        if (otp == null) {
            return false;
        }

        if (Arrays.equals(otp, hash(password))) {
            CubesideUtils.getInstance().getGlobalDataHelper().sendData(false, MessageType.OTP_CHANGED, key, false, holderId);
            currentOtps.remove(holderId);
            return true;
        }
        return false;
    }

    @Override
    public String generate(UUID holderId, int length) {
        String result = RandomUtil.generateRandomAlphaNumericalString(length);
        byte[] otp = hash(result);
        currentOtps.put(holderId, otp);
        CubesideUtils.getInstance().getGlobalDataHelper().sendData(false, MessageType.OTP_CHANGED, key, true, holderId, otp);
        return result;
    }

    private byte[] hash(String password) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), new byte[] { 0 }, ITERATION_COUNT, 8 * HASH_LENGTH);
        try {
            return FACTORY.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

}
