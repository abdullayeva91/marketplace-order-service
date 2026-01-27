package com.marketplace.marketplaceorderservice;

import com.marketplace.marketplaceorderservice.client.NotificationClient;
import com.marketplace.marketplaceorderservice.client.ProductClient;
import com.marketplace.marketplaceorderservice.dto.OrderCreateRequest;
import com.marketplace.marketplaceorderservice.dto.ProductResponse;
import com.marketplace.marketplaceorderservice.enums.OrderStatus;
import com.marketplace.marketplaceorderservice.model.Order;
import com.marketplace.marketplaceorderservice.repository.OrderRepository;
import com.marketplace.marketplaceorderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductClient productClient;
    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrderSuccessfully_Unit() {
        Long userId = 55L;
        OrderCreateRequest request = new OrderCreateRequest();
        request.setProductId(1L);
        request.setQuantity(3);

        ProductResponse mockProduct = new ProductResponse();
        mockProduct.setPrice(new BigDecimal("10.0"));
        mockProduct.setQuantity(10);

        when(productClient.findProductById("1")).thenReturn(mockProduct);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order result = orderService.createOrder(userId, request);

        assertNotNull(result);
        assertEquals(new BigDecimal("30.0"), result.getPrice()); // 10 * 3 = 30
        assertEquals(OrderStatus.PENDING, result.getOrderStatus());

        verify(notificationClient).sendEmail(any());
    }

    @Test
    void shouldThrowException_WhenStockIsNotEnough() {
        ProductResponse mockProduct = new ProductResponse();
        mockProduct.setQuantity(1);
        when(productClient.findProductById("1")).thenReturn(mockProduct);

        OrderCreateRequest request = new OrderCreateRequest();
        request.setProductId(1L);
        request.setQuantity(5);

        assertThrows(RuntimeException.class, () -> orderService.createOrder(1L, request));
    }
}

