package com.mysite.core.servlets;

import com.drew.lang.annotations.NotNull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import javax.jcr.Node;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.jcr.Session;

@Component(service = {Servlet.class}, immediate = true)
@SlingServletPaths(value = "/bin/testservlet")
public class TestServlet extends SlingAllMethodsServlet {

    String NODE_PATH = "/content/mysite/us/en/chichu/jcr:content";

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws ServletException, IOException {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);
        String lat = request.getParameter("lat");
        String lon = request.getParameter("lon");

        try {
            Node node = session.getNode(NODE_PATH);
            node.setProperty("test", "test");
            node.setProperty("lat", lat);
            node.setProperty("lon", lon);
            session.save();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        session.logout();

        response.sendRedirect("/content/mysite/us/en/chichu.html");

    }
}
