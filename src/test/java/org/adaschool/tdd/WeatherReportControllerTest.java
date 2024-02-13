package org.adaschool.tdd;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.adaschool.tdd.controller.weather.WeatherReportController;
import org.adaschool.tdd.controller.weather.dto.NearByWeatherReportsQueryDto;
import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.adaschool.tdd.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherReportController.class)
class WeatherReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    private WeatherReportDto weatherReportDto;
    private WeatherReport weatherReport;
    GeoLocation geoLocation = new GeoLocation(40.7128, -74.0060);
    double temperature = 25.5;
    double humidity = 60.0;
    String reporter = "TestReporter";
    Date created = new Date();
    float distanceRangeInMeters = 5000;


    @BeforeEach
    void setUp() {
        weatherReportDto = new WeatherReportDto(geoLocation, temperature, humidity, reporter, created);
        weatherReport = new WeatherReport(geoLocation, temperature, humidity, reporter, created);

    }

    @Test
    void createWeatherReportTest() throws Exception {
        when(weatherService.report(any(WeatherReportDto.class))).thenReturn(weatherReport);

        mockMvc.perform(post("/v1/weather")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(weatherReportDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reporter").value(weatherReport.getReporter())); // ejemplo de verificación

        verify(weatherService).report(any(WeatherReportDto.class));
    }
    @Test
    void findByIdTest() throws Exception {
        String id = "testId";
        when(weatherService.findById(id)).thenReturn(weatherReport);

        mockMvc.perform(get("/v1/weather/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reporter").value(weatherReport.getReporter()));

        verify(weatherService).findById(id);
    }
    @Test
    void findNearByReportsTest() throws Exception {
        NearByWeatherReportsQueryDto queryDto = new NearByWeatherReportsQueryDto(geoLocation, distanceRangeInMeters);
        List<WeatherReport> reports = Collections.singletonList(weatherReport);

        when(weatherService.findNearLocation(any(GeoLocation.class), anyFloat())).thenReturn(reports);

        mockMvc.perform(post("/v1/weather/nearby")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(queryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reporter").value(weatherReport.getReporter()));

        verify(weatherService).findNearLocation(any(GeoLocation.class), anyFloat());
    }
    @Test
    void findByReporterIdTest() throws Exception {
        String reporterName = "TestReporter";
        List<WeatherReport> reports = Collections.singletonList(weatherReport);

        when(weatherService.findWeatherReportsByName(reporterName)).thenReturn(reports);

        mockMvc.perform(get("/v1/weather/reporter/{name}", reporterName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reporter").value(weatherReport.getReporter()));

        verify(weatherService).findWeatherReportsByName(reporterName);
    }



}