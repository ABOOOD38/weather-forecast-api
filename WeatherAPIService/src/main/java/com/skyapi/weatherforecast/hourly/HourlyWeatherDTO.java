package com.skyapi.weatherforecast.hourly;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@JsonPropertyOrder(value = {"location", "hourly_forecast"})
@Setter
@Getter
@AllArgsConstructor
@ToString
public class HourlyWeatherDTO {

    private String location;

    @JsonProperty("hourly_forecast")
    private List<HourlyForecastDTO> hourlyForecast;
}
