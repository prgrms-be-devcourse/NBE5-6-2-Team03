package grepp.NBE5_6_2_Team03.api.controller.map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import grepp.NBE5_6_2_Team03.api.controller.map.dto.MapPageDataResponse;
import grepp.NBE5_6_2_Team03.api.controller.map.dto.MapResponse;
import grepp.NBE5_6_2_Team03.api.controller.map.dto.MapSearchRequest;
import grepp.NBE5_6_2_Team03.global.response.ApiResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

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
    void testGetMapPage() throws JsonProcessingException {
        for (int i = 0; i < 5; i++) {
            MapSearchRequest searchRequest = MapSearchRequest.builder()
                .country("일본")
                .city("오사카")
                .page(i)
                .size(1)
                .build();

            ResponseEntity<ApiResponse<MapPageDataResponse>> response = controller.getMapPage(searchRequest);
            String json = objectMapper.writeValueAsString(response);
            System.out.println(json);
        }

    }

    @Test
    @DisplayName("맵 페이지 응답 코드 및 메시지 확인")
    void testMapPageResponseMetadata() throws JsonProcessingException {
        int pageNumber = 4;
        int pageSize = 5;

        MapSearchRequest searchRequest = MapSearchRequest.builder()
            .country("일본")
            .city("오사카")
            .page(pageNumber)
            .size(pageSize)
            .build();

        // when
        ResponseEntity<ApiResponse<MapPageDataResponse>> responseEntity = controller.getMapPage(searchRequest);
        String json = objectMapper.writeValueAsString(responseEntity);
        System.out.println(json);

        // Then
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK); // HttpStatus.OK
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);

        ApiResponse<MapPageDataResponse> apiResponse = responseEntity.getBody();
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.code()).isEqualTo("2000");
        assertThat(apiResponse.message()).isEqualTo("success");

    }

    @Test
    @DisplayName("맵 페이지 국가/도시 필드 확인")
    void testMapPageCountriesAndCities() {
        int pageNumber = 4;
        int pageSize = 5;

        MapSearchRequest searchRequest = MapSearchRequest.builder()
            .country("일본")
            .city("오사카")
            .page(pageNumber)
            .size(pageSize)
            .build();

        ResponseEntity<ApiResponse<MapPageDataResponse>> responseEntity = controller.getMapPage(searchRequest);
        ApiResponse<MapPageDataResponse> apiResponse = responseEntity.getBody();
        // data 필드 검증
        MapPageDataResponse dataContent = apiResponse.data();
        assertThat(dataContent).isNotNull();
        assertThat(dataContent.getCountries()).containsExactly("일본", "태국");
        assertThat(dataContent.getCities()).containsExactly(
            "도쿄", "오사카", "교토", "방콕", "치앙마이", "푸켓"
        );
    }

    @Test
    @DisplayName("맵 페이지 페이징 정보(Page 객체) 검증")
    void testMapPagePageObject() {
        int pageNumber = 4;
        int pageSize = 5;

        MapSearchRequest searchRequest = MapSearchRequest.builder()
            .country("일본")
            .city("오사카")
            .page(pageNumber)
            .size(pageSize)
            .build();

        ResponseEntity<ApiResponse<MapPageDataResponse>> responseEntity = controller.getMapPage(searchRequest);
        ApiResponse<MapPageDataResponse> apiResponse = responseEntity.getBody();
        MapPageDataResponse dataContent = apiResponse.data();
        // mapPage 필드 검증
        Page<MapResponse> mapPage = dataContent.getMapPage();
        assertThat(mapPage).isNotNull();
        assertThat(mapPage.isLast()).isFalse();
        assertThat(mapPage.getTotalElements()).isEqualTo(56);
        assertThat(mapPage.getTotalPages()).isEqualTo(Math.ceilDiv(mapPage.getTotalElements(), pageSize));
        assertThat(mapPage.isFirst()).isFalse();
        assertThat(mapPage.getSize()).isEqualTo(pageSize);
        assertThat(mapPage.getNumber()).isEqualTo(pageNumber);
        assertThat(mapPage.getNumberOfElements()).isEqualTo(pageSize);
        assertThat(mapPage.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("맵 페이지 정렬(Sort) 정보 검증")
    void testMapPageSortInfo() {
        int pageNumber = 4;
        int pageSize = 5;

        MapSearchRequest searchRequest = MapSearchRequest.builder()
            .country("일본")
            .city("오사카")
            .page(pageNumber)
            .size(pageSize)
            .build();

        ResponseEntity<ApiResponse<MapPageDataResponse>> responseEntity = controller.getMapPage(searchRequest);
        ApiResponse<MapPageDataResponse> apiResponse = responseEntity.getBody();
        MapPageDataResponse dataContent = apiResponse.data();
        Page<MapResponse> mapPage = dataContent.getMapPage();

        // mapPage.sort 필드 검증 (Page 객체의 getSort()는 Sort 반환)
        Sort pageSort = mapPage.getSort();
        assertThat(pageSort).isNotNull();
        assertThat(pageSort.isEmpty()).isTrue();
        assertThat(pageSort.isUnsorted()).isTrue();
        assertThat(pageSort.isSorted()).isFalse();
    }

    @Test
    @DisplayName("맵 페이지 페이징(Pageable) 정보 검증")
    void testMapPagePageableInfo() {
        int pageNumber = 4;
        int pageSize = 5;

        MapSearchRequest searchRequest = MapSearchRequest.builder()
            .country("일본")
            .city("오사카")
            .page(pageNumber)
            .size(pageSize)
            .build();
        ResponseEntity<ApiResponse<MapPageDataResponse>> responseEntity = controller.getMapPage(searchRequest);
        ApiResponse<MapPageDataResponse> apiResponse = responseEntity.getBody();
        MapPageDataResponse dataContent = apiResponse.data();
        Page<MapResponse> mapPage = dataContent.getMapPage();

        // mapPage.pageable 필드 검증 (Page 객체의 getPageable()는 Pageable 반환)
        Pageable pageable = mapPage.getPageable();
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(pageable.getPageSize()).isEqualTo(pageSize);
        assertThat(pageable.getOffset()).isEqualTo((long)pageNumber * pageSize); // offset은 long 타입
        assertThat(pageable.isUnpaged()).isFalse();
        assertThat(pageable.isPaged()).isTrue();
    }

    @Test
    @DisplayName("맵 페이지 컨텐츠 리스트 검증")
    void testMapPageContentList() {
        int pageNumber = 4;
        int pageSize = 5;

        MapSearchRequest searchRequest = MapSearchRequest.builder()
            .country("일본")
            .city("오사카")
            .page(pageNumber)
            .size(pageSize)
            .build();

        ResponseEntity<ApiResponse<MapPageDataResponse>> responseEntity = controller.getMapPage(searchRequest);
        ApiResponse<MapPageDataResponse> apiResponse = responseEntity.getBody();
        MapPageDataResponse dataContent = apiResponse.data();
        Page<MapResponse> mapPage = dataContent.getMapPage();
        Pageable pageable = mapPage.getPageable();

        // mapPage.content 배열 검증
        List<MapResponse> contentList = mapPage.getContent();
        System.out.println( "call list : " + contentList);
        assertThat(contentList).isNotNull().hasSize(pageSize);
    }
}