package com.sashimi.course.infrastructure.persistence;

import com.sashimi.course.application.port.InstructorPort;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InstructorPortAdapter implements InstructorPort {

    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public String getInstructorName(Long instructorId) {
        return userRepository.findById(instructorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getName();
    }

    @Override
    public String getInstructorEmail(Long instructorId) {
        return userRepository.findById(instructorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getEmail();
    }

    @Override
    public String getInstructorLoginId(Long instructorId) {
        return userRepository.findById(instructorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getLoginId();
    }

    @Override
    public InstructorInfo getInstructorInfo(Long instructorId) {
        String name = getInstructorName(instructorId);

        return jdbcTemplate.query(
                """
                SELECT profile_image_path, bio, main_careers, portfolio_url
                FROM instructor_profiles
                WHERE user_id = ? AND approval_status = 'APPROVED'
                ORDER BY approved_at DESC
                LIMIT 1
                """,
                rs -> {
                    if (rs.next()) {
                        String mainCareersJson = rs.getString("main_careers");
                        List<String> mainCareers = parseJsonArray(mainCareersJson);
                        return new InstructorInfo(
                                name,
                                rs.getString("profile_image_path"),
                                rs.getString("bio"),
                                mainCareers,
                                rs.getString("portfolio_url")
                        );
                    }
                    return new InstructorInfo(name, null, null, List.of(), null);
                },
                instructorId
        );
    }

    private List<String> parseJsonArray(String json) {
        if (json == null || json.isBlank() || json.equals("[]")) return List.of();
        String stripped = json.strip().replaceAll("^\\[|]$", "");
        return Arrays.stream(stripped.split(","))
                .map(s -> s.strip().replaceAll("^\"|\"$", ""))
                .filter(s -> !s.isBlank())
                .toList();
    }
}