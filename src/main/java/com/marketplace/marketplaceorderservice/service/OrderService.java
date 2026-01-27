package com.marketplace.marketplaceorderservice.service;

import com.marketplace.marketplaceorderservice.client.NotificationClient;
import com.marketplace.marketplaceorderservice.client.ProductClient;
import com.marketplace.marketplaceorderservice.dto.NotificationRequest;
import com.marketplace.marketplaceorderservice.dto.OrderCreateRequest;
import com.marketplace.marketplaceorderservice.dto.ProductResponse;
import com.marketplace.marketplaceorderservice.enums.OrderStatus;
import com.marketplace.marketplaceorderservice.exception.OrderNotFoundException;
import com.marketplace.marketplaceorderservice.exception.ProductNotFoundException;
import com.marketplace.marketplaceorderservice.model.Order;
import com.marketplace.marketplaceorderservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final NotificationClient notificationClient;

    public OrderService(OrderRepository orderRepository,
                        ProductClient productClient,
                        NotificationClient notificationClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.notificationClient = notificationClient;
    }

    @CircuitBreaker(name = "product-service", fallbackMethod = "productFallback")
    public Order createOrder(Long userId, OrderCreateRequest orderCreateRequest) {
        ProductResponse product = productClient.findProductById(String.valueOf(orderCreateRequest.getProductId()));

        if (product == null) {
            throw new ProductNotFoundException("Məhsul tapılmadı: " + orderCreateRequest.getProductId());
        }

        if (product.getQuantity() < orderCreateRequest.getQuantity()) {
            throw new RuntimeException("Kifayət qədər stok yoxdur!");
        }

        BigDecimal price = product.getPrice().multiply(BigDecimal.valueOf(orderCreateRequest.getQuantity()));

        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(orderCreateRequest.getProductId());
        order.setQuantity(orderCreateRequest.getQuantity());
        order.setPrice(price);
        order.setOrderStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        sendNotificationSafe(savedOrder, price);

        return savedOrder;
    }

    @CircuitBreaker(name = "notification-service", fallbackMethod = "notificationFallback")
    public void sendNotificationSafe(Order order, BigDecimal price) {
        NotificationRequest notification = new NotificationRequest();
        notification.setTo("abdullayevala91@gmail.com");
        notification.setSubject("Sifariş Təsdiqi #" + order.getId());
        notification.setMessage("Sifarişiniz qəbul edildi. Məbləğ: " + price + " AZN");
        notificationClient.sendEmail(notification);
    }
    public void notificationFallback(Order order, BigDecimal price, Throwable e) {
        System.err.println("Bildiriş göndərilə bilmədi (Circuit Breaker OPEN): " + e.getMessage());
    }

    public Order productFallback(Long userId, OrderCreateRequest orderCreateRequest, Throwable e) {
        System.err.println("Product Service xətası: " + e.getMessage());
        throw new RuntimeException("Məhsul servisi hazırda əlçatmazdır. Zəhmət olmasa bir az sonra cəhd edin.");
    }




    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Sifariş tapılmadı: " + id));
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public void updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Sifariş tapılmadı: " + id));
        order.setOrderStatus(OrderStatus.valueOf(status));
        orderRepository.save(order);
    }
}