package com.order_service.service;

import com.order_service.dto.AddressRequest;
import com.order_service.dto.OrderDTO;
import com.order_service.external.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {

    OrderDTO orderPlace(OrderDTO orderDTO);

    ResponseEntity<?> orderById(String authHeader,long id, long cid, AddressRequest address);

    OrderDTO cancelOrder(Long orderId, User user);
    List<OrderDTO> getAllOrders();
    List<OrderDTO> getAllOrdersForUser(String token);

    ResponseEntity<?> orderByUser(String authHeader,Long id, AddressRequest address);
}
