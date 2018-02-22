package com.superaligator.konferencja.models;

import java.util.List;

public class EventsResponse {
    public List<RawEvent> events;

    public class RawEvent {
        public String eventId;
        public String title;
        public String type;
        public String description;
        public String accessCode;
    }
}

