package com.order_service.client;

import com.order_service.external.Cart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "CART-MICROSERVICE")
public interface CartClient {

    @GetMapping("cart/{id}")
    Cart getCartById(@PathVariable Long id);

    @GetMapping("cart/users/{id}")
    List<Long> getCartIdByUser(@PathVariable Long id);

    @DeleteMapping("cart/removeFromCart/{cartId}")
    void removeFromCart(@RequestHeader("Authorization") String authHeader,@PathVariable Long cartId);
}
