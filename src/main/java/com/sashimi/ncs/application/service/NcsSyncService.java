package com.sashimi.ncs.application.service;

import com.sashimi.category.infrastructure.persistence.CategoryJpaEntity;
import com.sashimi.category.infrastructure.persistence.SpringDataCategoryRepository;
import com.sashimi.ncs.infrastructure.persistence.NcsInfoJpaEntity;
import com.sashimi.ncs.infrastructure.persistence.SpringDataNcsInfoRepository;
import com.sashimi.ncs.infrastructure.publicdata.NcsApiClient;
import com.sashimi.ncs.infrastructure.publicdata.NcsCompeUnitApiResponse;
import com.sashimi.ncs.infrastructure.publicdata.NcsDutyApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class NcsSyncService {

    private static final String SUCCESS_CODE = "000";

    private static final Map<String, String> CATEGORY_MAPPING_KEYWORDS = Map.of(
            "정보처리기사", "응용SW엔지니어링",
            "SQLD", "DB엔지니어링",
            "빅데이터분석기사", "빅데이터",
            "컴퓨터활용능력", "사무행정"
    );

    private final NcsApiClient ncsApiClient;
    private final SpringDataNcsInfoRepository ncsInfoRepository;
    private final SpringDataCategoryRepository categoryRepository;

    public NcsSyncService(NcsApiClient ncsApiClient,
                          SpringDataNcsInfoRepository ncsInfoRepository,
                          SpringDataCategoryRepository categoryRepository) {
        this.ncsApiClient = ncsApiClient;
        this.ncsInfoRepository = ncsInfoRepository;
        this.categoryRepository = categoryRepository;
    }

    public int syncByDutyCd(String dutyCd) {
        NcsDutyApiResponse dutyResponse = ncsApiClient.fetchNcsDutyInfoAsDto(dutyCd, 1);
        NcsDutyApiResponse.NcsDutyItem duty = extractDuty(dutyResponse, dutyCd);

        int savedCount = 0;
        int pageNo = 1;

        while (true) {
            NcsCompeUnitApiResponse unitResponse = ncsApiClient.fetchNcsCompeUnitInfoAsDto(dutyCd, pageNo);
            validateSuccess(unitResponse.dataInfo().code(), unitResponse.dataInfo().message());

            List<NcsCompeUnitApiResponse.NcsCompeUnitItem> units = unitResponse.data();
            if (units == null || units.isEmpty()) {
                break;
            }

            for (NcsCompeUnitApiResponse.NcsCompeUnitItem unit : units) {
                upsertNcsInfo(duty, unit);
                savedCount++;
            }

            if (pageNo >= unitResponse.dataInfo().totalPage()) {
                break;
            }

            pageNo++;
        }

        mapCategoriesToNcsInfo();

        return savedCount;
    }

    private NcsDutyApiResponse.NcsDutyItem extractDuty(NcsDutyApiResponse response, String dutyCd) {
        validateSuccess(response.dataInfo().code(), response.dataInfo().message());

        if (response.data() == null || response.data().isEmpty()) {
            throw new IllegalStateException("NCS duty not found: " + dutyCd);
        }

        return response.data().get(0);
    }

    private void upsertNcsInfo(NcsDutyApiResponse.NcsDutyItem duty,
                               NcsCompeUnitApiResponse.NcsCompeUnitItem unit) {
        LocalDateTime syncedAt = LocalDateTime.now();

        NcsInfoJpaEntity ncsInfo = ncsInfoRepository.findByNcsCode(unit.ncsClCd())
                .orElseGet(() -> new NcsInfoJpaEntity(
                        unit.ncsClCd(),
                        duty.dutyNm(),
                        duty.dutyNm(),
                        duty.dutyDef(),
                        unit.compUnitCd(),
                        unit.compUnitName(),
                        unit.compUnitDef(),
                        syncedAt
                ));

        ncsInfo.updateFromSync(
                duty.dutyNm(),
                duty.dutyNm(),
                duty.dutyDef(),
                unit.compUnitCd(),
                unit.compUnitName(),
                unit.compUnitDef(),
                syncedAt
        );

        ncsInfoRepository.save(ncsInfo);
    }

    private void mapCategoriesToNcsInfo() {
        List<CategoryJpaEntity> categories = categoryRepository.findAllByActiveTrueOrderBySortOrderAsc();

        for (CategoryJpaEntity category : categories) {
            resolveMappingKeyword(category.getSubCategory())
                    .flatMap(this::findNcsInfoByKeyword)
                    .ifPresent(ncsInfo -> category.updateNcsInfoId(ncsInfo.getId()));
        }
    }

    private Optional<String> resolveMappingKeyword(String subCategory) {
        if (subCategory == null || subCategory.isBlank()) {
            return Optional.empty();
        }

        return CATEGORY_MAPPING_KEYWORDS.entrySet().stream()
                .filter(entry -> subCategory.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    private Optional<NcsInfoJpaEntity> findNcsInfoByKeyword(String keyword) {
        return ncsInfoRepository.findFirstByCategoryPathContainingOrJobNameContainingOrAbilityUnitNameContaining(
                keyword,
                keyword,
                keyword
        );
    }

    private void validateSuccess(String code, String message) {
        if (!SUCCESS_CODE.equals(code)) {
            throw new IllegalStateException("NCS API failed: " + code + " / " + message);
        }
    }
}