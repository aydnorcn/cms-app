package com.aydnorcn.mis_app.dto.option;

import com.aydnorcn.mis_app.entity.Option;
import lombok.Getter;

@Getter
public class OptionResponse {

    private final String id;
    private final String text;
    private final int voteCount;

    public OptionResponse(Option option) {
        this.id = option.getId();
        this.text = option.getText();
        this.voteCount = option.getVotes().size();
    }
}
