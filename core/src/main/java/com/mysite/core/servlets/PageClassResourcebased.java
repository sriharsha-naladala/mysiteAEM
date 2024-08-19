package com.mysite.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
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

@Component(service = Servlet.class)
@SlingServletResourceTypes(
        resourceTypes = "mysite/components/page",
        selectors = {"add","customsub"},
        extensions = {"txt","json","xml"}
)
public class PageClassResourcebased extends SlingAllMethodsServlet {
    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws ServletException, IOException {
        String pagepath = request.getParameter("pagepath");
        if(pagepath ==null){
            pagepath = "/content/mysite/us/en";
        }
        ResourceResolver resourceResolver = request.getResourceResolver();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page page = pageManager.getPage(pagepath);

        Iterator<Page> childPages = page.listChildren();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        while(childPages.hasNext())
        {
            Page next = childPages.next();
            JsonObjectBuilder job = Json.createObjectBuilder();
            job.add("Title",next.getTitle());
            job.add("path",next.getPath());
            arrayBuilder.add(job);
        }
        response.getWriter().write(arrayBuilder.build().toString());

    }
}
