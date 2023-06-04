package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.entity.Location;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class LocationDTOMapper implements Function<Location, LocationDTO> {

    @Override
    public LocationDTO apply(Location location) {
        return new LocationDTO(
                location.getCode(),
                location.getCityName(),
                location.getRegionName(),
                location.getCountryName(),
                location.getCountryCode(),
                location.getEnabled()
        );
    }
}
