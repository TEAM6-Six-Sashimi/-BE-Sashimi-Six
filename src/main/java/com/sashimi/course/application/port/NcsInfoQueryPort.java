package com.sashimi.course.application.port;

import java.util.Optional;

public interface NcsInfoQueryPort {
    Optional<NcsInfoView> findViewByRepresentativeId(Long ncsInfoId);
}