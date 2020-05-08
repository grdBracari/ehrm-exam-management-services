package com.bracari.services.va.ehrmexammanagementservices.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResponseStatusGuidException extends ResponseStatusException {
    public ResponseStatusGuidException(HttpStatus status) {
        super(status);
    }

    public ResponseStatusGuidException(HttpStatus status, String reason, String guid) {
        super(status, "<" + guid + "> " + reason);
    }

    public ResponseStatusGuidException(HttpStatus status, String reason, Throwable cause, String guid) {
        super(status, "<" + guid + "> " + reason, cause);
    }
}
