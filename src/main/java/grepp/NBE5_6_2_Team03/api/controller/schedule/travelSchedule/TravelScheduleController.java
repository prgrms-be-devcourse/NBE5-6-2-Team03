package grepp.NBE5_6_2_Team03.api.controller.schedule.travelSchedule;

import grepp.NBE5_6_2_Team03.api.controller.schedule.travelSchedule.dto.request.TravelScheduleRequest;
import grepp.NBE5_6_2_Team03.domain.travelschedule.TravelSchedule;
import grepp.NBE5_6_2_Team03.domain.travelschedule.ScheduleStatus;
import grepp.NBE5_6_2_Team03.domain.travelschedule.service.TravelScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/plan/{travelPlanId}/schedule")
public class TravelScheduleController {

    private final TravelScheduleService travelScheduleService;

    @GetMapping
    public String list(@PathVariable Long travelPlanId, Model model) {
        Map<LocalDate, Map<ScheduleStatus, List<TravelSchedule>>> groupedSchedules = travelScheduleService.getGroupedSchedules(travelPlanId);
        model.addAttribute("groupedSchedules", groupedSchedules);
        model.addAttribute("travelPlanId", travelPlanId);
        return "schedule/schedule-list";
    }

    @GetMapping("/add")
    public String addForm(@PathVariable Long travelPlanId, Model model) {
        model.addAttribute("travelPlanId", travelPlanId);
        model.addAttribute("request", new TravelScheduleRequest());
        return "schedule/schedule-form";
    }

    @PostMapping("/add")
    public String addSchedule(@PathVariable Long travelPlanId,
                              @ModelAttribute TravelScheduleRequest request,
                              Model model) {
        model.addAttribute("travelPlanId", travelPlanId);

        try {
            travelScheduleService.addSchedule(travelPlanId, request);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("request", request);
            return "schedule/schedule-form";
        }

        return "redirect:/plan/" + travelPlanId + "/schedule";
    }

    @GetMapping("/{travelScheduleId}/edit")
    public String editForm(@PathVariable Long travelPlanId,
                           @PathVariable Long travelScheduleId,
                           Model model) {
        TravelSchedule schedule = travelScheduleService.findById(travelScheduleId);

        model.addAttribute("travelPlanId", travelPlanId);
        model.addAttribute("travelScheduleId", travelScheduleId);
        model.addAttribute("request", TravelScheduleRequest.fromEntity(schedule));
        return "schedule/schedule-form";
    }

    @PostMapping("/{travelScheduleId}/edit")
    public String editSchedule(@PathVariable Long travelPlanId,
                               @PathVariable Long travelScheduleId,
                               @ModelAttribute TravelScheduleRequest request,
                               Model model) {
        model.addAttribute("travelPlanId", travelPlanId);

        try {
            travelScheduleService.editSchedule(travelScheduleId, request);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("request", request);
            return "schedule/schedule-form";
        }

        return "redirect:/plan/" + travelPlanId + "/schedule";
    }

    @PostMapping("/{travelScheduleId}/delete")
    public String deleteSchedule(@PathVariable Long travelPlanId,
                                 @PathVariable Long travelScheduleId) {
        travelScheduleService.deleteSchedule(travelScheduleId);
        return "redirect:/plan/" + travelPlanId + "/schedule";
    }

    @PostMapping("/{travelScheduleId}/status")
    public String scheduleStatus(@PathVariable Long travelPlanId,
                                 @PathVariable Long travelScheduleId) {
        travelScheduleService.scheduleStatus(travelScheduleId);
        return "redirect:/plan/" + travelPlanId + "/schedule";
    }
}
