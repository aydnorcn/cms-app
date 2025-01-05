package com.aydnorcn.mis_app.utils;

public enum EventStatus {
    FINISHED,
    UPCOMING,
    ONGOING;

    public static EventStatus fromString(String status) {
        for (EventStatus eventStatus : EventStatus.values()) {
            if (eventStatus.name().equalsIgnoreCase(status)) {
                return eventStatus;
            }
        }
        return null;
    }
}
