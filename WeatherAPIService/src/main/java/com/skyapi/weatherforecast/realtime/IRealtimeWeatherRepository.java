package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.entity.RealtimeWeather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface IRealtimeWeatherRepository extends JpaRepository<RealtimeWeather, String> {

    Optional<RealtimeWeather> findByLocationCountryCodeAndLocationCityName(final String countryCode, final String city);

    Optional<RealtimeWeather> findByLocationCodeAndLocationTrashedIsFalse(final String locationCode);

}
