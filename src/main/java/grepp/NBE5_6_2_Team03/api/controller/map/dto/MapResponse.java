package grepp.NBE5_6_2_Team03.api.controller.map.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MapResponse {
    private String id;
    private String country;
    private String city;
    private String placeName;
    private Double latitude;
    private Double longitude;

    public MapResponse(String id, String country, String city, String placeName, Double latitude, Double longitude) {
        this.id = id;
        this.country = country;
        this.city = city;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
