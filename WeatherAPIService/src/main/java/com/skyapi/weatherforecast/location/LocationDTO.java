package com.skyapi.weatherforecast.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"code", "cityName", "regionName", "countryName", "countryCode", "enabled"})
public record LocationDTO(
        @JsonProperty("code")
        String code,
        @JsonProperty("city_name")
        String cityName,
        @JsonProperty("region_name")
        String regionName,
        @JsonProperty("country_name")
        String countryName,
        @JsonProperty("country_code")
        String countryCode,
        @JsonProperty("enabled")
        Boolean enabled) {
}
