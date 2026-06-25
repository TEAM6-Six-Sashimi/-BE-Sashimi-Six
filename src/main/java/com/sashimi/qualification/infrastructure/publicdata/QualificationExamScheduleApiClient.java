package com.sashimi.qualification.infrastructure.publicdata;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class QualificationExamScheduleApiClient {

    private final RestClient restClient;
    private final QualificationExamScheduleApiProperties properties;

    public QualificationExamScheduleApiClient(
            QualificationExamScheduleApiProperties properties
    ) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }

    public String fetchSchedules(
            int implYy,
            String qualgbCd,
            String jmCd,
            int pageNo
    ) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder
                            .path("/getQualExamSchdList")
                            .queryParam("serviceKey", properties.serviceKey())
                            .queryParam("pageNo", pageNo)
                            .queryParam("numOfRows", properties.numOfRows())
                            .queryParam("dataFormat", "json")
                            .queryParam("implYy", implYy)
                            .queryParam("qualgbCd", qualgbCd);

                    if (jmCd != null && !jmCd.isBlank()) {
                        uriBuilder.queryParam("jmCd", jmCd);
                    }

                    return uriBuilder.build();
                })
                .retrieve()
                .body(String.class);
    }
}