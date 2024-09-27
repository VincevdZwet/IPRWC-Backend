package com.hsleiden.iprwcbackend;

import com.github.javafaker.Faker;
import com.hsleiden.iprwcbackend.model.Product;
import com.hsleiden.iprwcbackend.model.User;
import com.hsleiden.iprwcbackend.repository.ProductRepo;
import com.hsleiden.iprwcbackend.repository.UserRepo;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class Seeder {
    private final Faker faker = new Faker();
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;

    public Seeder(
            PasswordEncoder passwordEncoder,
            UserRepo userRepo,
            ProductRepo productRepo
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        this.seedUser();
        this.seedAdmin();
        this.seedProduct();
    }

    public void seedUser() {
        if (userRepo.findByEmail("test@mail.com").isEmpty()) {
            User user = new User();
            user.setEmail("test@mail.com");
            user.setFirstname("John");
            user.setLastname("Doe");
            user.setBirthdate(faker.date().birthday());
            user.setGender(faker.dog().gender());
            user.setPassword(passwordEncoder.encode("Test123!"));
            userRepo.save(user);
        }
    }

    public void seedAdmin() {
        if (userRepo.findByEmail("admin@mail.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@mail.com");
            admin.setFirstname(faker.name().firstName());
            admin.setLastname(faker.name().lastName());
            admin.setBirthdate(faker.date().birthday());
            admin.setGender(faker.dog().gender());
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setRole(User.Role.ADMIN);
            userRepo.save(admin);
        }
    }

    public void seedProduct() {
        while (productRepo.count() < 12) {
            String title = faker.book().title();
            if (productRepo.findByTitle(title) == null) {
                Product product = new Product();
                product.setDuration(String.valueOf(faker.number().numberBetween(30, 300)));
                product.setTitle(title);
                product.setPrice(BigDecimal.valueOf(faker.number().numberBetween(1, 10)));
                product.setImageUrl(faker.internet().image());
                product.setReleaseDate(faker.date().birthday());
                productRepo.save(product);
            }
        }
    }
}
