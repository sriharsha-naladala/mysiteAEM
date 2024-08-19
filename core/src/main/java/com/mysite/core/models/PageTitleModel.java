package com.mysite.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
//import org.apache.sling.models.annotations.SlingObject;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PageTitleModel {

    @SlingObject
    private SlingHttpServletRequest request;

    public String getPageTitle() {
        Resource pageResource = request.getResource().getParent(); // Get parent resource
        if (pageResource != null) {
            String title = pageResource.getValueMap().get("jcr:title", "Default Title");
            return title;
        }
        return "Default Title";
    }
}
