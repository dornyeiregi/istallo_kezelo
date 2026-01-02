package edu.pte.ttk.istallo_kezelo.mapper;

import edu.pte.ttk.istallo_kezelo.dto.CalendarEventDTO;
import edu.pte.ttk.istallo_kezelo.model.CalendarEvent;

public final class CalendarEventMapper {

    private CalendarEventMapper() {}

    public static CalendarEventDTO toDTO(CalendarEvent event) {
        CalendarEventDTO dto = new CalendarEventDTO();

        dto.setId(event.getId());
        dto.setHorseId(event.getHorse().getId());
        dto.setHorseName(event.getHorse().getHorseName());
        dto.setStableId(event.getHorse().getStable().getId());
        dto.setEventType(event.getEventType());
        dto.setEventDate(event.getEventDate());
        dto.setRelatedEntityId(event.getRelatedEntityId());

        return dto;
    }
}
