package com.spring_commerce.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

  @Override
  public ProductDTO createProduct(ProductDTO newProductDTO, Long categoryId) {

    Optional<Category> existingCategory = categoryRepository.findById(categoryId);

    if (existingCategory.isEmpty())
      throw new ResourceNotFoundException("Category", "categoryId", categoryId);

    Product newProduct = modelMapper.map(newProductDTO, Product.class);

    newProduct.setCategory(existingCategory.get());

    double specialPrice = newProductDTO.getSpecialPrice();
    // double specialPrice = newProductDTO.getPrice() * (100 -
    // newProductDTO.getDiscount()) / 100;
    newProduct.setSpecialPrice(specialPrice);
    newProduct.setImage("default.png");

    Product savedProduct = productRepository.save(newProduct);
    ProductDTO savedProductDTO = modelMapper.map(savedProduct, ProductDTO.class);

    return savedProductDTO;
  }

  @Override
  public ProductResponse getAllProducts() {
    List<Product> allProducts = productRepository.findAll();

    List<ProductDTO> allProductsDTO = allProducts.stream()
        .map(product -> modelMapper.map(product, ProductDTO.class))
        .collect(Collectors.toList());

    return new ProductResponse(allProductsDTO);

  }

  @Override
  public ProductResponse getProductsbyCategoryId(Long categoryId) {

    Category _ = getOrThrow(categoryRepository, categoryId, "Category");

    List<Product> allProducts = productRepository.findByCategoryCategoryId(categoryId);

    List<ProductDTO> allProductsDTO = allProducts.stream()
        .map(product -> modelMapper.map(product, ProductDTO.class))
        .collect(Collectors.toList());

    return new ProductResponse(allProductsDTO);
  }

  @Override
  public ProductResponse getProductsbyKeyword(String keyword) {
    List<Product> allProducts = productRepository.findByNameContainingIgnoreCase(keyword);

    List<ProductDTO> allProductsDTO = allProducts.stream()
        .map(product -> modelMapper.map(product, ProductDTO.class))
        .collect(Collectors.toList());

    return new ProductResponse(allProductsDTO);
  }

  @Override
  public ProductDTO updateProduct(ProductDTO newProductDTO, Long productId) {
    Product productFromDB = getOrThrow(productRepository, productId, "Product");

    modelMapper.map(newProductDTO, productFromDB);

    Product updatedProduct = productRepository.save(productFromDB);

    ProductDTO updatedProductDTO = modelMapper.map(updatedProduct, ProductDTO.class);

    return updatedProductDTO;
  }

  @Override
  public void deleteProduct(Long productId) {
    Product productFromDB = getOrThrow(productRepository, productId, "Product");
    productRepository.delete(productFromDB);

  }

  @Override
  public ProductDTO updateProductImage(Long productId, MultipartFile imagefile) throws IOException {
    Product productFromdB = getOrThrow(productRepository, productId, "Product");

    String path = "/images";
    String fileName = AppUtils.uploadImage(path, imagefile);

    productFromdB.setImage(fileName);
    Product updatedProduct = productRepository.save(productFromdB);

    ProductDTO updatedProductDTO = modelMapper.map(updatedProduct, ProductDTO.class);

    return updatedProductDTO;
  }
}
