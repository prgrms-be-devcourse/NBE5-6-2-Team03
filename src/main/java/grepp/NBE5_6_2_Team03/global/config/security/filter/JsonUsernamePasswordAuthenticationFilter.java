package grepp.NBE5_6_2_Team03.global.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import grepp.NBE5_6_2_Team03.api.controller.user.dto.request.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(email, password);

            setDetails(request, authRequest);
            Authentication auth = this.getAuthenticationManager().authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(auth);
            return auth;

        } catch (IOException e) {
            throw new RuntimeException("JSON 로그인 실패", e);
        }
    }
}
