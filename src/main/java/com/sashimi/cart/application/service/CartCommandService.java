package com.sashimi.cart.application.service;

import com.sashimi.cart.application.command.AddCartItemCommand;
import com.sashimi.cart.application.command.DeleteCartItemCommand;
import com.sashimi.cart.application.port.CourseInfo;
import com.sashimi.cart.application.port.CoursePort;
import com.sashimi.cart.application.port.EnrollmentPort;
import com.sashimi.cart.application.usecase.CartCommandUseCase;
import com.sashimi.cart.domain.model.CartItem;
import com.sashimi.cart.domain.repository.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sashimi.cart.application.command.UpdateCartItemSelectionCommand;
import com.sashimi.cart.application.command.UpdateCartItemsSelectionCommand;

@Service
@Transactional
public class CartCommandService implements CartCommandUseCase {

    private final CartItemRepository cartItemRepository;
    private final CoursePort coursePort;
    private final EnrollmentPort enrollmentPort;

    public CartCommandService(
            CartItemRepository cartItemRepository,
            CoursePort coursePort,
            EnrollmentPort enrollmentPort
    ) {
        this.cartItemRepository = cartItemRepository;
        this.coursePort = coursePort;
        this.enrollmentPort = enrollmentPort;
    }

    @Override
    public Long addCartItem(AddCartItemCommand command) {
        CourseInfo courseInfo = coursePort.getCourseInfo(command.courseId());

        if (!courseInfo.purchasable()) {
            throw new IllegalArgumentException("구매할 수 없는 강의입니다.");
        }

        if (enrollmentPort.isEnrolled(command.userId(), command.courseId())) {
            throw new IllegalArgumentException("이미 수강 중인 강의입니다.");
        }

        if (cartItemRepository.existsByUserIdAndCourseId(command.userId(), command.courseId())) {
            throw new IllegalArgumentException("이미 장바구니에 담긴 강의입니다.");
        }

        CartItem cartItem = CartItem.create(
                command.userId(),
                command.courseId(),
                courseInfo.price()
        );

        return cartItemRepository.save(cartItem).getId();
    }

    @Override
    public void deleteCartItem(DeleteCartItemCommand command) {
        CartItem cartItem = cartItemRepository.findByIdAndUserId(command.cartItemId(), command.userId())
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다."));

        cartItemRepository.delete(cartItem);
    }

    @Override
    public void updateCartItemSelection(UpdateCartItemSelectionCommand command) {
        if (command.userId() == null) {
            throw new IllegalArgumentException("User id is required.");
        }

        if (command.cartItemId() == null) {
            throw new IllegalArgumentException("Cart item id is required.");
        }

        CartItem cartItem = cartItemRepository.findByIdAndUserId(command.cartItemId(), command.userId())
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다."));

        cartItemRepository.save(cartItem.changeSelected(command.selected()));
    }

    @Override
    public void updateCartItemsSelection(UpdateCartItemsSelectionCommand command) {
        if (command.userId() == null) {
            throw new IllegalArgumentException("User id is required.");
        }

        if (command.cartItemIds() == null || command.cartItemIds().isEmpty()) {
            throw new IllegalArgumentException("Cart item ids are required.");
        }

        for (Long cartItemId : command.cartItemIds()) {
            if (cartItemId == null) {
                throw new IllegalArgumentException("Cart item id is required.");
            }

            CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, command.userId())
                    .orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다."));

            cartItemRepository.save(cartItem.changeSelected(command.selected()));
        }
    }

}