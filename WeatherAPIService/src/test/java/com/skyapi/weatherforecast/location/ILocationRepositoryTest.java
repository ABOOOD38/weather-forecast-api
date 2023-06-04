package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.AbstractTestContainers;
import com.skyapi.weatherforecast.common.entity.HourlyWeather;
import com.skyapi.weatherforecast.common.entity.Location;
import com.skyapi.weatherforecast.common.entity.RealtimeWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:db/V1_schema.sql", "classpath:db/clear_all.sql", "classpath:db/weather-forecast-db.sql"})
class ILocationRepositoryTest extends AbstractTestContainers {

    @Autowired
    private ILocationRepository underTest;


    @Test
    void testListAllUnTrashed_shouldSuccess() {
        final List<Location> locations = underTest.findAllByTrashedIsFalse();

        assertThat(locations.size()).isGreaterThan(0);

        locations.forEach(System.out::println);
    }

    @Test
    void testGetByCode_notFoundCode_shouldReturnEmptyOptional() {
        final String code = "ABCD";

        final Optional<Location> notFoundLoc = underTest.findByCodeAndTrashedIsFalse(code);

        assertThat(notFoundLoc.isPresent()).isFalse();
    }

    @Test
    void testGetByCode_existingCode_shouldReturnNonEmptyOptional() {
        final String code = "DELHI_IN";

        final Optional<Location> foundLoc = underTest.findByCodeAndTrashedIsFalse(code);

        assertThat(foundLoc.isPresent()).isTrue();
    }

    @Test
    void testTrashedSuccess() {
        final String code = "NYC_USA";

        underTest.trashByCode(code);

        final Optional<Location> trashedLocation = underTest.findByCodeAndTrashedIsFalse(code);

        assertThat(trashedLocation.isPresent()).isFalse();
    }

    @Test
    void testAddRealtimeWeatherData() {
        final String delhiInCode = "DELHI_IN";

        final Location delhiInLocation = underTest.findById(delhiInCode).orElseThrow();

        final RealtimeWeather delhiInRealtimeWeather = new RealtimeWeather();

        delhiInRealtimeWeather.setLocation(delhiInLocation);
        delhiInLocation.setRealTimeWeather(delhiInRealtimeWeather);

        delhiInRealtimeWeather.setTemperature(10);
        delhiInRealtimeWeather.setHumidity(60);
        delhiInRealtimeWeather.setPrecipitation(70);
        delhiInRealtimeWeather.setStatus("Sunny");
        delhiInRealtimeWeather.setWindSpeed(10);

        final Location updatedDelhiInLocation = underTest.save(delhiInLocation);

        assertThat(updatedDelhiInLocation.getRealTimeWeather().getLocationCode()).isEqualTo(delhiInCode);
    }

    @Test
    void testAddHourlyWeatherData() {
        final String mumbaiCode = "MBMH_IN";

        final Location mumbaiLocation = underTest.findById(mumbaiCode)
                .orElseThrow();

        final Set<HourlyWeather> hourlyWeatherSet = mumbaiLocation.getHourlyWeatherSet();

        final HourlyWeather forecast1 = HourlyWeather.builder()
                .hourlyWeatherId(new HourlyWeather.HourlyWeatherId(1, mumbaiLocation))
                .temperature(20)
                .precipitation(60)
                .status("Cloudy")
                .build();

        final HourlyWeather forecast2 = HourlyWeather.builder()
                .hourlyWeatherId(new HourlyWeather.HourlyWeatherId(2, mumbaiLocation))
                .temperature(21)
                .precipitation(62)
                .status("Cloudy")
                .build();

        hourlyWeatherSet.add(forecast1);
        hourlyWeatherSet.add(forecast2);

        final Location updatedMumbaiLocation = underTest.save(mumbaiLocation);

        assertThat(updatedMumbaiLocation).isNotNull();
        updatedMumbaiLocation.getHourlyWeatherSet()
                .forEach(System.out::println);
    }

    @Test
    void testFindByCodeAndCityNameAndTrashedIsFalse_invalidCountryCodeAndName_shouldReturnEmptyOptional() {
        final String countryCode = "BlaBla_code";
        final String cityName = "BlaBla_city";

        final Optional<Location> notFoundLoc = underTest.findByCountryCodeAndCityNameAndTrashedIsFalse(countryCode, cityName);

        assertThat(notFoundLoc).isNotPresent();
    }

    @Test
    void testFindByCodeAndCityNameAndTrashedIsFalse_validCountryCodeAndNameAndNotTrashed_shouldReturnNonEmptyOptional() {
        final String countryCode = "IN";
        final String cityName = "Mumbai";

        final Optional<Location> foundLoc = underTest.findByCountryCodeAndCityNameAndTrashedIsFalse(countryCode, cityName);

        assertThat(foundLoc).isPresent();
    }

    @Test
    void testFindByCodeAndCityNameAndTrashedIsFalse_validCountryCodeAndNameAndTrashed_shouldReturnEmptyOptional() {
        final String countryCode = "JO";
        final String cityName = "AMMAN";

        final Optional<Location> notFoundLoc = underTest.findByCountryCodeAndCityNameAndTrashedIsFalse(countryCode, cityName);

        assertThat(notFoundLoc).isNotPresent();
    }
}