package models;

import utils.PageUtil;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.UUID.randomUUID;

public class Memory {

    private static Map<String, Object> values = new HashMap<>();

    public static Pattern templatePattern = Pattern.compile("<<(.*?)>>");

    public static Boolean exists(String key) {
        return values.containsKey(key);
    }


    public static <T> T getValue(String key) {
        return (T) values.get(key);
    }

    public static void generateAndSetRandomValue(String key) {
        // example: 54947df8-0e9e-4471-a2f9-9af509fb5889
        setValue(key, randomUUID().toString());
    }

    public static void setValue(String key, Object value) {
        values.put("<<" + key + ">>", value);
    }

    public static boolean isATemplate(String text) {
        return templatePattern.matcher(text).find();
    }

    // Replaces any keys that exist in memory
    public static String replaceKeys(String text) {
        Matcher templateMatcher = templatePattern.matcher(text);
        StringBuilder output = new StringBuilder();
        int lastIndex = 0;

        while (templateMatcher.find()) {
            String foundTag = templateMatcher.group(1);

            // Apply everything before the found tag
            output.append(text, lastIndex, templateMatcher.start());

            // Check to see if the tag exists in memory
            if (Memory.exists("<<" + foundTag + ">>")) {
                // If it does, add the value from memory
                Object value = getValue("<<" + foundTag + ">>");
                if (value instanceof Instant) {
                    output.append(PageUtil.dateTimeFormatter.format((Instant) value));
                } else {
                    output.append(value.toString());
                }
            } else {
                // If not output the tag in the text
                output.append(foundTag);
            }

            // Jump to the end of the template string so we know we can look for the next one 
            lastIndex = templateMatcher.end();
        }
        // Add any of the original string we are missing
        if (lastIndex < text.length()) {
            output.append(text, lastIndex, text.length());
        }

        return output.toString();
    }

    public static void forgetAll() {
        values.clear();
    }
}
