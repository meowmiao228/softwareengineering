package org.example.aicao.common.page;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 通用分页入参：与具体业务解耦，由 Web 层绑定查询参数。
 */
public record PageRequest(
        @Min(0) int page,
        @Min(1) @Max(100) int size
) {
    public static final int DEFAULT_SIZE = 20;

    public PageRequest {
        if (size == 0) {
            size = DEFAULT_SIZE;
        }
    }

    public int offset() {
        return page * size;
    }

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size == 0 ? DEFAULT_SIZE : size);
    }
}
