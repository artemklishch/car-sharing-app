package com.example.carsharingapp.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.net.MalformedURLException;
import java.net.URL;

@Converter(autoApply = true)
public class UrlConverter implements AttributeConverter<URL, String> {
    @Override
    public String convertToDatabaseColumn(URL url) {
        return (url != null) ? url.toString() : null;
    }

    @Override
    public URL convertToEntityAttribute(String dbData) {
        try {
            return (dbData != null) ? new URL(dbData) : null;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL in the database: " + dbData, e);
        }
    }
}
