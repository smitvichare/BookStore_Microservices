package com.book_service.controller;

import com.book_service.client.UserClient;
import com.book_service.external.User;
import com.book_service.model.Book;
import com.book_service.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    private UserClient userClient;

    private User getAuthenticatedAdminUser(String authHeader) {
        User user = userClient.validate(authHeader);
        if(user!=null && user.getRole().equalsIgnoreCase("ADMIN"))
            return user;

        return null;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addBookWithLogoWithToken(@RequestHeader("Authorization") String authHeader,
                                                      @RequestBody Book book) {
        try {
            User isAdmin = getAuthenticatedAdminUser(authHeader);
            if (isAdmin != null) {
                Book addedBook = bookService.addBook(book);
                return new ResponseEntity<>(addedBook, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public List<Book> getAllBooks() {
        return bookService.getAllBookDetails();
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {

        //return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        return bookService.getBookDetailsById(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBookById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {

        User isAdmin = getAuthenticatedAdminUser(authHeader);

        if(isAdmin!=null) {
            return bookService.deleteBookDetailsById(id);

        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }



    @PutMapping("/update/qty/{id}")
    public ResponseEntity<?> changeBookQty(@RequestHeader("Authorization") String authHeader, @PathVariable long id,@RequestParam("qty") long qty) {

        User isAdmin=getAuthenticatedAdminUser(authHeader);
        if(isAdmin!=null) {
            return new ResponseEntity<>(bookService.ChangeBookQuantity(id, qty),HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/update/price/{id}")
    public ResponseEntity<?> changeBookPrice(@RequestHeader("Authorization") String authHeader, @PathVariable long id,@RequestParam("price") long price) {

        User isAdmin=getAuthenticatedAdminUser(authHeader);
        if(isAdmin!=null ) {
            return new ResponseEntity<>(bookService.ChangeBookPrice(id, price),HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/rmvFromCartAddToBook/{bookId}/{qty}")
    public ResponseEntity<?> addedBackToBook(@PathVariable Long bookId,@PathVariable Long qty) {
        return new ResponseEntity<>(bookService.addBackToBookTable(bookId,qty),HttpStatus.OK);

    }

    @PutMapping("/orderPlacedUpdateBookTable/{bookId}/{qty}")
    public ResponseEntity<?> placedOrderUpdateBookTable(@PathVariable Long bookId,@PathVariable Long qty){
        return bookService.orderPlacedUpdateBookTable(bookId,qty);
    }
}