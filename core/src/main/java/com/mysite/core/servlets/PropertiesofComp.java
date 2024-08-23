package com.mysite.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.drew.lang.annotations.NotNull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Component(service = Servlet.class)
@SlingServletResourceTypes(
        resourceTypes = "mysite/components/page",
        selectors = {"allpageallcomp"},
        extensions = {"txt", "json", "xml"}
)
public class PropertiesofComp extends SlingAllMethodsServlet {
    public static final Logger log = LoggerFactory.getLogger(PropertiesofComp.class);
    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws IOException {
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
            JsonObjectBuilder pageJson = Json.createObjectBuilder();
            pageJson.add("Title", next.getTitle());
            pageJson.add("Path", next.getPath());

            // Retrieve the jcr:content resource of the page
            Resource contentResource = next.getContentResource();
            if (contentResource != null) {
                // Iterate over the components under jcr:content
                Iterator<Resource> componentResources = contentResource.listChildren();
                JsonArrayBuilder componentsArray = Json.createArrayBuilder();

                while (componentResources.hasNext()) {
                    Resource componentResource = componentResources.next();
                    JsonObjectBuilder componentJson = Json.createObjectBuilder();
                    componentJson.add("ComponentName", componentResource.getName());

                    // Get all properties of the component
                    Map<String, Object> properties = componentResource.getValueMap();
                    log.info("mapvalues "+properties);
                    for (Map.Entry<String, Object> entry : properties.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        componentJson.add(key, value != null ? value.toString() : "");
                    }

                    componentsArray.add(componentJson);
                }

                pageJson.add("Components", componentsArray);
            }

            arrayBuilder.add(pageJson);
        }

        response.setContentType("application/json");
        response.getWriter().write(arrayBuilder.build().toString());
    }
}
