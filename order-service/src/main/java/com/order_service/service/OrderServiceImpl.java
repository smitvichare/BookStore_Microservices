package com.order_service.service;



import com.order_service.client.BookClient;
import com.order_service.client.CartClient;
import com.order_service.client.UserClient;
import com.order_service.dto.AddressRequest;
import com.order_service.dto.OrderDTO;
import com.order_service.exception.OrderNotFoundException;
import com.order_service.external.Book;
import com.order_service.external.Cart;
import com.order_service.external.User;
import com.order_service.model.Order;
import com.order_service.repository.OrderRepository;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookClient bookClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private CartClient cartClient;

    public OrderDTO mapToOrderDTO(Order order){
        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setAddress(order.getAddress());
        orderDTO.setQty(order.getQty());
        orderDTO.setUserId(order.getUserId());
        orderDTO.setCancel(order.getCancel());
        orderDTO.setBookId(order.getBookId());

        return orderDTO;
    }

    @Override
    public OrderDTO orderPlace(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setQty(orderDTO.getQty());
        order.setAddress(orderDTO.getAddress());
        order.setUserId(orderDTO.getUserId());
        order.setBookId(orderDTO.getBookId());
        order.setCancel(false);

        Book book = bookClient.aParticularBook(orderDTO.getBookId());
        bookClient.placedOrderUpdateBookTable(orderDTO.getBookId(), orderDTO.getQty());
        order.setPrice(book.getBookPrice()*orderDTO.getQty());

        return mapToOrderDTO(orderRepository.save(order));
    }

    @Override
    public ResponseEntity<?> orderById(String authHeader,long id, long cid, AddressRequest address) {
        Cart cart = cartClient.getCartById(cid);
        if (cart.getUserId()== id) {
            Order order = new Order();
            order.setOrderDate(LocalDate.now());
            order.setQty(cart.getQuantity());
            order.setAddress(address.getAddress());
            order.setUserId(id);
            order.setBookId(cart.getBookId());
            order.setCancel(false);

            Book book = bookClient.aParticularBook(cart.getBookId());
            bookClient.placedOrderUpdateBookTable(cart.getBookId(), cart.getQuantity());
            order.setPrice(book.getBookPrice()* cart.getQuantity());
            cartClient.removeFromCart(authHeader,cid);
            return ResponseEntity.ok(orderRepository.save(order));
        }
        return ResponseEntity.ok("Wrong Cart ID");
    }
    @Override
    public OrderDTO cancelOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order != null && Objects.equals(order.getUserId(), user.getId())){
            if(!order.getCancel()){
                order.setCancel(true);
                bookClient.addedBackToBook(order.getBookId(),order.getQty());
                return mapToOrderDTO(orderRepository.save(order));
            }else{
                throw new OrderNotFoundException("The order is already canceled...");
            }
        }else {
            throw new OrderNotFoundException("Order Not Found");
        }
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        List<Order> orderList = orderRepository.findAll();
        List<Order> orders = new ArrayList<>();
        for (Order order:orderList){
            if(!order.getCancel())
                orders.add(order);
        }
        return orders.stream()
                .map(this::mapToOrderDTO)
                .toList();
    }

    @Override
    public List<OrderDTO> getAllOrdersForUser(String token) {
        User user = userClient.validate(token);
        List<Order> orderList = orderRepository.findAllByUserId(user.getId());
        return orderList.stream()
                .map(this::mapToOrderDTO)
                .toList();
    }

    @Override
    public ResponseEntity<?> orderByUser(String authHeader,Long id, AddressRequest address) {
        System.out.println("lol");
        List<Long> userList = cartClient.getCartIdByUser(id);
        System.out.println("lol2");
        if(!userList.isEmpty()) {
            System.out.println("in");
            List<ResponseEntity> orderList = new ArrayList<>(List.of());
            userList.forEach(cid -> orderList.add(orderById(authHeader,id, cid, address)));
            return ResponseEntity.ok(orderList);
        }
        else {
            return ResponseEntity.ok("No Items in Cart.");
        }
    }

}