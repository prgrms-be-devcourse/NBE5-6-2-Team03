package grepp.NBE5_6_2_Team03.api.controller.user.dto.request;

import grepp.NBE5_6_2_Team03.global.validation.annotation.EmailCheck;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {

    @EmailCheck
    private String email;

    @NotBlank(message = "비밀번호는 빈 값을 허용 하지 않습니다.")
    private String password;

    protected LoginRequest(){
    }
}
