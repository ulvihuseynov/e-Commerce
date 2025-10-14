package org.ideaprojects.ecommerce.service;

import org.ideaprojects.ecommerce.payload.ProductDTO;
import org.ideaprojects.ecommerce.payload.ProductResponse;

public interface ProductService {
    ProductDTO addProduct(ProductDTO productDTO, Long categoryId);

    ProductResponse getAllProducts();

    ProductResponse searchByCategory(Long categoryId);

    ProductResponse searchProductByKeyword(String keyword);

    ProductDTO updateProduct(ProductDTO productDTO, Long productId);

    ProductDTO deleteProduct(Long productId);
}
