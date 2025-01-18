package com.aydnorcn.mis_app.dto.poll;

import com.aydnorcn.mis_app.utils.PollType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

import static com.aydnorcn.mis_app.utils.MessageConstants.*;

@Getter
public class CreatePollRequest {

    @NotBlank(message = TITLE_NOT_BLANK)
    private final String title;
    @NotBlank(message = DESCRIPTION_NOT_BLANK)
    private final String description;
    @NotNull(message = POLL_TYPE_NOT_NULL)
    private final PollType type;
    @NotNull(message = CHOICES_NOT_NULL)
    private final List<String> choices;

    @Min(value = 1, message = MAX_VOTE_COUNT_MIN)
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
