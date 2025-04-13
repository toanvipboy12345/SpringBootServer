package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.exception.InvalidInputException;
import com.ecommerce.Ecommerce.model.Category;
import com.ecommerce.Ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Lấy tất cả danh mục mà không áp dụng sắp xếp.
     *
     * @return danh sách tất cả các danh mục
     */
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    /**
     * Lấy danh sách danh mục đã được sắp xếp theo trường chỉ định và hướng sắp xếp.
     *
     * @param sortBy trường cần sắp xếp, có thể là "name" hoặc "id"
     * @param sortDir hướng sắp xếp, có thể là "asc" hoặc "desc"
     * @return danh sách danh mục đã sắp xếp
     * @throws InvalidInputException nếu trường sắp xếp không hợp lệ
     */
    public List<Category> getAllSorted(String sortBy, String sortDir) {
        if (!sortBy.equals("name") && !sortBy.equals("id")) {
            throw new InvalidInputException("Invalid sort parameter. Use 'name' or 'id'.");
        }

        // Xác định hướng sắp xếp
        Sort.Direction direction = Sort.Direction.ASC; // Mặc định là ASC
        if ("desc".equalsIgnoreCase(sortDir)) {
            direction = Sort.Direction.DESC;
        }

        return categoryRepository.findAll(Sort.by(direction, sortBy));
    }

    /**
     * Tìm kiếm danh mục dựa trên từ khóa trong tên (không phân biệt hoa thường).
     *
     * @param keyword từ khóa để tìm kiếm
     * @return danh sách danh mục khớp với từ khóa
     */
    public List<Category> searchCategoriesByName(String keyword) {
        return categoryRepository.findByNameContainingIgnoreCase(keyword);
    }

    /**
     * Lấy một danh mục theo ID.
     *
     * @param id ID của danh mục
     * @return danh mục có ID tương ứng, hoặc Optional.empty nếu không tìm thấy
     */
    public Optional<Category> getById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Tạo một danh mục mới sau khi kiểm tra tính hợp lệ.
     *
     * @param category đối tượng danh mục cần tạo
     * @return danh mục đã được tạo
     * @throws InvalidInputException nếu tên danh mục đã tồn tại hoặc dữ liệu không hợp lệ
     */
    public Category create(Category category) {
        validateCategory(category);
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new InvalidInputException("Danh mục đã tồn tại");
        }
        return categoryRepository.save(category);
    }
    public List<Category> createCategories(List<Category> categories) {
        for (Category category : categories) {
            validateCategory(category);
            if (categoryRepository.findByName(category.getName()).isPresent()) {
                throw new InvalidInputException("Danh mục đã tồn tại");
            }
        }
        return categoryRepository.saveAll(categories); // Lưu tất cả các danh mục
    }
    

    /**
     * Cập nhật danh mục có ID chỉ định với dữ liệu mới sau khi kiểm tra tính hợp lệ.
     *
     * @param id       ID của danh mục cần cập nhật
     * @param category đối tượng danh mục chứa dữ liệu cập nhật
     * @return danh mục đã được cập nhật
     * @throws InvalidInputException nếu tên danh mục trùng hoặc dữ liệu không hợp lệ
     * @throws RuntimeException      nếu không tìm thấy danh mục với ID chỉ định
     */
    public Category update(Long id, Category category) {
        validateCategory(category);
        if (categoryRepository.findByName(category.getName()).isPresent() &&
            !categoryRepository.findById(id).map(Category::getName).get().equals(category.getName())) {
            throw new InvalidInputException("Danh mục đã tồn tại");
        }
        return categoryRepository.findById(id).map(existingCategory -> {
            existingCategory.setName(category.getName());
            existingCategory.setDescription(category.getDescription());
            return categoryRepository.save(existingCategory);
        }).orElseThrow(() -> new RuntimeException("Category not found with id " + id));
    }

    /**
     * Xóa danh mục theo ID sau khi kiểm tra tính hợp lệ của ID.
     *
     * @param id ID của danh mục cần xóa
     * @throws InvalidInputException nếu ID không hợp lệ hoặc không tồn tại
     */
    public void delete(Long id) {
        if (id == null || !categoryRepository.existsById(id)) {
            throw new InvalidInputException("Invalid category id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    /**
     * Kiểm tra tính hợp lệ của đối tượng danh mục.
     * Tên danh mục không được rỗng hoặc null.
     *
     * @param category đối tượng danh mục cần kiểm tra
     * @throws InvalidInputException nếu tên danh mục rỗng hoặc null
     */
    private void validateCategory(Category category) {
        if (category.getName() == null || category.getName().isEmpty()) {
            throw new InvalidInputException("Category name cannot be null or empty.");
        }
    }
}
