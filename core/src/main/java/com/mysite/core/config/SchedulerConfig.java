package com.mysite.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Cleanup Scheduler Configuration")
public @interface SchedulerConfig {

    @AttributeDefinition(
            name = "Scheduler Name",
            description = "Name of the scheduler"
    )
    String schedulerName() default "Cleanup Scheduler";

    @AttributeDefinition(
            name = "Cron Expression",
            description = "Cron expression to define the schedule"
    )
    String cronExpression() default "0/20 * * * * ?"; // Runs every 30 seconds

    @AttributeDefinition(
            name = "Content Path",
            description = "Path of the content to clean up"
    )
    String contentPath() default "/content/mysite/us/en/4rwtw";

    @AttributeDefinition(
            name = "Retention Period (seconds)",
            description = "Number of seconds to retain content before deletion"
    )
    int retentionPeriod() default 30;
}
