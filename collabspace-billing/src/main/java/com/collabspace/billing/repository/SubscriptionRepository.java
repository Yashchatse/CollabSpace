package com.collabspace.billing.repository;

import com.collabspace.billing.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByWorkspaceId(Long workspaceId);
    Optional<Subscription> findByRazorpayOrderId(String orderId);
}