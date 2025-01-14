//package com.user_service.client;
//
//import com.user_service.model.User;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestHeader;
//
//
//@FeignClient(name = "GATEWAY-MICROSERVICE")
//public interface GatewayClient {
//
//    @GetMapping("gateway/validate")
//    Long validate(@RequestHeader("Authorization") String authHeader);
//
//    @GetMapping("gateway/getToken/{id}/{role}")
//    String getToken(@PathVariable Long id, @PathVariable String role);
//}