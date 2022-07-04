package com.store.management.api.errorhandling;

import org.springframework.util.StringUtils;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Class clazz, String param, String value) {
        super(ProductNotFoundException.generateMessage(clazz.getSimpleName(), param, value));
    }

    private static String generateMessage(String entity, String param, String value) {
        return StringUtils.capitalize(entity) +
                " was not found for parameter {" + param + "} = " + value;
    }
}
