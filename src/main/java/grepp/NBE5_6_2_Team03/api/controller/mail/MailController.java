package grepp.NBE5_6_2_Team03.api.controller.mail;

import grepp.NBE5_6_2_Team03.api.controller.travelplan.dto.response.TravelPlanAdjustResponse;
import grepp.NBE5_6_2_Team03.domain.mail.service.MimeMailService;
import grepp.NBE5_6_2_Team03.domain.travelplan.service.TravelPlanQueryService;
import grepp.NBE5_6_2_Team03.domain.user.CustomUserDetails;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MimeMailService mimeMailService;
    private final TravelPlanQueryService travelPlanQueryService;

    @PostMapping("/send")
    public String sendSettlementMail(@RequestParam Long planId, @AuthenticationPrincipal CustomUserDetails customUser) {

        TravelPlanAdjustResponse response = travelPlanQueryService.getAdjustmentInfo(planId);
        Map<String,Object> settlementForm = Map.of("response",response);

        mimeMailService.sendSettlementMail(
            customUser.getUser().getEmail(),
            "정산 결과 안내",
            "settlement-summary",
            settlementForm
        );
        return "redirect:/users/home";
    }
}
