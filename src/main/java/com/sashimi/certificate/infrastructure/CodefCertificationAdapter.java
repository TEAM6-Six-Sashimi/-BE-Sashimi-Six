package com.sashimi.certificate.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sashimi.certificate.application.port.CertificationVerificationPort;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
public class CodefCertificationAdapter implements CertificationVerificationPort {

    private static final String CERT_API_URL = "/v1/kr/public/mw/national-technical-certification/info";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${codef.client-id}")
    private String clientId;

    @Value("${codef.client-secret}")
    private String clientSecret;

    @Value("${codef.public-key}")
    private String publicKey;

    @Override
    public VerificationResult verify(VerificationRequest request) {
        try {
            EasyCodef codef = new EasyCodef();
            codef.setClientInfoForDemo(clientId, clientSecret);
            codef.setPublicKey(publicKey);

            HashMap<String, Object> params = new HashMap<>();
            params.put("organization", "0001");
            params.put("loginType", "5");
            params.put("loginTypeLevel", request.loginTypeLevel());
            params.put("userName", request.userName());
            params.put("identity", request.identity());
            params.put("phoneNo", request.phoneNo());
            params.put("identityEncYn", "N");

            // Step 1: 카카오 푸시 발송
            String step1Result = codef.requestProduct(CERT_API_URL, EasyCodefServiceType.DEMO, params);
            JsonNode step1Json = objectMapper.readTree(step1Result);
            String code = step1Json.path("result").path("code").asText();

            if (!"CF-03002".equals(code)) {
                log.error("CODEF Step1 오류: code={}, message={}",
                        code, step1Json.path("result").path("message").asText());
                return new VerificationResult(null, null, null, false);
            }

            log.info("CODEF 카카오 푸시 발송 완료, 사용자 승인 대기 중...");

            // twoWayInfo 추출
            JsonNode data = step1Json.path("data");
            HashMap<String, Object> twoWayInfo = new HashMap<>();
            twoWayInfo.put("jobIndex", data.path("jobIndex").asInt());
            twoWayInfo.put("threadIndex", data.path("threadIndex").asInt());
            twoWayInfo.put("jti", data.path("jti").asText());
            twoWayInfo.put("twoWayTimestamp", data.path("twoWayTimestamp").asLong());

            params.put("simpleAuth", "1");
            params.put("is2Way", true);
            params.put("twoWayInfo", twoWayInfo);

            // Step 2: 사용자 승인 폴링 (3초 간격, 최대 60초)
            for (int i = 0; i < 20; i++) {
                Thread.sleep(3000);
                String step2Result = codef.requestCertification(CERT_API_URL, EasyCodefServiceType.DEMO, params);
                JsonNode step2Json = objectMapper.readTree(step2Result);
                String step2Code = step2Json.path("result").path("code").asText();

                if ("CF-00000".equals(step2Code)) {
                    JsonNode resultData = step2Json.path("data");
                    return new VerificationResult(
                            resultData.path("resCertificateNo").asText(),
                            resultData.path("resCategory").asText(),
                            resultData.path("resPassDate").asText(),
                            true
                    );
                }

                if (!"CF-03002".equals(step2Code)) {
                    log.error("CODEF Step2 오류: code={}, message={}",
                            step2Code, step2Json.path("result").path("message").asText());
                    return new VerificationResult(null, null, null, false);
                }
            }

            log.error("CODEF 간편인증 타임아웃 (60초 초과)");
            return new VerificationResult(null, null, null, false);

        } catch (Exception e) {
            log.error("CODEF 자격증 조회 실패", e);
            return new VerificationResult(null, null, null, false);
        }
    }
}
