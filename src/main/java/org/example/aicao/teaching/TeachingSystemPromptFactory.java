package org.example.aicao.teaching;

import org.example.aicao.config.TeachingProperties;
import org.springframework.stereotype.Component;

/**
 * 将系统提示词模板与科目代码组合；模板来自配置，避免硬编码长文案。
 */
@Component
public class TeachingSystemPromptFactory {

    private static final String SUBJECT_PLACEHOLDER = "{subject}";

    private final TeachingProperties teachingProperties;

    public TeachingSystemPromptFactory(TeachingProperties teachingProperties) {
        this.teachingProperties = teachingProperties;
    }

    public String build(String subjectCode) {
        String template = teachingProperties.systemPromptTemplate();
        if (!template.contains(SUBJECT_PLACEHOLDER)) {
            return template;
        }
        return template.replace(SUBJECT_PLACEHOLDER, subjectCode);
    }
}
