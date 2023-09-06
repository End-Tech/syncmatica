package ch.endte.syncmatica.util;

import java.util.ArrayList;
import java.util.List;

public class StringTools {
    public static String getHexString(byte[] bytes) {
        List<String> list = new ArrayList<>();
        for (byte b : bytes) {
            list.add(String.format("%02x", b));
        }
        return String.join(" ", list);
    }
}
