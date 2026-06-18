package com.sashimi.cart.infrastructure.persistence;

import com.sashimi.cart.domain.model.CartItem;
import com.sashimi.cart.domain.repository.CartItemRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.stereotype.Repository;
import com.sashimi.cart.domain.model.CartItemType;

import java.util.List;
import java.util.Optional;

@Repository
public class CartItemRepositoryAdapter implements CartItemRepository {

    private final SpringDataCartItemRepository repository;

    public CartItemRepositoryAdapter(SpringDataCartItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public CartItem save(CartItem cartItem) {
        CartItemJpaEntity entity = cartItem.getId() == null
                ? new CartItemJpaEntity(
                cartItem.getUserId(),
                cartItem.getItemType(),
                cartItem.getItemId(),
                cartItem.getCourseId(),
                cartItem.getPrice(),
                cartItem.isSelected(),
                cartItem.getCreatedAt()
        )
                : repository.findById(cartItem.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        entity.changeSelected(cartItem.isSelected());

        return toDomain(repository.save(entity));
    }

    @Override
    public List<CartItem> findAllByUserId(Long userId) {
        return repository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUserIdAndCourseId(Long userId, Long courseId) {
        return existsByUserIdAndItemTypeAndItemId(userId, CartItemType.COURSE, courseId);
    }

    @Override
    public Optional<CartItem> findByIdAndUserId(Long cartItemId, Long userId) {
        return repository.findByIdAndUserId(cartItemId, userId)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByUserIdAndItemTypeAndItemId(Long userId, CartItemType itemType, Long itemId) {
        return repository.existsByUserIdAndItemTypeAndItemId(userId, itemType, itemId);
    }

    @Override
    public void delete(CartItem cartItem) {
        repository.deleteById(cartItem.getId());
    }

    private CartItem toDomain(CartItemJpaEntity entity) {
        return CartItem.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getItemType(),
                entity.getItemId(),
                entity.getCourseId(),
                entity.getPrice(),
                entity.isSelected(),
                entity.getCreatedAt()
        );
    }

    @Override
    public List<CartItem> findAllSelectedByUserId(Long userId) {
        return repository.findAllByUserIdAndSelectedTrueOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteAllSelectedByUserId(Long userId) {
        repository.deleteAllByUserIdAndSelectedTrue(userId);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        repository.deleteAllByUserId(userId);
    }

    @Override
    public void deleteByUserIdAndCourseId(Long userId, Long courseId) {
        deleteByUserIdAndItemTypeAndItemId(userId, CartItemType.COURSE, courseId);
    }

    @Override
    public void deleteByUserIdAndItemTypeAndItemId(Long userId, CartItemType itemType, Long itemId) {
        repository.deleteByUserIdAndItemTypeAndItemId(userId, itemType, itemId);
    }
    
}