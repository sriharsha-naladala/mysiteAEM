package com.mysite.core.models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class WeatherMod {

    private static final Logger Log = LoggerFactory.getLogger(WeatherMod.class);
//

//    private String lat;
//
//    private String lon;
@SlingObject
private SlingHttpServletRequest request;

//    @ValueMapValue
//    private String lat;
//
//    @ValueMapValue
//    private String longi;
    @Self
    private Resource resource;

    private String temperature;
    private String humidity;
    private String placeName;
    private String description;
    private String climate;
    private String Icon;
    private String windspeed;
    private String feelslike;
    private String pressure;
    private String min;
    private String max;
    private String country;
    private String date;
    private String sunset;
    private String sunrise;
    private String time;




    @PostConstruct
    protected void init() {
        String lat= request.getParameter("lat");
        String lon = request.getParameter("lon");
        try {
            // Prepare credentials
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials("admin", "admin") // Replace with actual username and password
            );
            try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build()) {
                // Construct the URL to call the servlet
                String apiUrl = "http://localhost:6502/bin/weatherdata?lat="+lat+"&lon="+lon;

                HttpGet httpGet = new HttpGet(apiUrl);
                HttpResponse httpResponse = httpClient.execute(httpGet);

                int statusCode = httpResponse.getStatusLine().getStatusCode();
                Log.info("HTTP Response Status: " + statusCode);

                if (statusCode == 200) {
                    String weatherData = EntityUtils.toString(httpResponse.getEntity());

                    JsonObject jsonObject = JsonParser.parseString(weatherData).getAsJsonObject();
                    temperature = jsonObject.get("temperature").getAsString();
                    humidity = jsonObject.get("humidity").getAsString();
                    placeName = jsonObject.get("placeName").getAsString();
                    description = jsonObject.get("description").getAsString();
                    climate = jsonObject.get("climate").getAsString();
                    Icon = jsonObject.get("icon").getAsString();
                    windspeed = jsonObject.get("windspeed").getAsString();
                    feelslike = jsonObject.get("feelslike").getAsString();
                    pressure = jsonObject.get("pressure").getAsString();
                    min = jsonObject.get("min").getAsString();
                    max = jsonObject.get("max").getAsString();
                    country = jsonObject.get("country").getAsString();
                    date = jsonObject.get("date").getAsString();
                    time = jsonObject.get("time").getAsString();
                    sunset = jsonObject.get("sunset").getAsString();
                    sunrise = jsonObject.get("sunrise").getAsString();
                } else {
                    Log.error("Failed to get valid response, status code: " + statusCode);
                    throw new RuntimeException("Failed to get valid response from weather servlet");
                }
            }
        } catch (Exception e) {
            Log.error("Error calling weather servlet", e);
        }
    }

    public String getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getDescription() {
        return description;
    }
    public String getPressure() {
        return pressure;
    }

    public String getIcon() {
        return Icon;
    }

    public String getClimate() {
        return climate;
    }

    public String getWindspeed() {
        return windspeed;
    }

    public String getFeelslike() {
        return feelslike;
    }

    public String getMin() {
        return min;
    }

    public String getCountry() {
        return country;
    }

    public String getMax() {
        return max;
    }
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getSunset() {
        return sunset;
    }

    public String getSunrise() {
        return sunrise;
    }
}
