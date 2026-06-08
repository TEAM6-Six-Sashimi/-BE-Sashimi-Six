package com.sashimi.cart.application.service;

import com.sashimi.cart.application.command.AddCartItemCommand;
import com.sashimi.cart.application.command.DeleteCartItemCommand;
import com.sashimi.cart.application.policy.CoursePurchasePolicy;
import com.sashimi.cart.application.port.CourseInfo;
import com.sashimi.cart.application.usecase.CartCommandUseCase;
import com.sashimi.cart.domain.model.CartItem;
import com.sashimi.cart.domain.repository.CartItemRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sashimi.cart.application.command.UpdateCartItemSelectionCommand;
import com.sashimi.cart.application.command.UpdateCartItemsSelectionCommand;
import org.springframework.dao.DataIntegrityViolationException;

@Service
@Transactional
public class CartCommandService implements CartCommandUseCase {

    private final CartItemRepository cartItemRepository;
    private final CoursePurchasePolicy coursePurchasePolicy;

    public CartCommandService(
            CartItemRepository cartItemRepository,
            CoursePurchasePolicy coursePurchasePolicy
    ) {
        this.cartItemRepository = cartItemRepository;
        this.coursePurchasePolicy = coursePurchasePolicy;
    }

    @Override
    public Long addCartItem(AddCartItemCommand command) {
        CourseInfo courseInfo = coursePurchasePolicy.validatePurchasable(
                command.userId(),
                command.courseId()
        );

        if (cartItemRepository.existsByUserIdAndCourseId(command.userId(), command.courseId())) {
            throw new BusinessException(ErrorCode.CART_ITEM_ALREADY_EXISTS);
        }

        CartItem cartItem = CartItem.create(
                command.userId(),
                command.courseId(),
                courseInfo.price()
        );

        try {
            return cartItemRepository.save(cartItem).getId();
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.CART_ITEM_ALREADY_EXISTS);
        }
    }

    @Override
    public void deleteCartItem(DeleteCartItemCommand command) {
        if (command.userId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (command.cartItemIds() == null || command.cartItemIds().isEmpty()) {
            throw new BusinessException(ErrorCode.CART_INVALID_SELECTION);
        }

        for (Long cartItemId : command.cartItemIds()) {
            if (cartItemId == null) {
                throw new BusinessException(ErrorCode.CART_INVALID_SELECTION);
            }

            CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, command.userId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

            cartItemRepository.delete(cartItem);
        }
    }

    @Override
    public void updateCartItemSelection(UpdateCartItemSelectionCommand command) {
        if (command.userId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (command.cartItemId() == null) {
            throw new BusinessException(ErrorCode.CART_INVALID_SELECTION);
        }

        CartItem cartItem = cartItemRepository.findByIdAndUserId(command.cartItemId(), command.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItemRepository.save(cartItem.changeSelected(command.selected()));
    }

    @Override
    public void updateCartItemsSelection(UpdateCartItemsSelectionCommand command) {
        if (command.userId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (command.cartItemIds() == null || command.cartItemIds().isEmpty()) {
            throw new BusinessException(ErrorCode.CART_INVALID_SELECTION);
        }

        for (Long cartItemId : command.cartItemIds()) {
            if (cartItemId == null) {
                throw new BusinessException(ErrorCode.CART_INVALID_SELECTION);
            }

            CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, command.userId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

            cartItemRepository.save(cartItem.changeSelected(command.selected()));
        }
    }

}