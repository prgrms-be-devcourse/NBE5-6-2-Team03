package grepp.NBE5_6_2_Team03.domain.expense;

import grepp.NBE5_6_2_Team03.domain.BaseEntity;
import grepp.NBE5_6_2_Team03.domain.travelschedule.TravelSchedule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class Expense extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseId;

    @OneToOne
    @JoinColumn(name = "travelScheduleId")
    private TravelSchedule travelSchedule;

    private Integer expectPrice;
    private Integer payedPrice;
    private String currency;
    private boolean isCompleted;
    private LocalDate expenseDate;

    protected Expense() {
    }

    public void edit(Integer expectPrice, Integer payedPrice) {
        this.expectPrice = expectPrice;
        this.payedPrice = payedPrice;
        this.isCompleted = (payedPrice != null);
    }
}
