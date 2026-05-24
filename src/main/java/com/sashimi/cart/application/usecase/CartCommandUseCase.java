package com.sashimi.cart.application.usecase;

import com.sashimi.cart.application.command.AddCartItemCommand;
import com.sashimi.cart.application.command.DeleteCartItemCommand;
import com.sashimi.cart.application.command.UpdateCartItemSelectionCommand;
import com.sashimi.cart.application.command.UpdateCartItemsSelectionCommand;

public interface CartCommandUseCase {

    Long addCartItem(AddCartItemCommand command);

    void deleteCartItem(DeleteCartItemCommand command);

    void updateCartItemSelection(UpdateCartItemSelectionCommand command);

    void updateCartItemsSelection(UpdateCartItemsSelectionCommand command);
}