package com.skyapi.weatherforecast.location;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("v1/locations")
@RequiredArgsConstructor
public class LocationAPIController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<List<LocationDTO>> listAllLocations() {
        List<LocationDTO> allLocations = locationService.listAll();

        if (allLocations.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(allLocations, HttpStatus.OK);
    }

    @GetMapping("{code}")
    public ResponseEntity<LocationDTO> getLocationByCode(
            @PathVariable("code")
            String locationCode
    ) {

        LocationDTO byCodeLocation = locationService.getLocationByCode(locationCode);
        return new ResponseEntity<>(byCodeLocation, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LocationDTO> registerLocation(
            @RequestBody
            @Valid
            LocationRequest locationRegistrationRequest
    ) {

        LocationDTO registeredLocation = locationService.register(locationRegistrationRequest);
        URI uri = URI.create("/v1/locations/%s".formatted(registeredLocation.code()));
        return ResponseEntity.created(uri).body(registeredLocation);
    }

    @PutMapping
    public ResponseEntity<LocationDTO> replaceLocation(
            @RequestBody
            @Valid
            LocationRequest locationUpdateRequest
    ) {

        LocationDTO updatedLocation = locationService.replaceLocation(locationUpdateRequest);
        return ResponseEntity.ok(updatedLocation);
    }

    @DeleteMapping("{code}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable("code")
            String locationCode
    ) {

        locationService.deleteLocation(locationCode);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
