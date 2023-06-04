package com.skyapi.weatherforecast.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "weather_hourly")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@ToString
public class HourlyWeather {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private HourlyWeatherId hourlyWeatherId = new HourlyWeatherId();

    @Column(length = 12, nullable = false)
    private Integer temperature;

    @Column(length = 12, nullable = false)
    private Integer precipitation;

    @Column(length = 12, nullable = false)
    private String status;

    @Embeddable
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class HourlyWeatherId implements Serializable {

        private Integer hourOfDay;

        @ManyToOne
        @JoinColumn(name = "location_code")
        private Location location;
    }
}
