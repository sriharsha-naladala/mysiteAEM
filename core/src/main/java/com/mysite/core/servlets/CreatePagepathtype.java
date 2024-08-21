package com.mysite.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.tools.serialver.resources.serialver;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/createpage")
public class CreatePagepathtype extends SlingAllMethodsServlet {
    private static final Logger Log = LoggerFactory.getLogger(WeatherLat.class);
    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws ServletException, IOException {
        ResourceResolver resourceResolver = request.getResourceResolver();

        try {
            if (resourceResolver != null) {
                PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                String pageTitle = request.getParameter("title");
                String pageName = request.getParameter("name");
                Log.info("Received pageTitle: {}", pageTitle);
                Log.info("Received pageName: {}", pageName);
                if (pageTitle != null && pageName != null) {
                    String pagePath = "/content/mysite/us/en/" + pageName;
                    Page existingPage = pageManager.getPage(pagePath);
                    if (existingPage == null) {
                        String templatePath = "/conf/mysite/settings/wcm/templates/mysitetemp1";
                        Page newPage = pageManager.create("/content/mysite/us/en", pageName, templatePath, pageTitle);
                        response.getWriter().println("New page has been created under " + pagePath);
                    } else {
                        response.getWriter().println("Page with the specified name already exists");
                    }
                } else {
                    response.getWriter().println("Missing page title or name parameters");
                }
            }
        } catch (Exception e) {
            Log.error("Error in doGet method", e);
        } finally {
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        }

    }
}
