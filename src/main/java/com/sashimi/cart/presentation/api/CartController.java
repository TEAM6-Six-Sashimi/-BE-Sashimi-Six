package com.sashimi.cart.presentation.api;

import com.sashimi.cart.application.command.AddCartItemCommand;
import com.sashimi.cart.application.command.DeleteCartItemCommand;
import com.sashimi.cart.application.command.UpdateCartItemSelectionCommand;
import com.sashimi.cart.application.command.UpdateCartItemsSelectionCommand;
import com.sashimi.cart.application.usecase.CartCommandUseCase;
import com.sashimi.cart.application.usecase.CartQueryUseCase;
import com.sashimi.cart.presentation.api.request.AddCartItemRequest;
import com.sashimi.cart.presentation.api.request.DeleteCartItemsRequest;
import com.sashimi.cart.presentation.api.request.UpdateCartItemSelectionRequest;
import com.sashimi.cart.presentation.api.request.UpdateCartItemsSelectionRequest;
import com.sashimi.cart.presentation.api.response.AddCartItemResponse;
import com.sashimi.cart.presentation.api.response.CartResponse;
import com.sashimi.global.exception.ErrorResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "장바구니 API")
@SecurityRequirement(name = "bearerAuth")
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

    @Operation(summary = "장바구니 조회", description = "로그인한 사용자의 장바구니 목록과 선택된 항목의 총 금액을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장바구니 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        CartQueryUseCase.CartView cartView = cartQueryUseCase.getCart(principal.getId());
        return ResponseEntity.ok(CartResponse.from(cartView));
    }

    @Operation(summary = "장바구니 항목 추가", description = "강의를 장바구니에 추가합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "장바구니 추가 성공",
                    content = @Content(schema = @Schema(implementation = AddCartItemResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "구매할 수 없는 강의 또는 잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 장바구니에 있거나 이미 수강 중인 강의",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<AddCartItemResponse> addCartItem(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid AddCartItemRequest request
    ) {
        Long cartItemId = cartCommandUseCase.addCartItem(
                new AddCartItemCommand(principal.getId(), request.courseId())
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AddCartItemResponse(request.courseId(), cartItemId));
    }

    @Operation(summary = "장바구니 항목 삭제", description = "장바구니 항목 ID 목록으로 단건 또는 다건 항목을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "장바구니 항목 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 삭제 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "장바구니 항목을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteCartItem(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid DeleteCartItemsRequest request
    ) {
        cartCommandUseCase.deleteCartItem(
                new DeleteCartItemCommand(principal.getId(), request.cartItemIds())
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "장바구니 단일 항목 선택 상태 변경", description = "장바구니 항목 하나의 선택 여부를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "선택 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 선택 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "장바구니 항목을 찾을 수 없음")
    })
    @PatchMapping("/{cartItemId}/selected")
    public ResponseEntity<Void> updateCartItemSelection(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long cartItemId,
            @RequestBody @Valid UpdateCartItemSelectionRequest request
    ) {
        cartCommandUseCase.updateCartItemSelection(
                new UpdateCartItemSelectionCommand(principal.getId(), cartItemId, request.selected())
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "장바구니 여러 항목 선택 상태 변경", description = "장바구니 항목 여러 개의 선택 여부를 한 번에 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "선택 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 선택 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "장바구니 항목을 찾을 수 없음")
    })
    @PatchMapping("/selected")
    public ResponseEntity<Void> updateCartItemsSelection(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid UpdateCartItemsSelectionRequest request
    ) {
        cartCommandUseCase.updateCartItemsSelection(
                new UpdateCartItemsSelectionCommand(principal.getId(), request.cartItemIds(), request.selected())
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "장바구니 결제 전 조회", description = "결제 대상으로 선택된 장바구니 항목과 총 금액을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 대상 조회 성공"),
            @ApiResponse(responseCode = "400", description = "선택된 장바구니 항목이 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/checkout")
    public ResponseEntity<CartResponse> checkout(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        CartQueryUseCase.CartView cartView =
                cartQueryUseCase.getCheckoutCart(principal.getId());

        return ResponseEntity.ok(CartResponse.from(cartView));
    }
}