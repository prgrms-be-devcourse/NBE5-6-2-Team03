package grepp.NBE5_6_2_Team03.domain.schedule.treveltimeai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import grepp.NBE5_6_2_Team03.api.controller.schedule.traveltimeai.dto.TravelTimeRequest;
import grepp.NBE5_6_2_Team03.api.controller.schedule.traveltimeai.dto.TravelTimeResponse;

@AiService(
    wiringMode = AiServiceWiringMode.EXPLICIT,
    chatModel = "googleAiGeminiChatModel"
)
public interface TravelTimeAiService {

    @SystemMessage("너는 친절한 여행 일정 도우미야. 사용자의 출발지, 도착지, 교통수단을 기반으로 이동 예상 시간을 예측해서 알려줘. ex)40분, 30분, 1시간 10분")
    TravelTimeResponse predictTime(@UserMessage TravelTimeRequest request);

}
