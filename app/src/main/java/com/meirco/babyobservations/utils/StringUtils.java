package com.meirco.babyobservations.utils;

/**
 * Created by nitsa_000 on 10-Aug-15.
 */
public class StringUtils {
    public static final String EMPTY_STRING = "";

    public static boolean isNullOrEmpty(String s) {
        if (null == s) {
            return true;
        }
        return s.isEmpty();
    }
}
