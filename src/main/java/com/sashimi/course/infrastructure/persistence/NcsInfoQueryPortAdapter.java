package com.sashimi.course.infrastructure.persistence;

import com.sashimi.course.application.port.NcsInfoQueryPort;
import com.sashimi.course.application.port.NcsInfoView;
import com.sashimi.ncs.infrastructure.persistence.NcsInfoJpaEntity;
import com.sashimi.ncs.infrastructure.persistence.SpringDataNcsInfoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class NcsInfoQueryPortAdapter implements NcsInfoQueryPort {

    private static final int DISPLAY_ABILITY_UNIT_LIMIT = 3;

    private final SpringDataNcsInfoRepository springDataNcsInfoRepository;

    public NcsInfoQueryPortAdapter(SpringDataNcsInfoRepository springDataNcsInfoRepository) {
        this.springDataNcsInfoRepository = springDataNcsInfoRepository;
    }

    @Override
    public Optional<NcsInfoView> findViewByRepresentativeId(Long ncsInfoId) {
        return springDataNcsInfoRepository.findById(ncsInfoId)
                .map(this::toView);
    }

    private NcsInfoView toView(NcsInfoJpaEntity representative) {
        List<String> allAbilityUnitNames = springDataNcsInfoRepository
                .findByJobNameOrderByAbilityUnitCodeAsc(representative.getJobName())
                .stream()
                .map(NcsInfoJpaEntity::getAbilityUnitName)
                .map(this::cleanAbilityUnitName)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .toList();

        List<String> displayAbilityUnitNames = allAbilityUnitNames.stream()
                .limit(DISPLAY_ABILITY_UNIT_LIMIT)
                .toList();

        return new NcsInfoView(
                representative.getCategoryPath(),
                representative.getJobDescription(),
                displayAbilityUnitNames,
                allAbilityUnitNames.size()
        );
    }

    private String cleanAbilityUnitName(String abilityUnitName) {
        if (abilityUnitName == null) {
            return null;
        }

        return abilityUnitName.replaceFirst("^\\d+\\.", "");
    }
}