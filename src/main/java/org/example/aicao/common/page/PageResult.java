package org.example.aicao.common.page;

import java.util.List;

/**
 * 通用分页出参：业务层只关心数据集与总数，由统一结构承载。
 */
public record PageResult<T>(List<T> items, long total, int page, int size) {

    public static <T> PageResult<T> of(List<T> items, long total, PageRequest request) {
        return new PageResult<>(items, total, request.page(), request.size());
    }
}
