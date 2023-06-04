package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.common.entity.HourlyWeather;
import com.skyapi.weatherforecast.common.entity.Location;
import com.skyapi.weatherforecast.exception.InvalidResourceProvidedException;
import com.skyapi.weatherforecast.exception.NoDataAvailableException;
import com.skyapi.weatherforecast.exception.ResourceNotFoundException;
import com.skyapi.weatherforecast.location.ILocationRepository;
import com.skyapi.weatherforecast.util.MappingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class HourlyWeatherService {

    private final IHourlyWeatherRepository hourlyWeatherRepository;

    private final ILocationRepository locationRepository;

    private final HourlyWeatherDTOMapper hourlyWeatherDTOMapper;

    public HourlyWeatherDTO getByLocationAndCurrentHour(final Location location, final Integer currentHour) {

        final String countryCode = location.getCountryCode();

        final String cityName = location.getCityName();

        final String locationCode =
                getLocationCodeByCountryCodeAndCityName(countryCode, cityName);

        return getHourlyWeatherByLocationCodeAndCurrentHour(locationCode, currentHour);
    }

    public HourlyWeatherDTO getByLocationCodeAndCurrentHour(final String locationCode, final Integer currentHour) {
        getLocationByCode(locationCode);

        return getHourlyWeatherByLocationCodeAndCurrentHour(locationCode, currentHour);
    }

    private HourlyWeatherDTO getHourlyWeatherByLocationCodeAndCurrentHour(final String code, final Integer currentHour) {

        final List<HourlyWeather> hourlyWeatherList = hourlyWeatherRepository
                .findAllByLocationCodeAndAfterCurrentHour(code, currentHour);

        if (hourlyWeatherList.isEmpty())
            throw new NoDataAvailableException(
                    "No Hourly Weather data found for this location right now, check later"
            );

        return hourlyWeatherDTOMapper.apply(hourlyWeatherList);
    }

    public HourlyWeatherDTO replaceByLocationCode(
            final String locationCode,
            final List<HourlyForecastDTO> hourlyForecastDTOList
    ) {

        throwExceptionIfEmptyListProvided(hourlyForecastDTOList);

        final Location locationByCode = getLocationByCode(locationCode);

        final List<HourlyWeather> updatedHourlyWeatherList = MappingUtil
                .listOfHourlyForecastDTO2listOfHourlyWeatherEntity(hourlyForecastDTOList);

        for (final HourlyWeather hw : updatedHourlyWeatherList) {
            hw.getHourlyWeatherId().setLocation(locationByCode);
        }

        final Set<HourlyWeather> oldHourlyWeatherSet = locationByCode.getHourlyWeatherSet();

        oldHourlyWeatherSet.removeIf(oldHourlyWeather -> !updatedHourlyWeatherList.contains(oldHourlyWeather));

        final var savedHourlyWeatherList = hourlyWeatherRepository.saveAll(updatedHourlyWeatherList);

        return hourlyWeatherDTOMapper.apply(savedHourlyWeatherList);
    }

    private Location getLocationByCode(final String locationCode) {
        return locationRepository.findByCodeAndTrashedIsFalse(locationCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "No Location found with given location code: {%s}".formatted(locationCode)
                        )
                );
    }

    private String getLocationCodeByCountryCodeAndCityName(final String countryCode, final String cityName) {
        return locationRepository
                .findByCountryCodeAndCityNameAndTrashedIsFalse(countryCode, cityName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No Location found with given country code {%s} and city name {%s}".formatted(countryCode, cityName))
                )
                .getCode();
    }

    private void throwExceptionIfEmptyListProvided(final List<HourlyForecastDTO> hourlyForecastDTOList) {
        if (hourlyForecastDTOList.isEmpty())
            throw new InvalidResourceProvidedException(
                    "The provided forecast list cannot be empty!"
            );
    }
}
