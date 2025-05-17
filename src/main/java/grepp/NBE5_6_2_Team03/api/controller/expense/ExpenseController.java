package grepp.NBE5_6_2_Team03.api.controller.expense;

import grepp.NBE5_6_2_Team03.api.controller.expense.dto.request.ExpenseRequest;
import grepp.NBE5_6_2_Team03.domain.expense.Expense;
import grepp.NBE5_6_2_Team03.domain.expense.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/plan/{travelPlanId}/schedule/{travelScheduleId}/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public String list(@PathVariable Long travelPlanId,
                       @PathVariable Long travelScheduleId,
                       Model model) {
        Expense expense = expenseService.findByScheduleId(travelScheduleId).orElse(null);
        model.addAttribute("expense", expense);
        model.addAttribute("travelPlanId", travelPlanId);
        model.addAttribute("travelScheduleId", travelScheduleId);
        return "expense/expense-list";
    }

    @GetMapping("/add")
    public String addForm(@PathVariable Long travelPlanId,
                          @PathVariable Long travelScheduleId,
                          Model model) {
        model.addAttribute("travelPlanId", travelPlanId);
        model.addAttribute("travelScheduleId", travelScheduleId);
        model.addAttribute("request", new ExpenseRequest());
        return "expense/expense-form";
    }

    @PostMapping("/add")
    public String addExpense(@PathVariable Long travelPlanId,
                              @PathVariable Long travelScheduleId,
                              @ModelAttribute ExpenseRequest request) {
        expenseService.addExpense(travelScheduleId, request);
        return "redirect:/plan/" + travelPlanId + "/schedule/" + travelScheduleId + "/expense";
    }

    @GetMapping("/{expenseId}/edit")
    public String editForm(@PathVariable Long travelPlanId,
                           @PathVariable Long travelScheduleId,
                           @PathVariable Long expenseId,
                           Model model) {
        Expense expense = expenseService.findById(expenseId);

        model.addAttribute("travelPlanId", travelPlanId);
        model.addAttribute("travelScheduleId", travelScheduleId);
        model.addAttribute("expenseId", expenseId);
        model.addAttribute("request", ExpenseRequest.fromEntity(expense));
        return "expense/expense-form";
    }

    @PostMapping("/{expenseId}/edit")
    public String editExpense(@PathVariable Long travelPlanId,
                               @PathVariable Long travelScheduleId,
                               @PathVariable Long expenseId,
                               @ModelAttribute ExpenseRequest request) {
        expenseService.editExpense(expenseId, request);
        return "redirect:/plan/" + travelPlanId + "/schedule/" + travelScheduleId + "/expense";
    }

    @PostMapping("/{expenseId}/delete")
    public String deleteExpense(@PathVariable Long travelPlanId,
                                @PathVariable Long travelScheduleId,
                                @PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return "redirect:/plan/" + travelPlanId + "/schedule/" + travelScheduleId + "/expense";
    }
}
