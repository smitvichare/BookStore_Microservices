package com.cart_service.controller;

import com.cart_service.client.BookClient;
import com.cart_service.client.UserClient;
import com.cart_service.exception.CartNotFoundException;
import com.cart_service.external.Book;
import com.cart_service.external.User;
import com.cart_service.model.Cart;
import com.cart_service.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private BookClient bookClient;


    private User getAuthenticatedUser(String authHeader) {
        User user = userClient.validate(authHeader);
        System.out.println(user);
        if (user != null)
            return user;

        return null;
    }

    private long getUserID(String authHeader) {
        return userClient.validate(authHeader).getId();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestHeader("Authorization") String authHeader, @RequestParam long bookId, @RequestParam long quantity) {
        Cart cart = new Cart();
        cart.setBookId(bookId);
        cart.setQuantity(quantity);
        cart.setUserId(getUserID(authHeader));
        User isUserAuthenticated = getAuthenticatedUser(authHeader);
        if (isUserAuthenticated != null) {
            Book book = bookClient.getBookById(cart.getBookId());
            if (book != null && book.getBookQuantity() > 0) {
                if (book.getBookQuantity() < cart.getQuantity()) {
                    return new ResponseEntity<>("Only " + book.getBookQuantity() + " are available; " + cart.getQuantity() + " requested.", HttpStatus.CONFLICT);
                }

                return new ResponseEntity<>(cartService.addToCart(cart), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Book Not Available", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("UnAuthorized User", HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/removeFromCart/{cartId}")
    public ResponseEntity<String> removeFromCart(@RequestHeader("Authorization") String authHeader, @PathVariable Long cartId) {
        User isUserAuthenticated = getAuthenticatedUser(authHeader);
        if (isUserAuthenticated != null) {
            Cart cart = cartService.removeFromCart(cartId);
            if (cart != null) {
                return new ResponseEntity<>("Successfully remove from the cart", HttpStatus.OK);
            }
            throw new CartNotFoundException("No Such a Cart of cart Id :" + cartId);
        } else {
            return new ResponseEntity<>("UnAuthorized User", HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/removeByUserId")
    public ResponseEntity<?> removeByUserId(@RequestHeader("Authorization") String authHeader) {
        User isUserAuthenticated = getAuthenticatedUser(authHeader);

        if (isUserAuthenticated != null) {
            List<Cart> cartList = cartService.deleteCartByUserId(isUserAuthenticated.getId());
            if (cartList.isEmpty()) {

                return new ResponseEntity<>(new CartNotFoundException("User does not have this cart access"), HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>("Successfully Deleted cart of User Id: " + isUserAuthenticated.getId(), HttpStatus.OK);
            }

        } else {
            return new ResponseEntity<>("UnAuthorized User", HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping("/updateQty")
    public ResponseEntity<?> updateQuantityInCartItem(@RequestHeader("Authorization") String authHeader,
                                                      @RequestParam Long cartId,
                                                      @RequestParam Long qty) {
        User isUserAuthenticated = getAuthenticatedUser(authHeader);

        if (isUserAuthenticated != null) {
            Cart cart = cartService.getByCartId(cartId);
            if (cart != null) {
                Book book = bookClient.getBookById(cart.getBookId());
                if (qty > book.getBookQuantity() + cart.getQuantity()) {
                    return new ResponseEntity<>("Only " + book.getBookQuantity() + cart.getQuantity() + " are available", HttpStatus.CONFLICT);
                }

                return new ResponseEntity<>(cartService.updateQuantityInCartItem(cartId, qty), HttpStatus.OK);
            } else {
                throw new CartNotFoundException("No Such a cart of cart Id :" + cartId);
            }
        } else {
            return new ResponseEntity<>("UnAuthorized User", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/getCartItemsForUser")
    public List<Cart> getAllCartItemsForUser(@RequestHeader("Authorization") String authHeader) {
        User isUserAuthenticated = getAuthenticatedUser(authHeader);
        if (isUserAuthenticated != null) {

            System.out.println(cartService.getAllByUserId(isUserAuthenticated.getId()));
            return cartService.getAllByUserId(isUserAuthenticated.getId());
            //return new ResponseEntity<>(cartService.getAllByUserId(isUserAuthenticated.getId()),HttpStatus.OK);
        } else {
            //return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            return null;
        }
    }

    @GetMapping("/getAllCartItems")
    public ResponseEntity<List<Cart>> getAllCartItems() {
        return new ResponseEntity<>(cartService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cart> getByCartId(@PathVariable Long id) {
        return new ResponseEntity<>(cartService.getByCartId(id), HttpStatus.OK);
    }

    //    @GetMapping("user/{id}")
//    public List<Long> getCartIdByUser(@PathVariable Long id){
//        return new cartService.getCartIdByUser(id);
//    }
    @GetMapping("users/{id}")
    public List<Long> getCartIdByUser(@PathVariable Long id) {

        return cartService.getByCartUserId(id);
    }

//    @DeleteMapping("/delCart/{cartId}")
//    public void removeFromCart(@PathVariable Long cartId){
//        cartService.delCart(cartId);
//    }


}
