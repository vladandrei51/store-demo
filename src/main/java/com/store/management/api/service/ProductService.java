package com.store.management.api.service;

import com.store.management.api.DomainToDTOMapper;
import com.store.management.api.dto.ProductDTO;
import com.store.management.api.errorhandling.ProductNotFoundException;
import com.store.management.dao.CategoryDao;
import com.store.management.dao.ProductDao;
import com.store.management.dao.ProductSpecDao;
import com.store.management.dao.SupplierDao;
import com.store.management.model.Category;
import com.store.management.model.Product;
import com.store.management.model.ProductSpec;
import com.store.management.model.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductDao productDao;
    private final DomainToDTOMapper domainToDTOMapper;
    private final ProductSpecDao productSpecDao;
    private final SupplierDao supplierDao;
    private final CategoryDao categoryDao;

    @Autowired
    public ProductService(ProductDao productDao, DomainToDTOMapper domainToDTOMapper, ProductSpecDao productSpecDao, SupplierDao supplierDao, CategoryDao categoryDao) {
        this.productDao = productDao;
        this.domainToDTOMapper = domainToDTOMapper;
        this.productSpecDao = productSpecDao;
        this.supplierDao = supplierDao;
        this.categoryDao = categoryDao;
    }

    public List<ProductDTO> getAll() {
        return productDao.findAll()
                .stream()
                .map(domainToDTOMapper::mapProductToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getById(int id) {
        return productDao.findById(id)
                .map(domainToDTOMapper::mapProductToDTO)
                .orElseThrow(() -> new ProductNotFoundException(Product.class, "id", String.valueOf(id)));
    }

    public ProductDTO updateProduct(ProductDTO updatedProduct, int idToModify) {
        return productDao.findById(idToModify)
                .map(toBeUpdatedProduct -> {
                    toBeUpdatedProduct.setName(updatedProduct.getProductName());
                    toBeUpdatedProduct.setPrice(updatedProduct.getPrice());
                    toBeUpdatedProduct.setDescription(updatedProduct.getDescription());
                    final boolean productSpecMatches = toBeUpdatedProduct.getProductSpec().getColor().equals(updatedProduct.getColor()) &&
                            toBeUpdatedProduct.getProductSpec().getCapacity().equals(updatedProduct.getCapacity());
                    final boolean supplierNameMatches = toBeUpdatedProduct.getSupplier().getName().equals(updatedProduct.getProductSupplierName());
                    final boolean categoriesMatch = toBeUpdatedProduct.getCategories().stream().map(Category::getCategoryName).sorted().toList()
                                .equals(updatedProduct.getCategories().stream().sorted().collect(Collectors.toList()));
                    if (!productSpecMatches) {
                        updateProductSpec(toBeUpdatedProduct, updatedProduct.getColor(), updatedProduct.getCapacity());
                    }
                    if (!supplierNameMatches) {
                        updateSupplier(toBeUpdatedProduct, updatedProduct.getProductSupplierName());
                    }
                    if (!categoriesMatch) {
                        updateCategories(toBeUpdatedProduct, updatedProduct.getCategories());
                    }
                    return domainToDTOMapper.mapProductToDTO(toBeUpdatedProduct);
                })
                .orElseThrow(() -> new ProductNotFoundException(Product.class, "id", String.valueOf(idToModify)));
    }

    private void updateCategories(Product toBeUpdatedProduct, List<String> newCategories) {
        var categoriesToAssign = new HashSet<Category>();
        newCategories.forEach(categoryName ->
                categoryDao.findByCategoryName(categoryName)
                    .ifPresentOrElse(
                            categoriesToAssign::add,
                            () -> categoriesToAssign.add(categoryDao.save(new Category(categoryName)))
                    ));
        toBeUpdatedProduct.setCategories(categoriesToAssign);
    }

    private void updateSupplier(Product toBeUpdatedProduct, String productSupplierName) {
        supplierDao.findByName(productSupplierName)
                .ifPresentOrElse(
                        toBeUpdatedProduct::setSupplier,
                        () -> toBeUpdatedProduct.setSupplier(supplierDao.save(new Supplier(productSupplierName)))
                );
    }


    private void updateProductSpec(Product toBeUpdatedProduct, String color, String capacity) {
        productSpecDao.findByColorAndCapacity(color, capacity)
                .ifPresentOrElse(
                        toBeUpdatedProduct::setProductSpec,
                        () -> toBeUpdatedProduct.setProductSpec(productSpecDao.save(new ProductSpec(color, capacity)))
                );
    }
}
