package com.sashimi;

import com.sashimi.ncs.infrastructure.publicdata.NcsApiProperties;
import com.sashimi.qualification.infrastructure.publicdata.QualificationCodeApiProperties;
import com.sashimi.qualification.infrastructure.publicdata.QualificationExamScheduleApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableConfigurationProperties({
        NcsApiProperties.class,
        QualificationExamScheduleApiProperties.class,
        QualificationCodeApiProperties.class
})
@SpringBootApplication
public class LmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LmsApplication.class, args);
    }
}
