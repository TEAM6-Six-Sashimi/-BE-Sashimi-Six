package com.sashimi.cart.presentation.api;

import com.sashimi.cart.application.command.AddCartItemCommand;
import com.sashimi.cart.application.command.DeleteCartItemCommand;
import com.sashimi.cart.application.command.UpdateCartItemSelectionCommand;
import com.sashimi.cart.application.command.UpdateCartItemsSelectionCommand;
import com.sashimi.cart.application.usecase.CartCommandUseCase;
import com.sashimi.cart.application.usecase.CartQueryUseCase;
import com.sashimi.cart.presentation.api.request.AddCartItemRequest;
import com.sashimi.cart.presentation.api.request.UpdateCartItemSelectionRequest;
import com.sashimi.cart.presentation.api.request.UpdateCartItemsSelectionRequest;
import com.sashimi.cart.presentation.api.response.CartResponse;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sashimi.security.principal.CustomUserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartCommandUseCase cartCommandUseCase;
    private final CartQueryUseCase cartQueryUseCase;

    public CartController(
            CartCommandUseCase cartCommandUseCase,
            CartQueryUseCase cartQueryUseCase
    ) {
        this.cartCommandUseCase = cartCommandUseCase;
        this.cartQueryUseCase = cartQueryUseCase;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        CartQueryUseCase.CartView cartView = cartQueryUseCase.getCart(principal.getId());
        return ResponseEntity.ok(CartResponse.from(cartView));
    }

    @PostMapping
    public ResponseEntity<Long> addCartItem(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody AddCartItemRequest request
    ) {
        Long cartItemId = cartCommandUseCase.addCartItem(
                new AddCartItemCommand(principal.getId(), request.courseId())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemId);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long cartItemId
    ) {
        cartCommandUseCase.deleteCartItem(
                new DeleteCartItemCommand(principal.getId(), cartItemId)
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{cartItemId}/selected")
    public ResponseEntity<Void> updateCartItemSelection(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long cartItemId,
            @RequestBody UpdateCartItemSelectionRequest request
    ) {
        if (request.selected() == null) {
            throw new BusinessException(ErrorCode.CART_INVALID_SELECTION);
        }

        cartCommandUseCase.updateCartItemSelection(
                new UpdateCartItemSelectionCommand(principal.getId(), cartItemId, request.selected())
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/selected")
    public ResponseEntity<Void> updateCartItemsSelection(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody UpdateCartItemsSelectionRequest request
    ) {
        if (request.selected() == null) {
            throw new BusinessException(ErrorCode.CART_INVALID_SELECTION);
        }

        cartCommandUseCase.updateCartItemsSelection(
                new UpdateCartItemsSelectionCommand(principal.getId(), request.cartItemIds(), request.selected())
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<CartResponse> checkout(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        CartQueryUseCase.CartView cartView =
                cartQueryUseCase.getCheckoutCart(principal.getId());

        return ResponseEntity.ok(CartResponse.from(cartView));
    }


}