package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.dto.BlogDTO;
import com.ecommerce.Ecommerce.model.Blog;
import com.ecommerce.Ecommerce.model.User;
import com.ecommerce.Ecommerce.repository.BlogRepository;
import com.ecommerce.Ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Thêm bài đăng mới
    public BlogDTO createBlog(BlogDTO blogDTO, MultipartFile thumbnailFile) throws IOException {
        validateBlog(blogDTO);

        // Kiểm tra userId và lấy thông tin từ bảng User
        User user = userRepository.findById(blogDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại với id: " + blogDTO.getUserId()));

        Blog blog = new Blog();
        blog.setTitle(blogDTO.getTitle());
        blog.setContent(blogDTO.getContent());
        blog.setIsPublished(blogDTO.getIsPublished());
        blog.setAuthorId(user.getId());
        blog.setAuthorUsername(user.getUsername());

        // Xử lý upload thumbnail nếu có
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String imagePath = saveImage(blogDTO.getTitle(), thumbnailFile);
            blog.setThumbnail(imagePath);
        }

        // Nếu bài đăng được xuất bản, đặt ngày xuất bản
        if (blogDTO.getIsPublished()) {
            blog.setPublishedAt(LocalDateTime.now());
        }

        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());

        Blog savedBlog = blogRepository.save(blog);
        return mapToDTO(savedBlog);
    }

    // Cập nhật bài đăng
    public BlogDTO updateBlog(Long id, BlogDTO blogDTO, MultipartFile thumbnailFile) throws IOException {
        validateBlog(blogDTO);

        // Kiểm tra userId và lấy thông tin từ bảng User
        User user = userRepository.findById(blogDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại với id: " + blogDTO.getUserId()));

        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if (!optionalBlog.isPresent()) {
            throw new IllegalArgumentException("Bài đăng không tồn tại với id: " + id);
        }

        Blog blog = optionalBlog.get();
        blog.setTitle(blogDTO.getTitle());
        blog.setContent(blogDTO.getContent());
        blog.setIsPublished(blogDTO.getIsPublished());
        blog.setAuthorId(user.getId());
        blog.setAuthorUsername(user.getUsername());

        // Xử lý upload thumbnail mới nếu có
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String newImagePath = saveImage(blogDTO.getTitle(), thumbnailFile);
            if (blog.getThumbnail() != null) {
                deleteImage(blog.getThumbnail()); // Xóa hình ảnh cũ
            }
            blog.setThumbnail(newImagePath);
        }

        // Cập nhật ngày xuất bản nếu trạng thái thay đổi
        if (blogDTO.getIsPublished() && blog.getPublishedAt() == null) {
            blog.setPublishedAt(LocalDateTime.now());
        } else if (!blogDTO.getIsPublished()) {
            blog.setPublishedAt(null);
        }

        blog.setUpdatedAt(LocalDateTime.now());
        Blog updatedBlog = blogRepository.save(blog);
        return mapToDTO(updatedBlog);
    }

    // Xóa bài đăng
    public void deleteBlog(Long id) {
        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if (!optionalBlog.isPresent()) {
            throw new IllegalArgumentException("Bài đăng không tồn tại với id: " + id);
        }

        Blog blog = optionalBlog.get();
        if (blog.getThumbnail() != null) {
            deleteImage(blog.getThumbnail()); // Xóa hình ảnh thumbnail
        }

        blogRepository.deleteById(id);
    }

    // Lấy bài đăng theo ID
    public BlogDTO getBlogById(Long id) {
        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if (!optionalBlog.isPresent()) {
            throw new IllegalArgumentException("Bài đăng không tồn tại với id: " + id);
        }
        return mapToDTO(optionalBlog.get());
    }

    // Lấy danh sách tất cả bài đăng
    public List<BlogDTO> getAllBlogs() {
        return blogRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Upload hình ảnh cho nội dung bài đăng
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File hình ảnh không được để trống");
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase()
                : ".jpg";

        String fileName = "content_image_" + System.currentTimeMillis() + fileExtension;
        return saveImage(fileName, file);
    }

    // Hàm kiểm tra dữ liệu blog
    private void validateBlog(BlogDTO blogDTO) {
        if (blogDTO.getTitle() == null || blogDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tiêu đề bài đăng không được để trống hoặc null.");
        }
        if (blogDTO.getContent() == null || blogDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung bài đăng không được để trống hoặc null.");
        }
        if (blogDTO.getIsPublished() == null) {
            throw new IllegalArgumentException("Trạng thái xuất bản không được để trống.");
        }
        if (blogDTO.getUserId() == null) {
            throw new IllegalArgumentException("ID người dùng không được để trống.");
        }
    }

    // Hàm lưu hình ảnh
    private String saveImage(String baseName, MultipartFile imageFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        // Không cần kiểm tra và tạo thư mục vì đã được tạo sẵn

        String originalFileName = imageFile.getOriginalFilename();
        String fileExtension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase()
                : ".jpg";

        String fileName = baseName.trim().replaceAll("[^a-zA-Z0-9]", "_") + fileExtension;
        Path filePath = uploadPath.resolve(fileName);

        int counter = 1;
        while (Files.exists(filePath)) {
            String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
            fileName = fileNameWithoutExt + "_" + counter + fileExtension;
            filePath = uploadPath.resolve(fileName);
            counter++;
        }

        Files.copy(imageFile.getInputStream(), filePath);
        return "/uploads/" + fileName;
    }

    // Hàm xóa hình ảnh
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

    // Chuyển từ Entity sang DTO
    private BlogDTO mapToDTO(Blog blog) {
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setId(blog.getId());
        blogDTO.setTitle(blog.getTitle());
        blogDTO.setContent(blog.getContent());
        blogDTO.setThumbnail(blog.getThumbnail());
        blogDTO.setPublishedAt(blog.getPublishedAt());
        blogDTO.setIsPublished(blog.getIsPublished());
        blogDTO.setUserId(blog.getAuthorId()); // Trả về userId (tương ứng với authorId trong Blog)
        blogDTO.setAuthorUsername(blog.getAuthorUsername());
        blogDTO.setCreatedAt(blog.getCreatedAt());
        blogDTO.setUpdatedAt(blog.getUpdatedAt());
        return blogDTO;
    }
}