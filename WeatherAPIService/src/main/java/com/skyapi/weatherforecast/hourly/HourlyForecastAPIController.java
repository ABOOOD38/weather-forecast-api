package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.common.entity.Location;
import com.skyapi.weatherforecast.geolocation.GeolocationService;
import com.skyapi.weatherforecast.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/hourly")
@RequiredArgsConstructor
@Validated
public class HourlyForecastAPIController {

    private final HourlyWeatherService hourlyWeatherService;

    private final GeolocationService geolocationService;

    @GetMapping
    public ResponseEntity<HourlyWeatherDTO> listHourlyForecastByIPAddress(HttpServletRequest request) {
        final String ipAddress = CommonUtil.getIPAddress(request);

        final Integer currentHour = CommonUtil.getCurrentHour(request);

        final Location locationFromIP = geolocationService.getLocationFromIPAddress(ipAddress);

        final HourlyWeatherDTO hourlyWeatherDTO = hourlyWeatherService
                .getByLocationAndCurrentHour(locationFromIP, currentHour);

        return ResponseEntity.ok(hourlyWeatherDTO);
    }

    @GetMapping("{code}")
    public ResponseEntity<HourlyWeatherDTO> listHourlyForecastByLocationCode(
            @PathVariable("code") String locationCode,
            HttpServletRequest request
    ) {
        final Integer currentHour = CommonUtil.getCurrentHour(request);

        final HourlyWeatherDTO hourlyWeatherDTO = hourlyWeatherService
                .getByLocationCodeAndCurrentHour(locationCode, currentHour);

        return ResponseEntity.ok(hourlyWeatherDTO);
    }

    @PutMapping("{code}")
    public ResponseEntity<HourlyWeatherDTO> replaceByLocationCode(
            @PathVariable("code") final String locationCode,
            @RequestBody
            @Valid final List<HourlyForecastDTO> hourlyForecastDTOList
    ) {

        final HourlyWeatherDTO hourlyWeatherDTO = hourlyWeatherService
                .replaceByLocationCode(locationCode, hourlyForecastDTOList);

        return ResponseEntity.ok(hourlyWeatherDTO);
    }
}
