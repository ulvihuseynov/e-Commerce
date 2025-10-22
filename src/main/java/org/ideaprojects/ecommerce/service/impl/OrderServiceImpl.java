package org.ideaprojects.ecommerce.service.impl;

import jakarta.transaction.Transactional;
import org.ideaprojects.ecommerce.exceptions.APIException;
import org.ideaprojects.ecommerce.exceptions.ResourceNotFoundException;
import org.ideaprojects.ecommerce.model.*;
import org.ideaprojects.ecommerce.payload.OrderDTO;
import org.ideaprojects.ecommerce.payload.OrderItemDTO;
import org.ideaprojects.ecommerce.repository.*;
import org.ideaprojects.ecommerce.service.CartService;
import org.ideaprojects.ecommerce.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(CartRepository cartRepository, AddressRepository addressRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, PaymentRepository paymentRepository, ProductRepository productRepository, CartService cartService, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {

        Cart cart = cartRepository.findCartByEmail(emailId);

        if (cart == null) {
         throw new ResourceNotFoundException("Cart","email",emailId);
        }

        Address address = addressRepository.findById(addressId).
                orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        Order order=new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted");
        order.setAddress(address);

        Payment payment=new Payment( paymentMethod,  pgPaymentId,  pgResponseMessage,  pgName, pgStatus);
        payment.setOrder(order);
        payment=paymentRepository.save(payment);

        order.setPayment(payment);

        Order saveOrder = orderRepository.save(order);

        List<CartItem> cartItems = cart.getCartItems();

        if (cartItems.isEmpty()){
            throw new APIException("Cart is empty");
        }

        List<OrderItem> orderItems=new ArrayList<>();

        for (CartItem cartItem: cartItems){
            OrderItem orderItem=new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(saveOrder);
            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);

        cart.getCartItems().forEach(item->{
            Integer quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity()-quantity);
            productRepository.save(product);

            cartService.deleteProductFromCart(cart.getCartId(),item.getProduct().getProductId());
        });

        OrderDTO orderDTO = modelMapper.map(saveOrder, OrderDTO.class);
        orderItems.forEach(orderItem->orderDTO.getOrderItems().add(modelMapper.map(orderItem, OrderItemDTO.class)));
        orderDTO.setAddressId(addressId);
        return orderDTO;
    }
}
