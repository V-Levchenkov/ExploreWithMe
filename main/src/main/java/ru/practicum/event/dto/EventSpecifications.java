package ru.practicum.event.dto;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.event.model.State;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventForSpecific;
import ru.practicum.utilits.DateFormatterCustom;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventSpecifications {

    private static final DateFormatterCustom FORMATTER = new DateFormatterCustom();

    public static Specification<Event> getUserSpecifications(EventForSpecific criteria) {
        return (textIn("annotation", criteria.getText())
                .or(textIn("description", criteria.getText())))
                .and(idsIn(criteria.getCategories(), "category"))
                .and(isPaid(criteria.getPaid()))
                .and(dateBetween(FORMATTER.stringToDate(criteria.getRangeStart()),
                        FORMATTER.stringToDate(criteria.getRangeEnd())))
                .and(isAvailable(criteria.isOnlyAvailable()))
                .and(stateIs(List.of(State.PUBLISHED)));
    }

    public static Specification<Event> getAdminSpecifications(EventForSpecific criteria) {
        return idsIn(criteria.getUsers(), "initiator")
                .and(stateIs(stateMapping(criteria.getStates())))
                .and(idsIn(criteria.getCategories(), "category"))
                .and(dateBetween(FORMATTER.stringToDate(criteria.getRangeStart()),
                        FORMATTER.stringToDate(criteria.getRangeEnd())));
    }

    private static Specification<Event> textIn(String field, String text) {
        return (root, query, builder) -> {
            if (text == null || text.length() < 2) {
                return builder.conjunction();
            }
            return builder.like(builder.lower(root.get(field)), "%" + text.toLowerCase() + "%");
        };
    }

    private static Specification<Event> idsIn(Long[] ids, String column) {
        return (root, query, builder) -> {
            if (ids == null) {
                return builder.conjunction();
            }
            return root.get(column).in(Arrays.asList(ids));
        };
    }

    private static Specification<Event> isPaid(boolean paid) {
        return (root, query, builder) -> builder.equal(root.get("paid"), paid);
    }

    private static Specification<Event> dateBetween(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        return (root, query, builder) -> {
            if (rangeStart == null || rangeEnd == null) {
                return builder.greaterThan(root.get("eventDate"), LocalDateTime.now());
            }
            return builder.between(root.get("eventDate"), rangeStart, rangeEnd);
        };
    }

    private static Specification<Event> isAvailable(boolean available) {
        return (root, query, builder) -> {
            if (available) {
                return builder.equal(root.get("confirmedRequests"), root.get("participantLimit"));
            }
            return builder.lessThan(root.get("confirmedRequests"), root.get("participantLimit"));
        };
    }

    private static Specification<Event> stateIs(List<State> states) {
        return (root, query, builder) -> {
            if (states == null) {
                return builder.conjunction();
            }
            return root.get("state").in(states);
        };
    }

    private static List<State> stateMapping(String[] strings) {
        List<State> states = new ArrayList<>();
        for (String string : strings) {
            if (string.equals(State.valueOf(string).toString())) {
                states.add(State.valueOf(string));
            }
        }
        return states;
    }
}
