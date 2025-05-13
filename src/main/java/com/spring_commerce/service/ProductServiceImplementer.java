package com.spring_commerce.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.spring_commerce.exceptions.APIException;
import com.spring_commerce.exceptions.ResourceNotFoundException;
import com.spring_commerce.model.Category;
import com.spring_commerce.model.Product;
import com.spring_commerce.payload.ProductDTO;
import com.spring_commerce.payload.ProductResponse;
import com.spring_commerce.repositories.CategoryRepository;
import com.spring_commerce.repositories.ProductRepository;
import com.spring_commerce.utils.AppUtils;

@Service
public class ProductServiceImplementer extends BaseServiceImplementer implements ProductService {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Value("${project.image}")
  private String path;

  @Override
  public ProductDTO createProduct(ProductDTO newProductDTO, Long categoryId) {

    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("Category", "ID", categoryId));

    if (productRepository.existsByName(newProductDTO.getName())) {
      throw new APIException("Product with the name " + newProductDTO.getName() + " already exists.");
    }

    Product newProduct = modelMapper.map(newProductDTO, Product.class);

    newProduct.setCategory(category);

    newProduct.setSpecialPrice(newProductDTO.getSpecialPrice());
    newProduct.setImage("default.png");

    Product savedProduct = productRepository.save(newProduct);
    return modelMapper.map(savedProduct, ProductDTO.class);
  }

  @Override
  public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

    Page<Product> productPage = getPaginatedResponse(
        productRepository,
        Product.class,
        pageNumber,
        pageSize,
        sortBy,
        sortOrder);

    List<ProductDTO> productDTOs = productPage.getContent().stream()
        .map(product -> modelMapper.map(product, ProductDTO.class))
        .collect(Collectors.toList());

    ProductResponse productResponse = new ProductResponse();
    productResponse.setContent(productDTOs);
    productResponse.setPageNumber(productPage.getNumber());
    productResponse.setPageSize(productPage.getSize());
    productResponse.setTotalElements(productPage.getTotalElements());
    productResponse.setTotalpages(productPage.getTotalPages());
    productResponse.setLastPage(productPage.isLast());

    return productResponse;

  }

  @Override
  public ProductResponse getProductsbyCategoryId(Long categoryId, int pageNumber, int pageSize, String sortBy,
      String sortOrder) {

    getOrThrow(categoryRepository, categoryId, "Category");

    Pageable pageable = getPageable(Product.class, pageNumber, pageSize, sortBy, sortOrder);
    Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);

    if (productPage.isEmpty()) {
      throw new APIException("No products found for the given category.");
    }

    List<ProductDTO> productsDTO = productPage.getContent().stream()
        .map(product -> modelMapper.map(product, ProductDTO.class))
        .collect(Collectors.toList());

    return new ProductResponse(
        productsDTO,
        productPage.getNumber(),
        productPage.getSize(),
        productPage.getTotalElements(),
        productPage.getTotalPages(),
        productPage.isLast());
  }

  @Override
  public ProductResponse getProductsbyKeyword(String keyword, int pageNumber, int pageSize, String sortBy,
      String sortOrder) {

    Pageable pageable = getPageable(Product.class, pageNumber, pageSize, sortBy, sortOrder);
    Page<Product> productPage = productRepository.findByNameContainingIgnoreCase(keyword, pageable);

    if (productPage.isEmpty()) {
      throw new APIException("No products found for the given keyword.");
    }

    List<ProductDTO> productsDTO = productPage.getContent().stream()
        .map(product -> modelMapper.map(product, ProductDTO.class))
        .collect(Collectors.toList());

    return new ProductResponse(
        productsDTO,
        productPage.getNumber(),
        productPage.getSize(),
        productPage.getTotalElements(),
        productPage.getTotalPages(),
        productPage.isLast());
  }

  @Override
  public ProductDTO updateProduct(ProductDTO newProductDTO, Long productId) {
    Product productFromDB = getOrThrow(productRepository, productId, "Product");

    modelMapper.map(newProductDTO, productFromDB);

    Product updatedProduct = productRepository.save(productFromDB);
    return modelMapper.map(updatedProduct, ProductDTO.class);
  }

  @Override
  public void deleteProduct(Long productId) {
    Product productFromDB = getOrThrow(productRepository, productId, "Product");
    productRepository.delete(productFromDB);
  }

  @Override
  public ProductDTO updateProductImage(Long productId, MultipartFile imagefile) throws IOException {
    Product productFromDB = getOrThrow(productRepository, productId, "Product");

    String fileName = AppUtils.uploadImage(path, imagefile);
    productFromDB.setImage(fileName);

    Product updatedProduct = productRepository.save(productFromDB);
    return modelMapper.map(updatedProduct, ProductDTO.class);
  }
}
