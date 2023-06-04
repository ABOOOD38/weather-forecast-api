package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.entity.RealtimeWeather;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class RealtimeWeatherDTOMapper implements Function<RealtimeWeather, RealtimeWeatherDTO> {

    @Override
    public RealtimeWeatherDTO apply(final RealtimeWeather realtimeWeather) {

        return new RealtimeWeatherDTO(
                realtimeWeather.getLocation().toString(),
                realtimeWeather.getTemperature(),
                realtimeWeather.getHumidity(),
                realtimeWeather.getPrecipitation(),
                realtimeWeather.getStatus(),
                realtimeWeather.getWindSpeed(),
                realtimeWeather.getLastUpdated()
        );
    }
}
