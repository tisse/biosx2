package tisse.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum EventType {

    EVENT_IN(151),
    EVENT_OUT(152),
    EVENT_CARD_IN(153),
    EVENT_CARD_OUT(154),
    EVENT_TO_LAUNCH(202),
    EVENT_FROM_LAUNCH(203);

    private int code;

    EventType(int code) {
        this.code = code;
    }

    public static EventType getByCode(int code) {
        EventType type = null;
        for (EventType eventType : values()) {
            if (eventType.code == code) {
                type = eventType;
                break;
            }
        }
        return type;
    }

    public static List<Integer> getEventTypesIn() {
        return new ArrayList<>(Arrays.asList(EVENT_CARD_IN.getCode(), EVENT_IN.getCode()));
    }

    public static List<Integer> getEventTypesOut() {
        return new ArrayList<>(Arrays.asList(EVENT_CARD_OUT.getCode(), EVENT_OUT.getCode()));
    }

    public int getCode() {
        return code;
    }

}
