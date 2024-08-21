package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.apache.sling.api.resource.ResourceResolver;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Objects;

@Component(service = Servlet.class)
@SlingServletResourceTypes(resourceTypes = "mysite/components/page", extensions = "html",selectors = "createPage")
public class CreatePageResTyp extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        final Logger LOG = LoggerFactory.getLogger(this.getServletName());
        try {
            ResourceResolver resolver = request.getResourceResolver();
            if (Objects.nonNull(resolver)) {
                PageManager pageManager = resolver.adaptTo(PageManager.class);
                String parentPagePath = "/content/mysite/us/en";
                Page parentPage = pageManager.getPage(parentPagePath);
                String template = "/conf/mysite/settings/wcm/templates/page-content";
                if (pageManager != null) {
                    String pageName = request.getParameter("pageName"), pageTitle = request.getParameter("pageTitle");

                    if (isPageNameAndTitleUnique(parentPage, pageName, pageTitle)) {
                        Page newPage = pageManager.create(parentPagePath, pageName, "/conf/mysite/settings/wcm/templates/page-content", pageTitle);
                        response.getWriter().println("New page has been created under " + parentPagePath);
                    } else {
                        response.getWriter().println("Page with the same name or title already exists. Please choose a different name or title.");
                    }
                } else {
                    response.getWriter().println("Getting pageManager object null in " + this.getServletName());
                }
            }
            response.getWriter().close();
            resolver.close();
        }catch(WCMException e){
            throw new RuntimeException(e);
        }
    }
    private boolean isPageNameAndTitleUnique(Page parentPage, String pageName, String pageTitle) {
        PageManager pageManager = parentPage.getPageManager();

        Page existingPageByName = pageManager.getPage(parentPage.getPath() + "/" + pageName);
        Page existingPageByTitle = pageManager.getPage(parentPage.getPath() + "/" + pageTitle);

        return existingPageByName == null && existingPageByTitle == null;
    }
}

