package com.marketplace.marketplaceorderservice.repository;

import com.marketplace.marketplaceorderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Long id(Long id);

    List<Order> findByUserId(Long userId);
}
