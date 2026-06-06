package de.iani.cubesideutils.plugin;

import java.time.Instant;
import java.util.UUID;

public record OtpData(byte[] key, UUID player, Instant validUntil, String payload) {}
