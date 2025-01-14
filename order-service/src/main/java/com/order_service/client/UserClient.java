package com.order_service.client;

import com.order_service.external.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "USER-MICROSERVICE")
public interface UserClient {

    @GetMapping("user/validate")
    User validate(@RequestHeader("Authorization") String authHeader);
}
