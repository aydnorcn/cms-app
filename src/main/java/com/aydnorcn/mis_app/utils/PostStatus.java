package com.aydnorcn.mis_app.utils;

import static com.aydnorcn.mis_app.utils.PollType.MULTIPLE_CHOICE;

public enum PostStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public static PostStatus fromString(String type) {
        for (PostStatus postStatus : PostStatus.values()) {
            if (postStatus.name().equalsIgnoreCase(type)) {
                return postStatus;
            }
        }
        if(type.equalsIgnoreCase("pending")) {
            return PENDING;
        } else if(type.equalsIgnoreCase("approved")) {
            return APPROVED;
        } else if(type.equalsIgnoreCase("rejected")){
            return REJECTED;
        }
        return null;
    }
}
