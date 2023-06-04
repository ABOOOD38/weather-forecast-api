package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.entity.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LocationDTOMapperTest {

    private final LocationDTOMapper mapper = new LocationDTOMapper();

    @Test
    @DisplayName("test apply() method for LocationDTOMapper")
    void testApply() {
        Location testLocation = new Location(
                "NYC_USA",
                "Ney York City",
                "New York",
                "United States of America",
                "US",
                true,
                false);

        LocationDTO testLocationDTO = mapper.apply(testLocation);

        assertThat(testLocationDTO.code()).isEqualTo(testLocation.getCode());
        assertThat(testLocationDTO.cityName()).isEqualTo(testLocation.getCityName());
        assertThat(testLocationDTO.regionName()).isEqualTo(testLocation.getRegionName());
        assertThat(testLocationDTO.countryName()).isEqualTo(testLocation.getCountryName());
        assertThat(testLocationDTO.countryCode()).isEqualTo(testLocation.getCountryCode());
        assertThat(testLocationDTO.enabled()).isEqualTo(testLocation.getEnabled());
    }
}