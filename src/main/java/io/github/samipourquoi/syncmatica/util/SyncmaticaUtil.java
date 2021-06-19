package io.github.samipourquoi.syncmatica.util;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.UUID;

public class SyncmaticaUtil {

    public static UUID createChecksum(final InputStream fis) throws Exception {
        // source StackOverflow
        final byte[] buffer = new byte[4096]; // 4096 is the most common cluster size
        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                messageDigest.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return UUID.nameUUIDFromBytes(messageDigest.digest());
    }
}
