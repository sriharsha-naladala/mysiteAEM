package com.mysite.core.servlets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
//import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/weatherdata")
public class WeatherLat extends SlingAllMethodsServlet {
    private static final Logger Log = LoggerFactory.getLogger(WeatherLat.class);

    private static final String API_KEY = "b2414ba1e88d59ea74dd45ec33b4614a"; // Replace with your actual API key
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=imperial";

    @Override
    public void doGet( SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String latitude = request.getParameter("lat");
        String longitude = request.getParameter("lon");

        if (latitude != null && longitude != null) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                String apiUrl = String.format(API_URL, latitude, longitude, API_KEY);
                Log.info("Weather API URL: " + apiUrl);
                HttpGet httpGet = new HttpGet(apiUrl);
                HttpResponse httpResponse = httpClient.execute(httpGet);

                String weatherData = EntityUtils.toString(httpResponse.getEntity());

                JsonObject jsonObject = JsonParser.parseString(weatherData).getAsJsonObject();
                JsonObject main = jsonObject.getAsJsonObject("main");
                JsonObject weather = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject();
                JsonObject wind = jsonObject.getAsJsonObject("wind");
                JsonObject sys = jsonObject.getAsJsonObject("sys");

                String temperature = main.get("temp").getAsString();
                String humidity = main.get("humidity").getAsString();
                String placeName = jsonObject.get("name").getAsString();
                String description = weather.get("description").getAsString();
                String climate = weather.get("main").getAsString();
                String Icon = weather.get("icon").getAsString();
                String windspeed = wind.get("speed").getAsString();
                String feelslike = main.get("feels_like").getAsString();
                String pressure = main.get("pressure").getAsString();
                String min = main.get("temp_min").getAsString();
                String max = main.get("temp_max").getAsString();
                String country = sys.get("country").getAsString();

                // Convert timestamps
                long dateTimestamp = jsonObject.get("dt").getAsLong();
                long sunriseTimestamp = sys.get("sunrise").getAsLong();
                long sunsetTimestamp = sys.get("sunset").getAsLong();
                String sunrise = convertTimestampToTime(sunriseTimestamp);
                String sunset = convertTimestampToTime(sunsetTimestamp);
                String date = convertTimestampToDate(dateTimestamp);
                String time = convertTimestampToTime(dateTimestamp);

                JsonObject jsonResponseObj = new JsonObject();
                jsonResponseObj.addProperty("temperature", temperature);
                jsonResponseObj.addProperty("humidity", humidity);
                jsonResponseObj.addProperty("placeName", placeName);
                jsonResponseObj.addProperty("description", description);
                jsonResponseObj.addProperty("climate", climate);
                jsonResponseObj.addProperty("icon", Icon);
                jsonResponseObj.addProperty("windspeed", windspeed);
                jsonResponseObj.addProperty("feelslike", feelslike);
                jsonResponseObj.addProperty("pressure", pressure);
                jsonResponseObj.addProperty("min", min);
                jsonResponseObj.addProperty("max", max);
                jsonResponseObj.addProperty("country", country);
                jsonResponseObj.addProperty("sunrise", sunrise);
                jsonResponseObj.addProperty("sunset", sunset);
                jsonResponseObj.addProperty("date", date);
                jsonResponseObj.addProperty("time",time);

                response.setContentType("application/json");
                response.getWriter().write(jsonResponseObj.toString());
                response.sendRedirect("");
            } catch (Exception e) {
                Log.error("Error fetching weather data", e);
                response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to fetch weather data.");
            }
        } else {
            response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, "Missing latitude or longitude parameter.");
        }
    }
    private String convertTimestampToDate(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }
    private String convertTimestampToTime(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }
}
