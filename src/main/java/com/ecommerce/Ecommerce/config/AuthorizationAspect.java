// package com.ecommerce.Ecommerce.config;

// import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
// import com.ecommerce.Ecommerce.model.User;
// import com.ecommerce.Ecommerce.service.UserService;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpSession;
// import org.aspectj.lang.ProceedingJoinPoint;
// import org.aspectj.lang.annotation.Around;
// import org.aspectj.lang.annotation.Aspect;
// import org.aspectj.lang.reflect.MethodSignature;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Component;
// import org.springframework.web.context.request.RequestContextHolder;
// import org.springframework.web.context.request.ServletRequestAttributes;

// import java.lang.reflect.Method;
// import java.util.Arrays;

// @Aspect
// @Component
// public class AuthorizationAspect {

//     private final UserService userService;

//     @Autowired
//     public AuthorizationAspect(UserService userService) {
//         this.userService = userService;
//     }

//     @Around("@annotation(com.ecommerce.Ecommerce.annotation.RequireAdminRole)")
//     public Object checkAdminRole(ProceedingJoinPoint joinPoint) throws Throwable {
//         // Lấy HttpServletRequest từ request
//         ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//         if (attributes == null) {
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Unauthorized: No session found.\"}");
//         }

//         HttpServletRequest request = attributes.getRequest();
//         // Bỏ qua yêu cầu OPTIONS (preflight request)
//         if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//             return joinPoint.proceed();
//         }

//         HttpSession session = request.getSession(false);
//         if (session == null || session.getAttribute("user") == null) {
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Unauthorized: Please log in.\"}");
//         }

//         User user = (User) session.getAttribute("user");

//         // Kiểm tra role của user có phải là "admin" không
//         if (!"admin".equals(user.getRole())) {
//             System.out.println("Access denied. User role: " + user.getRole());
//             return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"Forbidden: You do not have permission to access this resource.\"}");
//         }

//         // Lấy annotation @RequireAdminRole từ method
//         MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//         Method method = signature.getMethod();
//         RequireAdminRole requireAdminRole = method.getAnnotation(RequireAdminRole.class);

//         // Lấy danh sách các vai trò được phép từ annotation
//         String[] requiredRoles = requireAdminRole.roles();
//         System.out.println("Required roles: " + Arrays.toString(requiredRoles));

//         // Kiểm tra xem user có một trong các vai trò yêu cầu không
//         boolean hasRequiredRole = userService.hasRequiredAdminRole(user, requiredRoles);
//         if (!hasRequiredRole) {
//             System.out.println("Access denied. User does not have required roles: " + Arrays.toString(requiredRoles));
//             return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"Forbidden: You do not have permission to access this resource.\"}");
//         }

//         // Nếu có quyền, tiếp tục gọi endpoint
//         return joinPoint.proceed();
//     }
// }
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
        System.out.println("User in session: " + user.getId() + ", Role: " + user.getRole());

        // Nếu user có role là "admin", kiểm tra admin_role
        if ("admin".equals(user.getRole())) {
            // Lấy annotation @RequireAdminRole từ method
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            RequireAdminRole requireAdminRole = method.getAnnotation(RequireAdminRole.class);

            // Lấy danh sách các vai trò được phép từ annotation
            String[] requiredRoles = requireAdminRole.roles();
            System.out.println("Required roles: " + Arrays.toString(requiredRoles));

            // Kiểm tra xem user có một trong các vai trò yêu cầu không
            boolean hasRequiredRole = userService.hasRequiredAdminRole(user, requiredRoles);
            if (!hasRequiredRole) {
                System.out.println("Access denied. User does not have required roles: " + Arrays.toString(requiredRoles));
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"Forbidden: You do not have permission to access this resource.\"}");
            }
        }

        // Nếu user không phải admin, để các aspect khác (như UserAuthorizationAspect) xử lý
        return joinPoint.proceed();
    }
}