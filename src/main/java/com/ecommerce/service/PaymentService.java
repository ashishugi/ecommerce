
package com.ecommerce.service;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.Payment;
import com.ecommerce.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    @Transactional
    public Payment processPayment(Long orderId, Payment.PaymentMethod method) {
        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }

        Order order = orderOpt.get();
        
        // Check if payment already exists for this order
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(orderId);
        if (existingPayment.isPresent()) {
            throw new RuntimeException("Payment already processed for this order");
        }

        Payment payment = new Payment(order, order.getTotalAmount(), method);
        
        // Simulate payment processing
        boolean paymentSuccess = simulatePaymentProcessing(method);
        
        if (paymentSuccess) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setTransactionId(UUID.randomUUID().toString());
            order.setStatus(Order.OrderStatus.CONFIRMED);
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
        }

        orderService.updateOrderStatus(orderId, order.getStatus());
        return paymentRepository.save(payment);
    }

    private boolean simulatePaymentProcessing(Payment.PaymentMethod method) {
        // Simulate payment processing - in real world, integrate with payment gateway
        // For demo purposes, assume 90% success rate
        return Math.random() > 0.1;
    }

    public Optional<Payment> getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }
}
