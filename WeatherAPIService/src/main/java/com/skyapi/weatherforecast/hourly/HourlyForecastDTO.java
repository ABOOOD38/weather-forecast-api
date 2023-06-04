package com.skyapi.weatherforecast.hourly;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonPropertyOrder(value = {"hour_of_day", "temperature", "precipitation", "status"})
public class HourlyForecastDTO {

    @JsonProperty("hour_of_day")
    @Range(min = 0, max = 24, message = "hour of day must be in the range of 0 to 24 inclusive")
    private Integer hourOfDay;

    @Range(min = -50, max = 50, message = "Temperature must be in the range of -50 to 50 Celsius degree")
    private Integer temperature;

    @Range(min = 0, max = 100, message = "Precipitation must be in the range of 0 to 100 percentage")
    private Integer precipitation;

    @NotBlank(message = "Status must not be empty")
    @Length(min = 3, max = 50, message = "Status must be in between 3-50 characters")
    private String status;
}
