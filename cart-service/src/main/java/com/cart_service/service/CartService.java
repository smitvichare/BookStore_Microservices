package com.cart_service.service;


import com.cart_service.model.Cart;

import java.util.List;

public interface CartService {

    Cart addToCart(Cart cart);

    Cart getByCartId(Long cartId);

    Cart  removeFromCart(long cartId);


    List<Cart> deleteCartByUserId(long userId);

    Cart updateQuantityInCartItem(Long cartId, Long qty);

    List<Cart> getAllByUserId(Long userId);

    List<Cart> getAll();

    List<Long> getByCartUserId(Long id);

//    List<Long> getCartIdByUser(Long id);



}