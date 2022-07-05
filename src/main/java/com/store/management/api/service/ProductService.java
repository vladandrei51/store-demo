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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

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
                .orElseThrow(() -> {
                    LOG.warn("Get by id {} failure! Id not found in the db", id);
                    throw new RecordNotFoundException(Product.class, "id", String.valueOf(id));
                });
    }

    public ProductDTO updateProduct(ProductDTO updatedProduct, int idToModify) {
        return productDao.findById(idToModify)
                .map(toBeUpdatedProduct -> {
                    createProductOffDTO(updatedProduct, toBeUpdatedProduct);
                    return domainToDTOMapper.mapProductToDTO(toBeUpdatedProduct);
                })
                .orElseThrow(() -> {
                    LOG.error("Product with id {} doesn't exist so it can't be updated", idToModify);
                    throw new RecordNotFoundException(Product.class, "id", String.valueOf(idToModify));
                });
    }

    private void createProductOffDTO(ProductDTO updatedProduct, Product toBeUpdatedProduct) {
        Optional.of(updatedProduct.getProductName()).ifPresent(toBeUpdatedProduct::setName);
        Optional.of(updatedProduct.getPrice()).ifPresent(toBeUpdatedProduct::setPrice);
        Optional.of(updatedProduct.getDescription()).ifPresent(toBeUpdatedProduct::setDescription);

        // use Objects.equals since it allow for potential null params
        final boolean productSpecMatches = Objects.equals(toBeUpdatedProduct.getProductSpec().getColor(), updatedProduct.getColor()) && Objects.equals(toBeUpdatedProduct.getProductSpec().getCapacity(), updatedProduct.getCapacity());
        if (!productSpecMatches) {
            updateProductSpec(toBeUpdatedProduct, updatedProduct.getColor(), updatedProduct.getCapacity());
        }

        final boolean supplierNameMatches = Objects.equals(toBeUpdatedProduct.getSupplier().getName(), updatedProduct.getProductSupplierName());
        if (!supplierNameMatches) {
            updateSupplier(toBeUpdatedProduct, updatedProduct.getProductSupplierName());
        }

        final boolean categoriesMatch = toBeUpdatedProduct.getCategories().stream().map(Category::getCategoryName).sorted().toList()
                .equals(updatedProduct.getCategories().stream().sorted().collect(Collectors.toList()));
        if (!categoriesMatch) {
            updateCategories(toBeUpdatedProduct, updatedProduct.getCategories());
        }
    }

    private void updateCategories(Product toBeUpdatedProduct, List<String> newCategories) {
        var categoriesToAssign = new HashSet<Category>();
        newCategories.forEach(categoryName ->
                categoryDao.findByCategoryName(categoryName)
                        .ifPresentOrElse(
                                categoriesToAssign::add,
                                () -> {
                                    LOG.info("New category: {} will be added to the db and assigned to the product", categoryName);
                                    categoriesToAssign.add(categoryDao.save(new Category(categoryName)));
                                }
                        ));
        toBeUpdatedProduct.setCategories(categoriesToAssign);
    }

    private void updateSupplier(Product toBeUpdatedProduct, String productSupplierName) {
        supplierDao.findByName(productSupplierName)
                .ifPresentOrElse(
                        toBeUpdatedProduct::setSupplier,
                        () -> {
                            LOG.info("New supplier: {} will be added to the db and assigned to the product", productSupplierName);
                            toBeUpdatedProduct.setSupplier(supplierDao.save(new Supplier(productSupplierName)));
                        }
                );
    }


    private void updateProductSpec(Product toBeUpdatedProduct, String color, String capacity) {
        productSpecDao.findByColorAndCapacity(color, capacity)
                .ifPresentOrElse(
                        toBeUpdatedProduct::setProductSpec,
                        () -> {
                            LOG.info("New Product spec color: {}, capacity: {} will be added to the db and assigned to the product", color, capacity);
                            toBeUpdatedProduct.setProductSpec(productSpecDao.save(new ProductSpec(color, capacity)));
                        }
                );
    }

    public ProductDTO deleteProduct(int id) {
        return productDao.findById(id)
                .map((prod) -> {
                    productDao.delete(prod);
                    return domainToDTOMapper.mapProductToDTO(prod);
                })
                .orElseThrow(() -> {
                    LOG.warn("Delete failure! Cannot find product with id {}", id);
                    throw new RecordNotFoundException(Product.class, "id", String.valueOf(id));
                });
    }

    public ProductDTO insert(ProductDTO productDTO) {
        Product toAdd = new Product();
        createProductOffDTO(productDTO, toAdd);
        productDao.save(toAdd);
        return productDTO;
    }
}
