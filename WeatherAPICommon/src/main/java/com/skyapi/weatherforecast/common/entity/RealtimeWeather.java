package com.skyapi.weatherforecast.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "realtime_weather")
@Setter
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RealtimeWeather {

    @Id
    @Column(name = "location_code", length = 12, nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String locationCode;

    @Column(name = "temperature", length = 3, nullable = false)
    private Integer temperature;

    @Column(name = "humidity", length = 3, nullable = false)
    private Integer humidity;

    @Column(name = "precipitation", length = 3, nullable = false)
    private Integer precipitation;

    @Column(name = "wind_speed", length = 3, nullable = false)
    private Integer windSpeed;

    @Column(name = "status", length = 10, nullable = false)
    private String status;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "location_code")
    @MapsId
    private Location location;

    public void setLocation(Location location) {
        this.locationCode = location.getCode();
        this.location = location;
    }
}
