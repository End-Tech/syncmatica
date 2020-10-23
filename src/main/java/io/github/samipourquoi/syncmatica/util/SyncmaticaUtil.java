package io.github.samipourquoi.syncmatica.util;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.UUID;

public class SyncmaticaUtil {
	
	public static UUID createChecksum(InputStream fis) throws Exception {
		// source StackOverflow
	    byte[] buffer = new byte[4096]; // 4096 is the most common cluster size
	    MessageDigest complete = MessageDigest.getInstance("MD5");
	    int numRead;

	    do {
	        numRead = fis.read(buffer);
	        if (numRead > 0) {
	        	complete.update(buffer, 0, numRead);
	        }
	    } while (numRead != -1);

	    fis.close();
	    return UUID.nameUUIDFromBytes(complete.digest());
	}
}
