package com.itgnostic.test4sandbox.utils;

import org.apache.logging.log4j.util.Strings;
import org.h2.util.StringUtils;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestApiUtils {
    private static final String REGEX_PARAMS = "(\\w+,?)+";
    private static final String REGEX_INT_PARAMS = "(\\d+,?)+";
    public static String[] getParamsAsArray(String paramValue, boolean onlyUnique) {
        return !StringUtils.isNullOrEmpty(paramValue) && paramValue.matches(REGEX_PARAMS)
                ? split2String(paramValue, onlyUnique)
                : new String[0];
    }

    public static long[] getLongParamsAsArray(String paramValue, boolean onlyUnique) {
        return !StringUtils.isNullOrEmpty(paramValue) && paramValue.matches(REGEX_INT_PARAMS)
                ? split2Long(paramValue, onlyUnique)
                : new long[0];
    }

    public static Set<String> split2SetString(String paramValue) {
        return Stream.of(paramValue.split(",")).collect(Collectors.toSet());
    }

    public static Set<Long> split2SetLong(String paramValue) {
        return Strings.isNotBlank(paramValue) && paramValue.matches("(\\d+,?)+")
                ? Stream.of(paramValue.split(",")).mapToLong(Long::parseLong).boxed().collect(Collectors.toSet())
                : Collections.emptySet();
    }

    public static Long parseLong(String paramValue) {
        return Strings.isBlank(paramValue) || !paramValue.matches("\\d+")
                ? null
                : Long.parseLong(paramValue);
    }

    private static String[] split2String(String paramValue, boolean onlyUnique) {
        return onlyUnique
                ? Stream.of(paramValue.split(",")).distinct().toArray(String[]::new)
                : paramValue.split(",");
    }

    private static long[] split2Long(String paramValue, boolean onlyUnique) {
        return onlyUnique
                ? Stream.of(paramValue.split(",")).mapToLong(Long::parseLong).distinct().toArray()
                : Stream.of(paramValue.split(",")).mapToLong(Long::parseLong).toArray();
    }

}
