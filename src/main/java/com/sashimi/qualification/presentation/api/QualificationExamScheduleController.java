package com.sashimi.qualification.presentation.api;

import com.sashimi.qualification.infrastructure.persistence.SpringDataQualificationExamScheduleRepository;
import com.sashimi.qualification.presentation.api.response.QualificationExamScheduleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/qualifications/exam-schedules")
public class QualificationExamScheduleController {

    private final SpringDataQualificationExamScheduleRepository repository;

    public QualificationExamScheduleController(
            SpringDataQualificationExamScheduleRepository repository
    ) {
        this.repository = repository;
    }

    @GetMapping("/next")
    public ResponseEntity<QualificationExamScheduleResponse> getNext(
            @RequestParam String qualificationName
    ) {
        return repository
                .findFirstByQualificationNameAndDocExamStartDateGreaterThanEqualOrderByDocExamStartDateAsc(
                        qualificationName,
                        LocalDate.now()
                )
                .map(QualificationExamScheduleResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}