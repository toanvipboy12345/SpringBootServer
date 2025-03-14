package com.ecommerce.Ecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
public class Cart extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cart_token", unique = true, nullable = true)
    private String cartToken; // Định danh cho khách chưa đăng nhập, có thể null nếu là khách đã đăng nhập

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user; // Liên kết với User nếu khách đã đăng nhập

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>(); // Danh sách các mục trong giỏ

    // Constructors
    public Cart() {
        super(); // Kế thừa từ Auditable để có createdAt, updatedAt
        this.items = new ArrayList<>();
    }

    public Cart(String cartToken, User user) {
        this.cartToken = cartToken;
        this.user = user;
        this.items = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCartToken() { return cartToken; }
    public void setCartToken(String cartToken) { this.cartToken = cartToken; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
}