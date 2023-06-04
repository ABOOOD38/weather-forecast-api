package com.skyapi.weatherforecast.ip2location;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class IP2LocationTest {

    private final static String IPDatabasePath = "src/main/resources/IP2LocationDB/IP2LOCATION-LITE-DB3.BIN";
    private final static IP2Location ipLocator = new IP2Location();

    @BeforeAll
    @SneakyThrows
    static void loadDB() {
        ipLocator.Open(IPDatabasePath);
    }

    @AfterAll
    static void destroy() {
        ipLocator.Close();
    }

    @Test
    @SneakyThrows
    void testInvalidIP_shouldReturnIPResultWithStatus_INVALID_IP_ADDRESS() {
        final String invalidIPAddress = "ABC";

        final IPResult invalidIPAddressResult = ipLocator.IPQuery(invalidIPAddress);

        assertThat(invalidIPAddressResult.getStatus()).isEqualTo("INVALID_IP_ADDRESS");
    }

    @Test
    @SneakyThrows
    void testNewYorkIPAddress_shouldReturnIPResultWithStatus_OK() {
        final String newYorkIPAddress = "108.30.178.78";

        final IPResult newYorkIPAddressResult = ipLocator.IPQuery(newYorkIPAddress);

        assertThat(newYorkIPAddressResult.getStatus()).isEqualTo("OK");
        assertThat(newYorkIPAddressResult.getCity()).isEqualTo("New York City");
    }

    @Test
    @SneakyThrows
    void testDelhiIndiaIPAddress_shouldReturnIPResultWithStatus_OK() {
        final String delhiIPAddress = "103.48.198.141";

        final IPResult delhiIPAddressResult = ipLocator.IPQuery(delhiIPAddress);

        assertThat(delhiIPAddressResult.getStatus()).isEqualTo("OK");
        assertThat(delhiIPAddressResult.getCity()).isEqualTo("Delhi");
    }
}
