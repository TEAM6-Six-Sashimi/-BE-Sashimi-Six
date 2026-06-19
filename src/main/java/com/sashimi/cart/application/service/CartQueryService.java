package com.sashimi.cart.application.service;

import com.sashimi.cart.application.port.CourseInfo;
import com.sashimi.cart.application.port.CoursePort;
import com.sashimi.cart.application.usecase.CartQueryUseCase;
import com.sashimi.cart.domain.model.CartItem;
import com.sashimi.cart.domain.repository.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CartQueryService implements CartQueryUseCase {

    private final CartItemRepository cartItemRepository;
    private final CoursePort coursePort;

    public CartQueryService(
            CartItemRepository cartItemRepository,
            CoursePort coursePort
    ) {
        this.cartItemRepository = cartItemRepository;
        this.coursePort = coursePort;
    }

    @Override
    public CartView getCart(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findAllByUserId(userId);
        return toCartView(cartItems);
    }


    private CartView toCartView(List<CartItem> cartItems) {
        List<CartItemView> itemViews = cartItems.stream()
                .map(this::toView)
                .toList();

        Long totalPrice = itemViews.stream()
                .filter(CartItemView::selected)
                .mapToLong(CartItemView::price)
                .sum();

        int selectedItemCount = (int) itemViews.stream()
                .filter(CartItemView::selected)
                .count();

        return new CartView(
                itemViews,
                totalPrice,
                itemViews.size(),
                selectedItemCount
        );
    }

    private CartItemView toView(CartItem cartItem) {
        CourseInfo courseInfo =
                coursePort.getCourseInfo(cartItem.getCourseId());

        return new CartItemView(
                cartItem.getId(),
                cartItem.getCourseId(),
                courseInfo.title(),
                courseInfo.thumbnail(),
                courseInfo.instructorName(),
                cartItem.getPrice(),
                cartItem.isSelected()
        );
    }

}