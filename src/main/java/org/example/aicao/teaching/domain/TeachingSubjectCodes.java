package org.example.aicao.teaching.domain;

import java.util.Set;

/**
 * 允许的教学科目代码：与前端下拉选项保持一致，服务端做白名单校验。
 */
public final class TeachingSubjectCodes {

    public static final String MATH = "MATH";
    public static final String CHINESE = "CHINESE";
    public static final String ENGLISH = "ENGLISH";
    public static final String PHYSICS = "PHYSICS";
    public static final String CHEMISTRY = "CHEMISTRY";
    public static final String CS = "CS";
    public static final String GENERAL = "GENERAL";

    private static final Set<String> ALLOWED = Set.of(
            MATH, CHINESE, ENGLISH, PHYSICS, CHEMISTRY, CS, GENERAL
    );

    private TeachingSubjectCodes() {
    }

    public static boolean isAllowed(String code) {
        return code != null && ALLOWED.contains(code);
    }
}
