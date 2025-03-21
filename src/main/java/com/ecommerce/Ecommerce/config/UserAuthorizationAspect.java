package com.ecommerce.Ecommerce.config;

import com.ecommerce.Ecommerce.annotation.RequireUserRole;
import com.ecommerce.Ecommerce.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class UserAuthorizationAspect {

    @Around("@annotation(com.ecommerce.Ecommerce.annotation.RequireUserRole)")
    public Object checkUserRole(ProceedingJoinPoint joinPoint) throws Throwable {
        // Lấy HttpServletRequest từ request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal Server Error: No request attributes found.\"}");
        }

        HttpServletRequest request = attributes.getRequest();
        // Bỏ qua yêu cầu OPTIONS (preflight request)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return joinPoint.proceed();
        }

        // Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Unauthorized: Please log in.\"}");
        }

        User user = (User) session.getAttribute("user");

        // Nếu user có role là "user", kiểm tra xem user có đang thao tác trên chính mình không
        if ("user".equals(user.getRole())) {
            // Lấy id từ path variable (giả sử endpoint có dạng /user/{id})
            String path = request.getRequestURI(); // Ví dụ: /user/123
            String[] pathParts = path.split("/");
            Long userIdFromPath = null;
            try {
                // Giả sử id là phần cuối cùng của path
                userIdFromPath = Long.parseLong(pathParts[pathParts.length - 1]);
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Invalid user ID in path.\"}");
            }

            // Kiểm tra xem id trong path có trùng với id của user đang đăng nhập không
            if (!user.getId().equals(userIdFromPath)) {
                System.out.println("Access denied. User with role 'user' can only update their own information.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"Forbidden: You can only update your own information.\"}");
            }
        }

        // Nếu user có role là "admin", kiểm tra quyền sẽ được thực hiện bởi @RequireAdminRole (nếu có)
        // Nếu không có @RequireAdminRole, user với role "admin" sẽ được phép truy cập
        return joinPoint.proceed();
    }
}