
package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.security.UserPrincipal;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<Order> orders = orderService.getUserOrders(user);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId, Authentication authentication) {
        Optional<Order> order = orderService.getOrderById(orderId);
        if (order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = getCurrentUser(authentication);
        if (!order.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(order.get());
    }

    @PostMapping
    public ResponseEntity<?> createOrder(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            Order order = orderService.createOrderFromCart(user);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestParam Order.OrderStatus status) {
        try {
            Order order = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private User getCurrentUser(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userService.findByUsername(userPrincipal.getUsername());
    }
}
