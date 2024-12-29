package com.aydnorcn.mis_app.dto.poll;

import com.aydnorcn.mis_app.utils.PollType;
import lombok.Getter;

import java.util.List;

@Getter
public class PatchPollRequest {

    private final String title;
    private final String description;
    private final PollType type;
    private final List<String> choices;

    public PatchPollRequest(String title, String description, PollType type, List<String> choices) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.choices = choices;
    }
}
