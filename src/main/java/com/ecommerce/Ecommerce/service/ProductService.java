// package com.ecommerce.Ecommerce.service;

// import com.ecommerce.Ecommerce.model.Product;
// import com.ecommerce.Ecommerce.model.ProductVariant;
// import com.ecommerce.Ecommerce.model.VariantSize;
// import com.ecommerce.Ecommerce.repository.ProductRepository;
// import com.ecommerce.Ecommerce.repository.ProductVariantRepository;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Isolation;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;

// /**
//  * Lớp service để quản lý các thao tác liên quan đến sản phẩm, bao gồm các hoạt động CRUD cho sản phẩm và biến thể của chúng.
//  */
// @Service
// public class ProductService {

//     private final ProductRepository productRepository;
//     private final ProductVariantRepository variantRepository;
    
//     private final ObjectMapper objectMapper;

//     @Value("${file.upload-dir}")
//     private String uploadDir;

//     @Autowired
//     public ProductService(ProductRepository productRepository, ProductVariantRepository variantRepository, ObjectMapper objectMapper) {
//         this.productRepository = productRepository;
//         this.variantRepository = variantRepository;
//         this.objectMapper = objectMapper;
//     }

//     public List<Product> getAllProducts() {
//         return productRepository.findAll();
//     }

//     public Optional<Product> getProductById(Long id) {
//         return productRepository.findById(id);
//     }

//     @Transactional
//     public Product createProduct(Product product) throws IOException {
//         if (product.getCode() == null || product.getCode().trim().isEmpty()) {
//             throw new IllegalArgumentException("Mã sản phẩm không được để trống.");
//         }
//         if (productRepository.findByCode(product.getCode()).isPresent()) {
//             throw new IllegalArgumentException("Mã sản phẩm đã tồn tại.");
//         }
//         // Đặt quantity = 0 cho tất cả VariantSize
//         if (product.getVariants() != null) {
//             for (ProductVariant variant : product.getVariants()) {
//                 variant.setProduct(product);
//                 if (variant.getSizes() != null) {
//                     for (VariantSize size : variant.getSizes()) {
//                         if (size.getSize() == null || size.getSize().trim().isEmpty()) {
//                             throw new IllegalArgumentException("Kích thước không hợp lệ.");
//                         }
//                         size.setQuantity(0); // Quantity mặc định là 0
//                         size.setVariant(variant);
//                     }
//                 }
//             }
//         }
//         return productRepository.save(product);
//     }

//     public List<ProductVariant> getProductVariants(Long productId) {
//         Product product = productRepository.findById(productId)
//                 .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + productId));
//         return product.getVariants();
//     }

//     @Transactional(isolation = Isolation.SERIALIZABLE)
//     public ProductVariant createProductVariant(Long productId, ProductVariant variant,
//             MultipartFile mainImage, List<MultipartFile> images) throws IOException {
//         Product product = productRepository.findById(productId)
//                 .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + productId));
//         if (variant.getColor() == null || variant.getColor().trim().isEmpty()) {
//             throw new IllegalArgumentException("Màu sắc không được để trống.");
//         }

//         String normalizedColor = variant.getColor().trim().toLowerCase();
//         if (variantRepository.existsByProductIdAndColor(productId, normalizedColor)) {
//             throw new IllegalArgumentException("Biến thể với màu sắc này đã tồn tại cho sản phẩm.");
//         }

//         variant.setProduct(product);
//         product.getVariants().add(variant);

//         if (mainImage != null && !mainImage.isEmpty()) {
//             String mainImagePath = saveImage(generateVariantIdentifier(product.getCode(), variant.getColor()), mainImage, "main");
//             variant.setMainImage(mainImagePath);
//         }
//         if (images != null && !images.isEmpty()) {
//             List<String> imagePaths = new ArrayList<>();
//             for (MultipartFile image : images) {
//                 if (image != null && !image.isEmpty()) {
//                     String imagePath = saveImage(generateVariantIdentifier(product.getCode(), variant.getColor()), image, "sub");
//                     imagePaths.add(imagePath);
//                 }
//             }
//             variant.setImages(imagePaths);
//         }

//         if (variant.getSizes() == null || variant.getSizes().isEmpty()) {
//             throw new IllegalArgumentException("Vui lòng chọn size.");
//         }

//         for (VariantSize size : variant.getSizes()) {
//             if (size.getSize() == null || size.getSize().trim().isEmpty()) {
//                 throw new IllegalArgumentException("Kích thước không hợp lệ.");
//             }
//             size.setQuantity(0); // Quantity mặc định là 0
//             size.setVariant(variant);
//         }
//         productRepository.save(product);
//         return variant;
//     }

//     @Transactional
//     public Product updateProduct(Long id, Product product) throws IOException {
//         Product existingProduct = productRepository.findById(id)
//                 .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + id));

//         System.out.println("Updating product with ID: " + id + ", received data: " + product);

//         if (product.getCode() != null && !product.getCode().trim().isEmpty()) {
//             if (!existingProduct.getCode().equals(product.getCode()) &&
//                     productRepository.findByCode(product.getCode()).isPresent()) {
//                 throw new IllegalArgumentException("Mã sản phẩm đã tồn tại: " + product.getCode());
//             }
//             existingProduct.setCode(product.getCode());
//         }
//         if (product.getName() != null) existingProduct.setName(product.getName());
//         if (product.getBrandId() != null) existingProduct.setBrandId(product.getBrandId());
//         if (product.getCategoryId() != null) existingProduct.setCategoryId(product.getCategoryId());
//         if (product.getPrice() != null) existingProduct.setPrice(product.getPrice());
//         if (product.getDiscountRate() != null) existingProduct.setDiscountRate(product.getDiscountRate());
//         if (product.getDiscountPrice() != null) existingProduct.setDiscountPrice(product.getDiscountPrice());
//         if (product.getDescription() != null) existingProduct.setDescription(product.getDescription());

//         // Tính toán lại discountPrice nếu price hoặc discountRate thay đổi
//         if (existingProduct.getPrice() != null && existingProduct.getDiscountRate() != null) {
//             double newDiscountPrice = existingProduct.getPrice() * (1 - existingProduct.getDiscountRate() / 100.0);
//             existingProduct.setDiscountPrice(newDiscountPrice);
//         }

//         // Không cho phép cập nhật quantity trực tiếp qua đây
//         if (product.getVariants() != null && !product.getVariants().isEmpty()) {
//             throw new IllegalArgumentException("Không thể cập nhật biến thể hoặc số lượng qua API này. Sử dụng API cập nhật biến thể hoặc PurchaseOrder.");
//         }

//         Product savedProduct = productRepository.save(existingProduct);
//         System.out.println("Product updated successfully: " + savedProduct);
//         return savedProduct;
//     }

//     @Transactional
//     public ProductVariant updateProductVariant(Long productId, Long variantId, ProductVariant variant,
//             MultipartFile mainImage, List<MultipartFile> images, List<Map<String, String>> imageActions) throws IOException {
//         Product product = productRepository.findById(productId)
//                 .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + productId));
//         ProductVariant existingVariant = variantRepository.findById(variantId)
//                 .orElseThrow(() -> new IllegalArgumentException("Biến thể không tồn tại với ID: " + variantId));
//         if (!existingVariant.getProduct().getId().equals(productId)) {
//             throw new IllegalArgumentException("Biến thể không thuộc sản phẩm này.");
//         }

//         System.out.println("Updating variant with ID: " + variantId + ", received data: " + variant);
//         System.out.println("Received imageActions: " + imageActions);

//         if (variant.getColor() != null && !variant.getColor().trim().isEmpty()) {
//             String newColor = variant.getColor().trim().toLowerCase();
//             if (!existingVariant.getColor().equals(newColor) && 
//                     variantRepository.existsByProductIdAndColorAndIdNot(productId, newColor, variantId)) {
//                 throw new IllegalArgumentException("Biến thể với màu sắc này đã tồn tại: " + newColor);
//             }
//             existingVariant.setColor(newColor);
//         }

//         if (variant.getSizes() != null && !variant.getSizes().isEmpty()) {
//             List<VariantSize> newSizes = variant.getSizes();
//             for (VariantSize newSize : newSizes) {
//                 if (newSize.getQuantity() != null) {
//                     throw new IllegalArgumentException("Không thể cập nhật số lượng trực tiếp. Sử dụng PurchaseOrder để quản lý số lượng.");
//                 }
//                 if (newSize.getSize() != null && !newSize.getSize().trim().isEmpty()) {
//                     boolean sizeExists = existingVariant.getSizes().stream()
//                             .anyMatch(s -> s.getSize().equals(newSize.getSize()));
//                     if (!sizeExists) {
//                         newSize.setQuantity(0); // Quantity mặc định là 0
//                         newSize.setVariant(existingVariant);
//                         existingVariant.getSizes().add(newSize);
//                     }
//                 }
//             }
//         }

//         if (mainImage != null && !mainImage.isEmpty()) {
//             if (existingVariant.getMainImage() != null) {
//                 deleteImage(existingVariant.getMainImage());
//             }
//             String newMainImagePath = saveImage(generateVariantIdentifier(product.getCode(), existingVariant.getColor()), mainImage, "main");
//             existingVariant.setMainImage(newMainImagePath);
//         }

//         List<String> currentImages = existingVariant.getImages() != null ? new ArrayList<>(existingVariant.getImages()) : new ArrayList<>();
//         if (images != null && !images.isEmpty()) {
//             for (MultipartFile image : images) {
//                 if (image != null && !image.isEmpty()) {
//                     String imagePath = saveImage(generateVariantIdentifier(product.getCode(), existingVariant.getColor()), image, "sub");
//                     currentImages.add(imagePath);
//                 }
//             }
//         }

//         if (imageActions != null && !imageActions.isEmpty()) {
//             for (Map<String, String> action : imageActions) {
//                 String actionType = action.get("action");
//                 String imagePath = action.get("imagePath");
//                 if ("remove".equalsIgnoreCase(actionType) && currentImages.contains(imagePath)) {
//                     deleteImage(imagePath);
//                     currentImages.remove(imagePath);
//                     System.out.println("Removed sub-image from server: " + imagePath);
//                 }
//             }
//         }
//         existingVariant.setImages(currentImages);

//         ProductVariant savedVariant = variantRepository.save(existingVariant);
//         System.out.println("Updated images list: " + currentImages);
//         System.out.println("Variant updated successfully: " + savedVariant);
//         return savedVariant;
//     }

//     public void deleteProductVariant(Long productId, Long variantId) {
//         Product product = productRepository.findById(productId)
//                 .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + productId));
//         ProductVariant variant = variantRepository.findById(variantId)
//                 .orElseThrow(() -> new IllegalArgumentException("Biến thể không tồn tại với ID: " + variantId));
//         if (!variant.getProduct().getId().equals(productId)) {
//             throw new IllegalArgumentException("Biến thể không thuộc sản phẩm này.");
//         }

//         if (variant.getMainImage() != null) {
//             deleteImage(variant.getMainImage());
//         }
//         if (variant.getImages() != null) {
//             for (String image : variant.getImages()) {
//                 deleteImage(image);
//             }
//         }

//         product.getVariants().remove(variant);
//         variantRepository.delete(variant);
//         productRepository.save(product);
//     }

//     public void deleteProduct(Long id) {
//         Product product = productRepository.findById(id)
//                 .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + id));
//         for (ProductVariant variant : product.getVariants()) {
//             if (variant.getMainImage() != null) {
//                 deleteImage(variant.getMainImage());
//             }
//             if (variant.getImages() != null) {
//                 for (String image : variant.getImages()) {
//                     deleteImage(image);
//                 }
//             }
//         }
//         productRepository.delete(product);
//     }

//     private String saveImage(String identifier, MultipartFile image, String type) throws IOException {
//         if (image == null || image.isEmpty()) {
//             return null;
//         }

//         Path uploadPath = Paths.get(uploadDir);
//         if (!Files.exists(uploadPath)) {
//             Files.createDirectories(uploadPath);
//         }

//         String originalFileName = image.getOriginalFilename();
//         String fileExtension = originalFileName != null && originalFileName.contains(".")
//                 ? originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase()
//                 : ".jpg";

//         String fileName = identifier.trim().replaceAll("[^a-zA-Z0-9]", "_") + "_" + type + fileExtension;
//         Path filePath = uploadPath.resolve(fileName);

//         int counter = 1;
//         while (Files.exists(filePath)) {
//             String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
//             fileName = fileNameWithoutExt + "_" + counter + fileExtension;
//             filePath = uploadPath.resolve(fileName);
//             counter++;
//         }

//         Files.copy(image.getInputStream(), filePath);
//         return "/uploads/" + fileName;
//     }

//     private void deleteImage(String imagePath) {
//         if (imagePath != null) {
//             try {
//                 String relativePath = imagePath.startsWith("/uploads/") ? imagePath.substring(9) : imagePath;
//                 Path filePath = Paths.get(uploadDir, relativePath);
//                 Files.deleteIfExists(filePath);
//             } catch (IOException e) {
//                 throw new IllegalArgumentException("Không thể xóa hình ảnh: " + e.getMessage());
//             }
//         }
//     }

//     private String generateVariantIdentifier(String productCode, String color) {
//         return productCode.trim() + "_" + color.trim().toUpperCase();
//     }

//     public List<Product> getProductsWithFilters(String search, Long categoryId, Long brandId) {
//         if (search != null && !search.trim().isEmpty()) {
//             search = search.trim();
//             if (categoryId != null && brandId != null) {
//                 return productRepository.findByCodeContainingOrNameContainingAndCategoryIdAndBrandId(search, search, categoryId, brandId);
//             } else if (categoryId != null) {
//                 return productRepository.findByCodeContainingOrNameContainingAndCategoryId(search, search, categoryId);
//             } else if (brandId != null) {
//                 return productRepository.findByCodeContainingOrNameContainingAndBrandId(search, search, brandId);
//             } else {
//                 return productRepository.findByCodeContainingOrNameContaining(search, search);
//             }
//         } else {
//             if (categoryId != null && brandId != null) {
//                 return productRepository.findByCategoryIdAndBrandId(categoryId, brandId);
//             } else if (categoryId != null) {
//                 return productRepository.findByCategoryId(categoryId);
//             } else if (brandId != null) {
//                 return productRepository.findByBrandId(brandId);
//             } else {
//                 return productRepository.findAll();
//             }
//         }
//     }
// }
package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.Product;
import com.ecommerce.Ecommerce.model.ProductVariant;
import com.ecommerce.Ecommerce.model.VariantSize;
import com.ecommerce.Ecommerce.repository.ProductRepository;
import com.ecommerce.Ecommerce.repository.ProductVariantRepository;
import com.ecommerce.Ecommerce.repository.PurchaseOrderRepository;
import com.ecommerce.Ecommerce.repository.VariantSizeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Lớp service để quản lý các thao tác liên quan đến sản phẩm, bao gồm các hoạt động CRUD cho sản phẩm và biến thể của chúng.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final VariantSizeRepository variantSizeRepository;
    private final ObjectMapper objectMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          ProductVariantRepository variantRepository,
                          PurchaseOrderRepository purchaseOrderRepository,
                          VariantSizeRepository variantSizeRepository,
                          ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.variantSizeRepository = variantSizeRepository;
        this.objectMapper = objectMapper;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product createProduct(Product product) throws IOException {
        if (product.getCode() == null || product.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống.");
        }
        if (productRepository.findByCode(product.getCode()).isPresent()) {
            throw new IllegalArgumentException("Mã sản phẩm đã tồn tại.");
        }
        if (product.getVariants() != null) {
            for (ProductVariant variant : product.getVariants()) {
                variant.setProduct(product);
                if (variant.getSizes() != null) {
                    for (VariantSize size : variant.getSizes()) {
                        if (size.getSize() == null || size.getSize().trim().isEmpty()) {
                            throw new IllegalArgumentException("Kích thước không hợp lệ.");
                        }
                        size.setQuantity(0); // Quantity mặc định là 0
                        size.setVariant(variant);
                    }
                }
            }
        }
        return productRepository.save(product);
    }

    public List<ProductVariant> getProductVariants(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + productId));
        return product.getVariants();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ProductVariant createProductVariant(Long productId, ProductVariant variant,
            MultipartFile mainImage, List<MultipartFile> images) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + productId));
        if (variant.getColor() == null || variant.getColor().trim().isEmpty()) {
            throw new IllegalArgumentException("Màu sắc không được để trống.");
        }

        String normalizedColor = variant.getColor().trim().toLowerCase();
        if (variantRepository.existsByProductIdAndColor(productId, normalizedColor)) {
            throw new IllegalArgumentException("Biến thể với màu sắc này đã tồn tại cho sản phẩm.");
        }

        variant.setProduct(product);
        product.getVariants().add(variant);

        if (mainImage != null && !mainImage.isEmpty()) {
            String mainImagePath = saveImage(generateVariantIdentifier(product.getCode(), variant.getColor()), mainImage, "main");
            variant.setMainImage(mainImagePath);
        }
        if (images != null && !images.isEmpty()) {
            List<String> imagePaths = new ArrayList<>();
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    String imagePath = saveImage(generateVariantIdentifier(product.getCode(), variant.getColor()), image, "sub");
                    imagePaths.add(imagePath);
                }
            }
            variant.setImages(imagePaths);
        }

        if (variant.getSizes() == null || variant.getSizes().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn size.");
        }

        for (VariantSize size : variant.getSizes()) {
            if (size.getSize() == null || size.getSize().trim().isEmpty()) {
                throw new IllegalArgumentException("Kích thước không hợp lệ.");
            }
            size.setQuantity(0); // Quantity mặc định là 0
            size.setVariant(variant);
        }
        productRepository.save(product);
        return variant;
    }

    @Transactional
    public Product updateProduct(Long id, Product product) throws IOException {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + id));

        System.out.println("Updating product with ID: " + id + ", received data: " + product);

        if (product.getCode() != null && !product.getCode().trim().isEmpty()) {
            if (!existingProduct.getCode().equals(product.getCode()) &&
                    productRepository.findByCode(product.getCode()).isPresent()) {
                throw new IllegalArgumentException("Mã sản phẩm đã tồn tại: " + product.getCode());
            }
            existingProduct.setCode(product.getCode());
        }
        if (product.getName() != null) existingProduct.setName(product.getName());
        if (product.getBrandId() != null) existingProduct.setBrandId(product.getBrandId());
        if (product.getCategoryId() != null) existingProduct.setCategoryId(product.getCategoryId());
        if (product.getPrice() != null) existingProduct.setPrice(product.getPrice());
        if (product.getDiscountRate() != null) existingProduct.setDiscountRate(product.getDiscountRate());
        if (product.getDiscountPrice() != null) existingProduct.setDiscountPrice(product.getDiscountPrice());
        if (product.getDescription() != null) existingProduct.setDescription(product.getDescription());

        if (existingProduct.getPrice() != null && existingProduct.getDiscountRate() != null) {
            double newDiscountPrice = existingProduct.getPrice() * (1 - existingProduct.getDiscountRate() / 100.0);
            existingProduct.setDiscountPrice(newDiscountPrice);
        }

        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            throw new IllegalArgumentException("Không thể cập nhật biến thể hoặc số lượng qua API này. Sử dụng API cập nhật biến thể hoặc PurchaseOrder.");
        }

        Product savedProduct = productRepository.save(existingProduct);
        System.out.println("Product updated successfully: " + savedProduct);
        return savedProduct;
    }

    @Transactional
    public ProductVariant updateProductVariant(Long productId, Long variantId, ProductVariant variant,
            MultipartFile mainImage, List<MultipartFile> images, List<Map<String, String>> imageActions) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + productId));
        ProductVariant existingVariant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Biến thể không tồn tại với ID: " + variantId));
        if (!existingVariant.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Biến thể không thuộc sản phẩm này.");
        }

        System.out.println("Updating variant with ID: " + variantId + ", received data: " + variant);
        System.out.println("Received imageActions: " + imageActions);

        if (variant.getColor() != null && !variant.getColor().trim().isEmpty()) {
            String newColor = variant.getColor().trim().toLowerCase();
            if (!existingVariant.getColor().equals(newColor) && 
                    variantRepository.existsByProductIdAndColorAndIdNot(productId, newColor, variantId)) {
                throw new IllegalArgumentException("Biến thể với màu sắc này đã tồn tại: " + newColor);
            }
            existingVariant.setColor(newColor);
        }

        if (variant.getSizes() != null && !variant.getSizes().isEmpty()) {
            List<VariantSize> newSizes = variant.getSizes();
            for (VariantSize newSize : newSizes) {
                if (newSize.getQuantity() != null) {
                    throw new IllegalArgumentException("Không thể cập nhật số lượng trực tiếp. Sử dụng PurchaseOrder để quản lý số lượng.");
                }
                if (newSize.getSize() != null && !newSize.getSize().trim().isEmpty()) {
                    boolean sizeExists = existingVariant.getSizes().stream()
                            .anyMatch(s -> s.getSize().equals(newSize.getSize()));
                    if (!sizeExists) {
                        newSize.setQuantity(0); // Quantity mặc định là 0
                        newSize.setVariant(existingVariant);
                        existingVariant.getSizes().add(newSize);
                    }
                }
            }
        }

        if (mainImage != null && !mainImage.isEmpty()) {
            if (existingVariant.getMainImage() != null) {
                deleteImage(existingVariant.getMainImage());
            }
            String newMainImagePath = saveImage(generateVariantIdentifier(product.getCode(), existingVariant.getColor()), mainImage, "main");
            existingVariant.setMainImage(newMainImagePath);
        }

        List<String> currentImages = existingVariant.getImages() != null ? new ArrayList<>(existingVariant.getImages()) : new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    String imagePath = saveImage(generateVariantIdentifier(product.getCode(), existingVariant.getColor()), image, "sub");
                    currentImages.add(imagePath);
                }
            }
        }

        if (imageActions != null && !imageActions.isEmpty()) {
            for (Map<String, String> action : imageActions) {
                String actionType = action.get("action");
                String imagePath = action.get("imagePath");
                if ("remove".equalsIgnoreCase(actionType) && currentImages.contains(imagePath)) {
                    deleteImage(imagePath);
                    currentImages.remove(imagePath);
                    System.out.println("Removed sub-image from server: " + imagePath);
                }
            }
        }
        existingVariant.setImages(currentImages);

        ProductVariant savedVariant = variantRepository.save(existingVariant);
        System.out.println("Updated images list: " + currentImages);
        System.out.println("Variant updated successfully: " + savedVariant);
        return savedVariant;
    }

    @Transactional
    public void deleteProductVariant(Long productId, Long variantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + productId));
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Biến thể không tồn tại với ID: " + variantId));
        if (!variant.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Biến thể không thuộc sản phẩm này.");
        }

        if (variant.getMainImage() != null) {
            deleteImage(variant.getMainImage());
        }
        if (variant.getImages() != null) {
            for (String image : variant.getImages()) {
                deleteImage(image);
            }
        }

        // Xóa các VariantSize liên quan
        variantSizeRepository.deleteAll(variant.getSizes());

        product.getVariants().remove(variant);
        variantRepository.delete(variant);
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + id));

        // Xóa tất cả PurchaseOrder liên quan
        purchaseOrderRepository.deleteByProductId(id);

        // Xóa hình ảnh của các variant
        for (ProductVariant variant : product.getVariants()) {
            if (variant.getMainImage() != null) {
                deleteImage(variant.getMainImage());
            }
            if (variant.getImages() != null) {
                for (String image : variant.getImages()) {
                    deleteImage(image);
                }
            }
            // Xóa các VariantSize liên quan
            variantSizeRepository.deleteAll(variant.getSizes());
        }

        // Xóa Product và các Variant
        productRepository.delete(product);
    }

    private String saveImage(String identifier, MultipartFile image, String type) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = image.getOriginalFilename();
        String fileExtension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase()
                : ".jpg";

        String fileName = identifier.trim().replaceAll("[^a-zA-Z0-9]", "_") + "_" + type + fileExtension;
        Path filePath = uploadPath.resolve(fileName);

        int counter = 1;
        while (Files.exists(filePath)) {
            String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
            fileName = fileNameWithoutExt + "_" + counter + fileExtension;
            filePath = uploadPath.resolve(fileName);
            counter++;
        }

        Files.copy(image.getInputStream(), filePath);
        return "/uploads/" + fileName;
    }

    private void deleteImage(String imagePath) {
        if (imagePath != null) {
            try {
                String relativePath = imagePath.startsWith("/uploads/") ? imagePath.substring(9) : imagePath;
                Path filePath = Paths.get(uploadDir, relativePath);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new IllegalArgumentException("Không thể xóa hình ảnh: " + e.getMessage());
            }
        }
    }

    private String generateVariantIdentifier(String productCode, String color) {
        return productCode.trim() + "_" + color.trim().toUpperCase();
    }

    public List<Product> getProductsWithFilters(String search, Long categoryId, Long brandId) {
        if (search != null && !search.trim().isEmpty()) {
            search = search.trim();
            if (categoryId != null && brandId != null) {
                return productRepository.findByCodeContainingOrNameContainingAndCategoryIdAndBrandId(search, search, categoryId, brandId);
            } else if (categoryId != null) {
                return productRepository.findByCodeContainingOrNameContainingAndCategoryId(search, search, categoryId);
            } else if (brandId != null) {
                return productRepository.findByCodeContainingOrNameContainingAndBrandId(search, search, brandId);
            } else {
                return productRepository.findByCodeContainingOrNameContaining(search, search);
            }
        } else {
            if (categoryId != null && brandId != null) {
                return productRepository.findByCategoryIdAndBrandId(categoryId, brandId);
            } else if (categoryId != null) {
                return productRepository.findByCategoryId(categoryId);
            } else if (brandId != null) {
                return productRepository.findByBrandId(brandId);
            } else {
                return productRepository.findAll();
            }
        }
    }
}