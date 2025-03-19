package org.electricaltrainingalliance.exceptions;

public class ExceptionChainSearcher {

    public boolean exceptionChainContains(Throwable throwable, String searchTerm) {
        while (throwable != null) {
            if (throwable.getMessage() != null && throwable.getMessage().toLowerCase().contains(searchTerm.toLowerCase())) {
                return true;
            }
            throwable = throwable.getCause();
        }
        return false;
    }
    
    
}
