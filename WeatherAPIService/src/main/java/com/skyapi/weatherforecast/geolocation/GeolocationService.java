package com.skyapi.weatherforecast.geolocation;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.skyapi.weatherforecast.common.entity.Location;
import com.skyapi.weatherforecast.exception.GeolocationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class GeolocationService {

    private final static String IP_DATABASE_PATH = "/home/stranger/IdeaProjects/WeatherForecastAPI/WeatherAPIService/src/main/resources/IP2LocationDB/IP2LOCATION-LITE-DB3.BIN";

    private final static IP2Location IP_LOCATOR = new IP2Location();

    static {
        try {
            IP_LOCATOR.Open(IP_DATABASE_PATH);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public Location getLocationFromIPAddress(final String ipAddress) {
        final IPResult ipResult;

        try {
            ipResult = IP_LOCATOR.IPQuery(ipAddress);
            if (!"OK".equals(ipResult.getStatus()))
                throw new GeolocationException("Geolocation failed with status: %s".formatted(ipResult.getStatus()));

        } catch (IOException ex) {
            throw new GeolocationException("Error querying IP database", ex);
        }

        return Location.builder()
                .cityName(ipResult.getCity())
                .regionName(ipResult.getRegion())
                .countryName(ipResult.getCountryLong())
                .countryCode(ipResult.getCountryShort())
                .build();
    }
}
