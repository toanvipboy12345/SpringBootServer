
// package com.ecommerce.Ecommerce.service;

// import com.ecommerce.Ecommerce.model.PurchaseOrder;
// import com.ecommerce.Ecommerce.model.PurchaseOrderItem;
// import com.ecommerce.Ecommerce.model.Product;
// import com.ecommerce.Ecommerce.model.Supplier;
// import com.ecommerce.Ecommerce.model.VariantSize;
// import com.ecommerce.Ecommerce.repository.PurchaseOrderRepository;
// import com.ecommerce.Ecommerce.repository.SupplierRepository;
// import com.ecommerce.Ecommerce.repository.ProductRepository;
// import com.ecommerce.Ecommerce.repository.VariantSizeRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;

// @Service
// public class PurchaseOrderService {

//     private final PurchaseOrderRepository purchaseOrderRepository;
//     private final SupplierRepository supplierRepository;
//     private final ProductRepository productRepository;
//     private final VariantSizeRepository variantSizeRepository;

//     @Autowired
//     public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
//                                 SupplierRepository supplierRepository,
//                                 ProductRepository productRepository,
//                                 VariantSizeRepository variantSizeRepository) {
//         this.purchaseOrderRepository = purchaseOrderRepository;
//         this.supplierRepository = supplierRepository;
//         this.productRepository = productRepository;
//         this.variantSizeRepository = variantSizeRepository;
//     }

//     public List<PurchaseOrder> getAllPurchaseOrders() {
//         return purchaseOrderRepository.findAll();
//     }

//     public Optional<PurchaseOrder> getPurchaseOrderById(Long id) {
//         return purchaseOrderRepository.findById(id);
//     }

//     @Transactional
//     public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
//         // Kiểm tra dữ liệu đầu vào
//         validatePurchaseOrderInput(purchaseOrder);

//         // Kiểm tra Supplier và Product tồn tại
//         Supplier supplier = supplierRepository.findById(purchaseOrder.getSupplier().getId())
//                 .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp không tồn tại với ID: " + purchaseOrder.getSupplier().getId()));
//         Product product = productRepository.findById(purchaseOrder.getProduct().getId())
//                 .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + purchaseOrder.getProduct().getId()));

//         // Kiểm tra importPrice
//         if (purchaseOrder.getImportPrice() == null || purchaseOrder.getImportPrice() <= 0) {
//             throw new IllegalArgumentException("Giá nhập hàng phải lớn hơn 0.");
//         }

//         // Lọc các items có quantity hợp lệ (> 0)
//         List<PurchaseOrderItem> validItems = purchaseOrder.getItems().stream()
//                 .filter(item -> item.getQuantity() != null && item.getQuantity() > 0)
//                 .collect(Collectors.toList());

//         if (validItems.isEmpty()) {
//             throw new IllegalArgumentException("Phải có ít nhất một mục nhập hàng với số lượng lớn hơn 0.");
//         }

//         // Tính totalAmount chỉ dựa trên các items hợp lệ
//         double totalAmount = calculateTotalAmount(purchaseOrder.getImportPrice(), validItems);

//         // Gán giá trị và lưu
//         purchaseOrder.setSupplier(supplier);
//         purchaseOrder.setProduct(product);
//         purchaseOrder.setItems(validItems); // Chỉ lưu các items hợp lệ
//         purchaseOrder.setTotalAmount(totalAmount);
//         purchaseOrder.setStatus("PENDING"); // Trạng thái mặc định

//         return purchaseOrderRepository.save(purchaseOrder);
//     }

//     @Transactional
//     public PurchaseOrder confirmPurchaseOrder(Long id) {
//         PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
//                 .orElseThrow(() -> new IllegalArgumentException("Phiếu nhập hàng không tồn tại với ID: " + id));

//         if (!"PENDING".equalsIgnoreCase(purchaseOrder.getStatus())) {
//             throw new IllegalArgumentException("Chỉ có thể xác nhận phiếu ở trạng thái PENDING. Trạng thái hiện tại: " + purchaseOrder.getStatus());
//         }

//         // Cập nhật quantity trong VariantSize
//         for (PurchaseOrderItem item : purchaseOrder.getItems()) {
//             VariantSize variantSize = variantSizeRepository.findById(item.getVariantSizeId())
//                     .orElseThrow(() -> new IllegalArgumentException("Kích thước biến thể không tồn tại với ID: " + item.getVariantSizeId()));
//             variantSize.setQuantity(variantSize.getQuantity() + item.getQuantity());
//             variantSizeRepository.save(variantSize);
//         }

//         purchaseOrder.setStatus("COMPLETED");
//         return purchaseOrderRepository.save(purchaseOrder);
//     }

//     @Transactional
//     public PurchaseOrder cancelPurchaseOrder(Long id) {
//         PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
//                 .orElseThrow(() -> new IllegalArgumentException("Phiếu nhập hàng không tồn tại với ID: " + id));

//         if (!"PENDING".equalsIgnoreCase(purchaseOrder.getStatus())) {
//             throw new IllegalArgumentException("Chỉ có thể hủy phiếu ở trạng thái PENDING. Trạng thái hiện tại: " + purchaseOrder.getStatus());
//         }

//         purchaseOrder.setStatus("CANCELLED");
//         return purchaseOrderRepository.save(purchaseOrder);
//     }

//     private void validatePurchaseOrderInput(PurchaseOrder purchaseOrder) {
//         if (purchaseOrder.getPurchaseOrderCode() == null || purchaseOrder.getPurchaseOrderCode().trim().isEmpty()) {
//             throw new IllegalArgumentException("Mã phiếu nhập hàng không được để trống.");
//         }
//         if (purchaseOrderRepository.existsByPurchaseOrderCode(purchaseOrder.getPurchaseOrderCode())) {
//             throw new IllegalArgumentException("Mã phiếu nhập hàng đã tồn tại: " + purchaseOrder.getPurchaseOrderCode());
//         }
//         if (purchaseOrder.getSupplier() == null || purchaseOrder.getSupplier().getId() == null) {
//             throw new IllegalArgumentException("Nhà cung cấp không hợp lệ.");
//         }
//         if (purchaseOrder.getProduct() == null || purchaseOrder.getProduct().getId() == null) {
//             throw new IllegalArgumentException("Sản phẩm không hợp lệ.");
//         }
//         if (purchaseOrder.getItems() == null || purchaseOrder.getItems().isEmpty()) {
//             throw new IllegalArgumentException("Danh sách mục nhập hàng không được để trống.");
//         }
//     }

//     private double calculateTotalAmount(Double importPrice, List<PurchaseOrderItem> items) {
//         double totalAmount = 0;
//         for (PurchaseOrderItem item : items) {
//             // Đã lọc trước đó, nhưng kiểm tra lại để đảm bảo
//             if (item.getQuantity() == null || item.getQuantity() <= 0) {
//                 continue; // Bỏ qua các item không hợp lệ
//             }
//             VariantSize variantSize = variantSizeRepository.findById(item.getVariantSizeId())
//                     .orElseThrow(() -> new IllegalArgumentException("Kích thước biến thể không tồn tại với ID: " + item.getVariantSizeId()));
//             totalAmount += importPrice * item.getQuantity();
//         }
//         return totalAmount;
//     }
// }
package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.PurchaseOrder;
import com.ecommerce.Ecommerce.model.PurchaseOrderItem;
import com.ecommerce.Ecommerce.model.Product;
import com.ecommerce.Ecommerce.model.ProductVariant;
import com.ecommerce.Ecommerce.model.Supplier;
import com.ecommerce.Ecommerce.model.VariantSize;
import com.ecommerce.Ecommerce.model.dto.PurchaseOrderDTO;
import com.ecommerce.Ecommerce.repository.PurchaseOrderRepository;
import com.ecommerce.Ecommerce.repository.SupplierRepository;
import com.ecommerce.Ecommerce.repository.ProductRepository;
import com.ecommerce.Ecommerce.repository.VariantSizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final VariantSizeRepository variantSizeRepository;

    @Autowired
    public PurchaseOrderService(
            PurchaseOrderRepository purchaseOrderRepository,
            SupplierRepository supplierRepository,
            ProductRepository productRepository,
            VariantSizeRepository variantSizeRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.variantSizeRepository = variantSizeRepository;
    }

    public List<PurchaseOrderDTO> getAllPurchaseOrders(String search) {
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();
        return purchaseOrders.stream()
                .filter(order -> {
                    if (search == null || search.trim().isEmpty()) return true;
                    String searchLower = search.toLowerCase();
                    return (order.getPurchaseOrderCode() != null && order.getPurchaseOrderCode().toLowerCase().contains(searchLower))
                            || (order.getProduct() != null && order.getProduct().getName() != null && order.getProduct().getName().toLowerCase().contains(searchLower));
                })
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PurchaseOrderDTO> getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .map(this::mapToDTO);
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        validatePurchaseOrderInput(purchaseOrder);

        Supplier supplier = supplierRepository.findById(purchaseOrder.getSupplier().getId())
                .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp không tồn tại với ID: " + purchaseOrder.getSupplier().getId()));
        Product product = productRepository.findById(purchaseOrder.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + purchaseOrder.getProduct().getId()));

        if (purchaseOrder.getImportPrice() == null || purchaseOrder.getImportPrice() <= 0) {
            throw new IllegalArgumentException("Giá nhập hàng phải lớn hơn 0.");
        }

        List<PurchaseOrderItem> validItems = purchaseOrder.getItems().stream()
                .filter(item -> item.getQuantity() != null && item.getQuantity() > 0)
                .collect(Collectors.toList());

        if (validItems.isEmpty()) {
            throw new IllegalArgumentException("Phải có ít nhất một mục nhập hàng với số lượng lớn hơn 0.");
        }

        double totalAmount = calculateTotalAmount(purchaseOrder.getImportPrice(), validItems);

        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setProduct(product);
        purchaseOrder.setItems(validItems);
        purchaseOrder.setTotalAmount(totalAmount);
        purchaseOrder.setStatus("PENDING");

        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public PurchaseOrder confirmPurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu nhập hàng không tồn tại với ID: " + id));

        if (!"PENDING".equalsIgnoreCase(purchaseOrder.getStatus())) {
            throw new IllegalArgumentException("Chỉ có thể xác nhận phiếu ở trạng thái PENDING. Trạng thái hiện tại: " + purchaseOrder.getStatus());
        }

        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            VariantSize variantSize = variantSizeRepository.findById(item.getVariantSizeId())
                    .orElseThrow(() -> new IllegalArgumentException("Kích thước biến thể không tồn tại với ID: " + item.getVariantSizeId()));
            variantSize.setQuantity(variantSize.getQuantity() + item.getQuantity());
            variantSizeRepository.save(variantSize);
        }

        purchaseOrder.setStatus("COMPLETED");
        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public PurchaseOrder cancelPurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu nhập hàng không tồn tại với ID: " + id));

        if (!"PENDING".equalsIgnoreCase(purchaseOrder.getStatus())) {
            throw new IllegalArgumentException("Chỉ có thể hủy phiếu ở trạng thái PENDING. Trạng thái hiện tại: " + purchaseOrder.getStatus());
        }

        purchaseOrder.setStatus("CANCELLED");
        return purchaseOrderRepository.save(purchaseOrder);
    }

    private PurchaseOrderDTO mapToDTO(PurchaseOrder order) {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setId(order.getId());
        dto.setPurchaseOrderCode(order.getPurchaseOrderCode());
        dto.setSupplierName(order.getSupplier() != null ? order.getSupplier().getName() : "N/A");
        dto.setProductName(order.getProduct() != null ? order.getProduct().getName() : "N/A");

        String productVariantName = order.getProduct() != null ? order.getProduct().getName() : "N/A";
        String mainImage = null;
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            Long variantSizeId = order.getItems().get(0).getVariantSizeId();
            Optional<VariantSize> variantSizeOpt = variantSizeRepository.findById(variantSizeId);
            if (variantSizeOpt.isPresent()) {
                VariantSize variantSize = variantSizeOpt.get();
                ProductVariant variant = variantSize.getVariant();
                if (variant != null) {
                    productVariantName = String.format("%s - %s", order.getProduct().getName(), variant.getColor());
                    mainImage = variant.getMainImage();
                }
            }
        }
        dto.setProductVariantName(productVariantName);
        dto.setMainImage(mainImage);

        dto.setStatus(order.getStatus());
        dto.setItems(order.getItems().stream().map(item -> {
            PurchaseOrderDTO.PurchaseOrderItem itemDTO = new PurchaseOrderDTO.PurchaseOrderItem();
            itemDTO.setVariantSizeId(item.getVariantSizeId());
            itemDTO.setQuantity(item.getQuantity());
            Optional<VariantSize> variantSizeOpt = variantSizeRepository.findById(item.getVariantSizeId());
            if (variantSizeOpt.isPresent()) {
                VariantSize variantSize = variantSizeOpt.get();
                itemDTO.setSize(variantSize.getSize());
                ProductVariant variant = variantSize.getVariant();
                if (variant != null) {
                    itemDTO.setVariantColor(variant.getColor());
                    itemDTO.setVariantMainImage(variant.getMainImage());
                } else {
                    itemDTO.setVariantColor("N/A");
                    itemDTO.setVariantMainImage(null);
                }
            } else {
                itemDTO.setSize("N/A");
                itemDTO.setVariantColor("N/A");
                itemDTO.setVariantMainImage(null);
            }
            return itemDTO;
        }).collect(Collectors.toList()));
        dto.setImportPrice(order.getImportPrice());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }

    private void validatePurchaseOrderInput(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getPurchaseOrderCode() == null || purchaseOrder.getPurchaseOrderCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu nhập hàng không được để trống.");
        }
        if (purchaseOrderRepository.existsByPurchaseOrderCode(purchaseOrder.getPurchaseOrderCode())) {
            throw new IllegalArgumentException("Mã phiếu nhập hàng đã tồn tại: " + purchaseOrder.getPurchaseOrderCode());
        }
        if (purchaseOrder.getSupplier() == null || purchaseOrder.getSupplier().getId() == null) {
            throw new IllegalArgumentException("Nhà cung cấp không hợp lệ.");
        }
        if (purchaseOrder.getProduct() == null || purchaseOrder.getProduct().getId() == null) {
            throw new IllegalArgumentException("Sản phẩm không hợp lệ.");
        }
        if (purchaseOrder.getItems() == null || purchaseOrder.getItems().isEmpty()) {
            throw new IllegalArgumentException("Danh sách mục nhập hàng không được để trống.");
        }
    }

    private double calculateTotalAmount(Double importPrice, List<PurchaseOrderItem> items) {
        double totalAmount = 0;
        for (PurchaseOrderItem item : items) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                continue;
            }
            VariantSize variantSize = variantSizeRepository.findById(item.getVariantSizeId())
                    .orElseThrow(() -> new IllegalArgumentException("Kích thước biến thể không tồn tại với ID: " + item.getVariantSizeId()));
            totalAmount += importPrice * item.getQuantity();
        }
        return totalAmount;
    }
}