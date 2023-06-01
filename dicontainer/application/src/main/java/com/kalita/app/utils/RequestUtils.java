package com.kalita.app.utils;

import java.util.Arrays;

public class RequestUtils {

    public static String getParamValue(String[] splitParams, String paramName) {
        String param = Arrays.stream(splitParams).filter(s -> s.startsWith(paramName))
                .findFirst().orElseThrow();
        return param.substring(param.indexOf("=") + 1);
    }
}
