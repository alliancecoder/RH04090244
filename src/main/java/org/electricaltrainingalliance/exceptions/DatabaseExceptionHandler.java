package org.electricaltrainingalliance.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class DatabaseExceptionHandler {

    ExceptionChainSearcher exceptionChainSearcher = new ExceptionChainSearcher();

    public Response handleDatabaseException(Exception ex, String constraintCheck, String conflictMessage, String defaultMessage) {
        if (exceptionChainSearcher.exceptionChainContains(ex, constraintCheck)) {
            return Response.status(Status.CONFLICT).entity(conflictMessage).build();
        }
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(defaultMessage).build();
    }
    
}
