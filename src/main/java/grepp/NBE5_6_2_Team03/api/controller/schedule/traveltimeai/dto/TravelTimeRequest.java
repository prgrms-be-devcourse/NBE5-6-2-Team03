package grepp.NBE5_6_2_Team03.api.controller.schedule.traveltimeai.dto;

import lombok.Data;

@Data
public class TravelTimeRequest {
    private String departure;
    private String destination;
    private String transport;
}
