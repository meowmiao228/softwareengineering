package org.example.aicao.teaching.domain;

/**
 * 消息角色常量：集中管理，避免魔法字符串散落在 Mapper 与业务中。
 */
public final class TeachingMessageRoles {

    public static final String USER = "user";
    public static final String ASSISTANT = "assistant";
    public static final String SYSTEM = "system";

    private TeachingMessageRoles() {
    }
}
