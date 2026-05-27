package com.sashimi.course.infrastructure.persistence;

import com.sashimi.course.application.port.InstructorPort;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.user.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class InstructorPortAdapter implements InstructorPort {

    private final UserRepository userRepository;

    public InstructorPortAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String getInstructorName(Long instructorId) {
        return userRepository.findById(instructorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getName();
    }
}