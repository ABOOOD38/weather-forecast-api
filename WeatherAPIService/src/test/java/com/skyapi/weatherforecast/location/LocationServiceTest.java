package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.entity.Location;
import com.skyapi.weatherforecast.exception.DuplicateResourceException;
import com.skyapi.weatherforecast.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private ILocationRepository locationRepository;

    @Mock
    private LocationDTOMapper locationDTOMapper;

    @Mock
    private LocationRequestMapper locationRequestMapper;

    @InjectMocks
    private LocationService underTest;

    private LocationRequest testLocationRequest1;

    @BeforeEach
    void populateTestData() {
        testLocationRequest1 = new LocationRequest(
                "NYC_USA",
                "Ney York City",
                "New York",
                "United States of America",
                "US",
                true);
    }

    @Test
    @DisplayName("test register(locationRequest) duplicate code")
    void testRegister_withDuplicateCode_shouldThrowDuplicateResourceException() {
        //given
        //when
        Mockito.when(locationRepository.existsById(testLocationRequest1.code()))
                .thenReturn(true);

        //then
        assertThatExceptionOfType(DuplicateResourceException.class)
                .isThrownBy(() -> underTest.register(testLocationRequest1));
    }

    @Test
    @DisplayName("test register(locationRequest) no duplicate code")
    void testRegister_withNonDuplicateCode_shouldReturnRegisteredLocationDTO() {
        //given
        final Location testLocation = getTestLocationFromReq(testLocationRequest1);

        final LocationDTO testLocationDTO = getTestLocationDTOFromReq(testLocationRequest1);

        //when
        Mockito.when(locationRepository.existsById(testLocationRequest1.code()))
                .thenReturn(false);

        Mockito.when(locationRequestMapper.apply(testLocationRequest1))
                .thenReturn(testLocation);

        Mockito.when(locationDTOMapper.apply(testLocation))
                .thenReturn(testLocationDTO);

        underTest.register(testLocationRequest1);

        //then
        Mockito.verify(locationRepository).save(testLocation);
    }

    @Test
    @DisplayName("test listAll()")
    void testListAll_shouldReturnAllLocations() {
        //given
        final Location testLocation = getTestLocationFromReq(testLocationRequest1);

        final LocationDTO testLocationDTO = getTestLocationDTOFromReq(testLocationRequest1);

        //when
        Mockito.when(locationRepository.findAllByTrashedIsFalse())
                .thenReturn(List.of(testLocation));

        Mockito.when(locationDTOMapper.apply(testLocation))
                .thenReturn(testLocationDTO);

        final List<LocationDTO> locationDTOS = underTest.listAll();

        //then
        assertThat(locationDTOS.size()).isEqualTo(1);
        assertThat(locationDTOS.contains(testLocationDTO)).isTrue();
    }

    @Test
    @DisplayName("test getLocationByCode(locationCode) invalid code")
    void getLocationByCode_invalidCode_shouldThrowResourceNotFoundException() {
        //given
        final String nonExistingLocationCode = "NYC_USA";

        //when
        Mockito.when(locationRepository.findByCodeAndTrashedIsFalse(nonExistingLocationCode))
                .thenReturn(Optional.empty());

        //then
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> underTest.getLocationByCode(nonExistingLocationCode));
    }

    @Test
    @DisplayName("test getLocationByCode(locationCode) valid code")
    void getLocationByCode_validCode_shouldReturnLocationDTO() {
        //given
        final String existingLocationCode = "NYC_USA";

        final Location testLocation = getTestLocationFromReq(testLocationRequest1);

        final LocationDTO testLocationDTO = getTestLocationDTOFromReq(testLocationRequest1);

        //when
        Mockito.when(locationRepository.findByCodeAndTrashedIsFalse(existingLocationCode))
                .thenReturn(Optional.of(testLocation));

        Mockito.when(locationDTOMapper.apply(testLocation))
                .thenReturn(testLocationDTO);

        LocationDTO returnedLocationDTO = underTest.getLocationByCode(existingLocationCode);

        //then
        assertThatObject(returnedLocationDTO).isEqualTo(testLocationDTO);
    }

    @Test
    @DisplayName("test replaceLocation(locationUpdateRequest) invalid code")
    void testReplaceLocation_validCode_shouldThrowResourceNotFoundException() {
        //given
        final String nonExistingLocationCode = "NYC_USA";

        //when
        Mockito.when(locationRepository.findById(nonExistingLocationCode))
                .thenReturn(Optional.empty());

        //then
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> underTest.replaceLocation(testLocationRequest1));
    }

    @Test
    @DisplayName("test replaceLocation(locationUpdateRequest) valid code")
    void testReplaceLocation_validCode_shouldReturnUpdatedLocationDTO() {
        //given
        final String existingLocationCode = "NYC_USA";

        final Location testLocation = getTestLocationFromReq(testLocationRequest1);

        final LocationRequest updatedRegionNameAndEnabledReq = getUpdatedTestLocationReqFromTestReq1();

        final Location testUpdatedLocation = getTestLocationFromReq(updatedRegionNameAndEnabledReq);

        final LocationDTO testUpdatedLocationDTO = getTestLocationDTOFromReq(updatedRegionNameAndEnabledReq);

        //when
        Mockito.when(locationRepository.findById(existingLocationCode))
                .thenReturn(Optional.of(testLocation));

        Mockito.when(locationRequestMapper.apply(updatedRegionNameAndEnabledReq))
                .thenReturn(testUpdatedLocation);

        testUpdatedLocation.setTrashed(testLocation.getTrashed());

        Mockito.when(locationDTOMapper.apply(testUpdatedLocation))
                .thenReturn(testUpdatedLocationDTO);

        final LocationDTO actualLocationDTO = underTest.replaceLocation(updatedRegionNameAndEnabledReq);

        //then
        Mockito.verify(locationRepository).save(testUpdatedLocation);

        assertThatObject(actualLocationDTO).isEqualTo(testUpdatedLocationDTO);
    }

    @Test
    @DisplayName("test deleteLocation(locationCode) valid code")
    void testDeleteLocation_validCode_shouldHitTrashByCode() {
        final String validLocCode = "NYC_USA";

        Mockito.when(locationRepository.existsByCodeAndTrashedIsFalse(validLocCode))
                .thenReturn(true);

        underTest.deleteLocation(validLocCode);

        Mockito.verify(locationRepository).trashByCode(validLocCode);
    }

    @Test
    @DisplayName("test deleteLocation(locationCode) invalid code")
    void testDeleteLocation_invalidCode_shouldThrowResourceNotFoundException() {
        final String invalidLocCode = "NYC_USA";

        Mockito.when(locationRepository.existsByCodeAndTrashedIsFalse(invalidLocCode))
                .thenReturn(false);

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> underTest.deleteLocation(invalidLocCode));
    }

    private Location getTestLocationFromReq(LocationRequest locationRequest) {
        return new Location(
                locationRequest.code(),
                locationRequest.cityName(),
                locationRequest.regionName(),
                locationRequest.countryName(),
                locationRequest.countryCode(),
                locationRequest.enabled(),
                false
        );
    }

    private LocationDTO getTestLocationDTOFromReq(LocationRequest locationRequest) {
        return new LocationDTO(
                locationRequest.code(),
                locationRequest.cityName(),
                locationRequest.regionName(),
                locationRequest.countryName(),
                locationRequest.countryCode(),
                locationRequest.enabled()
        );
    }

    private LocationRequest getUpdatedTestLocationReqFromTestReq1() {
        return new LocationRequest(
                testLocationRequest1.code(),
                testLocationRequest1.cityName(),
                "XDD",
                testLocationRequest1.countryName(),
                testLocationRequest1.countryCode(),
                false
        );
    }
}