package com.mysite.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Component(service = Servlet.class)
@SlingServletResourceTypes(
        resourceTypes = "mysite/components/page",
        selectors = "allpageproperties",
        extensions = {"txt", "json", "xml"}
)
public class PageClassResourceBased extends SlingAllMethodsServlet {

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws ServletException, IOException {
        String pagepath = request.getParameter("pagepath");
        if (pagepath == null) {
            pagepath = "/content/mysite/us/en";
        }

        ResourceResolver resourceResolver = request.getResourceResolver();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        assert pageManager != null;
        Page page = pageManager.getPage(pagepath);

        Iterator<Page> childPages = page.listChildren();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        while (childPages.hasNext()) {
            Page next = childPages.next();
            JsonObjectBuilder job = Json.createObjectBuilder();
            job.add("Title", next.getTitle());
            job.add("path", next.getPath());

            // for Retrieving the jcr:content resource of the page
            Resource contentResource = next.getContentResource();
            if (contentResource != null) {
                // Get all properties of the jcr:content node
                Map<String, Object> properties = contentResource.getValueMap();

                // Iterating over properties and add them to the JSON object
                for (Map.Entry<String, Object> entry : properties.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    job.add(key,value.toString());  //job.add(key, value != null ? value.toString() : "");
                }
            }

            arrayBuilder.add(job);
        }

        response.setContentType("application/json");
        response.getWriter().write(arrayBuilder.build().toString());
    }
}


