package com.skyapi.weatherforecast.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.exception.DuplicateResourceException;
import com.skyapi.weatherforecast.exception.ResourceNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationAPIController.class)
class LocationAPIControllerTest {

    private static final String BASE_API_PATH = "/v1/locations";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocationService locationService;

    private LocationDTO testLocationDTO1;

    private LocationDTO testLocationDTO2;

    private LocationDTO testLocationDTO3;

    @BeforeEach
    void populateTestLocationDTOs() {
        testLocationDTO1 = new LocationDTO(
                "NYC_USA",
                "Ney York City",
                "New York",
                "United States of America",
                "US",
                true);

        testLocationDTO2 = new LocationDTO(
                "LACA_USA",
                "Los Angeles",
                "California",
                "United States of America",
                "US",
                true);

        testLocationDTO3 = new LocationDTO(
                "MBMH_IN",
                "Mumbai",
                "Maharashtra",
                "India",
                "IN",
                true);
    }

    @Test
    @DisplayName("test GET v1/locations listAllLocations noValidLocationsFound")
    @SneakyThrows
    void testListAllLocations_noValidLocations_shouldReturn204NoContent() {
        //given
        //when
        Mockito.when(locationService.listAll()).thenReturn(Collections.emptyList());

        //then
        mockMvc.perform(get(BASE_API_PATH))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("test GET v1/locations listAllLocations validLocationsFound")
    @SneakyThrows
    void testListAllLocations_validLocations_shouldReturn200OKAndLocationDTOs() {
        //given
        List<LocationDTO> locationDTOS = getTestLocationDTOs();

        //when
        Mockito.when(locationService.listAll()).thenReturn(locationDTOS);

        //then
        mockMvc.perform(get(BASE_API_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0].code").value("NYC_USA"))
                .andExpect(jsonPath("$[1].code").value("LACA_USA"))
                .andExpect(jsonPath("$[2].code").value("MBMH_IN"))
                .andDo(print());
    }

    @Test
    @DisplayName("test GET v1/locations/{code} getLocationByCode nonExistingCode")
    @SneakyThrows
    void testFindByLocationCode_nonExistingCode_shouldReturn404NotFoundAndAPIError() {
        //given
        String toGetCode = "DELHI_IN";

        //when
        Mockito.when(locationService.getLocationByCode(toGetCode))
                .thenThrow(new ResourceNotFoundException("whatever message"));

        //then
        mockMvc.perform(get(BASE_API_PATH + "/%s".formatted(toGetCode)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.messages[0]").value("whatever message"))
                .andDo(print());
    }

    @Test
    @DisplayName("test GET v1/locations/{code} getLocationByCode existingCode")
    @SneakyThrows
    void testFindByLocationCode_existingCode_shouldReturn200OKAndLocByCode() {
        //given
        String toGetCode = "NYC_USA";

        //when
        Mockito.when(locationService.getLocationByCode(toGetCode)).thenReturn(testLocationDTO1);

        //then
        mockMvc.perform(get(BASE_API_PATH + "/%s".formatted(toGetCode)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(testLocationDTO1.code()))
                .andExpect(jsonPath("$.city_name").value(testLocationDTO1.cityName()))
                .andExpect(jsonPath("$.region_name").value(testLocationDTO1.regionName()))
                .andExpect(jsonPath("$.country_name").value(testLocationDTO1.countryName()))
                .andExpect(jsonPath("$.country_code").value(testLocationDTO1.countryCode()))
                .andExpect(jsonPath("$.enabled").value(testLocationDTO1.enabled()))
                .andDo(print());
    }


    @Test
    @DisplayName("test POST v1/locations registerLocation validRegistrationRequest AND existingCode")
    @SneakyThrows
    void testRegisterLocation_validRegistrationRequestAndExistingCode_shouldReturn409ConflictAndAPIError() {
        //given
        LocationRequest toTestLocationRequest = getTestLocationRequest1_Registration();

        String toTestLocationRequestAsJsonBody = objectMapper.writeValueAsString(toTestLocationRequest);

        //when
        Mockito.when(locationService.register(toTestLocationRequest))
                .thenThrow(new DuplicateResourceException("whatever message"));

        //then
        mockMvc.perform(post(BASE_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toTestLocationRequestAsJsonBody))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.messages[0]").value("whatever message"))
                .andDo(print());
    }

    @Test
    @DisplayName("test POST v1/locations registerLocation validRegistrationRequest AND nonExistingCode")
    @SneakyThrows
    void testRegisterLocation_validRegistrationRequestAndNonExistingCode_shouldReturn201CreatedAndLocationDTO() {
        //given
        LocationRequest toTestLocationRequest = getTestLocationRequest1_Registration();

        String toTestLocationRequestAsJsonBody = objectMapper.writeValueAsString(toTestLocationRequest);

        //when
        Mockito.when(locationService.register(toTestLocationRequest)).thenReturn(testLocationDTO1);

        //then
        mockMvc.perform(post(BASE_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toTestLocationRequestAsJsonBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.LOCATION, BASE_API_PATH + "/%s".formatted(testLocationDTO1.code())))
                .andExpect(jsonPath("$.code").value(testLocationDTO1.code()))
                .andExpect(jsonPath("$.city_name").value(testLocationDTO1.cityName()))
                .andExpect(jsonPath("$.region_name").value(testLocationDTO1.regionName()))
                .andExpect(jsonPath("$.country_name").value(testLocationDTO1.countryName()))
                .andExpect(jsonPath("$.country_code").value(testLocationDTO1.countryCode()))
                .andExpect(jsonPath("$.enabled").value(testLocationDTO1.enabled()))
                .andDo(print());
    }

    @Test
    @DisplayName("test POST v1/locations registerLocation invalidRegistrationRequest")
    @SneakyThrows
    void testRegisterLocation_invalidRegistrationRequest_shouldReturn400BadRequestAndAPIError() {
        //given
        LocationRequest toTestLocationRequest = getInvalidRegistrationRequest_allFieldsViolateLength();

        String toTestLocationRequestAsJsonBody = objectMapper.writeValueAsString(toTestLocationRequest);

        //when
        //then
        MvcResult mvcResult = mockMvc.perform(post(BASE_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toTestLocationRequestAsJsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).contains("country_name's length must be between 3 and 64");
        assertThat(responseBody).contains("city_name's length must be between 3 and 12");
        assertThat(responseBody).contains("region_name's length must be between 3 and 128");
        assertThat(responseBody).contains("country_code's length must be 2");
        assertThat(responseBody).contains("Location code's length must be between 3 and 12");
    }

    @Test
    @DisplayName("test PUT v1/locations replaceLocation validUpdateLocationRequest")
    @SneakyThrows
    void testReplaceLocation_validUpdateRequest_shouldReturn200OkAndLocationDTO() {
        //given
        LocationRequest updateLocationRequest = getTestLocationRequest3_UpdatedCityAndRegionNames();

        LocationDTO updatedLocationDTO = getTestLocationDTO3_UpdatedCityAndRegionNamesAndEnabledToFalse();

        String updatedLocationRequestAsJson = objectMapper.writeValueAsString(updateLocationRequest);

        //when
        Mockito.when(locationService.replaceLocation(updateLocationRequest)).thenReturn(updatedLocationDTO);

        //then
        mockMvc.perform(put(BASE_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedLocationRequestAsJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(testLocationDTO3.code()))
                .andExpect(jsonPath("$.city_name").value(updatedLocationDTO.cityName()))
                .andExpect(jsonPath("$.region_name").value(updatedLocationDTO.regionName()))
                .andExpect(jsonPath("$.country_code").value(testLocationDTO3.countryCode()))
                .andExpect(jsonPath("$.country_name").value(testLocationDTO3.countryName()))
                .andExpect(jsonPath("$.enabled").value(updatedLocationDTO.enabled()))
                .andDo(print());
    }

    @Test
    @DisplayName("test PUT v1/locations replaceLocation invalidUpdateLocationRequest(Not found code)")
    @SneakyThrows
    void testReplaceLocation_invalidUpdateRequest_shouldReturn404NotFoundAndAPIError() {
        //given
        LocationRequest updateLocationRequest = getTestLocationRequest3_UpdatedCityAndRegionNamesAndCodeToNotFoundOne();

        String updatedLocationRequestAsJson = objectMapper.writeValueAsString(updateLocationRequest);

        //when
        Mockito.when(locationService.replaceLocation(updateLocationRequest))
                .thenThrow(new ResourceNotFoundException("whatever message"));

        //then
        mockMvc.perform(put(BASE_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedLocationRequestAsJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andDo(print());
    }

    @Test
    @DisplayName("test DELETE v1/locations deleteLocation invalidCode")
    @SneakyThrows
    void testDeleteLocation_invalidCode_ShouldReturn404NotFoundAndAPIError() {
        //given
        String toDeleteLocationCode = "AMMAN_JO";

        //when
        Mockito.doThrow(new ResourceNotFoundException("whatever message"))
                .when(locationService)
                .deleteLocation(toDeleteLocationCode);

        //then
        mockMvc.perform(delete(BASE_API_PATH + "/%s".formatted(toDeleteLocationCode)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andDo(print());
    }

    @Test
    @DisplayName("test DELETE v1/locations deleteLocation validCode")
    @SneakyThrows
    void testDeleteLocation_validCode_ShouldReturn204NoContent() {
        //given
        String toDeleteLocationCode = "NYC_USA";

        //when
        Mockito.doNothing()
                .when(locationService)
                .deleteLocation(toDeleteLocationCode);

        //then
        mockMvc.perform(delete(BASE_API_PATH + "/%s".formatted(toDeleteLocationCode)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }


    private List<LocationDTO> getTestLocationDTOs() {
        return List.of(testLocationDTO1, testLocationDTO2, testLocationDTO3);
    }

    private LocationRequest getTestLocationRequest1_Registration() {
        return new LocationRequest(
                testLocationDTO1.code(),
                testLocationDTO1.cityName(),
                testLocationDTO1.regionName(),
                testLocationDTO1.countryName(),
                testLocationDTO1.countryCode(),
                testLocationDTO1.enabled()
        );
    }

    private LocationRequest getTestLocationRequest3_UpdatedCityAndRegionNames() {
        return new LocationRequest(
                testLocationDTO3.code(),
                "IndiaXDD",
                "IndiaHaHaHa",
                testLocationDTO3.countryName(),
                testLocationDTO3.countryCode(),
                false
        );
    }

    private LocationDTO getTestLocationDTO3_UpdatedCityAndRegionNamesAndEnabledToFalse() {
        return new LocationDTO(
                testLocationDTO3.code(),
                "IndiaXDDD",
                "IndiaHaHaHa",
                testLocationDTO3.countryName(),
                testLocationDTO3.countryCode(),
                false);
    }

    private LocationRequest getTestLocationRequest3_UpdatedCityAndRegionNamesAndCodeToNotFoundOne() {
        return new LocationRequest(
                "XDD",
                "IndiaXDDD",
                "IndiaHaHaHa",
                testLocationDTO3.countryName(),
                testLocationDTO3.countryCode(),
                testLocationDTO3.enabled());
    }

    private LocationRequest getInvalidRegistrationRequest_allFieldsViolateLength() {
        return new LocationRequest(
                "CG",
                "GC",
                "LD",
                "QQ",
                "XDD",
                true
        );
    }
}