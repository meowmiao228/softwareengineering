package org.example.aicao.teaching;

public class TeachingSessionNotFoundException extends RuntimeException {

    public TeachingSessionNotFoundException(long sessionId) {
        super("教学会话不存在: " + sessionId);
    }
}
