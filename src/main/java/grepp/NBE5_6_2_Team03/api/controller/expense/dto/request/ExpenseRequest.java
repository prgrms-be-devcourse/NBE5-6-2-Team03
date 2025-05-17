package grepp.NBE5_6_2_Team03.api.controller.expense.dto.request;

import grepp.NBE5_6_2_Team03.domain.expense.Expense;
import grepp.NBE5_6_2_Team03.domain.travelschedule.TravelSchedule;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ExpenseRequest {

    @NotBlank
    private Integer expectPrice;
    private Integer payedPrice;

    public ExpenseRequest() {
    }

    public Expense toEntity(TravelSchedule schedule) {
        String currencyCode = schedule.getTravelPlan().getCountryStatus().getCode();

        return Expense.builder()
            .travelSchedule(schedule)
            .expectPrice(this.expectPrice)
            .payedPrice(this.payedPrice)
            .currency(currencyCode)
            .isCompleted(this.payedPrice != null)
            .expenseDate(schedule.getTravelScheduleDate())
            .build();
    }

    public static ExpenseRequest fromEntity(Expense expense) {
        return new ExpenseRequest(
            expense.getExpectPrice(),
            expense.getPayedPrice()
        );
    }
}
