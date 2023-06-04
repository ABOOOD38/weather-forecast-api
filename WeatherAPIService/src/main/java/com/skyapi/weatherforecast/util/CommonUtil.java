package com.skyapi.weatherforecast.util;

import com.skyapi.weatherforecast.exception.XCurrentHourHeaderException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtil {

    public static String getIPAddress(HttpServletRequest request) {
        //in case of load balancer
        String ip = request.getHeader("X-FORWARDED-FOR");

        if (ip == null || ip.isEmpty())
            ip = request.getRemoteAddr();

        log.info("Client's IP Address: %s".formatted(ip));
        return ip;
    }

    public static Integer getCurrentHour(HttpServletRequest request) {
        //in case the header is not provided
        final String xCurrentHeaderAsString = request.getHeader("X-Current-Hour");
        if (xCurrentHeaderAsString == null)
            throw new XCurrentHourHeaderException("X-Current-Hour is required, attach it");

        //in case the header is not numeric
        int currentHour;
        try {
            currentHour = Integer.parseInt(xCurrentHeaderAsString);
        } catch (NumberFormatException ex) {
            throw new XCurrentHourHeaderException("The provided value of X-Current-Hour cannot be String");
        }

        //in case the header is out of hour's range
        if (currentHour < 0 || currentHour > 24)
            throw new XCurrentHourHeaderException("X-Current-Hour must be in range 0 and 24 inclusive");

        return currentHour;
    }
}
