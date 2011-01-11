package org.kabeja.svg.tools;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author michele franzin
 */
public class FontReplacer {

    private static Map<String, String> replaceTable = new HashMap<String, String>();

    public static void addReplacement(String from, String to) {
        replaceTable.put(from, to);
    }

    public static boolean hasReplacement(String font) {
        return replaceTable.containsKey(font);
    }

    public static String getReplacement(String font) {
        return replaceTable.get(font);
    }

    public static void clearReplacements() {
        replaceTable.clear();
    }
}
