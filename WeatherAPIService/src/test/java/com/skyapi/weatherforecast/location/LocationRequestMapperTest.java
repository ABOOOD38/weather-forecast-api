package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.entity.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LocationRequestMapperTest {

    private final LocationRequestMapper mapper = new LocationRequestMapper();

    @Test
    @DisplayName("test apply() method for LocationRequestMapper")
    void testApply() {
        LocationRequest testLocationRequest = new LocationRequest(
                "NYC_USA",
                "Ney York City",
                "New York",
                "United States of America",
                "US",
                true);

        Location testLocation = mapper.apply(testLocationRequest);

        assertThat(testLocation.getCode()).isEqualTo(testLocationRequest.code());
        assertThat(testLocation.getCityName()).isEqualTo(testLocationRequest.cityName());
        assertThat(testLocation.getRegionName()).isEqualTo(testLocationRequest.regionName());
        assertThat(testLocation.getCountryName()).isEqualTo(testLocationRequest.countryName());
        assertThat(testLocation.getCountryCode()).isEqualTo(testLocationRequest.countryCode());
        assertThat(testLocation.getEnabled()).isEqualTo(testLocationRequest.enabled());
    }
}