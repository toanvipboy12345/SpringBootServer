// package com.ecommerce.Ecommerce.controller;

// import com.ecommerce.Ecommerce.model.Product;
// import com.ecommerce.Ecommerce.model.ProductVariant;
// import com.ecommerce.Ecommerce.model.VariantSize;
// import com.ecommerce.Ecommerce.service.ProductService;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/products")
// public class ProductController {

//     private final ProductService productService;
//     private final ObjectMapper objectMapper;

//     @Autowired
//     public ProductController(ProductService productService, ObjectMapper objectMapper) {
//         this.productService = productService;
//         this.objectMapper = new ObjectMapper();
//     }

//     // @GetMapping
//     // public ResponseEntity<List<Product>> getAllProducts() {
//     //     return ResponseEntity.ok(productService.getAllProducts());
//     // }
//     /**
//  * Lấy danh sách tất cả sản phẩm với các bộ lọc tuỳ chọn.
//  *
//  * @param search     Từ khóa tìm kiếm (tuỳ chọn)
//  * @param categoryId ID danh mục để lọc (tuỳ chọn)
//  * @param brandId    ID thương hiệu để lọc (tuỳ chọn)
//  * @return ResponseEntity chứa danh sách Product phù hợp
//  */
// @GetMapping
// public ResponseEntity<List<Product>> getAllProducts(
//         @RequestParam(required = false) String search,
//         @RequestParam(required = false) Long categoryId,
//         @RequestParam(required = false) Long brandId) {
//     try {
//         List<Product> products = productService.getProductsWithFilters(search, categoryId, brandId);
//         return ResponseEntity.ok(products);
//     } catch (Exception e) {
//         System.err.println("Error fetching products: " + e.getMessage());
//         return ResponseEntity.status(500).build();
//     }
// }

//     @GetMapping("/{id}")
//     public ResponseEntity<Product> getProductById(@PathVariable Long id) {
//         return productService.getProductById(id)
//                 .map(ResponseEntity::ok)
//                 .orElse(ResponseEntity.notFound().build());
//     }

//     /**
//      * Tạo sản phẩm chính.
//      * 
//      * @param productJson Dữ liệu sản phẩm chính dưới dạng chuỗi JSON.
//      * @return ResponseEntity chứa Product đã tạo với mã trạng thái 201 (Created).
//      */
//     @PostMapping(consumes = "multipart/form-data")
//     public ResponseEntity<Product> createProduct(
//             @RequestPart("product") String productJson) {
//         try {
//             System.out.println("Received productJson: " + productJson); // Log để debug

//             Product product = objectMapper.readValue(productJson, Product.class);
//             Product createdProduct = productService.createProduct(product);
//             return ResponseEntity.status(201).body(createdProduct);
//         } catch (IllegalArgumentException e) {
//             System.err.println("IllegalArgumentException: " + e.getMessage());
//             return ResponseEntity.badRequest().body(null);
//         } catch (IOException e) {
//             System.err.println("IOException: " + e.getMessage());
//             return ResponseEntity.badRequest().body(null);
//         }
//     }

//     @GetMapping("/{productId}/variants")
//     public ResponseEntity<List<ProductVariant>> getProductVariants(@PathVariable Long productId) {
//         try {
//             return ResponseEntity.ok(productService.getProductVariants(productId));
//         } catch (IllegalArgumentException e) {
//             return ResponseEntity.badRequest().body(null);
//         }
//     }

//     /**
//      * Tạo một biến thể cho sản phẩm đã tồn tại.
//      * 
//      * @param productId   ID của sản phẩm cần thêm biến thể.
//      * @param variantJson Dữ liệu biến thể dưới dạng chuỗi JSON.
//      * @param mainImage   File hình ảnh chính (tuỳ chọn).
//      * @param images      Danh sách file ảnh phụ (tuỳ chọn).
//      * @return ResponseEntity chứa ProductVariant đã tạo với mã trạng thái 201
//      *         (Created).
//      */

//     @PostMapping(value = "/{productId}/variants", consumes = "multipart/form-data")
// public ResponseEntity<ProductVariant> createProductVariant(
//         @PathVariable Long productId,
//         @RequestPart("variant") String variantJson,
//         @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
//         @RequestPart(value = "images", required = false) List<MultipartFile> images) {
//     try {
//         System.out.println("Received variantJson for productId " + productId + ": " + variantJson);
//         System.out.println("Received mainImage: " + (mainImage != null ? mainImage.getOriginalFilename() : "null"));
//         System.out.println("Received images: " + (images != null ? images.size() : "null"));

//         // Parse JSON thủ công để tránh lỗi unrecognized field
//         JsonNode jsonNode = objectMapper.readTree(variantJson);
//         String color = jsonNode.get("color").asText();
//         JsonNode sizesNode = jsonNode.get("sizes");
//         List<VariantSize> sizes = objectMapper.treeToValue(sizesNode, 
//             objectMapper.getTypeFactory().constructCollectionType(List.class, VariantSize.class));

//         // Tạo ProductVariant thủ công
//         ProductVariant variant = new ProductVariant();
//         variant.setColor(color);
//         variant.setSizes(sizes);

//         // Gọi service để tạo biến thể
//         ProductVariant createdVariant = productService.createProductVariant(productId, variant, mainImage, images);
//         return ResponseEntity.status(201).body(createdVariant);
//     } catch (IllegalArgumentException e) {
//         System.err.println("IllegalArgumentException: " + e.getMessage());
//         return ResponseEntity.badRequest().body(null);
//     } catch (IOException e) {
//         System.err.println("IOException: " + e.getMessage());
//         return ResponseEntity.badRequest().body(null);
//     }
// }

//     /**
//      * Cập nhật một biến thể cho sản phẩm đã tồn tại.
//      * 
//      * @param productId   ID của sản phẩm liên kết với biến thể.
//      * @param variantId   ID của biến thể cần cập nhật.
//      * @param variantJson Dữ liệu biến thể dưới dạng chuỗi JSON (bao gồm sizes và
//      *                    imageActions).
//      * @param mainImage   File hình ảnh chính mới (tuỳ chọn).
//      * @param images      Danh sách file ảnh phụ mới để thêm (tuỳ chọn).
//      * @return ResponseEntity chứa ProductVariant đã cập nhật với mã trạng thái 200
//      *         (OK).
//      */
//     @PutMapping(value = "/{productId}/variants/{variantId}", consumes = "multipart/form-data")
// public ResponseEntity<ProductVariant> updateProductVariant(
//         @PathVariable Long productId,
//         @PathVariable Long variantId,
//         @RequestPart(value = "variant", required = false) String variantJson,
//         @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
//         @RequestPart(value = "images", required = false) List<MultipartFile> images) {
//     try {
//         System.out.println("Received updateProductVariant request for productId: " + productId + ", variantId: " + variantId);
//         System.out.println("Received variantJson: " + variantJson);
//         System.out.println("Received mainImage: " + (mainImage != null ? mainImage.getOriginalFilename() : "null"));
//         System.out.println("Received images: " + (images != null ? images.size() : "null"));

//         ProductVariant variant = new ProductVariant();
//         List<Map<String, String>> imageActions = null;

//         if (variantJson != null) {
//             JsonNode jsonNode = objectMapper.readTree(variantJson);
//             variant.setColor(jsonNode.has("color") ? jsonNode.get("color").asText() : null);

//             if (jsonNode.has("sizes")) {
//                 JsonNode sizesNode = jsonNode.get("sizes");
//                 List<VariantSize> sizes = objectMapper.treeToValue(sizesNode,
//                         objectMapper.getTypeFactory().constructCollectionType(List.class, VariantSize.class));
//                 variant.setSizes(sizes);
//             }

//             if (jsonNode.has("imageActions")) {
//                 JsonNode imageActionsNode = jsonNode.get("imageActions");
//                 imageActions = objectMapper.treeToValue(imageActionsNode,
//                         objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
//             }
//         }

//         ProductVariant updatedVariant = productService.updateProductVariant(productId, variantId, variant, mainImage, images, imageActions);
//         System.out.println("Variant updated successfully: " + updatedVariant);

//         return ResponseEntity.ok(updatedVariant);
//     } catch (IllegalArgumentException e) {
//         System.err.println("IllegalArgumentException during updateProductVariant: " + e.getMessage());
//         return ResponseEntity.badRequest().body(null);
//     } catch (IOException e) {
//         System.err.println("IOException during updateProductVariant: " + e.getMessage());
//         return ResponseEntity.badRequest().body(null);
//     }
// }
//     @DeleteMapping("/{productId}/variants/{variantId}")
//     public ResponseEntity<Void> deleteProductVariant(
//             @PathVariable Long productId, @PathVariable Long variantId) {
//         try {
//             productService.deleteProductVariant(productId, variantId);
//             return ResponseEntity.noContent().build();
//         } catch (IllegalArgumentException e) {
//             return ResponseEntity.badRequest().build();
//         }
//     }

//     /**
//      * Cập nhật sản phẩm chính.
//      * 
//      * @param id          ID của sản phẩm cần cập nhật.
//      * @param productJson Dữ liệu sản phẩm chính dưới dạng chuỗi JSON.
//      * @return ResponseEntity chứa Product đã cập nhật với mã trạng thái 200 (OK).
//      */
//     @PutMapping(value = "/{id}", consumes = "multipart/form-data")
//     public ResponseEntity<Product> updateProduct(
//             @PathVariable Long id,
//             @RequestPart("product") String productJson) {
//         try {
//             System.out.println("Received updateProduct request for ID: " + id);
//             System.out.println("Received productJson: " + productJson); // Log dữ liệu gửi lên

//             // Parse dữ liệu JSON
//             Product product = objectMapper.readValue(productJson, Product.class);
//             System.out.println("Parsed product: " + product); // Log dữ liệu đã parse

//             // Gọi service để cập nhật sản phẩm
//             Product updatedProduct = productService.updateProduct(id, product);
//             System.out.println("Product updated successfully: " + updatedProduct);

//             return ResponseEntity.ok(updatedProduct);
//         } catch (IllegalArgumentException e) {
//             System.err.println("IllegalArgumentException during updateProduct: " + e.getMessage());
//             return ResponseEntity.badRequest().body(null); // Trả về null để client biết có lỗi
//         } catch (IOException e) {
//             System.err.println("IOException during updateProduct: " + e.getMessage());
//             return ResponseEntity.badRequest().body(null);
//         }
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
//         try {
//             productService.deleteProduct(id);
//             return ResponseEntity.noContent().build();
//         } catch (IllegalArgumentException e) {
//             return ResponseEntity.badRequest().build();
//         }
//     }
// }
package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
import com.ecommerce.Ecommerce.model.Product;
import com.ecommerce.Ecommerce.model.ProductVariant;
import com.ecommerce.Ecommerce.model.VariantSize;
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
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductController(ProductService productService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
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
            return ResponseEntity.status(500).build();
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
    public ResponseEntity<Product> createProduct(
            @RequestPart("product") String productJson) {
        try {
            System.out.println("Received productJson: " + productJson);
            Product product = objectMapper.readValue(productJson, Product.class);
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(201).body(createdProduct);
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{productId}/variants")
    public ResponseEntity<List<ProductVariant>> getProductVariants(@PathVariable Long productId) {
        try {
            return ResponseEntity.ok(productService.getProductVariants(productId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping(value = "/{productId}/variants", consumes = "multipart/form-data")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})    public ResponseEntity<ProductVariant> createProductVariant(
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
            return ResponseEntity.status(201).body(createdVariant);
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping(value = "/{productId}/variants/{variantId}", consumes = "multipart/form-data")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})    public ResponseEntity<ProductVariant> updateProductVariant(
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

            ProductVariant updatedVariant = productService.updateProductVariant(productId, variantId, variant, mainImage, images, imageActions);
            System.out.println("Variant updated successfully: " + updatedVariant);

            return ResponseEntity.ok(updatedVariant);
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException during updateProductVariant: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (IOException e) {
            System.err.println("IOException during updateProductVariant: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{productId}/variants/{variantId}")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})    public ResponseEntity<Void> deleteProductVariant(
            @PathVariable Long productId, @PathVariable Long variantId) {
        try {
            productService.deleteProductVariant(productId, variantId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") String productJson) {
        try {
            System.out.println("Received updateProduct request for ID: " + id);
            System.out.println("Received productJson: " + productJson);

            Product product = objectMapper.readValue(productJson, Product.class);
            System.out.println("Parsed product: " + product);

            Product updatedProduct = productService.updateProduct(id, product);
            System.out.println("Product updated successfully: " + updatedProduct);

            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException during updateProduct: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (IOException e) {
            System.err.println("IOException during updateProduct: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}