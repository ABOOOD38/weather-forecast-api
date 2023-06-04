package com.skyapi.weatherforecast.util;

import com.skyapi.weatherforecast.hourly.HourlyForecastDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

class MappingUtilTest {

    @Test
    void listOfHourlyForecastDTO2listOfHourlyWeatherEntity() {
        final var testDTO1 = new HourlyForecastDTO(
                10,
                133,
                70,
                "Cloudy"
        );

        final var testDTO2 = new HourlyForecastDTO(
                11,
                15,
                60,
                "Sunny"
        );

        final List<HourlyForecastDTO> listDTO = List.of(testDTO1, testDTO2);

        MappingUtil.listOfHourlyForecastDTO2listOfHourlyWeatherEntity(listDTO)
                .forEach(System.out::println);


    }
}