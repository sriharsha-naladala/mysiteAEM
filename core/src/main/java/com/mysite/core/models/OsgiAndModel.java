package com.mysite.core.models;

import com.mysite.core.services.MyService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class)
public class OsgiAndModel {

    @OSGiService
     MyService myService;

    private String message;

    @PostConstruct
    protected void init() {
        message = myService.getData();
    }

    public String getServiceData() {
        return message;
    }
}

