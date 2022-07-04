package com.store.management.api.service;

import com.store.management.api.DomainToDTOMapper;
import com.store.management.api.dto.ProductDTO;
import com.store.management.api.errorhandling.RecordNotFoundException;
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
import java.util.Objects;
import java.util.Optional;
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
                .orElseThrow(() -> new RecordNotFoundException(Product.class, "id", String.valueOf(id)));
    }

    public ProductDTO updateProduct(ProductDTO updatedProduct, int idToModify) {
        return productDao.findById(idToModify)
                .map(toBeUpdatedProduct -> {
                    createProductOffDTO(updatedProduct, toBeUpdatedProduct);
                    return domainToDTOMapper.mapProductToDTO(toBeUpdatedProduct);
                })
                .orElseThrow(() -> new RecordNotFoundException(Product.class, "id", String.valueOf(idToModify)));
    }

    private void createProductOffDTO(ProductDTO updatedProduct, Product result) {
        Optional.of(updatedProduct.getProductName()).ifPresent(result::setName);
        Optional.of(updatedProduct.getPrice()).ifPresent(result::setPrice);
        Optional.of(updatedProduct.getDescription()).ifPresent(result::setDescription);

        // use Objects.equals since it allow for potential null params
        final boolean productSpecMatches = Objects.equals(result.getProductSpec().getColor(), updatedProduct.getColor()) && Objects.equals(result.getProductSpec().getCapacity(), updatedProduct.getCapacity());
        if (!productSpecMatches) {
            updateProductSpec(result, updatedProduct.getColor(), updatedProduct.getCapacity());
        }

        final boolean supplierNameMatches = Objects.equals(result.getSupplier().getName(), updatedProduct.getProductSupplierName());
        if (!supplierNameMatches) {
            updateSupplier(result, updatedProduct.getProductSupplierName());
        }

        final boolean categoriesMatch = result.getCategories().stream().map(Category::getCategoryName).sorted().toList()
                .equals(updatedProduct.getCategories().stream().sorted().collect(Collectors.toList()));
        if (!categoriesMatch) {
            updateCategories(result, updatedProduct.getCategories());
        }
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

    public ProductDTO deleteProduct(int id) {
        return productDao.findById(id)
                .map((prod) -> {
                    productDao.delete(prod);
                    return domainToDTOMapper.mapProductToDTO(prod);
                })
                .orElseThrow(() -> new RecordNotFoundException(Product.class, "id", String.valueOf(id)));
    }

    public ProductDTO addNewProduct(ProductDTO productDTO) {
        Product toAdd = new Product();
        createProductOffDTO(productDTO, toAdd);
        productDao.save(toAdd);
        return productDTO;
    }
}
