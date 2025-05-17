package grepp.NBE5_6_2_Team03.domain.expense.repository;

import grepp.NBE5_6_2_Team03.domain.expense.Expense;
import grepp.NBE5_6_2_Team03.domain.travelschedule.TravelSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByTravelSchedule(TravelSchedule travelSchedule);
}
