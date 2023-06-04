package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.common.entity.HourlyWeather;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class HourlyWeatherDTOMapper implements Function<List<HourlyWeather>, HourlyWeatherDTO> {

    private final HourlyForecastDTOMapper hourlyForecastDTOMapper;

    @Override
    public HourlyWeatherDTO apply(final List<HourlyWeather> hourlyWeatherList) {

        final String locationAsString = hourlyWeatherList.stream()
                .findAny()
                .orElseThrow()
                .getHourlyWeatherId()
                .getLocation()
                .toString();

        return new HourlyWeatherDTO(
                locationAsString,
                hourlyWeatherList
                        .stream()
                        .map(hourlyForecastDTOMapper)
                        .toList()
        );
    }
}
