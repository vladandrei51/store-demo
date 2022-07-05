package com.store.management.utils;

import com.store.management.api.dto.ProductDTO;
import com.store.management.model.Category;
import com.store.management.model.Product;
import com.store.management.model.ProductSpec;
import com.store.management.model.Supplier;

import java.util.HashSet;
import java.util.Set;

import static com.store.management.utils.ProductConstants.*;

public class DummyObjectsCreationUtil {
    public static ProductDTO createDummyProductDTO() {
        return ProductDTO
                .builder()
                .productName(PRODUCT_NAME_DUMMY)
                .capacity(PRODUCT_CAPACITY_DUMMY)
                .color(PRODUCT_COLOR_DUMMY)
                .categories(PRODUCT_CATEGORIES_DUMMY)
                .price(PRODUCT_PRICE_DUMMY)
                .description(PRODUCT_DESCRIPTION_DUMMY)
                .productSupplierName(PRODUCT_SUPPLIER_NAME_DUMMY)
                .build();
    }

    public static Product createDummyProduct() {
        Product product = new Product();
        product.setName(PRODUCT_NAME_DUMMY);
        Supplier supplier = new Supplier();
        supplier.setName(PRODUCT_SUPPLIER_NAME_DUMMY);
        product.setSupplier(supplier);
        ProductSpec productSpec = new ProductSpec();
        productSpec.setColor(PRODUCT_COLOR_DUMMY);
        productSpec.setCapacity(PRODUCT_CAPACITY_DUMMY);
        product.setProductSpec(productSpec);
        Set<Category> categories = new HashSet<>();
        PRODUCT_CATEGORIES_DUMMY.forEach(x -> categories.add(new Category(x)));
        product.setCategories(categories);
        product.setPrice(PRODUCT_PRICE_DUMMY);
        product.setDescription(PRODUCT_DESCRIPTION_DUMMY);
        return product;
    }

}
