package com.jaspercloud.http.gateway.util;

import java.util.regex.Pattern;

public final class MatcherUtil {

    private MatcherUtil() {

    }

    public static boolean match(String pattern, String value) {
        String p = pattern.replaceAll("\\*", "(.+)");
        boolean matches = Pattern.matches(p, value);
        return matches;
    }
}
