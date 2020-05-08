package com.bracari.services.va.ehrmexammanagementservices.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XMLValidator {

    private static final Logger log = LoggerFactory.getLogger(XMLValidator.class);

    private Schema schema;
    private Validator validator;
    private static XMLValidator instance;

    private XMLValidator(Resource xsdPath) {
        SchemaFactory factory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            schema = factory.newSchema(xsdPath.getURL());
            validator = schema.newValidator();
        } catch (IOException | SAXException e) {
            log.error(e.getLocalizedMessage());
            schema = null;
            validator = null;
        }
    }

    public static XMLValidator getInstance(Resource xsdPath) {

        synchronized (XMLValidator.class)
        {
            if (instance == null)
            {
                // if instance is null, initialize
                instance = new XMLValidator(xsdPath);
            }
            return instance;
        }
    }

    synchronized public String validateXML(InputStream xml){

        if (schema == null || validator == null) {
            return "Invalid initialization of validator";
        }

        try {
            this.validator.validate(new StreamSource(new BufferedInputStream(xml)));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            return e.getLocalizedMessage();
        } catch (SAXParseException e) {
            log.error(e.getLocalizedMessage());
            return "Error Line[" + e.getLineNumber()  + "] Col[" + e.getColumnNumber() + "]: " + e.getLocalizedMessage();
        }
        catch (SAXException e) {
            log.error(e.getLocalizedMessage());
            return e.getLocalizedMessage();
        }
        return "";
    }


}
