package grepp.NBE5_6_2_Team03.domain.place.repository;

import grepp.NBE5_6_2_Team03.domain.place.entity.Place;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceRepository extends JpaRepository<Place, String>, PlaceRepositoryCustom {

    Place findByPlaceId(String placeId);

    @Query("select distinct p.country from Place p order by p.country")
    List<String> findDistinctCountries();

    @Query("select distinct p.city from Place p order by p.city")
    List<String> findDistinctCities();

    @Query("select p from Place p where p.city = :city")
    Page<Place> findByCity(@Param("city") String city, Pageable pageable);

    @Query("select p from Place p where p.country = :country")
    Page<Place> findByCountry(@Param("country") String country, Pageable pageable);

    List<Place> findAll();

    List<Place> findByCountry(String country);

    List<Place> findByCity(String city);

    @Query("select distinct p.country from Place p where p.city = :city")
    Optional<String> findCountryByCity(@Param("city") String city);
}
