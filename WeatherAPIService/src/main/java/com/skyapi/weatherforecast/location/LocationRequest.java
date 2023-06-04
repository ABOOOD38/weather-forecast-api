package com.skyapi.weatherforecast.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record LocationRequest(
        @Length(min = 3, max = 12, message = "Location code's length must be between 3 and 12")
        @NotNull(message = "Location code must not be null")
        @JsonProperty("code")
        String code,
        @Length(min = 3, max = 128, message = "city_name's length must be between 3 and 12")
        @NotNull(message = "city_name must not be null")
        @JsonProperty("city_name")
        String cityName,
        @JsonProperty("region_name")
        @Length(min = 3, max = 128, message = "region_name's length must be between 3 and 128")
        String regionName,
        @JsonProperty("country_name")
        @Length(min = 3, max = 64, message = "country_name's length must be between 3 and 64")
        @NotNull(message = "country_name must not be null")
        String countryName,
        @JsonProperty("country_code")
        @Length(min = 2, max = 2, message = "country_code's length must be 2")
        @NotNull(message = "country_code must not be null")
        String countryCode,
        @JsonProperty(value = "enabled", defaultValue = "true")
        Boolean enabled
) {
}
