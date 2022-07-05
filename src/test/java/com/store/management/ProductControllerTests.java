package com.store.management;

import com.store.management.api.DomainToDTOMapper;
import com.store.management.api.controller.ProductController;
import com.store.management.api.errorhandling.RecordNotFoundException;
import com.store.management.dao.CategoryDao;
import com.store.management.dao.ProductDao;
import com.store.management.dao.SupplierDao;
import com.store.management.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.testng.Assert;

import javax.transaction.Transactional;

import static com.store.management.utils.DummyObjectsCreationUtil.createDummyProduct;
import static com.store.management.utils.DummyObjectsCreationUtil.createDummyProductDTO;
import static com.store.management.utils.ProductConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Class responsible for using {@link com.store.management.api.controller.ProductController}
 * real implementation and do integration tests on all the endpoints with valid and invalid data
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProductControllerTests {
    @Autowired
    ProductController productController;

    @Autowired
    DomainToDTOMapper domainToDTOMapper;

    @Autowired
    ProductDao productDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    SupplierDao supplierDao;

    @Test
    @Transactional
    public void getAllProducts_whenGetAllMethod() {
        assertEquals(productController.getAll().size(), TOTAL_PRODUCTS_RECORD);
    }

    @Test
    @Transactional
    public void getProductById_whenGetMethod_withValidId() {
        // check if the response of /get-by/ API has the expected fields
        var response = productController.getById(VALID_PRODUCT_ID_111);
        Assert.assertEquals(response.getProductName(), PRODUCT_ID_111_NAME);
        Assert.assertEquals(response.getColor(), PRODUCT_ID_111_PRODUCT_SPEC_COLOR);
        Assert.assertEquals(response.getCapacity(), PRODUCT_ID_111_PRODUCT_SPEC_CAPACITY);
        Assert.assertEquals(response.getPrice(), PRODUCT_ID_111_PRICE);
        Assert.assertEquals(response.getDescription(), PRODUCT_ID_111_DESCRIPTION);
        Assert.assertEquals(response.getProductSupplierName(), PRODUCT_ID_111_SUPPLIER_NAME);
    }

    @Test
    public void getProductById_whenGetMethod_withInvalidId() {
        Throwable exception = assertThrows(RecordNotFoundException.class, () -> productController.getById(INVALID_PRODUCT_ID));
        assertEquals(RECORD_NOT_FOUND_ERROR_MESSAGE + INVALID_PRODUCT_ID, exception.getMessage());
    }

    @Test
    public void createProduct_whenPostMethod() {
        var toAddProduct = createDummyProduct();
        // add new product and check if the response has the adequate fields
        var response = productController.addNewProduct(domainToDTOMapper.mapProductToDTO(toAddProduct));
        Assert.assertEquals(response.getProductName(), PRODUCT_NAME_DUMMY);
        Assert.assertEquals(response.getColor(), PRODUCT_COLOR_DUMMY);
        Assert.assertEquals(response.getCapacity(), PRODUCT_CAPACITY_DUMMY);
        Assert.assertEquals(response.getPrice(), PRODUCT_PRICE_DUMMY);
        Assert.assertEquals(response.getDescription(), PRODUCT_DESCRIPTION_DUMMY);
        Assert.assertEquals(response.getProductSupplierName(), PRODUCT_SUPPLIER_NAME_DUMMY);

        // check if a new record has been added to db
        assertEquals(productDao.count(), TOTAL_PRODUCTS_RECORD + 1);
    }

    @Transactional
    @Test
    public void updateProduct_whenPostProduct_withValidId_forTrivialFields() {
        productDao.findById(VALID_PRODUCT_ID_111)
                .ifPresentOrElse(
                        product -> {
                            // set a new name and check if it's updated
                            product.setName(UPDATED_PRODUCT_NAME_DUMMY);
                            var response = productController.updateProduct(domainToDTOMapper.mapProductToDTO(product), product.getId());
                            assertEquals(response.getProductName(), UPDATED_PRODUCT_NAME_DUMMY);
                            assertNotEquals(response.getProductName(), PRODUCT_ID_111_NAME);
                        },
                        () -> {
                            throw new RecordNotFoundException(Product.class, "id", String.valueOf(VALID_PRODUCT_ID_111));
                        }
                );
    }

    @Transactional
    @Test
    public void updateProduct_whenPostProduct_withValidId_forNewCategory() {
        productDao.findById(VALID_PRODUCT_ID_111)
                .ifPresentOrElse(
                        product -> {
                            // adding to product a category that doesn't exist in db
                            var dto = domainToDTOMapper.mapProductToDTO(product);
                            var categories = dto.getCategories();
                            categories.add(NEW_CATEGORY_NAME);
                            dto.setCategories(categories);
                            // calling the update method and check if the new category is set and also saved in db
                            var response = productController.updateProduct(dto, VALID_PRODUCT_ID_111);
                            assertTrue(response.getCategories().contains(NEW_CATEGORY_NAME));
                            assertTrue(categoryDao.findByCategoryName(NEW_CATEGORY_NAME).isPresent());
                        },
                        () -> {
                            throw new RecordNotFoundException(Product.class, "id", String.valueOf(VALID_PRODUCT_ID_111));
                        }
                );
    }

    @Transactional
    @Test
    public void updateProduct_whenPostProduct_withValidId_forNewSupplier() {
        productDao.findById(VALID_PRODUCT_ID_111)
                .ifPresentOrElse(
                        product -> {
                            // adding to product a supplier name that doesn't exist in db
                            var dto = domainToDTOMapper.mapProductToDTO(product);
                            dto.setProductSupplierName(NEW_SUPPLIER_NAME);
                            // calling the update method and check if the new supplier is set and also saved in db
                            var response = productController.updateProduct(dto, VALID_PRODUCT_ID_111);
                            assertEquals(response.getProductSupplierName(), NEW_SUPPLIER_NAME);
                            assertTrue(supplierDao.findByName(NEW_SUPPLIER_NAME).isPresent());
                        },
                        () -> {
                            throw new RecordNotFoundException(Product.class, "id", String.valueOf(VALID_PRODUCT_ID_111));
                        }
                );
    }


    @Test
    public void updateProduct_whenPostProduct_withInvalidId() {
        Throwable exception = assertThrows(RecordNotFoundException.class, () -> productController.updateProduct(createDummyProductDTO(), INVALID_PRODUCT_ID));
        assertEquals(RECORD_NOT_FOUND_ERROR_MESSAGE + INVALID_PRODUCT_ID, exception.getMessage());
    }

    @Transactional
    @Test
    public void removeProductById_whenDeleteMethod_withValidId() {
        // add a dummy product
        var toAddProduct = createDummyProduct();
        var response = productController.addNewProduct(domainToDTOMapper.mapProductToDTO(toAddProduct));
        assertEquals(response.getProductName(), PRODUCT_NAME_DUMMY);
        assertEquals(response.getColor(), PRODUCT_COLOR_DUMMY);
        assertEquals(response.getCapacity(), PRODUCT_CAPACITY_DUMMY);
        assertEquals(response.getPrice(), PRODUCT_PRICE_DUMMY);
        assertEquals(response.getDescription(), PRODUCT_DESCRIPTION_DUMMY);
        assertEquals(response.getProductSupplierName(), PRODUCT_SUPPLIER_NAME_DUMMY);

        // check if it has been added to the db
        assertEquals(productDao.count(), TOTAL_PRODUCTS_RECORD + 1);
        productDao.findByName(PRODUCT_NAME_DUMMY).ifPresent(toDelete -> {
            productDao.deleteById(toDelete.getId());
            // check if the deletion works
            assertEquals(productDao.count(), TOTAL_PRODUCTS_RECORD);
        });
    }

    @Test
    public void removeProductById_whenDeleteMethod_withInvalidId() {
        Throwable exception = assertThrows(RecordNotFoundException.class, () -> productController.deleteProduct(INVALID_PRODUCT_ID));
        assertEquals(RECORD_NOT_FOUND_ERROR_MESSAGE + INVALID_PRODUCT_ID, exception.getMessage());
    }
}
