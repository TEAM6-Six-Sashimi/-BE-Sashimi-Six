package com.sashimi.instructorapplication.application.usecase;

import com.sashimi.instructorapplication.presentation.api.response.InstructorApplicationDetailResponse;
import com.sashimi.instructorapplication.presentation.api.response.InstructorApplicationListResponse;
import com.sashimi.instructorapplication.presentation.api.response.InstructorProfileResponse;
import com.sashimi.instructorapplication.presentation.api.response.MyInstructorApplicationDetailResponse;
import com.sashimi.instructorapplication.presentation.api.response.MyInstructorApplicationListResponse;
import com.sashimi.instructorapplication.presentation.api.response.RejectedApplicationListResponse;

import java.util.List;

public interface InstructorApplicationQueryUseCase {

    List<InstructorApplicationListResponse> getPendingInstructorApplications();

    InstructorApplicationDetailResponse getInstructorApplicationDetail(Long applicationId);

    List<MyInstructorApplicationListResponse> getMyInstructorApplications(Long userId);

    MyInstructorApplicationDetailResponse getMyInstructorApplicationDetail(Long userId, Long applicationId);

    InstructorProfileResponse getMyInstructorProfile(Long userId);

    List<RejectedApplicationListResponse> getRejectedInstructorApplications();
}
