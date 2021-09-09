package org.adaschool.tdd.repository.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

@Document
public class WeatherReport
{
    @Id
    String id;

    GeoLocation geoLocation;

    double temperature;

    double humidity;

    @Indexed( unique = true )
    String reporter;

    Date created;

    public WeatherReport( GeoLocation geoLocation, double temperature, double humidity, String reporter, Date created )
    {
        this.geoLocation = geoLocation;
        this.temperature = temperature;
        this.humidity = humidity;
        this.reporter = reporter;
        this.created = created;
    }

    public GeoLocation getGeoLocation()
    {
        return geoLocation;
    }

    public double getTemperature()
    {
        return temperature;
    }

    public double getHumidity()
    {
        return humidity;
    }

    public String getReporter()
    {
        return reporter;
    }

    public Date getCreated()
    {
        return created;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        WeatherReport that = (WeatherReport) o;
        return Double.compare( that.temperature, temperature ) == 0 && Double.compare( that.humidity, humidity ) == 0
            && Objects.equals( id, that.id ) && Objects.equals( geoLocation, that.geoLocation ) && Objects.equals(
            reporter, that.reporter ) && Objects.equals( created, that.created );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, geoLocation, temperature, humidity, reporter, created );
    }
}
