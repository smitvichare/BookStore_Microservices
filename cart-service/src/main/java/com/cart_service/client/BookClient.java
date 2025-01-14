package com.cart_service.client;

import com.cart_service.external.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "BOOK-MICROSERVICE")
public interface BookClient {

    @GetMapping("book/{id}")
    Book getBookById(@PathVariable Long id);

//    @PutMapping("book/decreaseBookQty/{bookId}/{oldQty}/{newQty}")
//    String addToCartUpdateBookQuantity(@PathVariable Long oldQty,@PathVariable Long bookId,@PathVariable Long newQty);
//
//    @PutMapping("book/rmvFromCartAddToBook/{bookId}/{qty}")
//    ResponseEntity<String> addedBackToBook(@PathVariable Long bookId, @PathVariable Long qty);
}
