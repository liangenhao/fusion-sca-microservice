package io.fusion.common.conditionquery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");
    
    public static String camelToUnderline(String str) {
        if (str == null || str.trim().isEmpty()) {
            return "";
        }
        Matcher matcher = HUMP_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}