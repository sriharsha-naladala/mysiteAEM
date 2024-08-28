package com.mysite.core.schedulers;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

@Component(
        service = { Runnable.class },
        immediate = true
)
public class SchedulerForPageCreation implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerForPageCreation.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Scheduler scheduler;

    private static final String SCHEDULER_JOB_NAME = "page creation job";

    @Activate
    protected void activate() {
        ScheduleOptions options = scheduler.EXPR("*/10 * * * * ?")
                .name(SCHEDULER_JOB_NAME)
                .canRunConcurrently(false);
        scheduler.schedule(this, options);
        LOG.info("Scheduled task '{}' activated.", SCHEDULER_JOB_NAME);
    }
    @Deactivate
    protected void deactivate() {
        scheduler.unschedule(SCHEDULER_JOB_NAME);
        LOG.info("Scheduled task '{}' deactivated.", SCHEDULER_JOB_NAME);
    }

    @Override
    public void run() {
        createPage();
    }

    private void createPage() {
        LOG.info("Running Page Creation Task");

        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "writeService");

        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param)) {
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            if (pageManager != null) {
                String parentPath = "/content/mysite/us/en";
                String pageName = "page-" + System.currentTimeMillis();
                String templatePath = "/conf/mysite/settings/wcm/templates/task1";  // Make sure this path is correct

                Page page = pageManager.create(parentPath, pageName, templatePath, pageName);

                if (page != null) {
                    LOG.info("Page created: {}", page.getPath());
                } else {
                    LOG.error("Page creation returned null");
                }
            } else {
                LOG.error("PageManager could not be adapted from ResourceResolver");
            }
        } catch (Exception e) {
            LOG.error("Error creating page: {}", e.getMessage(), e);
        }
    }
}
