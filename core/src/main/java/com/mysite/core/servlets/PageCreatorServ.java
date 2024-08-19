package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = Servlet.class)
@SlingServletResourceTypes(
        resourceTypes = "/mysite/components/page",
        selectors = "one",
        extensions = {"txt","html","json"}
)
public class PageCreatorServ extends SlingAllMethodsServlet {
    @Override
    protected void doPost(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws ServletException, IOException {
        ResourceResolver resourceResolver = request.getResourceResolver();

        String parentpath = request.getParameter("parentpagepath");
        String pageName = request.getParameter("pageName");      // Name of the new page
        String pageTitle = request.getParameter("pageTitle");

        Resource parentResource = resourceResolver.getResource(parentpath);
        if (parentResource == null) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Parent path does not exist.");
            return;
        }

        Map<String, Object> pageProperties = new HashMap<>();
        pageProperties.put("jcr:primaryType", "cq:Page");
        // Create a new page node under the parent
        Resource newPage = resourceResolver.create(parentResource, pageName, pageProperties);

        Map<String, Object> contentProperties = new HashMap<>();
//         Map<String, Object> contentProperties = new HashMap<>();
        contentProperties.put("jcr:primaryType", "cq:PageContent");
        contentProperties.put("jcr:title", pageTitle);
        contentProperties.put("sling:resourceType", "mysite/components/page");
        Resource contentresource = resourceResolver.create(newPage,"jcr:content",contentProperties);
        resourceResolver.commit();
        response.setContentType("application/json");
        response.getWriter().write("page created sucessfully"+newPage.getPath());

    }
}


