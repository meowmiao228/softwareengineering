package org.example.aicao.teaching.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.aicao.teaching.domain.TeachingMessage;

import java.util.List;

@Mapper
public interface TeachingMessageMapper {

    int insert(TeachingMessage message);

    long countBySessionId(@Param("sessionId") long sessionId);

    List<TeachingMessage> selectPageBySessionId(
            @Param("sessionId") long sessionId,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    /**
     * 按时间倒序取最近若干条，用于拼装模型上下文（调用方再反转为时间正序）。
     */
    List<TeachingMessage> selectLatestBySessionId(
            @Param("sessionId") long sessionId,
            @Param("limit") int limit
    );
}
