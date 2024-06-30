package com.crm.smsmanagementservice.util.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author : memo-aldu
 * @mailto : maldu064@uOttawa.ca
 * @created : 6/1/2024, Saturday
 */

public class PageableHelper {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;
    public static final String DEFAULT_SORT = "createdTime";
    public static final String DEFAULT_DIRECTION = "desc";

    public static Pageable createPage(int page, int size, String sortBy, String order) {
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = DEFAULT_SORT;
        }

        if (order == null || order.isEmpty() || (!order.equals("asc") && !order.equals("desc"))) {
            order = DEFAULT_DIRECTION;
        }

        return PageRequest.of(
                page < 0 ? DEFAULT_PAGE : page,
                size < 1 ? DEFAULT_SIZE : size,
                Sort.by(Sort.Direction.fromString(order), sortBy)
        );
    }
}
