package grepp.NBE5_6_2_Team03.api.controller.map;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import grepp.NBE5_6_2_Team03.api.controller.map.dto.MapPageDataResponse;
import grepp.NBE5_6_2_Team03.global.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MapControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASE_URL = "/map";

    @Test
    void testGetMapPage_WithValidRequest_ReturnsExpectedResponse() {
        WebTestClient client = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();

        client.get()
            .uri(uriBuilder -> uriBuilder
                .path(BASE_URL)
                .queryParam("country", "일본")
                .queryParam("city", "도쿄")
                .queryParam("page", "1")
                .queryParam("size", "5")
                .build())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .consumeWith(response -> {
                try {
                    String json = new String(response.getResponseBody());
                    ApiResponse<?> apiResponse = objectMapper.readValue(json, ApiResponse.class);

                    assert apiResponse.code().equals("2000");
                    assert apiResponse.message().equals("success");

                    // 선택된 도시 검증
                    MapPageDataResponse data = objectMapper.convertValue(
                        apiResponse.data(), MapPageDataResponse.class
                    );
                    assert data.getSelectedCity().equals("도쿄");
                    assert data.getSelectedCountry().equals("일본");
                    assert data.getMapPage().getNumber() == 1;
                    assert data.getMapPage().getSize() == 5;
                    assert !data.getMapPage().getContent().isEmpty();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }
}