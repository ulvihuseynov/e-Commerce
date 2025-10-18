package org.ideaprojects.ecommerce.service;

import org.ideaprojects.ecommerce.payload.CartDTO;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);
}
