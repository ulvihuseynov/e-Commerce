package org.ideaprojects.ecommerce.service.impl;

import org.ideaprojects.ecommerce.exceptions.ResourceNotFoundException;
import org.ideaprojects.ecommerce.model.Category;
import org.ideaprojects.ecommerce.model.Product;
import org.ideaprojects.ecommerce.payload.ProductDTO;
import org.ideaprojects.ecommerce.payload.ProductResponse;
import org.ideaprojects.ecommerce.repository.CategoryRepository;
import org.ideaprojects.ecommerce.repository.ProductRepository;
import org.ideaprojects.ecommerce.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {

        Product product = modelMapper.map(productDTO, Product.class);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        product.setCategory(category);
        product.setImage("default.png");
        double specialPrice= product.getPrice() - (product.getPrice()*(product.getDiscount()/100));
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {

        List<Product> products = productRepository.findAll();

        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);

        List<ProductDTO> productDTOS = products.stream().map(product ->
                modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword) {

        List<Product> products = productRepository.findByProductNameLikeIgnoreCase("%" +keyword + "%");

        List<ProductDTO> productDTOS = products.stream().map(product ->
                modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {

        Product product = modelMapper.map(productDTO, Product.class);

        Product productDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        productDB.setProductName(product.getProductName());
        productDB.setDescription(product.getDescription());
        productDB.setQuantity(product.getQuantity());
        productDB.setPrice(product.getPrice());
        productDB.setDiscount(product.getDiscount());
        productDB.setSpecialPrice(product.getSpecialPrice());

        Product updateProduct = productRepository.save(productDB);
        return modelMapper.map(updateProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }
}
