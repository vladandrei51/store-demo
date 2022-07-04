package com.store.management.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductDTO {
    private String productName;
    private String productSupplierName;
    private List<String> categories;
    private int price;
    private String description;
    private String color;
    private String capacity;
}
