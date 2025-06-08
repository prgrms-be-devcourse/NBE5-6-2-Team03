package grepp.NBE5_6_2_Team03.domain.place.repository;

import grepp.NBE5_6_2_Team03.domain.place.entity.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlaceRepositoryCustom {
    Page<Place> findPaged(String country, String city, Pageable pageable);
}
