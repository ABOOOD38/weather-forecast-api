package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.common.entity.HourlyWeather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IHourlyWeatherRepository extends JpaRepository<HourlyWeather, HourlyWeather.HourlyWeatherId> {

    @Query("""
            SELECT h FROM HourlyWeather h WHERE 
            h.hourlyWeatherId.location.code=:code AND 
            h.hourlyWeatherId.hourOfDay >:c_hour AND 
            h.hourlyWeatherId.location.trashed = false
            """)
    List<HourlyWeather> findAllByLocationCodeAndAfterCurrentHour(@Param("code") final String locationCode, @Param("c_hour") final Integer currentHour);
}
