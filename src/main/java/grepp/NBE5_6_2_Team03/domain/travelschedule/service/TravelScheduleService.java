package grepp.NBE5_6_2_Team03.domain.travelschedule.service;

import grepp.NBE5_6_2_Team03.api.controller.schedule.travelSchedule.dto.request.TravelScheduleRequest;
import grepp.NBE5_6_2_Team03.domain.travelplan.TravelPlan;
import grepp.NBE5_6_2_Team03.domain.travelplan.repository.TravelPlanRepository;
import grepp.NBE5_6_2_Team03.domain.travelschedule.TravelRoute;
import grepp.NBE5_6_2_Team03.domain.travelschedule.TravelSchedule;
import grepp.NBE5_6_2_Team03.domain.travelschedule.ScheduleStatus;
import grepp.NBE5_6_2_Team03.domain.travelschedule.repository.TravelScheduleRepository;
import grepp.NBE5_6_2_Team03.global.exception.Message;
import grepp.NBE5_6_2_Team03.global.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TravelScheduleService {

    private final TravelScheduleRepository travelScheduleRepository;
    private final TravelPlanRepository travelPlanRepository;

    @Transactional
    public void addSchedule(Long travelPlanId, TravelScheduleRequest request) {
        TravelPlan plan = travelPlanRepository.findById(travelPlanId)
            .orElseThrow(() -> new NotFoundException(Message.PLANNED_NOT_FOUND));

        validateTravelSchedule(request.getDeparture(), request.getDestination(), request.getTransportation(), request.getTravelScheduleDate(), plan.getTravelStartDate(), plan.getTravelEndDate());

        TravelSchedule schedule = request.toEntity(plan);
        travelScheduleRepository.save(schedule);
    }

    @Transactional
    public Long editSchedule(Long travelScheduleId, TravelScheduleRequest request) {
        TravelSchedule schedule = travelScheduleRepository.findById(travelScheduleId)
            .orElseThrow(() -> new NotFoundException(Message.SCHEDULE_NOT_FOUND));

        TravelPlan plan = schedule.getTravelPlan();
        validateTravelSchedule(request.getDeparture(), request.getDestination(), request.getTransportation(), request.getTravelScheduleDate(), plan.getTravelStartDate(), plan.getTravelEndDate());

        TravelRoute travelRoute = new TravelRoute(request.getDeparture(), request.getDestination(), request.getTransportation());

        schedule.edit(
            travelRoute,
            request.getContent(),
            request.getPlaceName(),
            request.getTravelScheduleDate()
        );

        return schedule.getTravelPlan().getTravelPlanId();
    }

    @Transactional
    public void deleteSchedule(Long travelScheduleId) {
        TravelSchedule schedule = travelScheduleRepository.findById(travelScheduleId)
            .orElseThrow(() -> new NotFoundException(Message.SCHEDULE_NOT_FOUND));

        travelScheduleRepository.delete(schedule);
    }

    @Transactional
    public void scheduleStatus(Long travelScheduleId) {
        TravelSchedule schedule = travelScheduleRepository.findById(travelScheduleId)
            .orElseThrow(() -> new NotFoundException(Message.SCHEDULE_NOT_FOUND));

        schedule.toggleStatus();
    }

    public Map<LocalDate, Map<ScheduleStatus, List<TravelSchedule>>> getGroupedSchedules(Long travelPlanId) {
        TravelPlan plan = travelPlanRepository.findById(travelPlanId)
            .orElseThrow(() -> new NotFoundException(Message.PLANNED_NOT_FOUND));

        List<TravelSchedule> schedules = travelScheduleRepository.findSortedSchedules(plan);
        Map<LocalDate, Map<ScheduleStatus, List<TravelSchedule>>> groupedSchedules = new LinkedHashMap<>();

        for (TravelSchedule schedule : schedules) {
            LocalDate date = schedule.getTravelScheduleDate();
            ScheduleStatus status = schedule.getScheduleStatus();

            groupedSchedules.putIfAbsent(date, new LinkedHashMap<>());
            groupedSchedules.get(date).putIfAbsent(status, new ArrayList<>());
            groupedSchedules.get(date).get(status).add(schedule);
        }

        return groupedSchedules;
    }

    public TravelSchedule findById(Long travelScheduleId) {
        return travelScheduleRepository.findById(travelScheduleId)
            .orElseThrow(() -> new NotFoundException(Message.SCHEDULE_NOT_FOUND));
    }

    private void validateTravelSchedule(String departure, String destination, String transportation, LocalDate travelScheduleDate, LocalDate travelStartDate, LocalDate travelEndDate) {
        boolean departureExists = departure != null && !departure.isBlank();
        boolean destinationExists = destination != null && !destination.isBlank();
        boolean transportationExists = transportation != null && !transportation.isBlank();

        if (!(departureExists == destinationExists && destinationExists == transportationExists)) {
            throw new IllegalArgumentException("출발지, 도착지, 이동수단은 모두 입력하거나 모두 비워야 합니다.");
        }

        if (travelScheduleDate.isBefore(travelStartDate) || travelScheduleDate.isAfter(travelEndDate)) {
            throw new IllegalArgumentException("여행 일정 날짜는 여행 계획 날짜 안에 포함되어야 합니다.");
        }
    }
}
