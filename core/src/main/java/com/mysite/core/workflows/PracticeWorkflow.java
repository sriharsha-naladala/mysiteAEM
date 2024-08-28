package com.mysite.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.Iterator;
import java.util.Set;

@Component(
        service = WorkflowProcess.class,
        immediate = true,
        property = {
                "process.label=Mysite Practice Workflow Process",
                Constants.SERVICE_VENDOR + "=AEM Mysite",
                Constants.SERVICE_DESCRIPTION + "=custom workflow step."
        }
)
public class PracticeWorkflow implements WorkflowProcess {
    private static final Logger log = LoggerFactory.getLogger(PracticeWorkflow.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments) {
        log.info("Starting the Create Page Workflow Process");
        log.info("==============================================");

        Session session = null;
        try {
            WorkflowData workflowData = workItem.getWorkflowData();
            if ("JCR_PATH".equals(workflowData.getPayloadType())) {
                session = workflowSession.adaptTo(Session.class);
                String path = workflowData.getPayload().toString() + "/jcr:content";
                Node node = (Node) session.getItem(path);
                log.info(path,"==============================================");

                if (node != null) {
                    String[] processArgs = processArguments.get("PROCESS_ARGS", "string").toString().split(",");
                    for (String wfargs : processArgs) {
                        String[] args = wfargs.split(":");
                        String prop = args[0];
                        String value = args[1];
                        node.setProperty(prop, value);
                        log.info(prop,"prop ---------");

                    }

                    MetaDataMap wfd = workItem.getWorkflow().getWorkflowData().getMetaDataMap();
                    Set<String> keyset = wfd.keySet();
                    Iterator<String>i=keyset.iterator();
                    while (i.hasNext())
                    {
                        log.info("==============================================");
                        String key =i.next();
                        log.info("/n ITEM Key -{},Value-{}",key,wfd.get(key));
                    }
                    session.save();
                }
            }
        } catch (Exception e) {
            log.error("Error in workflow process", e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }
}
