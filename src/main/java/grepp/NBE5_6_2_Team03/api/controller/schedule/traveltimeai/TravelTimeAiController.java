package grepp.NBE5_6_2_Team03.api.controller.schedule.traveltimeai;

import grepp.NBE5_6_2_Team03.api.controller.schedule.traveltimeai.dto.TravelTimeRequest;
import grepp.NBE5_6_2_Team03.api.controller.schedule.traveltimeai.dto.TravelTimeResponse;
import grepp.NBE5_6_2_Team03.domain.schedule.treveltimeai.service.TravelTimeAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plan/{travelPlanId}/schedule")
public class TravelTimeAiController {

    private final TravelTimeAiService travelTimeAiService;

    @GetMapping("/ai/predict-time")
    public ResponseEntity<TravelTimeResponse> predictTime(TravelTimeRequest request){
        TravelTimeResponse result = travelTimeAiService.predictTime(request);
        return ResponseEntity.ok(result);
    }
}
