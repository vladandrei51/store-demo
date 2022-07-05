package com.store.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.management.api.DomainToDTOMapper;
import com.store.management.api.dto.ProductDTO;
import com.store.management.api.errorhandling.RecordNotFoundException;
import com.store.management.api.service.ProductService;
import com.store.management.model.Category;
import com.store.management.model.Product;
import com.store.management.model.ProductSpec;
import com.store.management.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerMockTest {

	public static final String PRODUCT_NAME = "test-product";
	public static final String PRODUCT_CAPACITY = "256GB";
	public static final String PRODUCT_COLOR = "Red";
	public static final List<String> PRODUCT_CATEGORIES = Arrays.asList("category1", "category2");
	public static final int PRODUCT_PRICE = 125;
	public static final String PRODUCT_DESCRIPTION = "Test-description";
	public static final String PRODUCT_SUPPLIER_NAME = "Ebay";
	public static final String UPDATED_PRODUCT_NAME = "updated-name";
	private final int VALID_PRODUCT_ID = 111;
	private final int INVALID_PRODUCT_ID = -4;
	ObjectMapper mapper;
	@MockBean
	private ProductService productService;
	@MockBean
	private DomainToDTOMapper domainToDTOMapper;
	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() {
		mapper = new ObjectMapper();
	}

	@Test
	public void createProduct_whenPostMethod() throws Exception {
		var dto = createDummyProductDTO();
		var product = createDummyProduct();

		given(domainToDTOMapper.mapProductToDTO(product)).willReturn(dto);
		given(productService.addNewProduct(dto)).willReturn(dto);

		mockMvc.perform(post("/product/add-new-product")
						.content(mapper.writeValueAsString(domainToDTOMapper.mapProductToDTO(product)))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.productName", is(dto.getProductName())))
				.andExpect(jsonPath("$.capacity", is(dto.getCapacity())))
				.andExpect(jsonPath("$.color", is(dto.getColor())))
				.andExpect(jsonPath("$.categories", is(dto.getCategories())))
				.andExpect(jsonPath("$.price", is(dto.getPrice())))
				.andExpect(jsonPath("$.description", is(dto.getDescription())));
	}

	@Test
	public void updateProduct_whenPostProduct_withValidId() throws Exception {
		var dto = createDummyProductDTO();
		var product = createDummyProduct();
		product.setId(VALID_PRODUCT_ID);

		product.setName(UPDATED_PRODUCT_NAME);
		var updatedDto = createDummyProductDTO();
		updatedDto.setProductName(UPDATED_PRODUCT_NAME);

		given(domainToDTOMapper.mapProductToDTO(product)).willReturn(updatedDto);
		given(productService.updateProduct(updatedDto, product.getId())).willReturn(updatedDto);

		mockMvc.perform(post("/product/update-product/?id=" + product.getId())
						.content(mapper.writeValueAsString(domainToDTOMapper.mapProductToDTO(product)))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.productName", is(UPDATED_PRODUCT_NAME)))
				.andExpect(jsonPath("$.capacity", is(dto.getCapacity())))
				.andExpect(jsonPath("$.color", is(dto.getColor())))
				.andExpect(jsonPath("$.categories", is(dto.getCategories())))
				.andExpect(jsonPath("$.price", is(dto.getPrice())))
				.andExpect(jsonPath("$.description", is(dto.getDescription())));
	}

	@Test
	public void updateProduct_whenPostProduct_withInvalidId() throws Exception {
		var dto = createDummyProductDTO();
		var product = createDummyProduct();
		product.setId(INVALID_PRODUCT_ID);

		product.setName(UPDATED_PRODUCT_NAME);
		var updatedDto = createDummyProductDTO();
		updatedDto.setProductName(UPDATED_PRODUCT_NAME);

		given(domainToDTOMapper.mapProductToDTO(product)).willReturn(updatedDto);
		Mockito.doThrow(new RecordNotFoundException(Product.class, "id", String.valueOf(product.getId())))
				.when(productService).updateProduct(updatedDto, product.getId());

		mockMvc.perform(post("/product/update-product/?id=" + product.getId())
						.content(mapper.writeValueAsString(domainToDTOMapper.mapProductToDTO(product)))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getProductById_whenGetMethod_withValidId() throws Exception {

		var dto = createDummyProductDTO();
		var product = createDummyProduct();
		product.setId(VALID_PRODUCT_ID);

		given(productService.getById(product.getId()))
				.willReturn(dto);

		mockMvc.perform(get("/product/get-by-id/?id=" + product.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.productName", is(dto.getProductName())))
				.andExpect(jsonPath("$.capacity", is(dto.getCapacity())))
				.andExpect(jsonPath("$.color", is(dto.getColor())))
				.andExpect(jsonPath("$.categories", is(dto.getCategories())))
				.andExpect(jsonPath("$.price", is(dto.getPrice())))
				.andExpect(jsonPath("$.description", is(dto.getDescription())));
	}

	@Test
	public void getProductById_whenGetMethod_withInvalidId() throws Exception {
		var product = createDummyProduct();
		product.setId(INVALID_PRODUCT_ID);

		Mockito.doThrow(new RecordNotFoundException(Product.class, "id", String.valueOf(product.getId())))
				.when(productService).getById(product.getId());

		mockMvc.perform(get("/product/get-by-id/?id=" + product.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getAllProducts_whenGetAllMethod() throws Exception {
		var dto = createDummyProductDTO();
		List<ProductDTO> allProductsResponse = Collections.singletonList(dto);

		given(productService.getAll())
				.willReturn(allProductsResponse);

		mockMvc.perform(get("/product/get-all")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].productName", is(dto.getProductName())))
				.andExpect(jsonPath("$[0].capacity", is(dto.getCapacity())))
				.andExpect(jsonPath("$[0].color", is(dto.getColor())))
				.andExpect(jsonPath("$[0].categories", is(dto.getCategories())))
				.andExpect(jsonPath("$[0].price", is(dto.getPrice())))
				.andExpect(jsonPath("$[0].description", is(dto.getDescription())));
	}


	@Test
	public void removeProductById_whenDeleteMethod_withValidId() throws Exception {
		var dto = createDummyProductDTO();
		var product = createDummyProduct();
		product.setId(VALID_PRODUCT_ID);

		given(productService.deleteProduct(product.getId()))
				.willReturn(dto);

		mockMvc.perform(delete("/product/delete-by-id/?id=" + product.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.productName", is(dto.getProductName())))
				.andExpect(jsonPath("$.capacity", is(dto.getCapacity())))
				.andExpect(jsonPath("$.color", is(dto.getColor())))
				.andExpect(jsonPath("$.categories", is(dto.getCategories())))
				.andExpect(jsonPath("$.price", is(dto.getPrice())))
				.andExpect(jsonPath("$.description", is(dto.getDescription())));
	}

	@Test
	public void removeProductById_whenDeleteMethod_withInvalidId() throws Exception {
		var product = createDummyProduct();
		product.setId(INVALID_PRODUCT_ID);

		Mockito.doThrow(new RecordNotFoundException(Product.class, "id", String.valueOf(product.getId())))
				.when(productService).deleteProduct(product.getId());

		mockMvc.perform(delete("/product/delete-by-id/?id=" + product.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	private ProductDTO createDummyProductDTO() {
		return ProductDTO
				.builder()
				.productName(PRODUCT_NAME)
				.capacity(PRODUCT_CAPACITY)
				.color(PRODUCT_COLOR)
				.categories(PRODUCT_CATEGORIES)
				.price(PRODUCT_PRICE)
				.description(PRODUCT_DESCRIPTION)
				.productSupplierName(PRODUCT_SUPPLIER_NAME)
				.build();
	}

	private Product createDummyProduct() {
		Product product = new Product();
		product.setId(VALID_PRODUCT_ID);
		product.setName(PRODUCT_NAME);
		Supplier supplier = new Supplier();
		supplier.setName(PRODUCT_SUPPLIER_NAME);
		product.setSupplier(supplier);
		ProductSpec productSpec = new ProductSpec();
		productSpec.setColor(PRODUCT_COLOR);
		productSpec.setCapacity(PRODUCT_CAPACITY);
		Set<Category> categories = new HashSet<>();
		PRODUCT_CATEGORIES.forEach(x -> categories.add(new Category(x)));
		product.setCategories(categories);
		product.setPrice(PRODUCT_PRICE);
		product.setDescription(PRODUCT_DESCRIPTION);
		return product;
	}


}
