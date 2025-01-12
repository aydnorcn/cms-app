package com.aydnorcn.mis_app.dto.poll;

import com.aydnorcn.mis_app.utils.PollType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class CreatePollRequest {

    @NotBlank(message = "Title cannot be null")
    private final String title;
    @NotBlank(message = "Description cannot be null")
    private final String description;
    @NotNull(message = "Type cannot be null")
    private final PollType type;
    @NotNull(message = "Choices cannot be null")
    private final List<String> choices;

    @Min(value = 1, message = "Max vote count must be greater than 0")
    private final int maxVoteCount;

    public CreatePollRequest(String title, String description, PollType type, List<String> choices, Integer maxVoteCount) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.choices = choices;
        this.maxVoteCount = calculateMaxVoteCount(type, maxVoteCount);
    }

    private int calculateMaxVoteCount(PollType type, Integer maxVoteCount) {
        if (maxVoteCount == null) {
            return type.equals(PollType.SINGLE_CHOICE) ? 1 : 2;
        }
        return Math.min(maxVoteCount, choices.size());
    }
}
