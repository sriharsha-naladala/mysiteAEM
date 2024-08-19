package com.mysite.core.models;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.jcr.Node;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
//import org.apache.sling.api.resource.Resource;

//import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Required;
import org.apache.sling.models.annotations.injectorspecific.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import javax.annotation.Resources;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

//@Model(adaptables = Resource.class)
@Model(adaptables = {SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Authorimpl implements Author {
    private static final Logger Log = LoggerFactory.getLogger(Authorimpl.class);

    @ScriptVariable
    private Page currentPage;
    @ScriptVariable
    private Page page;



    @SlingObject
    private SlingHttpServletRequest request;


    @ValueMapValue
    @Required
    @Default(values = "hello give your name in AEM")
    private String textfield;

    @ValueMapValue
    private String datepicker;

    @ValueMapValue
    private String pathBrowser;

    @ValueMapValue
    private String password;
    @SlingObject
    private ResourceResolver resourceResolver;


    private static final String PAGE_PATH = "/content/mysite/us/en/4rwtw";

    public String getContentProperty() {
        if (PAGE_PATH != null) {
            // Obtain the ResourceResolver from the request
            ResourceResolver resourceResolver = request.getResourceResolver();
            if (resourceResolver != null) {
                // Fetch the Resource corresponding to jcr:content node
                Resource resource = resourceResolver.getResource(PAGE_PATH + "/jcr:content");
                if (resource != null) {
                    // Get the value of the specified property
                    return resource.getValueMap().get("jcr:title", String.class);
                }
            }
        }
        return null;
    }
    @Override
    public String getFname() {
        return textfield;
    }

    @Override
    public String getMyDate() {
        if (datepicker != null && !datepicker.isEmpty()) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(datepicker);
                datepicker = new SimpleDateFormat("MMMM dd, yyyy").format(date);
            } catch (ParseException e) {
                // Handle the parsing exception
                System.err.println("Error parsing date: " + e.getMessage());
            }
        }
        return datepicker;
    }

    @Override
    public String getFilePath() {
        return pathBrowser;
    }

    @Override
    public String getPass(){
        return password;

    }

    public String getCurrentNodeTitle() {
        try {
            Resource resource = request.getResource();
            Node currentNode = resource.adaptTo(Node.class);

            if (currentNode != null) {
                return currentNode.getProperty("jcr:title").getString();
            }
        } catch (RepositoryException e) {
            Log.error("Error accessing current node: {}", e.getMessage());
        }
        return null;
    }
    public String getCurrentSession(){

            ResourceResolver resourceResolver = request.getResourceResolver();
            Session currentSession = resourceResolver.adaptTo(Session.class);
            if(currentSession !=null){
                String userid =currentSession.getUserID();
                String attributes = (String) currentSession.getAttribute("ip");
                String s = ("your session id " + userid + " your attributes " + attributes);
                return s;
            }

        return null;
    }

}

