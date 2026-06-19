package com.sashimi.ncs.infrastructure.publicdata;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NcsApiClient {

    private final RestClient restClient;
    private final NcsApiProperties properties;

    public NcsApiClient(NcsApiProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }

    public String fetchNcsCodeInfo(int pageNo) {
        return fetchWithoutDutyCd("/ncsCdInfo", pageNo);
    }

    public String fetchNcsDutyInfo(String dutyCd, int pageNo) {
        return fetchWithDutyCd("/ncsDutyInfo", dutyCd, pageNo);
    }

    public String fetchNcsCompeUnitInfo(String dutyCd, int pageNo) {
        return fetchWithDutyCd("/ncsCompeUnitInfo", dutyCd, pageNo);
    }

    private String fetchWithoutDutyCd(String path, int pageNo) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("serviceKey", properties.serviceKey())
                        .queryParam("pageNo", pageNo)
                        .queryParam("numOfRows", properties.numOfRows())
                        .queryParam("returnType", "json")
                        .build())
                .retrieve()
                .body(String.class);
    }

    private String fetchWithDutyCd(String path, String dutyCd, int pageNo) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("serviceKey", properties.serviceKey())
                        .queryParam("pageNo", pageNo)
                        .queryParam("numOfRows", properties.numOfRows())
                        .queryParam("returnType", "json")
                        .queryParam("dutyCd", dutyCd)
                        .build())
                .retrieve()
                .body(String.class);
    }

    public NcsDutyApiResponse fetchNcsDutyInfoAsDto(String dutyCd, int pageNo) {
        return fetchWithDutyCdAsDto("/ncsDutyInfo", dutyCd, pageNo, NcsDutyApiResponse.class);
    }

    public NcsCompeUnitApiResponse fetchNcsCompeUnitInfoAsDto(String dutyCd, int pageNo) {
        return fetchWithDutyCdAsDto("/ncsCompeUnitInfo", dutyCd, pageNo, NcsCompeUnitApiResponse.class);
    }

    private <T> T fetchWithDutyCdAsDto(String path, String dutyCd, int pageNo, Class<T> responseType) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("serviceKey", properties.serviceKey())
                        .queryParam("pageNo", pageNo)
                        .queryParam("numOfRows", properties.numOfRows())
                        .queryParam("returnType", "json")
                        .queryParam("dutyCd", dutyCd)
                        .build())
                .retrieve()
                .body(responseType);
    }
}