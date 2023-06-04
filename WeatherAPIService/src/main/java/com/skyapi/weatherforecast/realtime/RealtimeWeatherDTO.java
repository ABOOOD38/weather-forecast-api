package com.skyapi.weatherforecast.realtime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder(value =
        {"location", "temperature", "humidity", "precipitation", "status", "wind_speed", "last_updated"}
)
public record RealtimeWeatherDTO(
        String location,
        Integer temperature,
        Integer humidity,
        Integer precipitation,
        String status,
        @JsonProperty("wind_speed")
        Integer windSpeed,
        @JsonProperty("last_updated")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime lastUpdated
) {
}
