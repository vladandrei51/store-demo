package com.store.management.api.controller;

import com.store.management.api.dto.ProductDTO;
import com.store.management.api.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * handles the get on /product/get-all request
     * and returns the list of all products
     *
     * @return list of all mapped {@link ProductDTO} from the in-memory db
     */
    @GetMapping("/get-all")
    public List<ProductDTO> getAll() {
        LOG.info("getAll invoked from /product/get-all");
        return productService.getAll();
    }

    /**
     * handles the get on /product/get-by-id request,
     * it returns the product by id
     * if it doesn't exist, it throws {@link com.store.management.api.errorhandling.RecordNotFoundException}
     * @param id
     * @return the mapped {@link ProductDTO}
     */
    @GetMapping("/get-by-id")
    public ProductDTO getById(@RequestParam("id") final int id) {
        LOG.info("getById invoked with argument id={} from /product/get-by-id", id);
        return productService.getById(id);
    }

    /**
     * handles the post on /product/update-product request
     * it compares the product found at {@code idToModify} with the one given at {@code updatedProduct}
     * If the new product has unknown fields (new product category / supplier etc), that new entitiy
     * will be saved to the DB and assigned to the new {@code updatedProduct}
     * if the {@code id} doesn't exist, it throws {@link com.store.management.api.errorhandling.RecordNotFoundException}
     *
     * @param updatedProduct
     * @param idToModify
     * @return
     */
    @PostMapping("/update-product")
    public ProductDTO updateProduct(@RequestBody final ProductDTO updatedProduct, @RequestParam("id") final int idToModify) {
        LOG.info("updateProduct invoked with argument updatedProduct={}, id={} from /product/update-product", updatedProduct, idToModify);
        return productService.updateProduct(updatedProduct, idToModify);
    }

    /**
     * Deletes the {@link com.store.management.model.Product} with the id = {@code id}
     * if it doesn't exist, it throws {@link com.store.management.api.errorhandling.RecordNotFoundException}
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete-by-id")
    public ProductDTO deleteProduct(@RequestParam("id") final int id) {
        return productService.deleteProduct(id);
    }

    /**
     * Persists a new product in DB based off the DTO provided
     *
     * @param productDTO
     * @return
     */
    @PostMapping("/add-new-product")
    public ProductDTO addNewProduct(@RequestBody final ProductDTO productDTO) {
        return productService.addNewProduct(productDTO);
    }
}
