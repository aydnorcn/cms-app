package com.aydnorcn.mis_app.utils.params;

import com.aydnorcn.mis_app.utils.PostStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PostParams extends PaginationParams {

    private String author = null;
    private String category = null;
    private List<PostStatus> statusList = new LinkedList<>();
    private LocalDateTime createAfter = null;
    private LocalDateTime createBefore = null;

    public PostParams(Map<String, Object> params) {
        super(params);
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
        if (params.containsKey("created-after"))
            createAfter = LocalDateTime.parse((String) params.get("created-after"));
        if (params.containsKey("created-before"))
            createBefore = LocalDateTime.parse((String) params.get("created-before"));
    }
}