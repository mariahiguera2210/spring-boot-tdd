package org.adaschool.tdd.service;

import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.exception.WeatherReportNotFoundException;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MongoWeatherService
        implements WeatherService {

    private final WeatherReportRepository repository;

    public MongoWeatherService(@Autowired WeatherReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public WeatherReport report(WeatherReportDto weatherReportDto) {
        WeatherReport weatherReport = new WeatherReport(
                weatherReportDto.getGeoLocation(),
                weatherReportDto.getTemperature(),
                weatherReportDto.getHumidity(),
                weatherReportDto.getReporter(),
                weatherReportDto.getCreated()
        );
        return repository.save(weatherReport);
    }

    @Override
    public WeatherReport findById(String id) {
        Optional<WeatherReport> weatherReportOptional = repository.findById(id);

        if (weatherReportOptional.isPresent())
            return weatherReportOptional.get();
        throw new WeatherReportNotFoundException("No weather report found with id: " + id);
    }

    @Override
    public List<WeatherReport> findNearLocation(GeoLocation geoLocation, float distanceRangeInMeters) {
        List<WeatherReport> allWeatherReports = repository.findAll();
        return allWeatherReports.stream()
                .filter(report -> isWithinRange(geoLocation, report.getGeoLocation(), distanceRangeInMeters))
                .collect(Collectors.toList());
    }

    private boolean isWithinRange(GeoLocation source, GeoLocation destination, float rangeInMeters) {
        double distance = calculateDistanceInMeters(source, destination);
        return distance <= rangeInMeters;
    }

    private double calculateDistanceInMeters(GeoLocation source, GeoLocation destination) {
        final int R = 6371;
        double latDistance = Math.toRadians(destination.getLat() - source.getLat());
        double lngDistance = Math.toRadians(destination.getLng() - source.getLng());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(source.getLat())) * Math.cos(Math.toRadians(destination.getLat()))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000;
    }

    @Override
    public List<WeatherReport> findWeatherReportsByName(String reporter) {
        return repository.findByReporter(reporter);
    }
}
