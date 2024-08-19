package com.mysite.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Weather API Configuration")
public @interface WeatherApiConfig {

    @AttributeDefinition(name = "API Key", description = "API key for accessing the weather API")
    String apiKey() default "b2414ba1e88d59ea74dd45ec33b4614a";

    @AttributeDefinition(name = "Base URL", description = "Base URL for the weather API")
    String apiUrl() default "https://api.openweathermap.org/data/2.5/weather?zip=%s,in&appid=%s&units=imperial";
}

