package org.example.aicao.teaching.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.aicao.teaching.domain.TeachingSession;

import java.util.List;

@Mapper
public interface TeachingSessionMapper {

    int insert(TeachingSession session);

    TeachingSession selectById(@Param("id") long id);

    long countAll();

    List<TeachingSession> selectPage(@Param("offset") int offset, @Param("limit") int limit);
}
