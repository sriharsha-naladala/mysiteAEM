package com.mysite.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.Rendition;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


@Component(service = WorkflowProcess.class,
        immediate = true,
        property = {
                "process.label=Generate PDF and Store in DAM",
                Constants.SERVICE_VENDOR + "=AEM Mysite",
                Constants.SERVICE_DESCRIPTION + "=Custom PDF Generation Process"
        })
public class PDFGenerationProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(PDFGenerationProcess.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        ResourceResolver resourceResolver = null;
        PDDocument document = null;
        try {
            // 1. Get the page content or resource path
            resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
            if (resourceResolver == null) {
                throw new WorkflowException("Could not adapt WorkflowSession to ResourceResolver.");
            }

            Resource pageResource = resourceResolver.getResource("/content/us/en/jcr:content");
            if (pageResource == null) {
                throw new WorkflowException("Could not find resource: /content/us/en/jcr:content");
            }

            ValueMap properties = pageResource.adaptTo(ValueMap.class);
            String pageTitle = properties.get("jcr:title", "Default Title");
            String pageContent = properties.get("jcr:description", "No content available");

            // 2. Use a PDF library to convert the content to PDF
            document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Title: " + pageTitle);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Content: " + pageContent);
                contentStream.endText();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

            // 3. Store the generated PDF in the DAM
            AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
            if (assetManager == null) {
                throw new WorkflowException("Could not adapt ResourceResolver to AssetManager.");
            }

            String pdfPath = "/content/dam/generated-pdfs/page-content.pdf";
            Asset pdfAsset = assetManager.createAsset(pdfPath, null, "application/pdf", true);

            // Add content to the asset
            try (InputStream pdfInputStream = new ByteArrayInputStream(pdfBytes)) {
                Rendition originalRendition = pdfAsset.addRendition("original", pdfInputStream, "application/pdf");
                log.info("PDF generated and stored in DAM at {}", pdfPath);
            }

        } catch (IOException e) {
            log.error("Error generating PDF", e);
            throw new WorkflowException("Error generating PDF", e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    log.error("Error closing PDDocument", e);
                }
            }
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        }
    }
}
