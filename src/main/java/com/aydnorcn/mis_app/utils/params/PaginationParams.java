package com.aydnorcn.mis_app.utils.params;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PaginationParams {

    private int pageNo = 0;
    private int pageSize = 10;
    private String sortBy = "id";
    private String sortOrder = "desc";

    public PaginationParams(Map<String, Object> params) {
        if (params.containsKey("page-no")) pageNo = Integer.parseInt((String) params.get("page-no"));
        if (params.containsKey("page-size")) pageSize = Integer.parseInt((String) params.get("page-size"));
        if (params.containsKey("sort-by")) sortBy = (String) params.get("sort-by");
        if (params.containsKey("sort-order")) sortOrder = (String) params.get("sort-order");
    }

    public PaginationParams(){

    }
}