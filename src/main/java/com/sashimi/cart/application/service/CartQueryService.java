package com.sashimi.cart.application.service;

import com.sashimi.cart.application.port.CourseInfo;
import com.sashimi.cart.application.port.CoursePort;
import com.sashimi.cart.application.usecase.CartQueryUseCase;
import com.sashimi.cart.domain.model.CartItem;
import com.sashimi.cart.domain.repository.CartItemRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
        List<CartItem> cartItems =
                cartItemRepository.findAllByUserId(userId);

        if (cartItems.isEmpty()) {
            return new CartView(
                    List.of(),
                    0L,
                    0,
                    0
            );
        }

        List<Long> courseIds = cartItems.stream()
                .map(CartItem::getCourseId)
                .distinct()
                .toList();

        Map<Long, CourseInfo> courseInfoMap =
                coursePort.getCourseInfos(courseIds);

        return toCartView(
                cartItems,
                courseInfoMap
        );
    }

    private CartView toCartView(
            List<CartItem> cartItems,
            Map<Long, CourseInfo> courseInfoMap
    ) {
        List<CartItemView> itemViews = cartItems.stream()
                .map(cartItem ->
                        toView(
                                cartItem,
                                getCourseInfo(
                                        cartItem,
                                        courseInfoMap
                                )
                        )
                )
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

    private CourseInfo getCourseInfo(
            CartItem cartItem,
            Map<Long, CourseInfo> courseInfoMap
    ) {
        CourseInfo courseInfo =
                courseInfoMap.get(cartItem.getCourseId());

        if (courseInfo == null) {
            throw new BusinessException(
                    ErrorCode.COURSE_NOT_FOUND
            );
        }

        return courseInfo;
    }

    private CartItemView toView(
            CartItem cartItem,
            CourseInfo courseInfo
    ) {
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