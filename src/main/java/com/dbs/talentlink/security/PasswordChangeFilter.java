package com.dbs.talentlink.security;

import com.dbs.talentlink.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PasswordChangeFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private static final Set<String> ALLOWED_PATHS = Set.of("/api/users/me/password", "/api/auth/logout");

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();

            if (currentUser.isPasswordChangeRequired()) {
                String requestPath = request.getServletPath();
                if (!ALLOWED_PATHS.contains(requestPath)) {
                    // 如果需要修改密码，但访问的不是允许的路径，则拒绝访问
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(objectMapper.writeValueAsString(
                            Map.of("statusCode", HttpStatus.FORBIDDEN.value(),
                                    "message", "Action Forbidden",
                                    "details", "User must change their temporary password before accessing this resource.")
                    ));
                    return; // 中断过滤器链
                }
            }
        }

        filterChain.doFilter(request, response); // 继续执行下一个过滤器
    }
}