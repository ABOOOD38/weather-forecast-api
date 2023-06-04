package com.skyapi.weatherforecast.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Location {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "code", length = 12, nullable = false, unique = true)
    private String code;

    @Column(name = "city_name", length = 128, nullable = false)
    private String cityName;

    @Column(name = "region_name", length = 128)
    private String regionName;

    @Column(name = "country_name", length = 64, nullable = false)
    private String countryName;

    @Column(name = "country_code", length = 2, nullable = false)
    private String countryCode;

    @Column(name = "enabled")
    private Boolean enabled = true;

    @Column(name = "trashed")
    private Boolean trashed = false;

    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private RealtimeWeather realTimeWeather;

    @OneToMany(mappedBy = "hourlyWeatherId.location",
            cascade = {CascadeType.ALL},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<HourlyWeather> hourlyWeatherSet;

    public Location(
            String code,
            String cityName,
            String regionName,
            String countryName,
            String countryCode,
            Boolean enabled,
            Boolean trashed
    ) {
        this.code = code;
        this.cityName = cityName;
        this.regionName = regionName;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.enabled = enabled;
        this.trashed = trashed;
    }

    @Override
    public String toString() {
        return "%s, %s%s".formatted(cityName, countryName, regionName == null ? "" : ", " + regionName);
    }


}
