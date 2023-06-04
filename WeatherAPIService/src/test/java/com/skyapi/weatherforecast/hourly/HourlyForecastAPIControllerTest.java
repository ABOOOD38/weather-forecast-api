package com.skyapi.weatherforecast.hourly;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.entity.Location;
import com.skyapi.weatherforecast.exception.GeolocationException;
import com.skyapi.weatherforecast.exception.InvalidResourceProvidedException;
import com.skyapi.weatherforecast.exception.NoDataAvailableException;
import com.skyapi.weatherforecast.exception.ResourceNotFoundException;
import com.skyapi.weatherforecast.geolocation.GeolocationService;
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

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HourlyForecastAPIController.class)
class HourlyForecastAPIControllerTest {

    private final static String BASE_API_PATH = "/v1/hourly";

    private final static String X_CURRENT_HOUR_HEADER = "X-Current-Hour";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HourlyWeatherService hourlyWeatherService;

    @MockBean
    private GeolocationService geolocationService;

    private HourlyWeatherDTO testHourlyWeatherDTO;

    private HourlyForecastDTO testHourlyForecastDTO1_invalid;

    private HourlyForecastDTO testHourlyForecastDTO2;

    private HourlyForecastDTO testHourlyForecastDTO3;


    @BeforeEach
    void populateData() {
        testHourlyForecastDTO1_invalid = new HourlyForecastDTO(
                10,
                133,
                70,
                "Cloudy"
        );

        testHourlyForecastDTO2 = new HourlyForecastDTO(
                11,
                15,
                60,
                "Sunny"
        );

        testHourlyForecastDTO3 = new HourlyForecastDTO(
                12,
                13,
                50,
                "Freezing"
        );

        testHourlyWeatherDTO = new HourlyWeatherDTO(
                "San Francisco, California, United States of America",
                List.of(testHourlyForecastDTO2, testHourlyForecastDTO3)
        );
    }


    @Test
    @DisplayName("GET /v1/hourly no X-Current-Hour-Header Found")
    @SneakyThrows
    void testListHourlyForecastByIPAddress_noXCurrentHeaderFound_shouldReturn400BadRequest() {
        mockMvc.perform(get(BASE_API_PATH))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("GET /v1/hourly not int X-Current-Hour-Header")
    @SneakyThrows
    void testListHourlyForecastByIPAddress_notIntegerXCurrentHeaderFound_shouldReturn400BadRequest() {
        mockMvc.perform(get(BASE_API_PATH).header(X_CURRENT_HOUR_HEADER, "ss"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("GET /v1/hourly int but invalid range X-Current-Hour-Header")
    @SneakyThrows
    void testListHourlyForecastByIPAddress_notInRangeIntegerXCurrentHeaderFound_shouldReturn400BadRequest() {
        mockMvc.perform(get(BASE_API_PATH).header(X_CURRENT_HOUR_HEADER, -1))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("GET /v1/hourly geolocation exception is thrown")
    @SneakyThrows
    void testListHourlyForecastByIPAddress_geolocationExceptionThrownForSomeReason_shouldReturn400BadRequest() {

        Mockito.when(geolocationService.getLocationFromIPAddress(Mockito.anyString()))
                .thenThrow(GeolocationException.class);

        mockMvc.perform(get(BASE_API_PATH).header(X_CURRENT_HOUR_HEADER, 1))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("GET /v1/hourly no data available")
    @SneakyThrows
    void testListHourlyForecastByIPAddress_noHourlyDataAvailable_shouldReturn204NoContent() {

        final Location delhiLocation = Location.builder().code("DELHI_IN").build();

        Mockito.when(geolocationService.getLocationFromIPAddress(Mockito.anyString()))
                .thenReturn(delhiLocation);

        Mockito.when(hourlyWeatherService.getByLocationAndCurrentHour(delhiLocation, 5))
                .thenThrow(NoDataAvailableException.class);

        mockMvc.perform(get(BASE_API_PATH).header(X_CURRENT_HOUR_HEADER, 5))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("GET /v1/hourly data available")
    @SneakyThrows
    void testListHourlyForecastByIPAddress_dataAvailable_shouldReturn200OK() {

        Mockito.when(hourlyWeatherService.getByLocationAndCurrentHour(Mockito.any(), Mockito.anyInt()))
                .thenReturn(testHourlyWeatherDTO);

        mockMvc.perform(get(BASE_API_PATH).header(X_CURRENT_HOUR_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location").value("San Francisco, California, United States of America"))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day").value(11))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /v1/hourly/locationCode no data available")
    @SneakyThrows
    void testListHourlyForecastByLocationCode_noXCurrentHeaderFound_shouldReturn400BadRequest() {
        mockMvc.perform(get(BASE_API_PATH.concat("/%s").formatted("ABC")))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("GET /v1/hourly/locationCode no data available")
    @SneakyThrows
    void testListHourlyForecastByLocationCode_noHourlyDataAvailable_shouldReturn204NoContent() {
        final String delhiLocationCode = "DELHI_IN";

        Mockito.when(hourlyWeatherService.getByLocationCodeAndCurrentHour(delhiLocationCode, 5))
                .thenThrow(NoDataAvailableException.class);

        mockMvc.perform(get(BASE_API_PATH.concat("/%s").formatted(delhiLocationCode))
                        .header(X_CURRENT_HOUR_HEADER, 5)
                )
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("GET /v1/hourly/locationCode data available")
    @SneakyThrows
    void testListHourlyForecastByLocationCode_dataAvailable_shouldReturn200OK() {

        final String sanFranLocationCode = "SA_US";

        Mockito.when(hourlyWeatherService.getByLocationCodeAndCurrentHour(sanFranLocationCode, 1))
                .thenReturn(testHourlyWeatherDTO);

        mockMvc.perform(get(BASE_API_PATH.concat("/%s").formatted(sanFranLocationCode))
                        .header(X_CURRENT_HOUR_HEADER, 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location").value("San Francisco, California, United States of America"))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day").value(11))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /v1/hourly/locationCode not found locationCode")
    @SneakyThrows
    void testListHourlyForecastByLocationCode_notFoundLocationCode_shouldReturn404NotFound() {

        final String sanFranLocationCode = "SA_US";

        Mockito.when(hourlyWeatherService.getByLocationCodeAndCurrentHour(sanFranLocationCode, 1))
                .thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(BASE_API_PATH.concat("/%s").formatted(sanFranLocationCode))
                        .header(X_CURRENT_HOUR_HEADER, 1)
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("PUT v1/hourly/locationCode invalid request body (empty)")
    @SneakyThrows
    void testReplaceByLocationCode_emptyRequestBody_shouldReturn400BadRequest() {
        final String locationCode = "NYC_USA";

        final List<HourlyForecastDTO> emptyHourlyForecastDTOList = Collections.emptyList();

        final String emptyHourlyForecastDTOListAsJson = objectMapper.writeValueAsString(emptyHourlyForecastDTOList);

        Mockito.when(hourlyWeatherService.replaceByLocationCode(locationCode, emptyHourlyForecastDTOList))
                .thenThrow(InvalidResourceProvidedException.class);

        mockMvc.perform(put(BASE_API_PATH.concat("/%s").formatted(locationCode))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyHourlyForecastDTOListAsJson)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("PUT v1/hourly/locationCode invalid location code")
    @SneakyThrows
    void testReplaceByLocationCode_invalidLocationCode_shouldReturn404NotFound() {
        final String locationCode = "NYC_USA";

        final List<HourlyForecastDTO> emptyHourlyForecastDTOList = Collections.emptyList();

        final String emptyHourlyForecastDTOListAsJson = objectMapper.writeValueAsString(emptyHourlyForecastDTOList);

        Mockito.when(hourlyWeatherService.replaceByLocationCode(locationCode, emptyHourlyForecastDTOList))
                .thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put(BASE_API_PATH.concat("/%s").formatted(locationCode))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyHourlyForecastDTOListAsJson)
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("PUT v1/hourly/locationCode invalid request body (data)")
    @SneakyThrows
    void testReplaceByLocationCode_invalidDataInRequestBody_shouldReturn400BadRequest() {
        final String locationCode = "NYC_USA";

        final List<HourlyForecastDTO> listDTO = List.of(testHourlyForecastDTO1_invalid, testHourlyForecastDTO2);

        final String listDTOAsJSON = objectMapper.writeValueAsString(listDTO);

        mockMvc.perform(put(BASE_API_PATH.concat("/%s").formatted(locationCode))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(listDTOAsJSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.messages[0]").value("Temperature must be in the range of -50 to 50 Celsius degree"))
                .andDo(print());
    }

    @Test
    @DisplayName("PUT v1/hourly/locationCode valid request body")
    @SneakyThrows
    void testReplaceByLocationCode_validDataInRequestBody_shouldReturn200OK() {
        final String locationCode = "NYC_USA";

        final List<HourlyForecastDTO> listDTO = List.of(testHourlyForecastDTO2, testHourlyForecastDTO3);

        final String listDTOAsJSON = objectMapper.writeValueAsString(listDTO);

        Mockito.when(hourlyWeatherService.replaceByLocationCode(locationCode, listDTO))
                .thenReturn(testHourlyWeatherDTO);

        mockMvc.perform(put(BASE_API_PATH.concat("/%s").formatted(locationCode))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(listDTOAsJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.location").value(testHourlyWeatherDTO.getLocation()))
                .andExpect(jsonPath("$.hourly_forecast[0].temperature").value(15))
                .andDo(print());
    }


}
