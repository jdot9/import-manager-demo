package com.dotwavesoftware.importscheduler.shared.util;

import java.util.UUID;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ConversionUtil {
    
    /**
     * Converts a byte array (BINARY(16)) to UUID
     * @param bytes The byte array to convert (must be 16 bytes)
     * @return UUID object or null if bytes are invalid
     */
    public static UUID bytesToUuid(byte[] bytes) {
        if (bytes == null || bytes.length != 16) {
            return null;
        }
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (bytes[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (bytes[i] & 0xff);
        }
        return new UUID(msb, lsb);
    }

    /**
     * Converts UUID to byte array (BINARY(16)) for database storage
     * @param uuid The UUID to convert
     * @return Byte array (16 bytes) or null if UUID is null
     */
    public static byte[] uuidToBytes(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        byte[] bytes = new byte[16];
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (msb >>> (8 * (7 - i)));
        }
        for (int i = 8; i < 16; i++) {
            bytes[i] = (byte) (lsb >>> (8 * (7 - (i - 8))));
        }
        return bytes;
    }

    /**
     * Converts SQL Timestamp to LocalDateTime
     * @param timestamp The SQL timestamp to convert
     * @return LocalDateTime object or null if timestamp is null
     */
    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}
