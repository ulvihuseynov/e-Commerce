package org.ideaprojects.ecommerce.service.impl;

import jakarta.transaction.Transactional;
import org.ideaprojects.ecommerce.exceptions.APIException;
import org.ideaprojects.ecommerce.exceptions.ResourceNotFoundException;
import org.ideaprojects.ecommerce.model.Cart;
import org.ideaprojects.ecommerce.model.CartItem;
import org.ideaprojects.ecommerce.model.Product;
import org.ideaprojects.ecommerce.payload.CartDTO;
import org.ideaprojects.ecommerce.payload.ProductDTO;
import org.ideaprojects.ecommerce.repository.CartItemRepository;
import org.ideaprojects.ecommerce.repository.CartRepository;
import org.ideaprojects.ecommerce.repository.ProductRepository;
import org.ideaprojects.ecommerce.service.CartService;
import org.ideaprojects.ecommerce.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final AuthUtil authUtil;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, ModelMapper modelMapper, AuthUtil authUtil) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.authUtil = authUtil;
    }

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        Cart cart=createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem=cartItemRepository.findCartItemByCartIdAndProductId(
                cart.getCartId(),
                productId
        );

        if (cartItem !=null){
            throw new APIException("Product "+ product.getProductName() + " already taken in the cart");
        }

        if (product.getQuantity() ==0){
            throw new APIException( product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity){
            throw new APIException("Please make an order of the "+ product.getProductName() + " " +
                    " less than or equal to the quantity " +product.getQuantity());
        }
        CartItem newCartItem=new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

//        product.setQuantity(product.getQuantity()-quantity);
        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice()+(product.getSpecialPrice()*quantity));

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();

        List<ProductDTO> productDTOS = cartItems.stream().map(item -> {
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);

            productDTO.setQuantity(item.getQuantity());

            return productDTO;
        }).toList();

        cartDTO.setProducts(productDTOS);

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {

        List<Cart> carts = cartRepository.findAll();

        if (carts.isEmpty()){
            throw new APIException("cart no exist");
        }

        List<CartDTO> cartDTOS = carts.stream().map(cart -> {
          CartDTO cartDTO=   modelMapper.map(cart, CartDTO.class);
            List<CartItem> cartItems = cart.getCartItems();
            List<ProductDTO> productDTOS = cartItems.stream().map(item -> {
                ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                productDTO.setQuantity(item.getQuantity());
                return productDTO;
            }).toList();
            cartDTO.setProducts(productDTOS);
            return cartDTO;
        }).toList();

        return cartDTOS;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart= cartRepository.findCartByEmailAndCartId(emailId,cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart","cartId",cartId);

        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();
        List<ProductDTO> productDTOS = cartItems.stream().map(item -> {
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
                }
        ).toList();
        cartDTO.setProducts(productDTOS);
        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));


        if (product.getQuantity() ==0){
            throw new APIException( product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity){
            throw new APIException("Please make an order of the "+ product.getProductName() + " " +
                    " less than or equal to the quantity " +product.getQuantity());
        }

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product "+ product.getProductName() + " is not available int the cart");
        }
        int newQuantity=cartItem.getQuantity()+quantity;

        if (newQuantity<0){
            throw new APIException("The resulting quantity cannot be negative");
        }

        if (newQuantity==0){
            deleteProductFromCart(cartId,productId);
        }else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());

            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));

            cartRepository.save(cart);
        }
        CartItem updateCartItem = cartItemRepository.save(cartItem);

        if (updateCartItem.getQuantity()==0){
            cartItemRepository.deleteById(updateCartItem.getCartItemId());
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();
        List<ProductDTO> productDTOS = cartItems.stream().map(item -> {
                    ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(item.getQuantity());
                    return productDTO;
                }
        ).toList();
        cartDTO.setProducts(productDTOS);
        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {


        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId, productId);

        if (cartItem ==null){
            throw  new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice()-
                (cartItem.getProductPrice()* cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(productId,cartId);

        return "Product "+ cartItem.getProduct().getProductName() + " remove the cart!";
    }

    private Cart createCart(){

        Cart userCart=cartRepository.findCartByEmail((authUtil.loggedInEmail()));

        if (userCart !=null){
            return userCart;
        }
        Cart cart=new Cart();

        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());

        return cartRepository.save(cart);
    }
}
