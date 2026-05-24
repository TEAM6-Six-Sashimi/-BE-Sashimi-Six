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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestHeader("X-USER-ID") Long userId
    ) {
        CartQueryUseCase.CartView cartView = cartQueryUseCase.getCart(userId);
        return ResponseEntity.ok(CartResponse.from(cartView));
    }

    @PostMapping
    public ResponseEntity<Long> addCartItem(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody AddCartItemRequest request
    ) {
        Long cartItemId = cartCommandUseCase.addCartItem(
                new AddCartItemCommand(userId, request.courseId())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemId);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long cartItemId
    ) {
        cartCommandUseCase.deleteCartItem(
                new DeleteCartItemCommand(userId, cartItemId)
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{cartItemId}/selected")
    public ResponseEntity<Void> updateCartItemSelection(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long cartItemId,
            @RequestBody UpdateCartItemSelectionRequest request
    ) {
        if (request.selected() == null) {
            throw new IllegalArgumentException("Selected is required.");
        }

        cartCommandUseCase.updateCartItemSelection(
                new UpdateCartItemSelectionCommand(userId, cartItemId, request.selected())
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/selected")
    public ResponseEntity<Void> updateCartItemsSelection(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody UpdateCartItemsSelectionRequest request
    ) {
        if (request.selected() == null) {
            throw new IllegalArgumentException("Selected is required.");
        }

        cartCommandUseCase.updateCartItemsSelection(
                new UpdateCartItemsSelectionCommand(userId, request.cartItemIds(), request.selected())
        );

        return ResponseEntity.noContent().build();
    }

}