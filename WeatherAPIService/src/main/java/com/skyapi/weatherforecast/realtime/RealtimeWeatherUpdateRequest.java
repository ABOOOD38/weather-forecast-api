package com.skyapi.weatherforecast.realtime;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

public record RealtimeWeatherUpdateRequest(
        @Range(min = -50, max = 50, message = "Temperature must be in the range of -50 to 50 Celsius degree")
        Integer temperature,
        @Range(min = 0, max = 100, message = "Humidity must be in the range of 0 to 100 percentage")
        Integer humidity,
        @Range(min = 0, max = 100, message = "Precipitation must be in the range of 0 to 100 percentage")
        Integer precipitation,
        @NotBlank(message = "Status must not be empty")
        @Length(min = 3, max = 50, message = "Status must be in between 3-50 characters")
        String status,
        @JsonProperty("wind_speed")
        @Range(min = 0, max = 200, message = "Wind speed must be in the range of 0 to 200 km/h")
        Integer windSpeed
) {
}
