package com.collabspace.billing.service;

import com.collabspace.billing.dto.OrderRequest;
import com.collabspace.billing.dto.OrderResponse;
import com.collabspace.billing.entity.Subscription;
import com.collabspace.billing.repository.SubscriptionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

    private final SubscriptionRepository subscriptionRepository;
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key-id}")
    private String keyId;

    // Pro plan price — 999 INR in paise (1 INR = 100 paise)
    private static final long PRO_PLAN_AMOUNT = 99900L;

    public BillingService(SubscriptionRepository subscriptionRepository,
                          RazorpayClient razorpayClient) {
        this.subscriptionRepository = subscriptionRepository;
        this.razorpayClient = razorpayClient;
    }

    public OrderResponse createOrder(OrderRequest request,
                                      String ownerEmail) throws Exception {
        JSONObject options = new JSONObject();
        options.put("amount", PRO_PLAN_AMOUNT);
        options.put("currency", "INR");
        options.put("receipt", "workspace_" + request.getWorkspaceId());

        Order order = razorpayClient.orders.create(options);
        String orderId = order.get("id");

        Subscription subscription = subscriptionRepository
                .findByWorkspaceId(request.getWorkspaceId())
                .orElse(new Subscription());

        subscription.setWorkspaceId(request.getWorkspaceId());
        subscription.setOwnerEmail(ownerEmail);
        subscription.setRazorpayOrderId(orderId);
        subscription.setStatus(Subscription.PaymentStatus.PENDING);
        subscriptionRepository.save(subscription);

        return new OrderResponse(orderId, PRO_PLAN_AMOUNT, "INR", keyId);
    }

    public void handleWebhook(String payload) {
        try {
            JSONObject json = new JSONObject(payload);
            String event = json.getString("event");

            if ("payment.captured".equals(event)) {
                String orderId = json.getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity")
                        .getString("order_id");

                String paymentId = json.getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity")
                        .getString("id");

                subscriptionRepository.findByRazorpayOrderId(orderId)
                        .ifPresent(subscription -> {
                            subscription.setPlanType(Subscription.PlanType.PRO);
                            subscription.setStatus(Subscription.PaymentStatus.SUCCESS);
                            subscription.setRazorpayPaymentId(paymentId);
                            subscriptionRepository.save(subscription);
                            System.out.println("Workspace upgraded to PRO: "
                                    + subscription.getWorkspaceId());
                        });
            }

            if ("payment.failed".equals(event)) {
                String orderId = json.getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity")
                        .getString("order_id");

                subscriptionRepository.findByRazorpayOrderId(orderId)
                        .ifPresent(subscription -> {
                            subscription.setStatus(Subscription.PaymentStatus.FAILED);
                            subscriptionRepository.save(subscription);
                        });
            }
        } catch (Exception e) {
            System.out.println("Webhook error: " + e.getMessage());
        }
    }

    public Subscription getPlan(Long workspaceId) {
        return subscriptionRepository.findByWorkspaceId(workspaceId)
                .orElseGet(() -> {
                    Subscription free = new Subscription();
                    free.setWorkspaceId(workspaceId);
                    free.setPlanType(Subscription.PlanType.FREE);
                    free.setStatus(Subscription.PaymentStatus.SUCCESS);
                    return free;
                });
    }
}