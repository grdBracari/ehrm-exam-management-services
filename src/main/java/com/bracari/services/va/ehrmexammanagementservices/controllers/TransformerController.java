package com.bracari.services.va.ehrmexammanagementservices.controllers;

import com.altova.io.DocumentInput;
import com.altova.io.Input;
import com.bracari.services.mapforce.MappingMapToREF_I12;
import com.bracari.services.va.ehrmexammanagementservices.util.ResponseStatusGuidException;
import com.bracari.services.va.ehrmexammanagementservices.util.XMLValidator;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.Base64;
import java.util.Iterator;
import java.util.UUID;

import static org.springframework.http.MediaType.*;


@RestController
@RequestMapping("/services")
public class TransformerController {

    private static final Logger log = LoggerFactory.getLogger(TransformerController.class);

    // These will be null in the case of an open connection
    @Value("${server.ssl.key-store:classpath:keystore.p12}")
    private Resource keyStoreLocation;
    @Value("${server.ssl.key-store-password:}")
    private String keyStorePassword;
    @Value("${server.ssl.trust-store:classpath:truststore.p12}")
    private Resource trustStoreLocation;
    @Value("${server.ssl.trust-store-password:}")
    private String trustStorePassword;

    @Value("${xsd.claim.location}")
    private Resource xsdPath;

    private Document retrieveXml(MultipartFile file, String guid){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
        dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // compliant
        // Not setting a validator as we are doing a separate validation below
        dbFactory.setNamespaceAware(true);
        Document document;

        try (ByteArrayInputStream content = new ByteArrayInputStream(file.getBytes())) {
            String payload = IOUtils.toString(content, "UTF-8");
            log.debug(payload);
        } catch (IOException e) {
            log.error("IO error logging payload.",e);
        }

        try (BufferedInputStream stream = new BufferedInputStream(file.getInputStream())){
            dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.parse(stream);
        } catch (ParserConfigurationException e) {
            log.error("XML can't be parsed.", e);
            throw new ResponseStatusGuidException(
                    HttpStatus.BAD_REQUEST, "XML can't be parsed.", e, guid);
        } catch (SAXException e) {
            log.error("SAX parsing error.", e);
            throw new ResponseStatusGuidException(
                    HttpStatus.BAD_REQUEST,  "SAX parsing error.", e, guid);
        } catch (IOException e) {
            log.error("Buffered XML input failure.", e);
            throw new ResponseStatusGuidException(
                    HttpStatus.BAD_REQUEST,  "Buffered XML input failure.", e, guid);
        }

        Timer dbqValidTimer = Timer.builder("service.exam-management")
                .publishPercentiles(0.5, 0.95)
                .register(Metrics.globalRegistry);

        Counter dbqValidCounter = Counter.builder("service.exam-management.errors")
                .register(Metrics.globalRegistry);

        dbqValidTimer.record(() -> {
            // Check that document is valid against the claim schema
            try {
                XMLValidator xmlValidator = XMLValidator.getInstance(xsdPath);
                String validationReturn = xmlValidator.validateXML(file.getInputStream());
                if (!validationReturn.isEmpty()) {
                    log.error("XML is not valid against Claim schema: " + validationReturn);
                    dbqValidCounter.increment(1);
                    throw new ResponseStatusGuidException(
                            HttpStatus.BAD_REQUEST,  "XML is not valid against Claim schema: " + validationReturn, guid);
                }
            } catch (IOException e) {
                log.error("Buffered XML input failure.", e);
                throw new ResponseStatusGuidException(
                        HttpStatus.BAD_REQUEST,  "Buffered XML input failure.", e, guid);
            }
        });
        dbqValidCounter.close();
        dbqValidTimer.close();

        return document;
    }

    private byte[] transformXml(Document document, String guid) {

        byte[] dbq;
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, ""); // Compliant
            tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = tf.newTransformer();

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Result result = new StreamResult(output);
            Source domSource = new DOMSource(document);

            transformer.transform(domSource, result);
            dbq = output.toByteArray();

        } catch (TransformerException e) {
            log.error("Failure to package result.", e);
            throw new ResponseStatusGuidException(
                    HttpStatus.INTERNAL_SERVER_ERROR,  "Failure to package result.", e, guid);
        }
        return dbq;
    }

    @PostMapping(value = "exam-management", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_XML_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> processExamManagement(@RequestParam("file") MultipartFile file, HttpServletResponse response) {

        String guid = String.valueOf(UUID.randomUUID());
        MDC.put("guid", guid);

        String[] typeComponents;
        String contentType = file.getContentType();
        if (contentType != null) {
            typeComponents = contentType.split(";");
        } else {
            log.error("Could not get message content type.");
            throw new ResponseStatusGuidException(
                    HttpStatus.BAD_REQUEST,  "Could not get message content type.", guid);
        }


        if(!typeComponents[0].equals(TEXT_XML_VALUE) &&
                !typeComponents[0].equals(APPLICATION_XML_VALUE)) {
            log.error("Only " + TEXT_XML_VALUE + " or " + APPLICATION_XML_VALUE);
            throw new ResponseStatusGuidException(
                    HttpStatus.BAD_REQUEST,  " Only " + TEXT_XML_VALUE + " or " + APPLICATION_XML_VALUE, null, guid);
        }

        Document document = retrieveXml(file, guid);
//        byte[] dbq = transformXml(document, guid);
        MDC.remove("guid");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            MappingMapToREF_I12 MappingMapToREF_I12Object = new MappingMapToREF_I12();
            com.altova.io.Input ExamManagement_3_12Source = new com.altova.io.DocumentInput(document); //com.altova.io.StreamInput.createInput("C:/Projects/VBMS-CS2/ExamManagement-3.1beta2.iepd/ExamManagement-3.1beta2.iepd/XMLsamples/3.1/basic_exam_schedule_request/1_ExamSchedulingRequestCreatedEvent.xml");
            com.altova.io.Output REF_I12Target = new com.altova.io.StreamOutput(buffer);

                    new com.altova.io.FileOutput("REF_I12.hl7");

            try {
                MappingMapToREF_I12Object.run(
                        ExamManagement_3_12Source,
                        REF_I12Target);

            } finally {
                (ExamManagement_3_12Source).close();
                REF_I12Target.close();
            }

        }  catch (com.altova.UserException ue) {
            System.err.print("USER EXCEPTION:");
            System.err.println( ue.getMessage() );
            System.exit(1);
        } catch (com.altova.AltovaException e) {
            System.err.print("ERROR: ");
            System.err.println( e.getMessage() );
            if (e.getInnerException() != null) {
                System.err.print("Inner exception: ");
                System.err.println(e.getInnerException().getMessage());
                if (e.getInnerException().getCause() != null) {
                    System.err.print("Cause: ");
                    System.err.println(e.getInnerException().getCause().getMessage());
                }
            }
            System.err.println("\nStack Trace: ");
            e.printStackTrace();
            System.exit(1);
        }catch (Exception e) {
            System.err.print("ERROR: ");
            System.err.println(e.getMessage());
            System.err.println("\nStack Trace: ");
            e.printStackTrace();
            System.exit(1);
        }


        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(buffer.toByteArray());
    }


}
