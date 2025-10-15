package org.ideaprojects.ecommerce.service.impl;

import org.ideaprojects.ecommerce.exceptions.APIException;
import org.ideaprojects.ecommerce.exceptions.ResourceNotFoundException;
import org.ideaprojects.ecommerce.model.Category;
import org.ideaprojects.ecommerce.model.Product;
import org.ideaprojects.ecommerce.payload.ProductDTO;
import org.ideaprojects.ecommerce.payload.ProductResponse;
import org.ideaprojects.ecommerce.repository.CategoryRepository;
import org.ideaprojects.ecommerce.repository.ProductRepository;
import org.ideaprojects.ecommerce.service.FileService;
import org.ideaprojects.ecommerce.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;


    @Value("${project.image}")
    private  String path;


    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper, FileService fileService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
    }



    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {

        Product product = modelMapper.map(productDTO, Product.class);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        boolean isProductNotPresent=true;
        List<Product> products = category.getProducts();

        for (Product value : products){


            if (value.getProductName().equalsIgnoreCase(product.getProductName())){
                isProductNotPresent=false;
                break;
            }
      }
       if (isProductNotPresent){
           product.setCategory(category);
           product.setImage("default.png");
           double specialPrice= product.getPrice() - (product.getPrice()*(product.getDiscount()/100));
           product.setSpecialPrice(specialPrice);
           Product savedProduct = productRepository.save(product);
           return modelMapper.map(savedProduct, ProductDTO.class);
       }else{
           throw new APIException("Product  " +product+ " already exist" );
       }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageSize,Integer pageNumber,String sortBy,String sortOrder) {

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize,sortByAndOrder);

        Page<Product> productPage = productRepository.findAll(pageRequest);

        List<Product> products = productPage.getContent();

        if (products.isEmpty()){
            throw new APIException("No product exist");
        }

        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse=new ProductResponse();

        productResponse.setContent(productDTOS);
        productResponse.setPageSize(productPage.getSize());
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPage(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());



        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId,Integer pageSize,Integer pageNumber,String sortBy,String sortOrder) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize,sortByAndOrder);

        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category, pageRequest);

        List<Product> products = productPage.getContent();

        if (products.isEmpty()){
            throw new APIException("No product exist");
        }

        List<ProductDTO> productDTOS = products.stream().map(product ->
                modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageSize(productPage.getSize());
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPage(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword,Integer pageSize,Integer pageNumber,String sortBy,String sortOrder) {

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize,sortByAndOrder);

        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%", pageRequest);

        List<Product> products = productPage.getContent();

        List<ProductDTO> productDTOS = products.stream().map(product ->
                modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageSize(productPage.getSize());
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPage(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());

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

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));


        String fileName= fileService.uploadImage(path,image);

        productFromDB.setImage(fileName);

        Product savedProduct = productRepository.save(productFromDB);
        return modelMapper.map(savedProduct,ProductDTO.class);
    }


}
