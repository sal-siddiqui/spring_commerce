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
import com.spring_commerce.model.Cart;
import com.spring_commerce.model.Category;
import com.spring_commerce.model.Product;
import com.spring_commerce.payload.CartDTO;
import com.spring_commerce.payload.ProductDTO;
import com.spring_commerce.payload.ProductResponse;
import com.spring_commerce.repositories.CartRepository;
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
  private CartRepository cartRepository;

  @Autowired
  private CartService cartService;

  @Autowired
  private ModelMapper modelMapper;

  @Value("${project.image}")
  private String path;

  @Override
  public ProductDTO createProduct(final ProductDTO newProductDTO, final Long categoryId) {

    final Category category = this.categoryRepository.findById(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("Category", "ID", categoryId));

    if (this.productRepository.existsByName(newProductDTO.getName())) {
      throw new APIException("Product with the name " + newProductDTO.getName() + " already exists.");
    }

    final Product newProduct = this.modelMapper.map(newProductDTO, Product.class);

    newProduct.setCategory(category);

    newProduct.setSpecialPrice(newProductDTO.getSpecialPrice());
    newProduct.setImage("default.png");

    final Product savedProduct = this.productRepository.save(newProduct);
    return this.modelMapper.map(savedProduct, ProductDTO.class);
  }

  @Override
  public ProductResponse getAllProducts(final Integer pageNumber, final Integer pageSize, final String sortBy,
      final String sortOrder) {

    final Page<Product> productPage = this.getPaginatedResponse(this.productRepository, Product.class, pageNumber,
        pageSize, sortBy, sortOrder);

    final List<ProductDTO> productDTOs = productPage.getContent().stream()
        .map(product -> this.modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());

    final ProductResponse productResponse = new ProductResponse();
    productResponse.setContent(productDTOs);
    productResponse.setPageNumber(productPage.getNumber());
    productResponse.setPageSize(productPage.getSize());
    productResponse.setTotalElements(productPage.getTotalElements());
    productResponse.setTotalpages(productPage.getTotalPages());
    productResponse.setLastPage(productPage.isLast());

    return productResponse;

  }

  @Override
  public ProductResponse getProductsbyCategoryId(final Long categoryId, final int pageNumber, final int pageSize,
      final String sortBy, final String sortOrder) {

    this.getOrThrow(this.categoryRepository, categoryId, "Category");

    final Pageable pageable = this.getPageable(Product.class, pageNumber, pageSize, sortBy, sortOrder);

    final Page<Product> productPage = this.productRepository.findByCategoryId(categoryId, pageable);

    if (productPage.isEmpty()) {
      throw new APIException("No products found for the given category.");
    }

    final List<ProductDTO> productsDTO = productPage.getContent().stream()
        .map(product -> this.modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());

    return new ProductResponse(productsDTO, productPage.getNumber(), productPage.getSize(),
        productPage.getTotalElements(), productPage.getTotalPages(), productPage.isLast());
  }

  @Override
  public ProductResponse getProductsbyKeyword(final String keyword, final int pageNumber, final int pageSize,
      final String sortBy, final String sortOrder) {

    final Pageable pageable = this.getPageable(Product.class, pageNumber, pageSize, sortBy, sortOrder);
    final Page<Product> productPage = this.productRepository.findByNameContainingIgnoreCase(keyword, pageable);

    if (productPage.isEmpty()) {
      throw new APIException("No products found for the given keyword.");
    }

    final List<ProductDTO> productsDTO = productPage.getContent().stream()
        .map(product -> this.modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());

    return new ProductResponse(productsDTO, productPage.getNumber(), productPage.getSize(),
        productPage.getTotalElements(), productPage.getTotalPages(), productPage.isLast());
  }

  @Override
  public ProductDTO updateProduct(final ProductDTO newProductDTO, final Long productId) {
    final Product productFromDB = this.getOrThrow(this.productRepository, productId, "Product");

    this.modelMapper.map(newProductDTO, productFromDB);

    final Product updatedProduct = this.productRepository.save(productFromDB);

    final List<Cart> carts = this.cartRepository.findAllByProductId(productId);

    final List<CartDTO> cartDTOs = carts.stream().map(cart -> {
      final CartDTO cartDTO = this.modelMapper.map(cart, CartDTO.class);
      final List<ProductDTO> products = cart.getItems().stream()
          .map(p -> this.modelMapper.map(p.getProduct(), ProductDTO.class)).toList();
      cartDTO.setProductDTOs(products);
      return cartDTO;
    }).toList();

    cartDTOs.forEach(cart -> this.cartService.updateProductInCarts(cart.getId(), productId));

    return this.modelMapper.map(updatedProduct, ProductDTO.class);
  }

  @Override
  public void deleteProduct(final Long productId) {
    final Product productFromDB = this.getOrThrow(this.productRepository, productId, "Product");

    final List<Cart> carts = this.cartRepository.findAllByProductId(productId);
    carts.forEach(cart -> this.cartService.deleteProductFromCart(cart.getId(), productId));

    this.productRepository.delete(productFromDB);
  }

  @Override
  public ProductDTO updateProductImage(final Long productId, final MultipartFile imagefile) throws IOException {
    final Product productFromDB = this.getOrThrow(this.productRepository, productId, "Product");

    final String fileName = AppUtils.uploadImage(this.path, imagefile);
    productFromDB.setImage(fileName);

    final Product updatedProduct = this.productRepository.save(productFromDB);
    return this.modelMapper.map(updatedProduct, ProductDTO.class);
  }
}
