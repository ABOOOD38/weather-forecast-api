package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.AbstractTestContainers;
import com.skyapi.weatherforecast.common.entity.RealtimeWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:db/V1_schema.sql", "classpath:db/clear_all.sql", "classpath:db/weather-forecast-db.sql"})
class IRealtimeWeatherRepositoryTest extends AbstractTestContainers {

    @Autowired
    private IRealtimeWeatherRepository underTest;

    @Test
    void testUpdate() {
        final String delhiInCode = "DELHI_IN";

        final RealtimeWeather realtimeWeatherDelhiInLoc = underTest.findById(delhiInCode).orElseThrow();

        realtimeWeatherDelhiInLoc.setTemperature(-2);
        realtimeWeatherDelhiInLoc.setHumidity(32);
        realtimeWeatherDelhiInLoc.setPrecipitation(42);
        realtimeWeatherDelhiInLoc.setWindSpeed(12);
        realtimeWeatherDelhiInLoc.setLastUpdated(LocalDateTime.now());

        final RealtimeWeather updatedRealtimeWeatherDelhiInLoc = underTest.save(realtimeWeatherDelhiInLoc);

        assertThat(updatedRealtimeWeatherDelhiInLoc.getHumidity()).isEqualTo(32);
        assertThat(updatedRealtimeWeatherDelhiInLoc.getTemperature()).isEqualTo(-2);
        assertThat(updatedRealtimeWeatherDelhiInLoc.getPrecipitation()).isEqualTo(42);
        assertThat(updatedRealtimeWeatherDelhiInLoc.getWindSpeed()).isEqualTo(12);
    }

    @Test
    void testFindByCountryCodeAndCityName_NotFound() {
        final String countryCode = "JP";

        final String cityName = "Tokyo";

        final Optional<RealtimeWeather> realtimeWeather =
                underTest.findByLocationCountryCodeAndLocationCityName(countryCode, cityName);

        assertThat(realtimeWeather.isPresent()).isFalse();
    }

    @Test
    void testFindByCountryCodeAndCityName_found() {
        final String countryCode = "US";

        final String cityName = "New York City";

        final Optional<RealtimeWeather> realtimeWeather =
                underTest.findByLocationCountryCodeAndLocationCityName(countryCode, cityName);

        assertThat(realtimeWeather.isPresent()).isTrue();
    }

    @Test
    public void testFindByLocationCodeAndTrashedIsFalse_locationCodeIsTrashed_shouldReturnEmptyOptional() {
        final String locationCode = "AMMAN_JO";

        final Optional<RealtimeWeather> realtimeWeather = underTest
                .findByLocationCodeAndLocationTrashedIsFalse(locationCode);

        assertThat(realtimeWeather.isPresent()).isFalse();
    }

    @Test
    public void testFindByLocationCodeAndTrashedIsFalse_locationCodeIsNotTrashed_shouldReturnNonEmptyOptional() {
        final String locationCode = "DELHI_IN";

        final Optional<RealtimeWeather> realtimeWeather = underTest
                .findByLocationCodeAndLocationTrashedIsFalse(locationCode);

        assertThat(realtimeWeather.isPresent()).isTrue();
    }

    @Test
    public void testFindByLocationCodeAndTrashedIsFalse_locationCodeIsNotFound_shouldReturnEmptyOptional() {
        final String locationCode = "ABC_EFG";

        final Optional<RealtimeWeather> realtimeWeather = underTest
                .findByLocationCodeAndLocationTrashedIsFalse(locationCode);

        assertThat(realtimeWeather.isPresent()).isFalse();
    }
}