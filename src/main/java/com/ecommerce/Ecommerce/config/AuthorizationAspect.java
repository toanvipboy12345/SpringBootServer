package com.ecommerce.Ecommerce.config;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
import com.ecommerce.Ecommerce.model.User;
import com.ecommerce.Ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class AuthorizationAspect {

    private final UserService userService;

    @Autowired
    public AuthorizationAspect(UserService userService) {
        this.userService = userService;
    }

    @Around("@annotation(com.ecommerce.Ecommerce.annotation.RequireAdminRole)")
    public Object checkAdminRole(ProceedingJoinPoint joinPoint) throws Throwable {
        // Lấy HttpServletRequest từ request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            System.out.println("No request attributes found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Unauthorized: No session found.\"}");
        }

        HttpServletRequest request = attributes.getRequest();
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Request Method: " + request.getMethod());

        // Bỏ qua yêu cầu OPTIONS (preflight request)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("Skipping OPTIONS request");
            return joinPoint.proceed();
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("Session not found or user not in session");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Unauthorized: Please log in.\"}");
        }

        User user = (User) session.getAttribute("user");
        
        // Tính thời gian còn lại của session
        int maxInactiveInterval = session.getMaxInactiveInterval(); // Giây
        long lastAccessedTime = session.getLastAccessedTime(); // Milliseconds
        long currentTime = System.currentTimeMillis(); // Milliseconds
        long timeElapsedSinceLastAccess = (currentTime - lastAccessedTime) / 1000; // Chuyển sang giây
        long timeRemaining = maxInactiveInterval - timeElapsedSinceLastAccess;

        System.out.println("User in session: " + user.getId() + ", Role: " + user.getRole() + 
                          ", Session timeout: " + maxInactiveInterval + "s, Time remaining: " + timeRemaining + "s");

        // Nếu user có role là "admin", kiểm tra admin_role
        if ("admin".equals(user.getRole())) {
            // Lấy annotation @RequireAdminRole từ method
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            RequireAdminRole requireAdminRole = method.getAnnotation(RequireAdminRole.class);

            // Lấy danh sách các vai trò được phép từ annotation
            String[] requiredRoles = requireAdminRole.roles();
            System.out.println("Required roles for method " + method.getName() + ": " + Arrays.toString(requiredRoles));

            // Kiểm tra xem user có một trong các vai trò yêu cầu không
            boolean hasRequiredRole = userService.hasRequiredAdminRole(user, requiredRoles);
            if (!hasRequiredRole) {
                System.out.println("Access denied for user ID: " + user.getId() + 
                                  ". Required roles: " + Arrays.toString(requiredRoles) + 
                                  ", User role: " + user.getRole() + 
                                  ", Session time remaining: " + timeRemaining + "s");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"Forbidden: You do not have permission to access this resource.\"}");
            }

            System.out.println("Access granted for user ID: " + user.getId() + 
                              " with role: " + user.getRole() + 
                              " to method: " + method.getName() + 
                              ", Session time remaining: " + timeRemaining + "s");
        }

        // Nếu user không phải admin, để các aspect khác xử lý
        return joinPoint.proceed();
    }
}