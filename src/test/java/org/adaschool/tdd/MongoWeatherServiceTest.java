package org.adaschool.tdd;

import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.exception.WeatherReportNotFoundException;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.adaschool.tdd.service.MongoWeatherService;
import org.adaschool.tdd.service.WeatherService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
class MongoWeatherServiceTest
{
    WeatherService weatherService;

    @Mock
    WeatherReportRepository repository;

    @BeforeEach()
    public void setup()
    {
        weatherService = new MongoWeatherService( repository );
    }

    @Test
    void createWeatherReportCallsSaveOnRepository()
    {
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReportDto weatherReportDto = new WeatherReportDto( location, 35f, 22f, "tester", new Date() );
        weatherService.report( weatherReportDto );
        verify( repository ).save( any( WeatherReport.class ) );
    }

    @Test
    void weatherReportIdFoundTest()
    {
        String weatherReportId = "awae-asd45-1dsad";
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReport weatherReport = new WeatherReport( location, 35f, 22f, "tester", new Date() );
        when(repository.findById(weatherReportId)).thenReturn(Optional.of(weatherReport));
        WeatherReport foundWeatherReport = weatherService.findById( weatherReportId );
        assertEquals( weatherReport, foundWeatherReport );
    }

    @Test
    void weatherReportIdNotFoundTest()
    {
        String weatherReportId = "dsawe1fasdasdoooq123";
        when( repository.findById( weatherReportId ) ).thenReturn( Optional.empty() );
        Assertions.assertThrows( WeatherReportNotFoundException.class, () -> {
            weatherService.findById( weatherReportId );
        } );
    }

    @Test
    public void findNearLocationTest() {
        GeoLocation testLocation = new GeoLocation(4.7110, 74.0721);
        float range = 10000; // 10 km

        List<WeatherReport> allReports = new ArrayList<>();
        allReports.add(new WeatherReport(new GeoLocation(4.7115, 74.0725), 25
                , 60, "reporter1", new Date()));
        allReports.add(new WeatherReport(new GeoLocation(5.0, 75.0), 26
                , 65, "reporter2", new Date()));

        when(repository.findAll()).thenReturn(allReports);

        List<WeatherReport> foundReports = weatherService.findNearLocation(testLocation, range);

        assertEquals(1, foundReports.size());
        verify(repository).findAll();
    }

    @Test
    public void findWeatherReportsByNameTest() {
        String reporterName = "reporter1";

        List<WeatherReport> reportsByName = new ArrayList<>();
        reportsByName.add(new WeatherReport(new GeoLocation(4.7110, 74.0721), 25
                , 60, reporterName, new Date()));

        when(repository.findByReporter(reporterName)).thenReturn(reportsByName);

        List<WeatherReport> foundReports = weatherService.findWeatherReportsByName(reporterName);

        assertEquals(reportsByName, foundReports);
        verify(repository).findByReporter(reporterName);
    }


}