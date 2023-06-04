package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.entity.Location;
import com.skyapi.weatherforecast.exception.DuplicateResourceException;
import com.skyapi.weatherforecast.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final static String LOCATION_NOT_FOUND_ERROR_MSG = """
            No Location found with given code: [%s]
            """;

    private final static String DUPLICATE_LOCATION_FOUND_ERROR_MSG = """
            could not register location, location with same code exists: [%s]
            """;

    private final ILocationRepository locationRepository;

    private final LocationDTOMapper locationDTOMapper;

    private final LocationRequestMapper locationRequestMapper;

    public LocationDTO register(LocationRequest registrationRequest) {
        throwExceptionIfDuplicateCode(registrationRequest.code());

        Location toRegisterLocation = locationRequestMapper.apply(registrationRequest);

        locationRepository.save(toRegisterLocation);

        return locationDTOMapper.apply(toRegisterLocation);
    }

    @Transactional(readOnly = true)
    public List<LocationDTO> listAll() {
        return locationRepository.findAllByTrashedIsFalse()
                .stream()
                .map(locationDTOMapper)
                .toList();
    }

    @Transactional(readOnly = true)
    public LocationDTO getLocationByCode(String locationCode) {
        return locationRepository.findByCodeAndTrashedIsFalse(locationCode)
                .map(locationDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        LOCATION_NOT_FOUND_ERROR_MSG.formatted(locationCode)
                ));
    }

    @Transactional
    public LocationDTO replaceLocation(LocationRequest locationUpdateRequest) {
        String toReplaceLocCode = locationUpdateRequest.code();

        Location oldLocation = locationRepository
                .findById(toReplaceLocCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        LOCATION_NOT_FOUND_ERROR_MSG.formatted(toReplaceLocCode)
                ));

        Location newLocation = locationRequestMapper.apply(locationUpdateRequest);
        newLocation.setTrashed(oldLocation.getTrashed());
        newLocation.setRealTimeWeather(oldLocation.getRealTimeWeather());
        newLocation.setHourlyWeatherSet(oldLocation.getHourlyWeatherSet());

        locationRepository.save(newLocation);

        return locationDTOMapper.apply(newLocation);
    }

    @Transactional
    public void deleteLocation(String locationCode) {
        if (!locationRepository.existsByCodeAndTrashedIsFalse(locationCode)) {
            throw new ResourceNotFoundException(
                    LOCATION_NOT_FOUND_ERROR_MSG.formatted(locationCode)
            );
        }

        locationRepository.trashByCode(locationCode);
    }

    private void throwExceptionIfDuplicateCode(String locationCode) {
        if (locationRepository.existsById(locationCode))
            throw new DuplicateResourceException(
                    DUPLICATE_LOCATION_FOUND_ERROR_MSG.formatted(locationCode)
            );
    }
}
