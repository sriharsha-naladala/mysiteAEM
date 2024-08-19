package com.mysite.core.services;

import com.mysite.core.config.WeatherApiConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = WeatherApiServiceImpl.class)
@Designate(ocd = WeatherApiConfig.class)
public class WeatherApiServiceImpl {

    private volatile String apiKey;
    private volatile String apiUrl;



    @Activate
    @Modified
    protected void activate(WeatherApiConfig config) {
        this.apiKey = config.apiKey();
        this.apiUrl = config.apiUrl();
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

}
