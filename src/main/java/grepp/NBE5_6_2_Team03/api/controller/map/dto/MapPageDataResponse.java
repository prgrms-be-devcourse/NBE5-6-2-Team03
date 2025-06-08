package grepp.NBE5_6_2_Team03.api.controller.map.dto;


import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class MapPageDataResponse {

    private final Page<MapResponse> mapPage;
    private final List<String> countries;
    private final List<String> cities;
    private final String selectedCountry;
    private final String selectedCity;

    public Map<String, Object> toModelAttributes() {
        return Map.of(
            "mapPage", mapPage,
            "countries", countries,
            "cities", cities,
            "selectedCountry", selectedCountry != null ? selectedCountry : "",
            "selectedCity", selectedCity != null ? selectedCity : ""
        );
    }
}
