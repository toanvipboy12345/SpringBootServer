
package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.Product;
import com.ecommerce.Ecommerce.model.ProductVariant;
import com.ecommerce.Ecommerce.model.VariantSize;
import com.ecommerce.Ecommerce.repository.ProductRepository;
import com.ecommerce.Ecommerce.repository.ProductVariantRepository;
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
    private final ObjectMapper objectMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Tạo một đối tượng ProductService mới với các repository và object mapper được chỉ định.
     *
     * @param productRepository    repository cho các thực thể Product
     * @param variantRepository    repository cho các thực thể ProductVariant
     * @param objectMapper         object mapper để tuần tự hóa/giải tuần tự JSON
     */
    @Autowired
    public ProductService(ProductRepository productRepository, ProductVariantRepository variantRepository, ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Lấy tất cả các sản phẩm từ cơ sở dữ liệu.
     *
     * @return danh sách tất cả các thực thể Product
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Lấy một sản phẩm theo ID của nó.
     *
     * @param id ID của sản phẩm cần lấy
     * @return một Optional chứa Product nếu tìm thấy, ngược lại trả về rỗng
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Tạo một sản phẩm mới trong cơ sở dữ liệu với dữ liệu sản phẩm được cung cấp.
     *
     * @param product thực thể Product cần tạo
     * @return thực thể Product đã tạo
     * @throws IOException nếu có lỗi xử lý dữ liệu sản phẩm
     * @throws IllegalArgumentException nếu mã sản phẩm không hợp lệ hoặc đã tồn tại, hoặc giá bán nhỏ hơn hoặc bằng giá nhập
     */
    @Transactional
    public Product createProduct(Product product) throws IOException {
        if (product.getCode() == null || product.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống.");
        }
        if (productRepository.findByCode(product.getCode()).isPresent()) {
            throw new IllegalArgumentException("Mã sản phẩm đã tồn tại.");
        }
        if (product.getPrice() != null && product.getImportPrice() != null) {
            if (product.getPrice() <= product.getImportPrice()) {
                throw new IllegalArgumentException("Giá bán phải lớn hơn giá nhập hàng.");
            }
        }
        return productRepository.save(product);
    }

    /**
     * Lấy tất cả các biến thể của một sản phẩm theo ID của sản phẩm.
     *
     * @param productId ID của sản phẩm cần lấy biến thể
     * @return danh sách các thực thể ProductVariant liên quan đến sản phẩm
     * @throws IllegalArgumentException nếu sản phẩm không tồn tại
     */
    public List<ProductVariant> getProductVariants(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + productId));
        return product.getVariants();
    }

    /**
     * Tạo một biến thể mới cho một sản phẩm đã tồn tại, bao gồm kích thước, hình ảnh chính và hình ảnh bổ sung.
     *
     * @param productId ID của sản phẩm cần liên kết với biến thể
     * @param variant thực thể ProductVariant cần tạo
     * @param mainImage file hình ảnh chính của biến thể (tuỳ chọn)
     * @param images danh sách các file hình ảnh bổ sung của biến thể (tuỳ chọn)
     * @return thực thể ProductVariant đã tạo
     * @throws IOException nếu có lỗi xử lý các file hình ảnh
     * @throws IllegalArgumentException nếu màu sắc không hợp lệ, biến thể đã tồn tại với cùng màu cho sản phẩm, hoặc không có kích thước được cung cấp
     */
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
    if (size.getSize() == null || size.getSize().trim().isEmpty() || size.getQuantity() == null || size.getQuantity() < 0) {
        throw new IllegalArgumentException("Kích thước và số lượng không hợp lệ.");
    }
    size.setVariant(variant);
}
    productRepository.save(product);
    return variant; // Trả về variant vừa thêm
}

    /**
     * Cập nhật một sản phẩm đã tồn tại với dữ liệu được cung cấp.
     *
     * @param id ID của sản phẩm cần cập nhật
     * @param product thực thể Product chứa dữ liệu cập nhật
     * @return thực thể Product đã cập nhật
     * @throws IllegalArgumentException nếu sản phẩm không tồn tại, mã sản phẩm không hợp lệ hoặc đã tồn tại, hoặc giá bán nhỏ hơn hoặc bằng giá nhập
     */
    // @Transactional
    // public Product updateProduct(Long id, Product product) throws IOException {
    //     Product existingProduct = productRepository.findById(id)
    //             .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + id));

    //     System.out.println("Updating product with ID: " + id + ", received data: " + product);

    //     if (product.getCode() != null && !product.getCode().trim().isEmpty()) {
    //         if (!existingProduct.getCode().equals(product.getCode()) &&
    //                 productRepository.findByCode(product.getCode()).isPresent()) {
    //             throw new IllegalArgumentException("Mã sản phẩm đã tồn tại: " + product.getCode());
    //         }
    //         existingProduct.setCode(product.getCode());
    //     }
    //     if (product.getName() != null) existingProduct.setName(product.getName());
    //     if (product.getBrandId() != null) existingProduct.setBrandId(product.getBrandId());
    //     if (product.getCategoryId() != null) existingProduct.setCategoryId(product.getCategoryId());
    //     if (product.getImportPrice() != null) existingProduct.setImportPrice(product.getImportPrice());
    //     if (product.getPrice() != null) existingProduct.setPrice(product.getPrice());
    //     if (product.getDiscountPrice() != null) existingProduct.setDiscountPrice(product.getDiscountPrice());
    //     if (product.getDiscountRate() != null) existingProduct.setDiscountRate(product.getDiscountRate());
    //     if (product.getDescription() != null) existingProduct.setDescription(product.getDescription());

    //     if (existingProduct.getPrice() != null && existingProduct.getImportPrice() != null) {
    //         if (existingProduct.getPrice() <= existingProduct.getImportPrice()) {
    //             throw new IllegalArgumentException("Giá bán phải lớn hơn giá nhập hàng. Giá bán: " + existingProduct.getPrice() + ", Giá nhập: " + existingProduct.getImportPrice());
    //         }
    //     }

    //     Product savedProduct = productRepository.save(existingProduct);
    //     System.out.println("Product updated successfully: " + savedProduct);
    //     return savedProduct;
    // }
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
    if (product.getImportPrice() != null) existingProduct.setImportPrice(product.getImportPrice());
    if (product.getPrice() != null) existingProduct.setPrice(product.getPrice());
    if (product.getDiscountRate() != null) existingProduct.setDiscountRate(product.getDiscountRate());
    if (product.getDiscountPrice() != null) existingProduct.setDiscountPrice(product.getDiscountPrice());
    if (product.getDescription() != null) existingProduct.setDescription(product.getDescription());

    // Tính toán lại discountPrice nếu price hoặc discountRate thay đổi
    if (existingProduct.getPrice() != null && existingProduct.getDiscountRate() != null) {
        double newDiscountPrice = existingProduct.getPrice() * (1 - existingProduct.getDiscountRate() / 100.0);
        existingProduct.setDiscountPrice(newDiscountPrice);
    }

    // Kiểm tra giá bán và giá nhập
    if (existingProduct.getPrice() != null && existingProduct.getImportPrice() != null) {
        if (existingProduct.getPrice() <= existingProduct.getImportPrice()) {
            throw new IllegalArgumentException("Giá bán phải lớn hơn giá nhập hàng. Giá bán: " + existingProduct.getPrice() + ", Giá nhập: " + existingProduct.getImportPrice());
        }
    }

    Product savedProduct = productRepository.save(existingProduct);
    System.out.println("Product updated successfully: " + savedProduct);
    return savedProduct;
}

    /**
     * Cập nhật một biến thể đã tồn tại cho một sản phẩm, bao gồm kích thước, hình ảnh chính và hình ảnh bổ sung.
     *
     * @param productId ID của sản phẩm liên kết với biến thể
     * @param variantId ID của biến thể cần cập nhật
     * @param variant thực thể ProductVariant chứa dữ liệu cập nhật (màu sắc, kích thước, hành động ảnh)
     * @param mainImage file hình ảnh chính mới (tuỳ chọn)
     * @param images danh sách các file hình ảnh bổ sung mới để thêm (tuỳ chọn)
     * @return thực thể ProductVariant đã cập nhật
     * @throws IllegalArgumentException nếu biến thể hoặc sản phẩm không tồn tại, hoặc dữ liệu không hợp lệ
     * @throws IOException nếu có lỗi khi xử lý hình ảnh
//      */
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

    // Cập nhật màu sắc nếu có
    if (variant.getColor() != null && !variant.getColor().trim().isEmpty()) {
        String newColor = variant.getColor().trim().toLowerCase();
        if (!existingVariant.getColor().equals(newColor) && 
            variantRepository.existsByProductIdAndColorAndIdNot(productId, newColor, variantId)) {
            throw new IllegalArgumentException("Biến thể với màu sắc này đã tồn tại: " + newColor);
        }
        existingVariant.setColor(newColor);
    }

    // Cập nhật kích thước
    if (variant.getSizes() != null) {
        List<VariantSize> existingSizes = existingVariant.getSizes();
        List<VariantSize> newSizes = variant.getSizes();
    
        List<VariantSize> updatedSizes = new ArrayList<>();
        for (VariantSize newSize : newSizes) {
            if (newSize.getSize() == null || newSize.getSize().trim().isEmpty() || newSize.getQuantity() == null || newSize.getQuantity() < 0) {
                throw new IllegalArgumentException("Kích thước hoặc số lượng không hợp lệ: " + newSize);
            }
            Optional<VariantSize> existingSizeOpt = existingSizes.stream()
                    .filter(s -> s.getSize().equals(newSize.getSize()))
                    .findFirst();
            if (existingSizeOpt.isPresent()) {
                VariantSize existingSize = existingSizeOpt.get();
                existingSize.setQuantity(newSize.getQuantity());
                updatedSizes.add(existingSize);
            } else {
                newSize.setVariant(existingVariant);
                updatedSizes.add(newSize);
            }
        }
        existingSizes.clear();
        existingSizes.addAll(updatedSizes);
    }

    // Cập nhật ảnh chính
    if (mainImage != null && !mainImage.isEmpty()) {
        if (existingVariant.getMainImage() != null) {
            deleteImage(existingVariant.getMainImage());
        }
        String newMainImagePath = saveImage(generateVariantIdentifier(product.getCode(), existingVariant.getColor()), mainImage, "main");
        existingVariant.setMainImage(newMainImagePath);
    }

    // Cập nhật ảnh phụ
    List<String> currentImages = existingVariant.getImages() != null ? new ArrayList<>(existingVariant.getImages()) : new ArrayList<>();
    if (images != null && !images.isEmpty()) {
        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                String imagePath = saveImage(generateVariantIdentifier(product.getCode(), existingVariant.getColor()), image, "sub");
                currentImages.add(imagePath);
            }
        }
    }

    // Xử lý imageActions trực tiếp từ tham số
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
    /**
     * Xóa một biến thể sản phẩm khỏi cơ sở dữ liệu.
     *
     * @param productId ID của sản phẩm liên kết với biến thể
     * @param variantId ID của biến thể cần xóa
     * @throws IllegalArgumentException nếu biến thể hoặc sản phẩm không tồn tại
     */
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

        product.getVariants().remove(variant);
        variantRepository.delete(variant);
        productRepository.save(product);
    }

    /**
     * Xóa một sản phẩm và tất cả các biến thể của nó khỏi cơ sở dữ liệu.
     *
     * @param id ID của sản phẩm cần xóa
     * @throws IllegalArgumentException nếu sản phẩm không tồn tại
     */
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + id));
        for (ProductVariant variant : product.getVariants()) {
            if (variant.getMainImage() != null) {
                deleteImage(variant.getMainImage());
            }
            if (variant.getImages() != null) {
                for (String image : variant.getImages()) {
                    deleteImage(image);
                }
            }
        }
        productRepository.delete(product);
    }

    /**
     * Lưu một file hình ảnh cho biến thể và trả về đường dẫn của nó.
     *
     * @param identifier mã định danh duy nhất cho biến thể
     * @param image file hình ảnh cần lưu
     * @param type loại hình ảnh (ví dụ: "main" hoặc "sub")
     * @return đường dẫn đến hình ảnh đã lưu
     * @throws IOException nếu có lỗi khi lưu hình ảnh
     */
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

    /**
     * Xóa một file hình ảnh khỏi bộ nhớ lưu trữ.
     *
     * @param imagePath đường dẫn của hình ảnh cần xóa
     * @throws IllegalArgumentException nếu có lỗi khi xóa hình ảnh
     */
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

    /**
     * Tạo một mã định danh duy nhất cho biến thể dựa trên mã sản phẩm và màu sắc.
     *
     * @param productCode mã của sản phẩm
     * @param color màu của biến thể
     * @return chuỗi mã định danh duy nhất
     */
    private String generateVariantIdentifier(String productCode, String color) {
        return productCode.trim() + "_" + color.trim().toUpperCase();
    }
    /**
 * Lấy danh sách sản phẩm với các bộ lọc tuỳ chọn.
 *
 * @param search     Từ khóa tìm kiếm (tuỳ chọn, áp dụng cho mã hoặc tên sản phẩm)
 * @param categoryId ID danh mục để lọc (tuỳ chọn)
 * @param brandId    ID thương hiệu để lọc (tuỳ chọn)
 * @return danh sách các thực thể Product phù hợp với bộ lọc
 */
public List<Product> getProductsWithFilters(String search, Long categoryId, Long brandId) {
    // Gọi phương thức từ repository với các điều kiện lọc
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