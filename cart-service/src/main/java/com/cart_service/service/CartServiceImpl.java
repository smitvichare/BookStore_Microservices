package com.cart_service.service;

import com.cart_service.client.BookClient;
import com.cart_service.exception.CartNotFoundException;
import com.cart_service.external.Book;
import com.cart_service.model.Cart;
import com.cart_service.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BookClient bookClient;

    @Override
    public Cart addToCart(Cart cart) {
        Book book = bookClient.getBookById(cart.getBookId());
        cart.setTotalPrice(book.getBookPrice()* cart.getQuantity());
        //bookClient.addToCartUpdateBookQuantity(0L,cart.getBookId(), cart.getQuantity());
         return cartRepository.save(cart);
    }

    @Override
    public Cart getByCartId(Long cartId) {
        return cartRepository.findById(cartId).orElseThrow(()->new CartNotFoundException("Cart ID not found"));
    }

    @Override
    public Cart  removeFromCart(long cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        try {
            if (cart != null){
                //bookClient.addedBackToBook(cart.getBookId(), cart.getQuantity());
                cartRepository.deleteById(cartId);
                return cart;
            }else {
                throw new CartNotFoundException("cart Id: " +cartId +" Not found in cart list" );
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while communicating with the Book service: "+ e);
        }

    }

    @Override
    public List<Cart> deleteCartByUserId(long userId) {
        List<Cart> allByUserId = cartRepository.findByUserId(userId);
        if (allByUserId.isEmpty()){
            return null;
        }

        for (Cart cart : allByUserId) {
            //bookClient.addedBackToBook(cart.getBookId(),1L);
            cartRepository.deleteById(cart.getCartId());
        }
        return allByUserId;
    }

    @Override
    public Cart updateQuantityInCartItem(Long cartId, Long qty){
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart != null && qty > 0){
            //bookClient.addToCartUpdateBookQuantity(cart.getQuantity(),cart.getBookId(),qty);
            cart.setQuantity(qty);
            Book book = bookClient.getBookById(cart.getBookId());
            cart.setTotalPrice(book.getBookPrice()*qty);
            return cartRepository.save(cart);
        }
        return null;
    }

    @Override
    public List<Cart> getAllByUserId(Long userId) {
        System.out.println(cartRepository.findByUserId(userId));
        return cartRepository.findByUserId(userId);// cartRepository.findAllByUserId(userId);
    }

    @Override
    public List<Cart> getAll() {
        return cartRepository.findAll();
    }

    @Override
    public List<Long> getByCartUserId(Long id) {
        return cartRepository.findAll()
                .stream().filter(i-> Objects.equals(i.getUserId(), id))
                .map(Cart::getCartId)
                .collect(Collectors.toList());
    }

//    @Override
//    public List<Long> getCartIdByUser(Long id) {
//        return cartRepository.findAll()
//                .stream().filter(i-> Objects.equals(i.getUserId(), id))
//                .map(Cart::getCartId)
//                .collect(Collectors.toList());
//    }




}