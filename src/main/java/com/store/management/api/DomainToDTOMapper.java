package com.store.management.api;

import com.store.management.api.dto.ProductDTO;
import com.store.management.model.Category;
import com.store.management.model.Product;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DomainToDTOMapper {
    public ProductDTO mapProductToDTO(Product p){
        return ProductDTO.builder()
                .productName(p.getName())
                .productSupplierName(p.getSupplier().getName())
                .categories(p.getCategories().stream().map(Category::getCategoryName).collect(Collectors.toList()))
                .price(p.getPrice())
                .description(p.getDescription())
                .color(p.getProductSpec().getColor())
                .capacity(p.getProductSpec().getCapacity())
                .build();
    }
}
