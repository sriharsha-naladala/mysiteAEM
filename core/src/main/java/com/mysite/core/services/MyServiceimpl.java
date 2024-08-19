package com.mysite.core.services;

import com.mysite.core.services.MyService;
import org.osgi.service.component.annotations.Component;


@Component(service = MyService.class)
public class MyServiceimpl implements MyService {
    @Override
    public String getData() {
        return "Data from OSGi service";
    }
}

