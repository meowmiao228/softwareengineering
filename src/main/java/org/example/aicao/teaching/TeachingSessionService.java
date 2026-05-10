package org.example.aicao.teaching;

import org.example.aicao.common.page.PageRequest;
import org.example.aicao.common.page.PageResult;
import org.example.aicao.teaching.api.CreateSessionRequest;
import org.example.aicao.teaching.api.SessionResponse;
import org.example.aicao.teaching.domain.TeachingSession;
import org.example.aicao.teaching.domain.TeachingSubjectCodes;
import org.example.aicao.teaching.mapper.TeachingSessionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeachingSessionService {

    private final TeachingSessionMapper teachingSessionMapper;

    public TeachingSessionService(TeachingSessionMapper teachingSessionMapper) {
        this.teachingSessionMapper = teachingSessionMapper;
    }

    @Transactional
    public SessionResponse create(CreateSessionRequest request) {
        if (!TeachingSubjectCodes.isAllowed(request.subjectCode())) {
            throw new IllegalArgumentException("不支持的科目代码: " + request.subjectCode());
        }
        TeachingSession session = new TeachingSession();
        session.setTitle(request.title().trim());
        session.setSubjectCode(request.subjectCode());
        teachingSessionMapper.insert(session);
        return toResponse(requireSession(session.getId()));
    }

    public PageResult<SessionResponse> list(PageRequest pageRequest) {
        long total = teachingSessionMapper.countAll();
        List<TeachingSession> rows = teachingSessionMapper.selectPage(pageRequest.offset(), pageRequest.size());
        List<SessionResponse> items = rows.stream().map(this::toResponse).toList();
        return PageResult.of(items, total, pageRequest);
    }

    public TeachingSession requireSession(long id) {
        TeachingSession session = teachingSessionMapper.selectById(id);
        if (session == null) {
            throw new TeachingSessionNotFoundException(id);
        }
        return session;
    }

    private SessionResponse toResponse(TeachingSession session) {
        return new SessionResponse(session.getId(), session.getTitle(), session.getSubjectCode(), session.getCreatedAt());
    }
}
