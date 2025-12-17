package com.marketplace.marketplaceorderservice.service;

import com.marketplace.marketplaceorderservice.client.ProductClient;
import com.marketplace.marketplaceorderservice.dto.OrderCreateRequest;
import com.marketplace.marketplaceorderservice.dto.ProductResponse;
import com.marketplace.marketplaceorderservice.enums.OrderStatus;
import com.marketplace.marketplaceorderservice.exception.OrderNotFoundException;
import com.marketplace.marketplaceorderservice.exception.ProductNotFoundException;
import com.marketplace.marketplaceorderservice.model.Order;
import com.marketplace.marketplaceorderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepository, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }
    public Order createOrder(Long userId, OrderCreateRequest orderCreateRequest) {
        ProductResponse product = productClient.findProductById(String.valueOf(orderCreateRequest.getProductId()));
        if (product.getQuantity()<orderCreateRequest.getQuantity()) {
        throw new ProductNotFoundException("Product not found");
        }
        BigDecimal price = product.getPrice().multiply(BigDecimal.valueOf(orderCreateRequest.getQuantity()));
         Order order = new Order();
         order.setUserId(userId);
         order.setQuantity(orderCreateRequest.getQuantity());
         order.setProductId(orderCreateRequest.getProductId());
         order.setPrice(price);
         order.setOrderStatus(OrderStatus.PENDING);
         order.setOrderDate(order.getOrderDate());
         return orderRepository.save(order);
    }
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
    public void updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(OrderStatus.valueOf(status));
        orderRepository.save(order);
    }
}
