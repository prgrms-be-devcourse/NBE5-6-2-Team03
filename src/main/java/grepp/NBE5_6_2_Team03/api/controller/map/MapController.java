package grepp.NBE5_6_2_Team03.api.controller.map;

import grepp.NBE5_6_2_Team03.api.controller.map.dto.MapPageDataResponse;
import grepp.NBE5_6_2_Team03.api.controller.map.dto.MapResponse;
import grepp.NBE5_6_2_Team03.api.controller.map.dto.MapSearchRequest;
import grepp.NBE5_6_2_Team03.domain.map.MapService;
import grepp.NBE5_6_2_Team03.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @GetMapping("/map")
    public ResponseEntity<ApiResponse<MapPageDataResponse>> getMapPage(@ModelAttribute MapSearchRequest request) {
        String resolvedCity = request.getCity();
        String resolvedCountry = request.getCountry();

        List<String> cities;

        if (request.hasCityOnly()) {
            resolvedCountry = mapService.getCountryByCity(resolvedCity).orElse(null);
            cities = mapService.getCity(resolvedCountry);
        } else if (request.hasCountryOnly()) {
            cities = mapService.getCity(resolvedCountry);
        } else {
            cities = mapService.getAllCities();
        }

        Page<MapResponse> mapPage = mapService.findPlaces(resolvedCountry, resolvedCity, request.getPageable());
        List<String> countries = mapService.getCountries();

        MapPageDataResponse pageData = MapPageDataResponse.builder()
            .mapPage(mapPage)
            .countries(countries)
            .cities(cities)
            .selectedCountry(resolvedCountry)
            .selectedCity(resolvedCity)
            .build();

        return ResponseEntity.ok(ApiResponse.success(pageData));
    }

//    @GetMapping("/map")
//    public ResponseEntity<ApiResponse<MapPageDataResponse>> getMapPage(Model model, MapSearchRequest request) {
//        MapPageDataResponse pageData = mapService.getMapPageData(request);
//        model.addAllAttributes(pageData.toModelAttributes());
//        return ResponseEntity.ok(ApiResponse.success(pageData));
//    }

    // 프론트가 분리된 경우 서버에서 리다이랙트를 해줄 필요가 없음
    @GetMapping("/home-redirect")
    public ResponseEntity<ApiResponse<String>> redirectBasedOnAuthStatus(Authentication authentication) {
        String redirectUrl = isNotAnonymous(authentication) ? "/users/home" : "/main";
        return ResponseEntity.ok(ApiResponse.success(redirectUrl));
    }

    private boolean isNotAnonymous(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private boolean hasCityRequest(String countryRequest, String cityRequest) {
        return cityRequest != null && (countryRequest == null || countryRequest.isEmpty());
    }

    private boolean hasCountryRequest(String countryRequest) {
        return countryRequest != null && !countryRequest.isEmpty();
    }

}
