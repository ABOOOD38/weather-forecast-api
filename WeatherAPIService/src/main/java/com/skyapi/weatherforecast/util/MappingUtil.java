package com.skyapi.weatherforecast.util;

import com.skyapi.weatherforecast.common.entity.HourlyWeather;
import com.skyapi.weatherforecast.hourly.HourlyForecastDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.ArrayList;
import java.util.List;

public class MappingUtil {
    private static final ModelMapper mapper = new ModelMapper();

    static {
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        var typeMap1 = mapper.typeMap(HourlyWeather.class, HourlyForecastDTO.class);
        typeMap1.addMapping(src -> src.getHourlyWeatherId().getHourOfDay(), HourlyForecastDTO::setHourOfDay);

        var typeMap2 = mapper.typeMap(HourlyForecastDTO.class, HourlyWeather.class);
        typeMap2.addMapping(HourlyForecastDTO::getHourOfDay, (
                (destination, value) ->
                        destination
                                .getHourlyWeatherId()
                                .setHourOfDay(value != null ? (int) value : 0))
        );
    }

    public static List<HourlyWeather> listOfHourlyForecastDTO2listOfHourlyWeatherEntity(final List<HourlyForecastDTO> hourlyForecastDTOList) {
        final var hourlyWeatherList_requested = new ArrayList<HourlyWeather>();

        hourlyForecastDTOList.forEach(
                forecastDTO -> hourlyWeatherList_requested.add(
                        mapper.map(forecastDTO, HourlyWeather.class)
                )
        );
        return hourlyWeatherList_requested;
    }
}
