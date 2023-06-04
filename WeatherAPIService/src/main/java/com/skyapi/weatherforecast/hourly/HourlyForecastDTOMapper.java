package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.common.entity.HourlyWeather;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class HourlyForecastDTOMapper implements Function<HourlyWeather, HourlyForecastDTO> {

    @Override
    public HourlyForecastDTO apply(final HourlyWeather hourlyWeather) {
        return new HourlyForecastDTO(
                hourlyWeather.getHourlyWeatherId().getHourOfDay(),
                hourlyWeather.getTemperature(),
                hourlyWeather.getPrecipitation(),
                hourlyWeather.getStatus());
    }
}
