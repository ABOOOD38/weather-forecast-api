package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.entity.Location;
import com.skyapi.weatherforecast.common.entity.RealtimeWeather;
import com.skyapi.weatherforecast.exception.ResourceNotFoundException;
import com.skyapi.weatherforecast.geolocation.GeolocationService;
import com.skyapi.weatherforecast.location.ILocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RealtimeWeatherService {

    private final IRealtimeWeatherRepository realtimeWeatherRepository;

    private final ILocationRepository locationRepository;

    private final RealtimeWeatherDTOMapper realtimeWeatherDTOMapper;

    private final RealtimeWeatherUpdateRequestMapper updateRequestMapper;

    private final GeolocationService geolocationService;

    public RealtimeWeatherDTO getByIPAddress(final String ipAddress) {
        final Location locationByIPAddress = geolocationService.getLocationFromIPAddress(ipAddress);

        if (locationByIPAddress.getCountryCode().equals("-") || locationByIPAddress.getCityName().equals("-"))
            throw new ResourceNotFoundException("No Location found for this IP Address: {%s}".formatted(ipAddress));

        return getByCountryCodeAndCityName(locationByIPAddress.getCountryCode(), locationByIPAddress.getCityName());
    }

    public RealtimeWeatherDTO getByLocationCode(final String locationCode) {
        final RealtimeWeather realtimeWeatherByCode = realtimeWeatherRepository
                .findByLocationCodeAndLocationTrashedIsFalse(locationCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No data available for RealtimeWeather at location: {%s}, at this moment".formatted(locationCode))
                );

        return realtimeWeatherDTOMapper.apply(realtimeWeatherByCode);
    }

    private RealtimeWeatherDTO getByCountryCodeAndCityName(final String countryCode, final String cityName) {

        final RealtimeWeather realtimeWeather = realtimeWeatherRepository
                .findByLocationCountryCodeAndLocationCityName(countryCode, cityName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No Location found with given country code {%s} and city name {%s}".formatted(countryCode, cityName))
                );

        return realtimeWeatherDTOMapper.apply(realtimeWeather);
    }

    public RealtimeWeatherDTO replaceRealtimeWeatherInformation(final String locationCode, final RealtimeWeatherUpdateRequest updateRequest) {
        final Location codeLocation = locationRepository.findByCodeAndTrashedIsFalse(locationCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No Location found with given location code {%s}".formatted(locationCode)
                ));

        final RealtimeWeather updatedRealtimeWeather = updateRequestMapper.apply(updateRequest);

        updatedRealtimeWeather.setLocation(codeLocation);

        if (codeLocation.getRealTimeWeather() == null) {
            codeLocation.setRealTimeWeather(updatedRealtimeWeather);
            locationRepository.save(codeLocation);
        } else
            realtimeWeatherRepository.save(updatedRealtimeWeather);

        return realtimeWeatherDTOMapper.apply(updatedRealtimeWeather);
    }
}
