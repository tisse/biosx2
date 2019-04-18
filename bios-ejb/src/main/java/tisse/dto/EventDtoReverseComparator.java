package tisse.dto;

import java.util.Comparator;

public class EventDtoReverseComparator implements Comparator<EventDto> {

    @Override
    public int compare(EventDto o1, EventDto o2) {
        return Comparator
                .comparing(EventDto::getLastName)
                .thenComparing(EventDto::getDateLocal)
                .compare(o1, o2);
    }

}
