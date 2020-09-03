package io.github.samipourquoi.syncmatica.util;

import java.io.InputStream;
import java.security.MessageDigest;

public class SyncmaticaUtil {
	
	public static byte[] createChecksum(InputStream fis) throws Exception {
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
	    return complete.digest();
	}
}
