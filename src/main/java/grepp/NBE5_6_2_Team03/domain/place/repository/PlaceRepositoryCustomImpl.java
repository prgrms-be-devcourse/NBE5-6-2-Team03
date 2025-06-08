package grepp.NBE5_6_2_Team03.domain.place.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import grepp.NBE5_6_2_Team03.domain.place.entity.Place;
import grepp.NBE5_6_2_Team03.domain.place.entity.QPlace;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class PlaceRepositoryCustomImpl implements PlaceRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QPlace place = QPlace.place;

    @Override
    public Page<Place> findPaged(String country, String city, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (country != null && !country.isBlank()) {
            builder.and(place.country.eq(country));
        }
        if (city != null && !city.isBlank()) {
            builder.and(place.city.eq(city));
        }

        List<Place> content = jpaQueryFactory
            .selectFrom(place)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = jpaQueryFactory
            .selectFrom(place)
            .where(builder)
            .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }
}
