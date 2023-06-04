package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ILocationRepository extends JpaRepository<Location, String> {

    List<Location> findAllByTrashedIsFalse();

    Optional<Location> findByCodeAndTrashedIsFalse(final String locationCode);

    @Query("UPDATE Location SET trashed = true WHERE code=:code")
    @Modifying
    void trashByCode(@Param("code") final String locationCode);

    Boolean existsByCodeAndTrashedIsFalse(final String locationCode);

    Optional<Location> findByCountryCodeAndCityNameAndTrashedIsFalse(final String countryCode, final String city);
}
