package com.marketplace.marketplaceorderservice;

import com.marketplace.marketplaceorderservice.client.NotificationClient;
import com.marketplace.marketplaceorderservice.client.ProductClient;
import com.marketplace.marketplaceorderservice.dto.OrderCreateRequest;
import com.marketplace.marketplaceorderservice.dto.ProductResponse;
import com.marketplace.marketplaceorderservice.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductClient productClient;

    @MockBean
    private NotificationClient notificationClient;

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        ProductResponse mockProduct = new ProductResponse();
        mockProduct.setId(1L);
        mockProduct.setName("Test Product");
        mockProduct.setPrice(new BigDecimal("100.0"));
        mockProduct.setQuantity(10);

        when(productClient.findProductById(anyString())).thenReturn(mockProduct);

        mockMvc.perform(post("/api/orders")
                        .header("X-Auth-User-Id", "55")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnForbidden_WhenUpdatingStatusAsUser() throws Exception {
        mockMvc.perform(post("/api/orders/1/update-status")
                        .header("X-Auth-User-Role", "USER")
                        .param("status", "SHIPPED"))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }
    @Test
    void shouldReturnUnauthorized_WhenUserIdIsMissing() throws Exception {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
