package grepp.NBE5_6_2_Team03.global.config.security;

import grepp.NBE5_6_2_Team03.domain.user.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final RedisTemplate redisTemplate;

    private static final String ADMIN_REDIRECT_URL = "/admin/dashboard";
    private static final String USER_REDIRECT_URL = "/users/home";
    private static final String DEFAULT_REDIRECT_URL = "/";

    public CustomLoginSuccessHandler(@Qualifier("redisTemplate") RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String email = request.getParameter("email");
        String key = "login:fail:" + email;
        redisTemplate.delete(key);

        if (isJsonRequest(request)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");

            String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("UNKNOWN");

            String json = String.format("{\"login\": true, \"role\": \"%s\"}", role);
            response.getWriter().write(json);
            response.flushBuffer();
            return;
        }

        if (hasRole(authentication, Role.ROLE_ADMIN)) {
           response.sendRedirect(ADMIN_REDIRECT_URL);
           return;
        }

        if(hasRole(authentication, Role.ROLE_USER)){
            response.sendRedirect(USER_REDIRECT_URL);
            return;
        }

        response.sendRedirect(DEFAULT_REDIRECT_URL);
    }

    private boolean hasRole(Authentication auth, Role role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> role.isSameRoleName(a.getAuthority()));
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getHeader("Content-Type");
        return contentType != null && contentType.contains("application/json");
    }
}
