package com.hsleiden.iprwcbackend.controllers;

import com.hsleiden.iprwcbackend.model.Cart;
import com.hsleiden.iprwcbackend.model.User;
import com.hsleiden.iprwcbackend.repository.CartRepo;
import com.hsleiden.iprwcbackend.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/cart")
@ResponseStatus(HttpStatus.OK)
public class CartController {

    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private AuthorizationService authorizationService;


    @GetMapping("/all")
    @ResponseBody
    public Cart getCart() {
        User loggedInUser = authorizationService.getLoggedInUser();

        if (loggedInUser.getCart() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CART_NOT_FOUND");
        }

        return loggedInUser.getCart();
    }

    @PutMapping("/")
    @ResponseBody
    public void saveCart(@RequestBody Cart cart) {
        User loggedInUser = authorizationService.getLoggedInUser();
        cartRepo.findById(loggedInUser.getCart().getId())
                .ifPresentOrElse(
                        cartToUpdate -> {
                            if (cart.getProducts() != null) cartToUpdate.setProducts(cart.getProducts());

                            cartRepo.save(cartToUpdate);
                        },
                        () -> {
                            cart.setUser(loggedInUser);
                            cartRepo.save(cart);
                        }
                );
    }
}
