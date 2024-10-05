package com.example.carsharingapp.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UrlBuilder {
    @Value("${spring.scheme}")
    private String scheme;
    @Value("${spring.host}")
    private String host;

    public String getBuiltUrl(String path, String query) {
        UriComponents successUrl = UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .path(path)
                .query(query)
                .build();
        return successUrl.toUriString();
    }

    public String getBuiltUrl(String path) {
        UriComponents successUrl = UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .path(path)
                .build();
        return successUrl.toUriString();
    }
}
