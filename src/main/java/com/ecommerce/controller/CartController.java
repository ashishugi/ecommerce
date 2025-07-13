
package com.ecommerce.controller;

import com.ecommerce.dto.AddToCartRequest;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.User;
import com.ecommerce.security.UserPrincipal;
import com.ecommerce.service.CartService;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCartItems(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<CartItem> cartItems = cartService.getCartItems(user);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody AddToCartRequest request, Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            CartItem cartItem = cartService.addToCart(user, request.getProductId(), request.getQuantity());
            return ResponseEntity.ok(cartItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartItemId, Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            cartService.removeFromCart(user, cartItemId);
            return ResponseEntity.ok("Item removed from cart");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemId, @RequestParam Integer quantity, Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            CartItem cartItem = cartService.updateCartItemQuantity(user, cartItemId, quantity);
            return ResponseEntity.ok(cartItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart(Authentication authentication) {
        User user = getCurrentUser(authentication);
        cartService.clearCart(user);
        return ResponseEntity.ok("Cart cleared");
    }

    private User getCurrentUser(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userService.findByUsername(userPrincipal.getUsername());
    }
}
