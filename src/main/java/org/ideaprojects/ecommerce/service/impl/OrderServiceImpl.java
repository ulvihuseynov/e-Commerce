package org.ideaprojects.ecommerce.service.impl;

import org.ideaprojects.ecommerce.payload.OrderDTO;
import org.ideaprojects.ecommerce.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
        return null;
    }
}
