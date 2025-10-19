package org.ideaprojects.ecommerce.controller;

import org.ideaprojects.ecommerce.model.Cart;
import org.ideaprojects.ecommerce.payload.CartDTO;
import org.ideaprojects.ecommerce.repository.CartRepository;
import org.ideaprojects.ecommerce.service.CartService;
import org.ideaprojects.ecommerce.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;
    private final AuthUtil authUtil;
    private final CartRepository cartRepository;

    public CartController(CartService cartService, AuthUtil authUtil, CartRepository cartRepository) {
        this.cartService = cartService;
        this.authUtil = authUtil;
        this.cartRepository = cartRepository;
    }

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,
                                                    @PathVariable Integer quantity){

        CartDTO cartDTO=cartService.addProductToCart(productId,quantity);

        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts(){

       List<CartDTO> cartDTO= cartService.getAllCarts();
       return new ResponseEntity<>(cartDTO,HttpStatus.FOUND);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById(){

        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();

        CartDTO cartDTO= cartService.getCart(emailId,cartId);
        return new ResponseEntity<>(cartDTO,HttpStatus.OK);
    }
}
