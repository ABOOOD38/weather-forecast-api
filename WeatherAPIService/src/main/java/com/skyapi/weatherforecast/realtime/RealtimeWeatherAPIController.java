package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/realtime")
@RequiredArgsConstructor
public class RealtimeWeatherAPIController {

    private final RealtimeWeatherService realtimeWeatherService;

    @GetMapping
    public ResponseEntity<RealtimeWeatherDTO> getRealtimeWeatherByIPAddress(
            HttpServletRequest request
    ) {

        final String ipAddress = CommonUtil.getIPAddress(request);

        final RealtimeWeatherDTO RealtimeWeatherByIPAddress = realtimeWeatherService.getByIPAddress(ipAddress);

        return ResponseEntity.ok(RealtimeWeatherByIPAddress);
    }

    @GetMapping("{locationCode}")
    public ResponseEntity<RealtimeWeatherDTO> getRealtimeWeatherByLocationCode(
            @PathVariable("locationCode") String locationCode
    ) {

        final RealtimeWeatherDTO RealtimeWeatherByLocationCode = realtimeWeatherService.getByLocationCode(locationCode);

        return ResponseEntity.ok(RealtimeWeatherByLocationCode);
    }

    @PutMapping("{locationCode}")
    public ResponseEntity<RealtimeWeatherDTO> replaceRealtimeWeatherInformation(
            @PathVariable("locationCode") String locationCode,
            @Valid
            @RequestBody
            RealtimeWeatherUpdateRequest updateRequest
    ) {

        final RealtimeWeatherDTO updatedRealtimeWeather = realtimeWeatherService
                .replaceRealtimeWeatherInformation(locationCode, updateRequest);

        return ResponseEntity.ok(updatedRealtimeWeather);
    }
}
