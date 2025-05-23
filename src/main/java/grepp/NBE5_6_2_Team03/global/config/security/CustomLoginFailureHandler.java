package grepp.NBE5_6_2_Team03.global.config.security;

import grepp.NBE5_6_2_Team03.domain.user.User;
import grepp.NBE5_6_2_Team03.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;

@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final Duration LOGIN_FAIL_TTL = Duration.ofHours(6);
    private static final String DEFAULT_URL = "/";
    private static final String LOGIN_LOCKED_URL = "/?error=isLock";
    private static final int MAX_FAIL_ATTEMPTS_COUNT = 5;

    public CustomLoginFailureHandler(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate,
                                     UserRepository userRepository, @Lazy BCryptPasswordEncoder passwordEncoder) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String email = request.getParameter("email");
        User user = userRepository.findByEmail(email).orElse(null);

        if (isJsonRequest(request)) {
            String errorMessage;
            boolean locked = false;

            if (isNotRegisteredUser(user)) {
                errorMessage = "존재하지 않는 사용자입니다.";
            } else {
                String key = findKey(email);
                Long failCount = redisTemplate.opsForValue().increment(key);

                if (isFirstLoginFail(failCount)) {
                    redisTemplate.expire(key, LOGIN_FAIL_TTL);
                }

                if (isOverMaxFailAttempts(failCount)) {
                    user.updateIsLocked(true);
                    locked = true;
                    errorMessage = "⚠ 계정이 잠겼습니다.<br> 비밀번호 찾기를 이용해 주세요";
                } else {
                    errorMessage = "비밀번호가 틀렸습니다.";
                }
            }

            jsonResponse(response, errorMessage, locked);
            return;
        }

        if(isNotRegisteredUser(user)){
            response.sendRedirect(DEFAULT_URL);
            return;
        }

        String key = findKey(email);
        Long failCount = redisTemplate.opsForValue().increment(key);

        if(isFirstLoginFail(failCount)){
            redisTemplate.expire(key, LOGIN_FAIL_TTL);
        }

        if(isOverMaxFailAttempts(failCount)){
            user.updateIsLocked(true);
            response.sendRedirect(LOGIN_LOCKED_URL);
            return;
        }

        response.sendRedirect(DEFAULT_URL);
    }

    private String findKey(String email) {
        return "login:fail:" + email;
    }

    private boolean isNotRegisteredUser(User user) {
        return user == null;
    }

    private boolean isOverMaxFailAttempts(Long failCount) {
        return failCount >= MAX_FAIL_ATTEMPTS_COUNT;
    }

    private boolean isFirstLoginFail(Long failCount) {
        return failCount == 1;
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getHeader("Content-Type");
        return contentType != null && contentType.contains("application/json");
    }

    private void jsonResponse(HttpServletResponse response, String message, boolean locked) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String json = locked
            ? String.format("{\"login\": false, \"error\": \"%s\", \"locked\": true}", message)
            : String.format("{\"login\": false, \"error\": \"%s\"}", message);

        response.getWriter().write(json);
        response.flushBuffer();
    }
}
