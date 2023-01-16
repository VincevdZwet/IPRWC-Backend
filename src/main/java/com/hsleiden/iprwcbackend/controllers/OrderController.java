package com.hsleiden.iprwcbackend.controllers;

import com.hsleiden.iprwcbackend.model.Order;
import com.hsleiden.iprwcbackend.model.OrderProduct;
import com.hsleiden.iprwcbackend.model.Product;
import com.hsleiden.iprwcbackend.model.User;
import com.hsleiden.iprwcbackend.repository.OrderRepo;
import com.hsleiden.iprwcbackend.repository.ProductRepo;
import com.hsleiden.iprwcbackend.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/order")
@ResponseStatus(HttpStatus.OK)
public class OrderController {

    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping("/all")
    public Order[] getAllOrders() {
        User loggedInUser = authorizationService.getLoggedInUser();

        if (loggedInUser.getRole() != User.Role.ADMIN) {
            Set<Order> orders = loggedInUser.getOrders();
            return orders.toArray(new Order[0]);
        }

        return orderRepo.findAll().toArray(new Order[0]);
    }

    @PutMapping("/")
    @ResponseBody
    public Order createOrder(@RequestBody Order order) {
        if (order.getEmailSentTo().isBlank() || order.getEmailSentTo().isEmpty() || !order.isValidBank(order.getBank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ORDER_DETAILS_MISSING");
        }

        User loggedInUser = authorizationService.getLoggedInUser();
        order.setUser(loggedInUser);
        order.setInvoiceNumber(order.createInvoiceNumber(orderRepo));

        Set<OrderProduct> orderProductSet = new HashSet<>();
        for (UUID productId : order.getProducts()) {
            Product product = productRepo.findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND"));

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProductName(product.getTitle());
            orderProduct.setProductPrice(product.getPrice());
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);

            orderProductSet.add(orderProduct);
        }
        order.setOrderProducts(orderProductSet);
        return orderRepo.save(order);
    }
}
