package com.mysite.core.models;

import com.mysite.core.services.WeatherApiServiceImpl;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceNotFoundException;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;

@Model(adaptables = SlingHttpServletRequest.class)
public class SlingservSlingmod {
    private static final Logger Log = LoggerFactory.getLogger(Tempclas.class);
    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private WeatherApiServiceImpl Weatherapiconf;

    @ValueMapValue
    private String pincode;

    public String getPincode() {
        return pincode;
    }


    private String servletResponse;



    @PostConstruct void init() throws ProtocolException {
        try{
            String baseurl = Weatherapiconf.getApiUrl();
            String pincode= getPincode();
            String key = Weatherapiconf.getApiKey();
            String apiUrl = baseurl+"?zip="+pincode+",in&appid="+key+"&units=imperial";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");


            servletResponse = "have a good day";

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (ResourceNotFoundException e) {
            throw e; // This will cause a 404 error in AEM

        }catch(Exception e){


            Log.info(servletResponse,e);
        }
    }

    public String getServletResponse(){
        return servletResponse;
    }

}

