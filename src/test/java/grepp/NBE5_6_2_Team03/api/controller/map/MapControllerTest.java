package grepp.NBE5_6_2_Team03.api.controller.map;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import grepp.NBE5_6_2_Team03.api.controller.map.dto.MapPageDataResponse;
import grepp.NBE5_6_2_Team03.api.controller.map.dto.MapSearchRequest;
import grepp.NBE5_6_2_Team03.global.response.ApiResponse;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference; // 이 import를 추가합니다.
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MapControllerTest {

    @LocalServerPort
    private int port;

    // 보통 assertJ 를 많이 씀 -> 검증에 대한 유용한 메소드들을 많이 줌

    @Autowired
    private MapController controller;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("맵 페이지 QueryDSL 필터 및 페이지 확인")
    void testGetMapPage_WithValidRequest_ReturnsExpectedResponse() throws JsonProcessingException {
        for (int i = 0; i < 5; i++) {
            MapSearchRequest searchRequest = MapSearchRequest.builder()
                .country("일본")
                .city("오사카")
                .page(i)
                .size(5)
                .build();

            ResponseEntity<ApiResponse<MapPageDataResponse>> response = controller.getMapPage(searchRequest);
            String json = objectMapper.writeValueAsString(response.getBody());
            System.out.println(json);
        }

    }
}