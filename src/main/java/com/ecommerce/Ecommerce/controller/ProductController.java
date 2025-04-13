package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
import com.ecommerce.Ecommerce.exception.InvalidInputException;
import com.ecommerce.Ecommerce.model.Product;
import com.ecommerce.Ecommerce.model.ProductVariant;
import com.ecommerce.Ecommerce.model.VariantSize;
import com.ecommerce.Ecommerce.service.NotificationService;
import com.ecommerce.Ecommerce.service.ProductService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductController(
            ProductService productService,
            NotificationService notificationService,
            ObjectMapper objectMapper) {
        this.productService = productService;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    // Định nghĩa class ErrorResponse để trả về thông điệp lỗi
    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId) {
        try {
            List<Product> products = productService.getProductsWithFilters(search, categoryId, brandId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            System.err.println("Error fetching products: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "multipart/form-data")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})
    public ResponseEntity<?> createProduct(
            @RequestPart("product") String productJson) {
        try {
            System.out.println("Received productJson: " + productJson);
            Product product = objectMapper.readValue(productJson, Product.class);
            Product createdProduct = productService.createProduct(product);
            notificationService.createNotification("1 sản phẩm mới được thêm: " + createdProduct.getCode());
            return ResponseEntity.status(201).body(createdProduct);
        } catch (InvalidInputException e) {
            System.err.println("InvalidInputException: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse("Lỗi xử lý dữ liệu: " + e.getMessage()));
        }
    }

    @GetMapping("/{productId}/variants")
    public ResponseEntity<?> getProductVariants(@PathVariable Long productId) {
        try {
            return ResponseEntity.ok(productService.getProductVariants(productId));
        } catch (InvalidInputException e) {
            System.err.println("InvalidInputException: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping(value = "/{productId}/variants", consumes = "multipart/form-data")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})
    public ResponseEntity<?> createProductVariant(
            @PathVariable Long productId,
            @RequestPart("variant") String variantJson,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            System.out.println("Received variantJson for productId " + productId + ": " + variantJson);
            System.out.println("Received mainImage: " + (mainImage != null ? mainImage.getOriginalFilename() : "null"));
            System.out.println("Received images: " + (images != null ? images.size() : "null"));

            JsonNode jsonNode = objectMapper.readTree(variantJson);
            String color = jsonNode.get("color").asText();
            JsonNode sizesNode = jsonNode.get("sizes");
            List<VariantSize> sizes = objectMapper.treeToValue(sizesNode,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, VariantSize.class));

            ProductVariant variant = new ProductVariant();
            variant.setColor(color);
            variant.setSizes(sizes);

            ProductVariant createdVariant = productService.createProductVariant(productId, variant, mainImage, images);
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new InvalidInputException("Product not found"));
            notificationService.createNotification(
                    "1 biến thể mới được thêm cho sản phẩm " + product.getCode() + ": màu " + createdVariant.getColor()
            );
            return ResponseEntity.status(201).body(createdVariant);
        } catch (InvalidInputException e) {
            System.err.println("InvalidInputException: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse("Lỗi xử lý dữ liệu: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/{productId}/variants/{variantId}", consumes = "multipart/form-data")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})
    public ResponseEntity<?> updateProductVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @RequestPart(value = "variant", required = false) String variantJson,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            ProductVariant variant = new ProductVariant();
            List<Map<String, String>> imageActions = null;

            if (variantJson != null) {
                JsonNode jsonNode = objectMapper.readTree(variantJson);
                variant.setColor(jsonNode.has("color") ? jsonNode.get("color").asText() : null);

                if (jsonNode.has("sizes")) {
                    JsonNode sizesNode = jsonNode.get("sizes");
                    List<VariantSize> sizes = objectMapper.treeToValue(sizesNode,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, VariantSize.class));
                    variant.setSizes(sizes);
                }

                if (jsonNode.has("imageActions")) {
                    JsonNode imageActionsNode = jsonNode.get("imageActions");
                    imageActions = objectMapper.treeToValue(imageActionsNode,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
                }
            }

            ProductVariant updatedVariant = productService.updateProductVariant(productId, variantId, variant,
                    mainImage, images, imageActions);
            System.out.println("Variant updated successfully: " + updatedVariant);
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new InvalidInputException("Product not found"));
            notificationService.createNotification(
                    "Biến thể ID " + variantId + " của sản phẩm " + product.getCode() + " đã được cập nhật"
            );
            return ResponseEntity.ok(updatedVariant);
        } catch (InvalidInputException e) {
            System.err.println("InvalidInputException during updateProductVariant: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (IOException e) {
            System.err.println("IOException during updateProductVariant: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse("Lỗi xử lý dữ liệu: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{productId}/variants/{variantId}")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})
    public ResponseEntity<?> deleteProductVariant(
            @PathVariable Long productId, @PathVariable Long variantId) {
        try {
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new InvalidInputException("Product not found"));
            productService.deleteProductVariant(productId, variantId);
            notificationService.createNotification(
                    "Biến thể ID " + variantId + " của sản phẩm " + product.getCode() + " đã bị xóa"
            );
            return ResponseEntity.noContent().build();
        } catch (InvalidInputException e) {
            System.err.println("InvalidInputException: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") String productJson) {
        try {
            System.out.println("Received updateProduct request for ID: " + id);
            System.out.println("Received productJson: " + productJson);

            Product product = objectMapper.readValue(productJson, Product.class);
            System.out.println("Parsed product: " + product);

            Product updatedProduct = productService.updateProduct(id, product);
            System.out.println("Product updated successfully: " + updatedProduct);
            notificationService.createNotification("Sản phẩm " + updatedProduct.getCode() + " đã được cập nhật");
            return ResponseEntity.ok(updatedProduct);
        } catch (InvalidInputException e) {
            System.err.println("InvalidInputException during updateProduct: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (IOException e) {
            System.err.println("IOException during updateProduct: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse("Lỗi xử lý dữ liệu: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new InvalidInputException("Product not found"));
            productService.deleteProduct(id);
            notificationService.createNotification("Sản phẩm " + product.getCode() + " đã bị xóa");
            return ResponseEntity.noContent().build();
        } catch (InvalidInputException e) {
            System.err.println("InvalidInputException: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}