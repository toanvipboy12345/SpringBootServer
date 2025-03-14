package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.*;
import com.ecommerce.Ecommerce.model.dto.CartDTO;
import com.ecommerce.Ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductVariantRepository variantRepository;
    @Autowired
    private VariantSizeRepository sizeRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CartDTO addToCart(String cartToken, Long userId, Long productId, Long variantId, List<CartDTO.SizeQuantityRequest> sizes) {
        Cart cart = resolveCart(cartToken, userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found"));

        for (CartDTO.SizeQuantityRequest sizeRequest : sizes) {
            Long sizeId = sizeRequest.getSizeId();
            Integer quantity = sizeRequest.getQuantity();

            VariantSize size = sizeRepository.findById(sizeId)
                    .orElseThrow(() -> new IllegalArgumentException("Size not found"));

            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
            if (quantity > size.getQuantity()) {
                throw new IllegalArgumentException("Requested quantity exceeds available stock for size " + size.getSize());
            }

            CartItem existingItem = cart.getItems().stream()
                    .filter(item -> item.getProduct().getId().equals(productId)
                            && item.getVariant().getId().equals(variantId)
                            && item.getSize().getId().equals(sizeId))
                    .findFirst().orElse(null);

            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + quantity;
                if (newQuantity > size.getQuantity()) {
                    throw new IllegalArgumentException("Total quantity exceeds available stock for size " + size.getSize());
                }
                existingItem.setQuantity(newQuantity);
                cartItemRepository.save(existingItem); // Đảm bảo lưu vào CSDL
            } else {
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setProduct(product);
                newItem.setVariant(variant);
                newItem.setSize(size);
                newItem.setQuantity(quantity);
                cart.getItems().add(newItem);
                cartItemRepository.save(newItem); // Lưu trực tiếp CartItem mới vào CSDL
            }
        }

        cartRepository.save(cart); // Đảm bảo lưu Cart vào CSDL
        return buildCartDTO(cart);
    }

    public CartDTO getCart(String cartToken, Long userId) {
        Cart cart = resolveCart(cartToken, userId);
        return buildCartDTO(cart);
    }

    public CartDTO getCartByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart(null, user);
                    return cartRepository.save(newCart);
                });
        return buildCartDTO(cart);
    }

    @Transactional
    public CartDTO removeItemFromCart(String cartToken, Long userId, Long itemId) {
        Cart cart = resolveCart(cartToken, userId);
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        cart.getItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);
        cartRepository.save(cart);
        return buildCartDTO(cart);
    }

    @Transactional
    public CartDTO updateItemQuantity(String cartToken, Long userId, Long itemId, Integer newQuantity) {
        Cart cart = resolveCart(cartToken, userId);
        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        if (newQuantity <= 0) {
            cart.getItems().remove(itemToUpdate);
            cartItemRepository.delete(itemToUpdate);
        } else {
            if (newQuantity > itemToUpdate.getSize().getQuantity()) {
                throw new IllegalArgumentException("Requested quantity exceeds available stock");
            }
            itemToUpdate.setQuantity(newQuantity);
            cartItemRepository.save(itemToUpdate);
        }

        cartRepository.save(cart);
        return buildCartDTO(cart);
    }

    @Transactional
    public CartDTO getGuestCart() {
        String cartToken = "guest_" + UUID.randomUUID().toString();
        Cart cart = cartRepository.findByCartToken(cartToken).orElseGet(() -> {
            Cart newCart = new Cart(cartToken, null);
            return cartRepository.save(newCart); // Đảm bảo lưu Cart vào CSDL
        });
        return buildCartDTO(cart);
    }

    @Transactional
    public CartDTO mergeCartOnLogin(String cartToken, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Cart userCart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart(null, user);
                    return cartRepository.save(newCart);
                });

        if (cartToken != null && !cartToken.isEmpty()) {
            Cart guestCart = cartRepository.findByCartToken(cartToken)
                    .orElse(null);
            if (guestCart != null) {
                for (CartItem guestItem : guestCart.getItems()) {
                    // Kiểm tra xem CartItem có tồn tại và hợp lệ trước khi merge
                    if (guestItem.getId() != null && cartItemRepository.existsById(guestItem.getId())) {
                        CartItem existingItem = userCart.getItems().stream()
                                .filter(item -> item.getProduct().getId().equals(guestItem.getProduct().getId())
                                        && item.getVariant().getId().equals(guestItem.getVariant().getId())
                                        && item.getSize().getId().equals(guestItem.getSize().getId()))
                                .findFirst().orElse(null);

                        if (existingItem != null) {
                            int newQuantity = existingItem.getQuantity() + guestItem.getQuantity();
                            if (newQuantity > guestItem.getSize().getQuantity()) {
                                throw new IllegalArgumentException("Total quantity exceeds available stock for item: " + guestItem.getId());
                            }
                            existingItem.setQuantity(newQuantity);
                            cartItemRepository.save(existingItem);
                        } else {
                            guestItem.setCart(userCart);
                            userCart.getItems().add(guestItem);
                            cartItemRepository.save(guestItem); // Lưu CartItem mới vào CSDL
                        }
                    }
                }
                // Không xóa guestCart ngay, chỉ cập nhật hoặc giữ lại
                // cartRepository.delete(guestCart); // Bỏ dòng này để tránh lỗi
            }
        }

        cartRepository.save(userCart);
        return buildCartDTO(userCart);
    }

    private Cart resolveCart(String cartToken, Long userId) {
        if (userId != null) {
            // In userId ra console
            System.out.println("Processing cart for userId: " + userId);
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
            Cart cart = cartRepository.findByUser(user)
                    .orElseGet(() -> {
                        Cart newCart = new Cart(null, user);
                        return cartRepository.save(newCart);
                    });
            return cart;
        } else if (cartToken != null && !cartToken.isEmpty()) {
            return cartRepository.findByCartToken(cartToken)
                    .orElseGet(() -> {
                        Cart newCart = new Cart(cartToken, null);
                        return cartRepository.save(newCart);
                    });
        } else {
            throw new IllegalArgumentException("Either User-Id or Cart-Token is required");
        }
    }

    private CartDTO buildCartDTO(Cart cart) {
        Integer totalItems = cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
    
        // Tính totalPrice dựa trên logic: dùng discountPrice nếu có (> 0), nếu không thì dùng price
        Double totalPrice = cart.getItems().stream()
                .mapToDouble(item -> {
                    Double price = item.getVariant().getProduct().getPrice();
                    Double discountPrice = item.getVariant().getProduct().getDiscountPrice();
                    // Nếu discountPrice tồn tại và lớn hơn 0, dùng discountPrice, nếu không thì dùng price
                    return (discountPrice != null && discountPrice > 0 ? discountPrice : price) * item.getQuantity();
                })
                .sum();
    
        List<CartDTO.CartItemDTO> itemResponses = cart.getItems().stream()
                .map(item -> new CartDTO.CartItemDTO(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName() + " - " + item.getVariant().getColor(),
                        new CartDTO.CartItemDTO.VariantDTO(
                                item.getVariant().getId(),
                                item.getVariant().getColor(),
                                item.getVariant().getMainImage()
                        ),
                        item.getSize().getId(),
                        item.getSize().getSize(),
                        item.getQuantity(),
                        item.getSize().getQuantity(),
                        item.getVariant().getProduct().getPrice(),
                        item.getVariant().getProduct().getDiscountPrice()
                ))
                .collect(Collectors.toList());
    
        return new CartDTO(
                cart.getId(),
                cart.getCartToken(),
                cart.getUser() != null ? cart.getUser().getId() : null,
                itemResponses,
                totalItems,
                totalPrice
        );
    }
}