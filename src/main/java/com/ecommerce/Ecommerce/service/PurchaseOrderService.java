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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
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

    public byte[] exportPurchaseOrderToPdf(Long id) throws Exception {
        Optional<PurchaseOrderDTO> purchaseOrderOpt = getPurchaseOrderById(id);
        if (!purchaseOrderOpt.isPresent()) {
            throw new IllegalArgumentException("Phiếu nhập hàng không tồn tại với ID: " + id);
        }
    
        PurchaseOrderDTO purchaseOrder = purchaseOrderOpt.get();
    
        // Lấy thông tin nhà cung cấp từ repository để lấy address và phone
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu nhập hàng không tồn tại với ID: " + id));
        Supplier supplier = po.getSupplier();
        String supplierAddress = supplier != null ? (supplier.getAddress() != null ? supplier.getAddress() : "N/A") : "N/A";
        String supplierPhone = supplier != null ? (supplier.getPhone() != null ? supplier.getPhone() : "N/A") : "N/A";
    
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
    
            // Tải font Arial từ resources
            InputStream fontStream = getClass().getResourceAsStream("/fonts/arial.ttf");
            if (fontStream == null) {
                throw new IllegalStateException("Không tìm thấy file font arial.ttf trong resources/fonts/");
            }
            PDType0Font font = PDType0Font.load(document, fontStream);
    
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Header: Thông tin công ty và tiêu đề
                contentStream.setFont(font, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 780);
                contentStream.showText("LITTLE USA");
                contentStream.endText();
    
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 765);
                contentStream.showText("Địa chỉ: 41A Đ. Phú Diễn, Phú Diễn, Bắc Từ Liêm, Hà Nội");
                contentStream.endText();
    
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Số điện thoại: 0211 3301 747");
                contentStream.endText();
    
                // Vẽ đường kẻ ngang trên cùng để phân cách thông tin công ty
                contentStream.setLineWidth(1f);
                contentStream.moveTo(50, 740);
                contentStream.lineTo(550, 740);
                contentStream.stroke();
    
                contentStream.setFont(font, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(200, 720);
                contentStream.showText("PHIẾU NHẬP HÀNG");
                contentStream.endText();
    
                // Vẽ đường kẻ dưới tiêu đề
                contentStream.setLineWidth(1f);
                contentStream.moveTo(50, 710);
                contentStream.lineTo(550, 710);
                contentStream.stroke();
    
                // Thông tin chính (không dùng bảng)
                float yPosition = 680;
                contentStream.setFont(font, 12);
    
                // Mã phiếu
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Mã phiếu: " + (purchaseOrder.getPurchaseOrderCode() != null ? purchaseOrder.getPurchaseOrderCode() : "N/A"));
                contentStream.endText();
    
                yPosition -= 20;
    
                // Nhà cung cấp
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Nhà cung cấp: " + purchaseOrder.getSupplierName());
                contentStream.endText();
    
                yPosition -= 20;
    
                // Địa chỉ nhà cung cấp
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Địa chỉ NCC: " + supplierAddress);
                contentStream.endText();
    
                yPosition -= 20;
    
                // Số điện thoại nhà cung cấp
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Số điện thoại NCC: " + supplierPhone);
                contentStream.endText();
    
                yPosition -= 20;
    
                // Sản phẩm
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Sản phẩm: " + purchaseOrder.getProductName());
                contentStream.endText();
    
                yPosition -= 20;
    
                // Ngày tạo
                String createdAtStr = "N/A";
                if (purchaseOrder.getCreatedAt() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    createdAtStr = ((LocalDateTime) purchaseOrder.getCreatedAt()).format(formatter);
                }
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Ngày tạo: " + createdAtStr);
                contentStream.endText();
    
                yPosition -= 20;
    
                // Ngày cập nhật
                String updatedAtStr = "N/A";
                if (purchaseOrder.getUpdatedAt() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    updatedAtStr = ((LocalDateTime) purchaseOrder.getUpdatedAt()).format(formatter);
                }
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Ngày cập nhật: " + updatedAtStr);
                contentStream.endText();
    
                yPosition -= 20;
    
                // Trạng thái (chuyển sang tiếng Việt)
                String statusInVietnamese = "N/A";
                if (purchaseOrder.getStatus() != null) {
                    switch (purchaseOrder.getStatus().toUpperCase()) {
                        case "PENDING":
                            statusInVietnamese = "Đang chờ";
                            break;
                        case "COMPLETED":
                            statusInVietnamese = "Hoàn thành";
                            break;
                        case "CANCELLED":
                            statusInVietnamese = "Đã hủy";
                            break;
                        default:
                            statusInVietnamese = purchaseOrder.getStatus();
                    }
                }
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Trạng thái: " + statusInVietnamese);
                contentStream.endText();
    
                yPosition -= 20;
    
                // Giá nhập
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Giá nhập: " + String.format("%,.0f đ", purchaseOrder.getImportPrice()));
                contentStream.endText();
    
                yPosition -= 20;
    
                // Tổng giá trị
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Tổng giá trị: " + String.format("%,.0f đ", purchaseOrder.getTotalAmount()));
                contentStream.endText();
    
                yPosition -= 30;
    
                // Danh sách mục nhập
                contentStream.setFont(font, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Danh sách mục nhập:");
                contentStream.endText();
    
                yPosition -= 30;
                float tableY = yPosition;
                float tableWidthItems = 500;
                float colWidth = tableWidthItems / 2; // Chỉ có 2 cột: Kích thước và Số lượng
    
                // Nhóm các mục nhập theo màu sắc (variantColor)
                Map<String, List<PurchaseOrderDTO.PurchaseOrderItem>> itemsByColor = purchaseOrder.getItems().stream()
                        .collect(Collectors.groupingBy(PurchaseOrderDTO.PurchaseOrderItem::getVariantColor));
    
                for (Map.Entry<String, List<PurchaseOrderDTO.PurchaseOrderItem>> entry : itemsByColor.entrySet()) {
                    String color = entry.getKey();
                    List<PurchaseOrderDTO.PurchaseOrderItem> items = entry.getValue();
    
                    // Lưu vị trí bắt đầu của bảng để vẽ viền dọc
                    float tableStartY = tableY;
    
                    // Hiển thị tên sản phẩm kèm màu sắc
                    contentStream.setFont(font, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, tableY);
                    contentStream.showText(purchaseOrder.getProductName() + " - " + color);
                    contentStream.endText();
    
                    tableY -= 20;
    
                    // Vẽ tiêu đề bảng
                    contentStream.setFont(font, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, tableY);
                    contentStream.showText("Kích thước");
                    contentStream.endText();
    
                    contentStream.beginText();
                    contentStream.newLineAtOffset(150, tableY);
                    contentStream.showText("Số lượng nhập");
                    contentStream.endText();
    
                    // Vẽ viền cho tiêu đề bảng
                    contentStream.setLineWidth(0.5f);
                    contentStream.moveTo(50, tableY + 10);
                    contentStream.lineTo(50 + tableWidthItems, tableY + 10);
                    contentStream.moveTo(50, tableY - 10);
                    contentStream.lineTo(50 + tableWidthItems, tableY - 10);
                    contentStream.stroke();
    
                    // Dữ liệu bảng
                    for (PurchaseOrderDTO.PurchaseOrderItem item : items) {
                        tableY -= 20;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, tableY);
                        contentStream.showText(item.getSize() != null ? item.getSize() : "N/A");
                        contentStream.endText();
    
                        contentStream.beginText();
                        contentStream.newLineAtOffset(150, tableY);
                        contentStream.showText(String.valueOf(item.getQuantity()));
                        contentStream.endText();
    
                        // Vẽ viền cho hàng
                        contentStream.moveTo(50, tableY - 10);
                        contentStream.lineTo(50 + tableWidthItems, tableY - 10);
                        contentStream.stroke();
                    }
    
                    // Vẽ viền dọc cho bảng (dùng tableStartY để đảm bảo viền bao quát toàn bộ bảng)
                    contentStream.moveTo(50, tableStartY);
                    contentStream.lineTo(50, tableY - 10);
                    contentStream.moveTo(150, tableStartY);
                    contentStream.lineTo(150, tableY - 10);
                    contentStream.moveTo(50 + tableWidthItems, tableStartY);
                    contentStream.lineTo(50 + tableWidthItems, tableY - 10);
                    contentStream.stroke();
    
                    tableY -= 20; // Khoảng cách giữa các nhóm màu
                }
    
                // Tổng số lượng
                int totalQuantity = purchaseOrder.getItems().stream()
                        .mapToInt(PurchaseOrderDTO.PurchaseOrderItem::getQuantity)
                        .sum();
                contentStream.setFont(font, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, tableY);
                contentStream.showText("Tổng số lượng: " + totalQuantity);
                contentStream.endText();
    
                // Footer: Ngày in
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String printDate = sdf.format(new Date());
                contentStream.setFont(font, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 30);
                contentStream.showText("Ngày in: " + printDate);
                contentStream.endText();
    
                contentStream.beginText();
                contentStream.newLineAtOffset(500, 30);
                contentStream.showText("Trang 1/1");
                contentStream.endText();
            }
    
            // Lưu PDF vào byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
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