package com.aydnorcn.mis_app.utils.params.commons;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.Map;

@Getter
@Setter
public class PaginationParams {

    private int pageNo = 0;
    private int pageSize = 10;
    private String sortBy = "id";
    private String sortOrder = "desc";

    private Sort sort;

    public PaginationParams(Map<String, Object> params) {
        if (params.containsKey("page-no")) pageNo = Integer.parseInt((String) params.get("page-no"));
        if (params.containsKey("page-size")) pageSize = Integer.parseInt((String) params.get("page-size"));
        if (params.containsKey("sort-by")) sortBy = (String) params.get("sort-by");
        if (params.containsKey("sort-order")) sortOrder = (String) params.get("sort-order");

        sort = getSortOrder().equalsIgnoreCase("asc")
                ? Sort.by(getSortBy()).ascending()
                : Sort.by(getSortBy()).descending();
    }
}