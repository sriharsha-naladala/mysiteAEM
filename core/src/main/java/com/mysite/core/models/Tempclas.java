package com.mysite.core.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysite.core.services.WeatherApiServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Tempclas {
    private static final Logger Log = LoggerFactory.getLogger(Tempclas.class);

    @ValueMapValue
    private String pincode;

    @OSGiService
    private WeatherApiServiceImpl weatherApiConfig;

    private String temperature;
    private String placename;
    private String climate_status;
    private String description;
    private String pressure;
    private String humidity;
    private String seaLevel;
    private String grandLevel;
    private String sunrise;
    private String sunset;
    private String speed;
    private String degree;
    private String feelslike;
    private String icon;

    @PostConstruct
    public void init() {
        if (pincode != null && !pincode.isEmpty()) {
            try {
                String apiKey = weatherApiConfig.getApiKey();
                String baseUrl = weatherApiConfig.getApiUrl();
                String apiUrl = String.format(baseUrl, pincode, apiKey);
                Log.info("api url ....."+apiUrl);

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream responseStream = connection.getInputStream();
                Log.info("connection ........"+ connection);
                String response = IOUtils.toString(responseStream, StandardCharsets.UTF_8);
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                Log.info("object ......."+jsonObject);
                // Parsing the main JSON fields
                JsonObject main = jsonObject.getAsJsonObject("main");
                placename = jsonObject.get("name").getAsString();
                temperature = main.get("temp").getAsString();
                pressure = main.get("pressure").getAsString();
                humidity = main.get("humidity").getAsString();
                feelslike = main.get("feels_like").getAsString();
                // Optional fields
                if (main.has("sea_level")) {
                    seaLevel = main.get("sea_level").getAsString();
                }
                if (main.has("grnd_level")) {
                    grandLevel = main.get("grnd_level").getAsString();
                }

                // Parsing weather array
                JsonArray weatherArray = jsonObject.getAsJsonArray("weather");
                if (weatherArray != null && !weatherArray.isEmpty()) {
                    JsonObject weather = weatherArray.get(0).getAsJsonObject();
                    climate_status = weather.get("main").getAsString();
                    description = weather.get("description").getAsString();
                    icon = weather.get("icon").getAsString();

                }

                // Parsing sys and wind objects
                JsonObject sys = jsonObject.getAsJsonObject("sys");
                sunrise = sys.get("sunrise").getAsString();
                sunset = sys.get("sunset").getAsString();

                JsonObject wind = jsonObject.getAsJsonObject("wind");
                speed = wind.get("speed").getAsString();
                degree = wind.get("deg").getAsString();

            } catch (Exception e) {
                Log.error("Error fetching weather data", e);
            }
        }
    }

    public String getTemperature() {
        return temperature;
    }

    public String getPlacename() {
        return placename;
    }

    public String getClimateStatus() {
        return climate_status;
    }

    public String getDescription() {
        return description;
    }

    public String getPressure() {
        return pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getSeaLevel() {
        return seaLevel;
    }

    public String getGrandLevel() {
        return grandLevel;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public String getSpeed() {
        return speed;
    }

    public String getDegree() {
        return degree;
    }
    public String getFeelslike(){return feelslike; }
    public String getIcon(){return icon;}
}
