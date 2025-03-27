
// package com.ecommerce.Ecommerce.model;

// import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import jakarta.persistence.*;
// import java.time.LocalDateTime;
// import java.util.HashSet;
// import java.util.Set;

// @Entity
// @Table(name = "coupons")
// public class Coupon extends Auditable {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(name = "code", nullable = false, unique = true)
//     private String code;

//     @Column(name = "discount_rate", nullable = false)
//     private int discountRate;

//     @Column(name = "start_date", nullable = false)
//     private LocalDateTime startDate;

//     @Column(name = "end_date", nullable = false)
//     private LocalDateTime endDate;

//     @Column(name = "max_uses", nullable = false)
//     private int maxUses;

//     @Column(name = "used_count")
//     private int usedCount;

//     @Enumerated(EnumType.STRING)
//     @Column(name = "status", nullable = false)
//     private CouponStatus status;

//     @Column(name = "applicable_to_discounted_products", nullable = false)
//     private boolean applicableToDiscountedProducts;

//     @Column(name = "used_by_users", columnDefinition = "TEXT")
//     private String usedByUsers; // Lưu danh sách userId dưới dạng JSON

//     @Transient
//     @JsonIgnore
//     private Set<Long> usedByUsersSet; // Để xử lý danh sách userId

//     // Constructors
//     public Coupon() {
//         super();
//         this.usedByUsers = "[]"; // Khởi tạo mặc định là JSON rỗng
//         this.usedByUsersSet = new HashSet<>();
//     }

//     public Coupon(String code, int discountRate, LocalDateTime startDate, LocalDateTime endDate, 
//                   int maxUses, int usedCount, CouponStatus status, boolean applicableToDiscountedProducts) {
//         super();
//         this.code = code;
//         this.discountRate = discountRate;
//         this.startDate = startDate;
//         this.endDate = endDate;
//         this.maxUses = maxUses;
//         this.usedCount = usedCount;
//         this.status = status;
//         this.applicableToDiscountedProducts = applicableToDiscountedProducts;
//         this.usedByUsers = "[]";
//         this.usedByUsersSet = new HashSet<>();
//     }

//     // Getters and Setters
//     public Long getId() { return id; }
//     public void setId(Long id) { this.id = id; }
//     public String getCode() { return code; }
//     public void setCode(String code) { this.code = code; }
//     public int getDiscountRate() { return discountRate; }
//     public void setDiscountRate(int discountRate) { this.discountRate = discountRate; }
//     public LocalDateTime getStartDate() { return startDate; }
//     public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
//     public LocalDateTime getEndDate() { return endDate; }
//     public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
//     public int getMaxUses() { return maxUses; }
//     public void setMaxUses(int maxUses) { this.maxUses = maxUses; }
//     public int getUsedCount() { return usedCount; }
//     public void setUsedCount(int usedCount) { this.usedCount = usedCount; }
//     public CouponStatus getStatus() { return status; }
//     public void setStatus(CouponStatus status) { this.status = status; }
//     public boolean isApplicableToDiscountedProducts() { return applicableToDiscountedProducts; }
//     public void setApplicableToDiscountedProducts(boolean applicableToDiscountedProducts) { 
//         this.applicableToDiscountedProducts = applicableToDiscountedProducts; 
//     }
//     public String getUsedByUsers() { return usedByUsers; }
//     public void setUsedByUsers(String usedByUsers) { 
//         this.usedByUsers = usedByUsers; 
//         loadUsedByUsersSet(); // Cập nhật Set khi set JSON
//     }

//     // Xử lý danh sách userId
//     private void loadUsedByUsersSet() {
//         try {
//             ObjectMapper mapper = new ObjectMapper();
//             if (this.usedByUsers != null && !this.usedByUsers.isEmpty()) {
//                 this.usedByUsersSet = mapper.readValue(this.usedByUsers, new TypeReference<Set<Long>>() {});
//             } else {
//                 this.usedByUsersSet = new HashSet<>();
//             }
//         } catch (Exception e) {
//             this.usedByUsersSet = new HashSet<>();
//         }
//     }

//     private void saveUsedByUsersSet() {
//         try {
//             ObjectMapper mapper = new ObjectMapper();
//             this.usedByUsers = mapper.writeValueAsString(this.usedByUsersSet);
//         } catch (Exception e) {
//             this.usedByUsers = "[]";
//         }
//     }

//     public boolean hasUserUsed(Long userId) {
//         if (usedByUsersSet == null) {
//             loadUsedByUsersSet();
//         }
//         return usedByUsersSet.contains(userId);
//     }

//     public void addUser(Long userId) {
//         if (usedByUsersSet == null) {
//             loadUsedByUsersSet();
//         }
//         usedByUsersSet.add(userId);
//         saveUsedByUsersSet();
//     }

//     @PostLoad
//     private void onLoad() {
//         loadUsedByUsersSet();
//     }
// }
package com.ecommerce.Ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "coupons")
public class Coupon extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "discount_rate", nullable = false)
    private int discountRate;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "max_uses", nullable = false)
    private int maxUses;

    @Column(name = "used_count")
    private int usedCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CouponStatus status;

    @Column(name = "applicable_to_discounted_products", nullable = false)
    private boolean applicableToDiscountedProducts;

    @Column(name = "used_by_users", columnDefinition = "TEXT")
    private String usedByUsers; // Lưu danh sách userId dưới dạng JSON

    @Column(name = "max_discount_amount", nullable = true)
    private Double maxDiscountAmount; // Giảm tối đa bao nhiêu VND (nullable nếu không giới hạn)

    @Transient
    @JsonIgnore
    private Set<Long> usedByUsersSet; // Để xử lý danh sách userId

    // Constructors
    public Coupon() {
        super();
        this.usedByUsers = "[]"; // Khởi tạo mặc định là JSON rỗng
        this.usedByUsersSet = new HashSet<>();
    }

    public Coupon(String code, int discountRate, LocalDateTime startDate, LocalDateTime endDate, 
                  int maxUses, int usedCount, CouponStatus status, boolean applicableToDiscountedProducts, 
                  Double maxDiscountAmount) {
        super();
        this.code = code;
        this.discountRate = discountRate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxUses = maxUses;
        this.usedCount = usedCount;
        this.status = status;
        this.applicableToDiscountedProducts = applicableToDiscountedProducts;
        this.usedByUsers = "[]";
        this.usedByUsersSet = new HashSet<>();
        this.maxDiscountAmount = maxDiscountAmount; // Thêm vào constructor
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public int getDiscountRate() { return discountRate; }
    public void setDiscountRate(int discountRate) { this.discountRate = discountRate; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public int getMaxUses() { return maxUses; }
    public void setMaxUses(int maxUses) { this.maxUses = maxUses; }
    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }
    public CouponStatus getStatus() { return status; }
    public void setStatus(CouponStatus status) { this.status = status; }
    public boolean isApplicableToDiscountedProducts() { return applicableToDiscountedProducts; }
    public void setApplicableToDiscountedProducts(boolean applicableToDiscountedProducts) { 
        this.applicableToDiscountedProducts = applicableToDiscountedProducts; 
    }
    public String getUsedByUsers() { return usedByUsers; }
    public void setUsedByUsers(String usedByUsers) { 
        this.usedByUsers = usedByUsers; 
        loadUsedByUsersSet(); // Cập nhật Set khi set JSON
    }
    public Double getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(Double maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }

    // Xử lý danh sách userId
    private void loadUsedByUsersSet() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (this.usedByUsers != null && !this.usedByUsers.isEmpty()) {
                this.usedByUsersSet = mapper.readValue(this.usedByUsers, new TypeReference<Set<Long>>() {});
            } else {
                this.usedByUsersSet = new HashSet<>();
            }
        } catch (Exception e) {
            this.usedByUsersSet = new HashSet<>();
        }
    }

    private void saveUsedByUsersSet() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.usedByUsers = mapper.writeValueAsString(this.usedByUsersSet);
        } catch (Exception e) {
            this.usedByUsers = "[]";
        }
    }

    public boolean hasUserUsed(Long userId) {
        if (usedByUsersSet == null) {
            loadUsedByUsersSet();
        }
        return usedByUsersSet.contains(userId);
    }

    public void addUser(Long userId) {
        if (usedByUsersSet == null) {
            loadUsedByUsersSet();
        }
        usedByUsersSet.add(userId);
        saveUsedByUsersSet();
    }

    @PostLoad
    private void onLoad() {
        loadUsedByUsersSet();
    }
}