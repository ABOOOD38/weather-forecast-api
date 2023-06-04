package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.entity.Location;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class LocationRequestMapper implements Function<LocationRequest, Location> {

    @Override
    public Location apply(LocationRequest request) {
        return new Location(
                request.code(),
                request.cityName(),
                request.regionName(),
                request.countryName(),
                request.countryCode(),
                request.enabled(),
                false
        );
    }
}
