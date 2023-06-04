package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.AbstractTestContainers;
import com.skyapi.weatherforecast.common.entity.HourlyWeather;
import com.skyapi.weatherforecast.common.entity.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:db/V1_schema.sql", "classpath:db/clear_all.sql", "classpath:db/weather-forecast-db.sql"})
class IHourlyWeatherRepositoryTest extends AbstractTestContainers {

    @Autowired
    private IHourlyWeatherRepository underTest;

    @Test
    void testAdd() {
        final String losAngelosCode = "LACA_USA";

        final Integer hourOfDay = 12;

        final Location losAngelosLocation = Location.builder().code(losAngelosCode).build();

        final var forecast1 = HourlyWeather.builder()
                .hourlyWeatherId(new HourlyWeather.HourlyWeatherId(hourOfDay, losAngelosLocation))
                .temperature(13)
                .precipitation(70)
                .status("Cloudy")
                .build();

        final var updatedForecast = underTest.save(forecast1);

        assertThat(updatedForecast.getHourlyWeatherId().getLocation().getCode()).isEqualTo(losAngelosCode);
        assertThat(updatedForecast.getHourlyWeatherId().getHourOfDay()).isEqualTo(hourOfDay);

        System.out.println("updatedForecast = " + updatedForecast);
    }

    @Test
    void testDelete() {
        final String losAngelosCode = "LACA_USA";

        final Integer hourOfDay = 12;

        final Location losAngelosLocation = Location.builder().code(losAngelosCode).build();

        final var hourlyWeatherId = new HourlyWeather.HourlyWeatherId(hourOfDay, losAngelosLocation);

        underTest.deleteById(hourlyWeatherId);

        Optional<HourlyWeather> res = underTest.findById(hourlyWeatherId);

        assertThat(res.isPresent()).isFalse();
    }

    @Test
    void testFindAllByLocationCodeAndAfterCurrentHour_foundLocationCode() {
        final String mumbaiCode = "MBMH_IN";

        final Integer currentHour = 1;

        final List<HourlyWeather> mumbaiHourlyWeather = underTest.findAllByLocationCodeAndAfterCurrentHour(mumbaiCode, currentHour);

        assertThat(mumbaiHourlyWeather.size()).isEqualTo(3);
        mumbaiHourlyWeather.forEach(System.out::println);
    }

    @Test
    void testFindAllByLocationCodeAndAfterCurrentHour_foundLocationCodeButNoDataAfterCurrentHour() {
        final String mumbaiCode = "MBMH_IN";

        final Integer currentHour = 15;

        final List<HourlyWeather> mumbaiHourlyWeather = underTest.findAllByLocationCodeAndAfterCurrentHour(mumbaiCode, currentHour);

        assertThat(mumbaiHourlyWeather.isEmpty()).isTrue();
    }

    @Test
    void testFindAllByLocationCodeAndAfterCurrentHour_notFoundLocationCode() {
        final String notFoundCode = "JPA";

        final Integer currentHour = 1;

        final List<HourlyWeather> emptyHourlyWeather = underTest.findAllByLocationCodeAndAfterCurrentHour(notFoundCode, currentHour);

        assertThat(emptyHourlyWeather.isEmpty()).isTrue();
    }

    @Test
    void testFindAllByLocationCodeAndAfterCurrentHour_locationIsTrashed() {
        final String trashedLocationCode = "AMMAN_JO";

        final Integer currentHour = 1;

        final List<HourlyWeather> emptyHourlyWeather = underTest.findAllByLocationCodeAndAfterCurrentHour(trashedLocationCode, currentHour);

        assertThat(emptyHourlyWeather.isEmpty()).isTrue();
    }
}