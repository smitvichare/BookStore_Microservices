package com.order_service.controller;

import com.order_service.client.UserClient;
import com.order_service.dto.AddressRequest;
import com.order_service.dto.OrderDTO;
import com.order_service.exception.OrderNotFoundException;
import com.order_service.external.User;
import com.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private UserClient userClient;

    @Autowired
    private OrderService orderService;

    private User getAuthenticatedUser(String authHeader) {
        User user = userClient.validate(authHeader);
        if(user!=null)
            return user;

        return null;
    }

    @GetMapping("/test")
    public String test(){
        return "Test";
    }

    @PostMapping("/place")
    public ResponseEntity<?> orderPlace(@RequestHeader("Authorization") String authHeader,
                                         @RequestBody OrderDTO orderDTO) {

        User isAuthenticatedUser = getAuthenticatedUser(authHeader);

        if(isAuthenticatedUser!=null) {

            if(!orderDTO.getCancel()) {
                orderDTO.setUserId(isAuthenticatedUser.getId());
                OrderDTO dto = orderService.orderPlace(orderDTO);
                return new ResponseEntity<>(orderDTO,HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new OrderNotFoundException("Order Canceled"),HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("unauthorised", HttpStatus.UNAUTHORIZED);
        }
    }
    @PostMapping("/cart/{cid}")
    public ResponseEntity<?> orderPlaceBYCid(@RequestHeader("Authorization") String authHeader, @PathVariable long cid, @RequestBody AddressRequest address) {

        User isAuthenticatedUser = getAuthenticatedUser(authHeader);

        if(isAuthenticatedUser!=null) {
            System.out.println("here");
            return new ResponseEntity<>(orderService.orderById(authHeader,isAuthenticatedUser.getId(),cid,address),HttpStatus.OK);
        } else {
            return new ResponseEntity<>("unauthorised", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/user")
    public ResponseEntity<?> orderPlaceByUser(@RequestHeader("Authorization") String authHeader, @RequestBody AddressRequest address) {
        System.out.println("test!");
        User isAuthenticatedUser = getAuthenticatedUser(authHeader);

        if(isAuthenticatedUser!=null) {
            System.out.println("here");
            return new ResponseEntity<>(orderService.orderByUser(authHeader,isAuthenticatedUser.getId(),address),HttpStatus.OK);
        } else {
            return new ResponseEntity<>("unauthorised", HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<?> orderCancel(@RequestHeader("Authorization") String authHeader,
                                           @PathVariable Long orderId) {
        User isAuthenticatedUser = getAuthenticatedUser(authHeader);

        if (isAuthenticatedUser != null) {
            orderService.cancelOrder(orderId,isAuthenticatedUser);
            return new ResponseEntity<>("Order canceled successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/getAllOrders")
    public ResponseEntity<?> getAllOrders(@RequestHeader("Authorization") String authHeader){
        User isAuthenticatedUser = getAuthenticatedUser(authHeader);
        if(isAuthenticatedUser != null && isAuthenticatedUser.getRole().equalsIgnoreCase("admin")){
            List<OrderDTO> orderDTOList = orderService.getAllOrders();

            return new ResponseEntity<>(orderDTOList,HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Unauthorized User", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/getOrder/forUser")
    public ResponseEntity<?> getAllOrdersForUser(@RequestHeader("Authorization") String authHeader){
        User isAuthenticatedUser = getAuthenticatedUser(authHeader);
        if(isAuthenticatedUser != null){
            List<OrderDTO> orderDTOList = orderService.getAllOrdersForUser(authHeader);
            return new ResponseEntity<>(orderDTOList,HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Unauthorized User", HttpStatus.UNAUTHORIZED);
        }
    }
}