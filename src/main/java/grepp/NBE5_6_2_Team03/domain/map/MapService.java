package grepp.NBE5_6_2_Team03.domain.map;

import grepp.NBE5_6_2_Team03.api.controller.map.dto.MapPageDataResponse;
import grepp.NBE5_6_2_Team03.api.controller.map.dto.MapResponse;
import grepp.NBE5_6_2_Team03.api.controller.map.dto.MapSearchRequest;
import grepp.NBE5_6_2_Team03.domain.place.entity.Place;
import grepp.NBE5_6_2_Team03.domain.place.repository.CountryRepository;
import grepp.NBE5_6_2_Team03.domain.place.repository.PlaceRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MapService {

    private final CountryRepository countryRepository;
    private final PlaceRepository placeRepository;

    // TODO : 동작확인
    public MapPageDataResponse getMapPageData(MapSearchRequest request) {
        Page<MapResponse> mapPage = findPlaces(request.getCountry(), request.getCity(), request.getPageable());
        List<String> countries = getCountries();
        List<String> cities = determineCitiesForRequest(request);

        return MapPageDataResponse.builder()
            .mapPage(mapPage)
            .countries(countries)
            .cities(cities)
            .build();
    }

    public List<String> getCity(String country) {
        return countryRepository.findCityByCountry(country);
    }

    public List<String> getCountries() {
        return countryRepository.findAllCountries();
    }

    public List<String> getAllCities() {
        return countryRepository.findAllCities();
    }

    public MapResponse getPlace(String placeId) {
        Place place = placeRepository.findByPlaceId(placeId);
        return convertToResponse(place);
    }

    public Page<MapResponse> findPlaces(String country, String city, Pageable pageable) {
        Page<Place> places;

        switch (hasFilterOption(country, city)){
            case COUNTRY -> places = placeRepository.findByCountry(country, pageable);
            case CITY -> places = placeRepository.findByCity(city, pageable);
            default -> places = placeRepository.findAll(pageable);
        }

        log.debug("Found {} places", places.getTotalElements());
        return places.map(this::convertToResponse);
    }

    public Optional<String> getCountryByCity(String city) {
        return placeRepository.findCountryByCity(city);
    }

    private FilterOptionType hasFilterOption(String country, String city) {
        if(city != null && !city.isEmpty()) return FilterOptionType.CITY;
        if(country != null && !country.isEmpty()) return FilterOptionType.COUNTRY;
        return FilterOptionType.ALL;
    }

    private MapResponse convertToResponse(Place place) {
        return MapResponse.builder()
            .id(place.getPlaceId())
            .country(place.getCountry())
            .city(place.getCity())
            .placeName(place.getPlaceName())
            .latitude(place.getLatitude())
            .longitude(place.getLongitude())
            .build();
    }

    private List<String> determineCitiesForRequest(MapSearchRequest request) {
        String country = request.getCountry();
        String city = request.getCity();

        // 도시만 선택된 경우 (국가가 없거나 비어있음)
        if (city != null && !city.isEmpty() && (country == null || country.isEmpty())) {
            // 해당 도시의 국가를 찾아서 그 국가의 도시들을 반환
            String countryByCity = getCountryByCity(city).orElse(null);
            if (countryByCity != null) {
                return getCity(countryByCity);
            }
            return getAllCities();
        }
        // 국가가 선택된 경우
        else if (country != null && !country.isEmpty()) {
            return getCity(country);
        }
        // 필터가 없는 경우
        else {
            return getAllCities();
        }
    }
}
