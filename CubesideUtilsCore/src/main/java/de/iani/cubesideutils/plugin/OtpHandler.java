package de.iani.cubesideutils.plugin;

import java.util.UUID;

public interface OtpHandler {

    static final int MAX_KEY_LENGTH = 64;

    boolean testAndReset(UUID holderId, String password);

    String generate(UUID holderId, int length);

}