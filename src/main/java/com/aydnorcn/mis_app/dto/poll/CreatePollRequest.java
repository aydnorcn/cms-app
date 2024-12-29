package com.aydnorcn.mis_app.dto.poll;

import com.aydnorcn.mis_app.utils.PollType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class CreatePollRequest {

    @NotNull(message = "Title cannot be null")
    private final String title;
    @NotNull(message = "Description cannot be null")
    private final String description;
    @NotNull(message = "Type cannot be null")
    private final PollType type;
    @NotNull(message = "Choices cannot be null")
    private final List<String> choices;

    public CreatePollRequest(String title, String description, PollType type, List<String> choices) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.choices = choices;
    }
}
