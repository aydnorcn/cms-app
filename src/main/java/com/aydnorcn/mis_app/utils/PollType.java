package com.aydnorcn.mis_app.utils;

public enum PollType {
    SINGLE_CHOICE,
    MULTIPLE_CHOICE;

    public static PollType fromString(String type) {
        for (PollType pollType : PollType.values()) {
            if (pollType.name().equalsIgnoreCase(type)) {
                return pollType;
            }
        }
        if(type.equalsIgnoreCase("single")) {
            return SINGLE_CHOICE;
        } else if(type.equalsIgnoreCase("multiple")) {
            return MULTIPLE_CHOICE;
        }
        return null;
    }
}
