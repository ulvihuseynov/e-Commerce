package org.ideaprojects.ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {


    private Long orderId;
    private String email;
    private List<OrderItemDTO> orderItems;
    private PaymentDTO payment;
    private LocalDate orderDate;
    private Double totalAmount;
    private String orderStatus;
    private Long addressId;
}
