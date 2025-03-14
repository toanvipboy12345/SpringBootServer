package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.exception.InvalidInputException;
import com.ecommerce.Ecommerce.model.Category;
import com.ecommerce.Ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Lấy danh sách tất cả các danh mục, có thể hỗ trợ tìm kiếm và sắp xếp.
     *
     * @param sortBy  trường cần sắp xếp (name hoặc id)
     * @param sortDir hướng sắp xếp (asc hoặc desc)
     * @param search  từ khóa tìm kiếm trong tên danh mục
     * @return danh sách các danh mục theo yêu cầu
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false) String search) {

        List<Category> categories;

        if (search != null && !search.isEmpty()) {
            categories = categoryService.searchCategoriesByName(search);
        } else if (sortBy != null && !sortBy.isEmpty()) {
            try {
                categories = categoryService.getAllSorted(sortBy, sortDir);
            } catch (InvalidInputException e) {
                return ResponseEntity.badRequest().body(null);
            }
        } else {
            categories = categoryService.getAll();
        }

        return ResponseEntity.ok(categories);
    }

    /**
     * Lấy danh mục theo ID.
     *
     * @param id ID của danh mục
     * @return danh mục có ID tương ứng, hoặc 404 nếu không tìm thấy
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(null));
    }

    /**
     * Tạo mới một danh mục.
     *
     * @param category đối tượng danh mục cần tạo
     * @return danh mục đã được tạo, hoặc thông báo lỗi nếu dữ liệu không hợp lệ
     */
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        try {
            Category createdCategory = categoryService.create(category);
            return ResponseEntity.status(201).body(createdCategory);
        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/multi")
    public ResponseEntity<?> createCategories(@RequestBody List<Category> categories) {
        try {
            List<Category> createdCategories = categoryService.createCategories(categories);
            return ResponseEntity.status(201).body(createdCategories);
        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Cập nhật danh mục theo ID.
     *
     * @param id       ID của danh mục cần cập nhật
     * @param category đối tượng danh mục chứa dữ liệu cập nhật
     * @return danh mục đã được cập nhật, hoặc thông báo lỗi nếu dữ liệu không hợp
     *         lệ
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.update(id, category);
            return ResponseEntity.ok(updatedCategory);
        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    /**
     * Xóa danh mục theo ID.
     *
     * @param id ID của danh mục cần xóa
     * @return mã phản hồi 204 nếu xóa thành công, hoặc mã lỗi nếu ID không hợp lệ
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }
}
