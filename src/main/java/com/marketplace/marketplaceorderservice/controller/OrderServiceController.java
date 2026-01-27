package com.marketplace.marketplaceorderservice.controller;

import com.marketplace.marketplaceorderservice.dto.OrderCreateRequest;
import com.marketplace.marketplaceorderservice.dto.OrderResponse;
import com.marketplace.marketplaceorderservice.mapper.OrderMapper;
import com.marketplace.marketplaceorderservice.model.Order;
import com.marketplace.marketplaceorderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderServiceController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderServiceController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader(value = "X-Auth-User-Id", required = false) String userIdHeader,
            @Valid @RequestBody OrderCreateRequest orderCreateRequest) {

        if (userIdHeader == null || userIdHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.parseLong(userIdHeader);
        Order order = orderService.createOrder(userId, orderCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toResponse(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @RequestHeader(value = "X-Auth-User-Role", required = false) String role) {


        List<OrderResponse> responses = orderService.getAllOrders().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @RequestHeader(value = "X-Auth-User-Id", required = false) String userIdHeader) {

        if (userIdHeader == null || userIdHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.parseLong(userIdHeader);
        List<OrderResponse> responses = orderService.getOrdersByUserId(userId).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-Auth-User-Role", required = false) String role) {

        if (userIdHeader == null || userIdHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Order order = orderService.getOrderById(id);
        Long userId = Long.parseLong(userIdHeader);

        if (!order.getUserId().equals(userId) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(orderMapper.toResponse(order));
    }


    @PostMapping("/{id}/update-status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader(value = "X-Auth-User-Role", required = false) String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok().build();
    }
}