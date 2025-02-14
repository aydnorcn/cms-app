package com.aydnorcn.mis_app.utils.params;

import com.aydnorcn.mis_app.utils.PostStatus;
import com.aydnorcn.mis_app.utils.params.commons.CreatedDateRangeParams;
import com.aydnorcn.mis_app.utils.params.commons.PaginationParams;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PostParams extends PaginationParams {

    private String author = null;
    private String category = null;
    private List<PostStatus> statusList = new LinkedList<>();

    @JsonUnwrapped
    private CreatedDateRangeParams createdDateRangeParams;

    public PostParams(Map<String, Object> params) {
        super(params);
        createdDateRangeParams = new CreatedDateRangeParams(params);
        if (params.containsKey("author")) author = (String) params.get("author");
        if (params.containsKey("category")) category = (String) params.get("category");
        if (params.containsKey("status")) {
            String[] statuses = ((String) params.get("status")).split(",");
            for (String status : statuses) {
                statusList.add(PostStatus.fromString(status.trim()));
            }
        } else {
            statusList.add(PostStatus.APPROVED);
        }
    }
}