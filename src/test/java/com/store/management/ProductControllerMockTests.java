package com.store.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.management.api.DomainToDTOMapper;
import com.store.management.api.dto.ProductDTO;
import com.store.management.api.errorhandling.RecordNotFoundException;
import com.store.management.api.service.ProductService;
import com.store.management.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static com.store.management.utils.DummyObjectsCreationUtil.createDummyProduct;
import static com.store.management.utils.DummyObjectsCreationUtil.createDummyProductDTO;
import static com.store.management.utils.ProductConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class responsible for mocking the {@link com.store.management.api.controller.ProductController}
 * and test all endpoints with valid and invalid data
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ProductControllerMockTests {

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
		product.setId(VALID_PRODUCT_ID_111);

		product.setName(UPDATED_PRODUCT_NAME_DUMMY);
		var updatedDto = createDummyProductDTO();
		updatedDto.setProductName(UPDATED_PRODUCT_NAME_DUMMY);

		given(domainToDTOMapper.mapProductToDTO(product)).willReturn(updatedDto);
		given(productService.updateProduct(updatedDto, product.getId())).willReturn(updatedDto);

		mockMvc.perform(post("/product/update-product/?id=" + product.getId())
						.content(mapper.writeValueAsString(domainToDTOMapper.mapProductToDTO(product)))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.productName", is(UPDATED_PRODUCT_NAME_DUMMY)))
				.andExpect(jsonPath("$.capacity", is(dto.getCapacity())))
				.andExpect(jsonPath("$.color", is(dto.getColor())))
				.andExpect(jsonPath("$.categories", is(dto.getCategories())))
				.andExpect(jsonPath("$.price", is(dto.getPrice())))
				.andExpect(jsonPath("$.description", is(dto.getDescription())));
	}

	@Test
	public void updateProduct_whenPostProduct_withInvalidId() throws Exception {
		var product = createDummyProduct();
		product.setId(INVALID_PRODUCT_ID);

		product.setName(UPDATED_PRODUCT_NAME_DUMMY);
		var updatedDto = createDummyProductDTO();
		updatedDto.setProductName(UPDATED_PRODUCT_NAME_DUMMY);

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
		product.setId(VALID_PRODUCT_ID_111);

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
		product.setId(VALID_PRODUCT_ID_111);

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

}
