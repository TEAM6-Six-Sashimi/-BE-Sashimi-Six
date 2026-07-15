package com.sashimi.coffeechat.infrastructure.persistence;

import com.sashimi.coffeechat.domain.model.CoffeeChatMessage;
import com.sashimi.coffeechat.domain.repository.CoffeeChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CoffeeChatMessageRepositoryAdapter implements CoffeeChatMessageRepository {

    private final SpringDataCoffeeChatMessageRepository springDataRepository;

    @Override
    public CoffeeChatMessage save(CoffeeChatMessage message) {
        CoffeeChatMessageJpaEntity entity = CoffeeChatMessageJpaEntity.from(message);
        return springDataRepository.save(entity).toDomain();
    }

    @Override
    public List<CoffeeChatMessage> findAllByCoffeeChatId(Long coffeeChatId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        return springDataRepository.findAllByCoffeeChatIdOrderByCreatedAtAsc(coffeeChatId, pageRequest)
                .stream()
                .map(CoffeeChatMessageJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Long> countUnreadMessagesByCoffeeChatIds(List<Long> coffeeChatIds, Long excludeSenderId) {
        if (coffeeChatIds.isEmpty()) {
            return Map.of();
        }
        return springDataRepository.countUnreadMessagesByCoffeeChatIds(coffeeChatIds, excludeSenderId)
                .stream()
                .collect(Collectors.toMap(
                        CoffeeChatUnreadCountProjection::getCoffeeChatId,
                        CoffeeChatUnreadCountProjection::getUnreadCount));
    }

    @Override
    public void markAllAsRead(Long coffeeChatId, Long readerId) {
        springDataRepository.markAllAsRead(coffeeChatId, readerId);
    }

    @Override
    public Map<Long, CoffeeChatMessage> findLatestMessagesByCoffeeChatIds(List<Long> coffeeChatIds) {
        if (coffeeChatIds.isEmpty()) {
            return Map.of();
        }
        return springDataRepository.findLatestMessagesByCoffeeChatIds(coffeeChatIds)
                .stream()
                .map(CoffeeChatMessageJpaEntity::toDomain)
                .collect(Collectors.toMap(
                        CoffeeChatMessage::getCoffeeChatId, Function.identity(), (first, second) -> first));
    }
}
