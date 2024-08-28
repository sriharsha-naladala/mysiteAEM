package com.mysite.core.schedulers;

import com.mysite.core.config.SchedulerConfig;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.util.Calendar;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = SchedulerConfig.class)
public class MyScheduler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(MyScheduler.class);

    @Reference
    private Scheduler scheduler;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private SlingRepository repository;

    private String schedulerName;
    private String cronExpression;
    private String contentPath;
    private int retentionPeriod;

    @Activate
    @Modified
    protected void activate(SchedulerConfig config) {
        this.schedulerName = config.schedulerName();
        this.cronExpression = config.cronExpression();
        this.contentPath = config.contentPath();
        this.retentionPeriod = config.retentionPeriod();

        ScheduleOptions options = scheduler.EXPR(this.cronExpression);
        options.name(this.schedulerName);
        options.canRunConcurrently(false);

        log.info("Scheduler '{}' activated with cron expression '{}', content path '{}', and retention period '{} seconds'",
                schedulerName, cronExpression, contentPath, retentionPeriod);

        scheduler.schedule(this, options);
    }

    @Override
    public void run() {
        log.info("Running cleanup task for content path '{}'", contentPath);

        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(null)) {
            Resource resource = resourceResolver.getResource(contentPath);

            if (resource != null) {
                log.info("Content path '{}' found. Starting to check child nodes.", contentPath);

                for (Resource child : resource.getChildren()) {
                    ValueMap properties = child.getValueMap();
                    Calendar lastModified = properties.get("jcr:lastModified", Calendar.class);

                    if (lastModified != null) {
                        log.debug("Checking node '{}', last modified on '{}'", child.getPath(), lastModified.getTime());

                        if (isOlderThanRetentionPeriod(lastModified)) {
                            Node node = child.adaptTo(Node.class);
                            if (node != null) {
                                log.info("Deleting node '{}'", child.getPath());
                                node.remove();
                                resourceResolver.commit();
                            }
                        } else {
                            log.debug("Node '{}' is within the retention period.", child.getPath());
                        }
                    } else {
                        log.warn("Node '{}' does not have a 'jcr:lastModified' property.", child.getPath());
                    }
                }
            } else {
                log.warn("Content path '{}' not found.", contentPath);
            }
        } catch (Exception e) {
            log.error("Error during cleanup task", e);
        }
    }

    private boolean isOlderThanRetentionPeriod(Calendar lastModified) {
        Calendar thresholdDate = Calendar.getInstance();
        thresholdDate.add(Calendar.SECOND, -retentionPeriod);
        return lastModified.before(thresholdDate);
    }
}
