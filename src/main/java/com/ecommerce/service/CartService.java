
package com.ecommerce.service;

import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductService productService;

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    @Transactional
    public CartItem addToCart(User user, Long productId, Integer quantity) {
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        Product product = productOpt.get();
        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByUserAndProductId(user, productId);
        
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem(user, product, quantity);
            return cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void removeFromCart(User user, Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    @Transactional
    public CartItem updateCartItemQuantity(User user, Long cartItemId, Integer quantity) {
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
        if (cartItemOpt.isEmpty()) {
            throw new RuntimeException("Cart item not found");
        }

        CartItem cartItem = cartItemOpt.get();
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }
}
