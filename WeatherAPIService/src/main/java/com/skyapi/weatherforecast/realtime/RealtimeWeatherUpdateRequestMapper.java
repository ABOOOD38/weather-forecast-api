package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.entity.RealtimeWeather;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Function;

@Component
public class RealtimeWeatherUpdateRequestMapper implements
        Function<RealtimeWeatherUpdateRequest, RealtimeWeather> {

    @Override
    public RealtimeWeather apply(RealtimeWeatherUpdateRequest request) {

        return RealtimeWeather.builder()
                .temperature(request.temperature())
                .humidity(request.humidity())
                .precipitation(request.precipitation())
                .status(request.status())
                .windSpeed(request.windSpeed())
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}
