package de.iani.cubesideutils.plugin;

import java.time.Duration;
import java.util.UUID;

public interface OtpHandler {

    static final int MAX_KEY_LENGTH = 64;

    OtpData testAndReset(String password);

    String generate(UUID holderId, Duration validity);

    String generate(UUID holderId, Duration validity, String payload);

}