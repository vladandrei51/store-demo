package com.store.management.api.errorhandling;

import org.springframework.util.StringUtils;

public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException(Class clazz, String param, String value) {
        super(RecordNotFoundException.generateMessage(clazz.getSimpleName(), param, value));
    }

    private static String generateMessage(String entity, String param, String value) {
        return StringUtils.capitalize(entity) + " was not found for parameter {" + param + "} = " + value;
    }
}
