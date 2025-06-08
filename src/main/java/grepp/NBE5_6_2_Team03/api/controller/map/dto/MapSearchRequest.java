package grepp.NBE5_6_2_Team03.api.controller.map.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter @Setter
@Builder
public class MapSearchRequest {

    private final String country;
    private final String city;

    @Min(value = 0, message = "페이지는 0 이상이어야 합니다")
    private final int page;

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    private final int size;

    public MapSearchRequest(String country, String city, Integer page, Integer size) {
        this.country = country;
        this.city = city;
        this.page = page != null ? Math.max(0, page) : 0;
        this.size = size != null ? Math.min(Math.max(1, size), 100) : 20;
    }

    public Pageable getPageable() {
        return PageRequest.of(this.page, this.size);
    }

    public boolean hasCountryOnly() {
        return hasValidString(country) && !hasValidString(city);
    }

    public boolean hasCityOnly() {
        return hasValidString(city) && !hasValidString(country);
    }

    public boolean hasNoFilter() {
        return !hasValidString(country) && !hasValidString(city);
    }

    private boolean hasValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
