package de.iani.cubesideutils.plugin;

import de.cubeside.connection.GlobalServer;
import de.iani.cubesideutils.RandomUtil;
import de.iani.cubesideutils.plugin.UtilsGlobalDataHelper.MessageType;
import de.iani.cubesideutils.primitivewrapper.ComparableByteArray;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class OtpHandlerImpl implements OtpHandler {

    public static final int ITERATION_COUNT = 10000;
    public static final int KEY_LENGTH = 12;
    public static final int HASH_LENGTH = 64;

    private static final SecretKeyFactory FACTORY;
    private volatile int cleanupCounter;

    static {
        try {
            FACTORY = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String key;

    private ConcurrentHashMap<ComparableByteArray, OtpData> currentOtps;

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
            int otpLength = data.readInt();
            byte[] otp = new byte[otpLength];
            data.readFully(otp);
            if (created) {
                UUID holderId = GlobalDataHelperBaseImpl.readUUID(data);
                Instant validUntil = Instant.ofEpochSecond(data.readLong(), data.readInt());
                String payload = data.readBoolean() ? data.readUTF() : null;
                OtpData otpData = new OtpData(otp, holderId, validUntil, payload);
                currentOtps.put(new ComparableByteArray(otp), otpData);
                checkForCleanup();
            } else {
                currentOtps.remove(new ComparableByteArray(otp));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkForCleanup() {
        if (++cleanupCounter > 1000) {
            cleanupCounter = 0;
            Instant now = Instant.now();
            currentOtps.values().removeIf(data -> data.validUntil().isBefore(now));
        }
    }

    @Override
    public OtpData testAndReset(String password) {
        checkForCleanup();
        byte[] otp = hash(password);
        OtpData data = currentOtps.remove(new ComparableByteArray(otp));
        if (data == null) {
            return null;
        }
        CubesideUtils.getInstance().getGlobalDataHelper().sendData(false, MessageType.OTP_CHANGED, key, false, otp.length, otp);
        if (data.validUntil().isBefore(Instant.now())) {
            return null;
        }
        return data;
    }

    @Override
    public String generate(UUID holderId, Duration validity) {
        return generate(holderId, validity, null);
    }

    @Override
    public String generate(UUID holderId, Duration validity, String payload) {
        String result = RandomUtil.generateRandomAlphaNumericalString(KEY_LENGTH);
        byte[] otp = hash(result);
        OtpData data = new OtpData(otp, holderId, Instant.now().plus(validity), payload);
        currentOtps.put(new ComparableByteArray(otp), data);
        if (data.payload() == null) {
            CubesideUtils.getInstance().getGlobalDataHelper().sendData(false, MessageType.OTP_CHANGED, key, true, otp.length, otp, holderId, data.validUntil().getEpochSecond(), data.validUntil().getNano(), false);
        } else {
            CubesideUtils.getInstance().getGlobalDataHelper().sendData(false, MessageType.OTP_CHANGED, key, true, otp.length, otp, holderId, data.validUntil().getEpochSecond(), data.validUntil().getNano(), true, data.payload());
        }
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
