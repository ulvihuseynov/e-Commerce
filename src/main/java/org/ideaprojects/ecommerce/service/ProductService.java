package org.ideaprojects.ecommerce.service;

import org.ideaprojects.ecommerce.payload.ProductDTO;
import org.ideaprojects.ecommerce.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(ProductDTO productDTO, Long categoryId);

    ProductResponse getAllProducts(Integer pageSize, Integer pageNumber, String sortBy, String sortOrder, String keyword, String category);

    ProductResponse searchByCategory(Long categoryId,Integer pageSize,Integer pageNumber,String sortBy,String sortOrder);

    ProductResponse searchProductByKeyword(String keyword,Integer pageSize,Integer pageNumber,String sortBy,String sortOrder);

    ProductDTO updateProduct(ProductDTO productDTO, Long productId);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
