package com.skyapi.weatherforecast.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.entity.Location;
import com.skyapi.weatherforecast.common.entity.RealtimeWeather;
import com.skyapi.weatherforecast.exception.GeolocationException;
import com.skyapi.weatherforecast.exception.ResourceNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RealtimeWeatherAPIController.class)
class RealtimeWeatherAPIControllerTest {

    private static final String BASE_API_PATH = "/v1/realtime";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RealtimeWeatherService realtimeWeatherService;

    private Location testLocation1;

    private RealtimeWeather testRealtimeWeather1;

    private RealtimeWeatherDTO testRealtimeWeatherDTO1;

    @BeforeEach
    void populateTestData() {
        testLocation1 = new Location(
                "SFCA_USA",
                "San Francisco",
                "United States of America",
                "California",
                "US",
                true,
                false
        );

        testRealtimeWeather1 = new RealtimeWeather(
                testLocation1.getCode(),
                12,
                32,
                88,
                5,
                "Cloudy",
                LocalDateTime.now(),
                testLocation1
        );

        testRealtimeWeatherDTO1 = new RealtimeWeatherDTO(
                "San Francisco, California, United States of America",
                12,
                32,
                88,
                "Cloudy",
                5,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Test GET getRealtimeWeatherByIPAddress() invalid IP")
    @SneakyThrows
    void testGetRealtimeWeatherByIPAddress_invalidIP_shouldReturn400BadRequest() {

        Mockito.when(realtimeWeatherService.getByIPAddress(Mockito.anyString()))
                .thenThrow(GeolocationException.class);

        mockMvc.perform(get(BASE_API_PATH))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("Test GET getRealtimeWeatherByIPAddress() no location found for IP")
    @SneakyThrows
    void testGetRealtimeWeatherByIPAddress_noLocationFoundForIP_shouldReturn404NotFound() {

        Mockito.when(realtimeWeatherService.getByIPAddress(Mockito.anyString()))
                .thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(BASE_API_PATH))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("Test GET getRealtimeWeatherByIPAddress() location found for IP")
    @SneakyThrows
    void testGetRealtimeWeatherByIPAddress_validIP_shouldReturn200OK() {

        Mockito.when(realtimeWeatherService.getByIPAddress(Mockito.anyString()))
                .thenReturn(testRealtimeWeatherDTO1);

        mockMvc.perform(get(BASE_API_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location").value(testRealtimeWeatherDTO1.location()))
                .andDo(print());
    }

    @Test
    @DisplayName("Test GET getRealtimeWeatherByIPAddress() location found for IP")
    @SneakyThrows
    void testGetRealtimeWeatherByLocationCode_validLocationCode_shouldReturn200OK() {

        final String locationCode = "SFCA_USA";

        Mockito.when(realtimeWeatherService.getByLocationCode(locationCode))
                .thenReturn(testRealtimeWeatherDTO1);

        mockMvc.perform(get(BASE_API_PATH + "/%s".formatted(locationCode)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location").value(testRealtimeWeatherDTO1.location()))
                .andDo(print());
    }

    @Test
    @DisplayName("Test GET getRealtimeWeatherByIPAddress() location found for IP")
    @SneakyThrows
    void testGetRealtimeWeatherByLocationCode_trashedLocationCode_shouldReturn404NotFound() {

        final String locationCode = "AMMAN_JO";

        Mockito.when(realtimeWeatherService.getByLocationCode(locationCode))
                .thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(BASE_API_PATH + "/%s".formatted(locationCode)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andDo(print());
    }

    @Test
    @DisplayName("Test PUT replaceRealtimeWeatherInformation(locCode, updateReq) invalid location code")
    @SneakyThrows
    void testReplaceRealtimeWeatherInformation_invalidLocationCode_shouldReturn404NotFound() {

        final String locationCode = "ABC_EFG";

        final RealtimeWeatherUpdateRequest testUpdateRequest = getTestRealtimeWeatherUpdateRequest_valid();

        final String testUpdateRequestAsJson = objectMapper.writeValueAsString(testUpdateRequest);

        Mockito.when(realtimeWeatherService.replaceRealtimeWeatherInformation(locationCode, testUpdateRequest))
                .thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put(BASE_API_PATH + "/%s".formatted(locationCode))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testUpdateRequestAsJson)
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andDo(print());
    }

    @Test
    @DisplayName("Test PUT replaceRealtimeWeatherInformation(locCode, updateReq) valid location code, invalid update request")
    @SneakyThrows
    void testReplaceRealtimeWeatherInformation_validLocationCodeInvalidUpdatedRequest_shouldReturn400BadRequest() {

        final String locationCode = "ABC_EFG";

        final RealtimeWeatherUpdateRequest testUpdateRequest = getTestRealtimeWeatherUpdateRequest_invalid();

        final String testUpdateRequestAsJson = objectMapper.writeValueAsString(testUpdateRequest);

        mockMvc.perform(put(BASE_API_PATH + "/%s".formatted(locationCode))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testUpdateRequestAsJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andDo(print());
    }

    @Test
    @DisplayName("Test PUT replaceRealtimeWeatherInformation(locCode, updateReq) valid location code, valid update request")
    @SneakyThrows
    void testReplaceRealtimeWeatherInformation_validLocationCodeValidUpdatedRequest_shouldReturn200OK() {

        final String locationCode = "ABC_EFG";

        final RealtimeWeatherUpdateRequest testUpdateRequest = getTestRealtimeWeatherUpdateRequest_valid();

        final RealtimeWeatherDTO testUpdatedRealtimeWeatherDTO = getTestUpdatedRealtimeWeather();

        final String testUpdateRequestAsJson = objectMapper.writeValueAsString(testUpdateRequest);

        Mockito.when(realtimeWeatherService.replaceRealtimeWeatherInformation(locationCode, testUpdateRequest))
                .thenReturn(testUpdatedRealtimeWeatherDTO);


        mockMvc.perform(put(BASE_API_PATH + "/%s".formatted(locationCode))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testUpdateRequestAsJson)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location").value("San Francisco, California, United States of America"))
                .andDo(print());
    }


    private RealtimeWeatherUpdateRequest getTestRealtimeWeatherUpdateRequest_valid() {
        return new RealtimeWeatherUpdateRequest(23, 12, 50, "Sunny", 20);
    }

    private RealtimeWeatherDTO getTestUpdatedRealtimeWeather() {
        return new RealtimeWeatherDTO(
                testLocation1.toString(),
                23,
                12,
                50,
                "Sunny",
                20,
                LocalDateTime.now()
        );
    }

    private RealtimeWeatherUpdateRequest getTestRealtimeWeatherUpdateRequest_invalid() {
        return new RealtimeWeatherUpdateRequest(230, 120, 500, "Su", -51);
    }
}