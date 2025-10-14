package org.ideaprojects.ecommerce.controller;

import org.ideaprojects.ecommerce.payload.ProductDTO;
import org.ideaprojects.ecommerce.payload.ProductResponse;
import org.ideaprojects.ecommerce.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId){

        ProductDTO savedProductDTO=productService.addProduct(productDTO,categoryId);

        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(){

        ProductResponse productResponse=productService.getAllProducts();

        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId){

        ProductResponse productResponse=productService.searchByCategory(categoryId);

        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword){

        ProductResponse productResponse=productService.searchProductByKeyword(keyword);

        return new ResponseEntity<>(productResponse,HttpStatus.FOUND);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO,
                                                    @PathVariable Long productId){

        ProductDTO updateProductDTO=productService.updateProduct(productDTO,productId);

        return new ResponseEntity<>(updateProductDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){

        ProductDTO deleteProduct=productService.deleteProduct(productId);

        return new ResponseEntity<>(deleteProduct,HttpStatus.OK);

    }
}
