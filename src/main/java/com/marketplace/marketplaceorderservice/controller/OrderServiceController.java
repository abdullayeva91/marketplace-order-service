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
    public ResponseEntity<OrderResponse> createOrder(Long userId, @Valid @RequestBody OrderCreateRequest orderCreateRequest) {
        Order order = orderService.createOrder(userId, orderCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toResponse(order));
    }
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> responses = orderService.getAllOrders().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderResponse> responses = orderService.getOrdersByUserId(userId).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    @PostMapping("/{id}/update-status")
    public void updateStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
    }
}
