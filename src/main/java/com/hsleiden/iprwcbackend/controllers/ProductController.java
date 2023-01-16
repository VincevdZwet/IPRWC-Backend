package com.hsleiden.iprwcbackend.controllers;

import com.hsleiden.iprwcbackend.model.Product;
import com.hsleiden.iprwcbackend.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/product")
@ResponseStatus(HttpStatus.OK)
public class ProductController {

    @Autowired
    private ProductRepo productRepo;

    @GetMapping("/all")
    public Product[] getAllProducts() {
        return productRepo.findByEnabled(true).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND"));
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Product getProductById(@PathVariable(value = "id") UUID id) {
        return productRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND"));
    }

    @PutMapping("/")
    @ResponseBody
    public Product createProduct(@RequestBody Product product) {
        try {
            if (product.getTitle().isEmpty() || product.getImageUrl().isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PRODUCT_DETAILS_MISSING");
            }

            if (productRepo.findByTitle(product.getTitle()) != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TITLE_EXISTS");
            }

            return productRepo.save(product);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PRODUCT_DETAILS_MISSING");
        }
    }

    @PostMapping("/{id}")
    @ResponseBody
    public Product updateProduct(@PathVariable(value = "id") UUID id, @RequestBody Product product) {
        Product productToUpdate = productRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND"));

        if (product.getTitle() != null) productToUpdate.setTitle(product.getTitle());
        if (product.getDuration() != null) productToUpdate.setDuration(product.getDuration());
        if (product.getReleaseDate() != null) productToUpdate.setReleaseDate(product.getReleaseDate());
        if (product.getImageUrl() != null) productToUpdate.setImageUrl(product.getImageUrl());
        if (product.getPrice() != null) productToUpdate.setPrice(product.getPrice());

        return productRepo.save(productToUpdate);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteProduct(@PathVariable(value = "id") UUID id) {
        Product productToDelete = productRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND"));

        productToDelete.setEnabled(false);
        productRepo.save(productToDelete);
    }
}
