package com.spring_commerce.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.spring_commerce.model.AppRole;
import com.spring_commerce.model.Category;
import com.spring_commerce.model.Product;
import com.spring_commerce.model.Role;
import com.spring_commerce.model.User;
import com.spring_commerce.repositories.CategoryRepository;
import com.spring_commerce.repositories.ProductRepository;
import com.spring_commerce.repositories.RoleRepository;
import com.spring_commerce.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

        @Autowired
        private CategoryRepository categoryRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RoleRepository roleRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public void run(final String... args) throws Exception {
                this.initData();
        }


        private void initData() {
                if (this.roleRepository.count() > 0)
                        return;
                // Roles
                final Role userRole = new Role(AppRole.ROLE_USER);
                final Role adminRole = new Role(AppRole.ROLE_ADMIN);
                final Role sellerRole = new Role(AppRole.ROLE_SELLER);
                this.roleRepository.saveAll(List.of(userRole, adminRole, sellerRole));

                // Users
                final User user_1 = new User("admin", "admin@spring_commerce.com",
                                this.passwordEncoder.encode("secret"));
                user_1.setRoles(Set.of(userRole, sellerRole, adminRole));

                final User user_2 = new User("seller", "seller@spring_commerce.com",
                                this.passwordEncoder.encode("password"));
                user_2.setRoles(Set.of(userRole, sellerRole));

                final User user_3 = new User("user", "user@spring_commerce.com",
                                this.passwordEncoder.encode("password"));
                user_3.setRoles(Set.of(userRole));
                this.userRepository.saveAll(List.of(user_1, user_2, user_3));

                // Categories
                final Category category_1 = new Category(null, "Electronics", new ArrayList<>());
                final Category category_2 = new Category(null, "Clothing", new ArrayList<>());
                final Category category_3 = new Category(null, "Books", new ArrayList<>());
                this.categoryRepository.saveAll(List.of(category_1, category_2, category_3));
                // Products
                final Product product_1 = new Product(null, "Galaxy Z Flip",
                                "Foldable smartphone with AMOLED display", 50, 999.99, 10.0, null,
                                "default.jpg", category_1, user_1, new ArrayList<>());
                product_1.setSpecialPrice(
                                (product_1.getPrice() / 100) * (100 - product_1.getDiscount()));
                final Product product_2 = new Product(null, "Noise Cancelling Headphones",
                                "Over-ear headphones with active noise cancellation", 120, 199.49,
                                10.0, null, "default.jpg", category_1, user_2, new ArrayList<>());
                product_2.setSpecialPrice(
                                (product_2.getPrice() / 100) * (100 - product_2.getDiscount()));
                final Product product_3 = new Product(null, "4K Action Camera",
                                "Waterproof action camera with 4K recording", 75, 149.99, 30.0,
                                null, "default.jpg", category_1, user_3, new ArrayList<>());
                product_3.setSpecialPrice(
                                (product_3.getPrice() / 100) * (100 - product_3.getDiscount()));
                final Product product_4 = new Product(null, "Graphic Hoodie",
                                "Unisex cotton hoodie with graphic print", 100, 45.0, 50.0, null,
                                "default.jpg", category_2, user_1, new ArrayList<>());
                product_4.setSpecialPrice(
                                (product_4.getPrice() / 100) * (100 - product_4.getDiscount()));
                final Product product_5 = new Product(null, "Slim Fit Jeans",
                                "Denim jeans with a slim fit cut", 80, 55.0, 12.0, null,
                                "default.jpg", category_2, user_2, new ArrayList<>());
                product_5.setSpecialPrice(
                                (product_5.getPrice() / 100) * (100 - product_5.getDiscount()));
                final Product product_6 = new Product(null, "Running Shoes",
                                "Lightweight breathable running shoes", 60, 89.99, 50.0, null,
                                "default.jpg", category_2, user_3, new ArrayList<>());
                product_6.setSpecialPrice(
                                (product_6.getPrice() / 100) * (100 - product_6.getDiscount()));
                final Product product_7 = new Product(null, "The Art of War",
                                "Ancient Chinese military treatise by Sun Tzu", 200, 12.99, 10.0,
                                null, "default.jpg", category_3, user_1, new ArrayList<>());
                product_7.setSpecialPrice(
                                (product_7.getPrice() / 100) * (100 - product_7.getDiscount()));
                final Product product_8 = new Product(null, "Atomic Habits",
                                "A guide to building good habits by James Clear", 80, 55.0, 12.0,
                                null, "default.jpg", category_3, user_2, new ArrayList<>());
                product_8.setSpecialPrice(
                                (product_8.getPrice() / 100) * (100 - product_8.getDiscount()));
                final Product product_9 = new Product(null, "Dune",
                                "Science fiction novel by Frank Herbert", 60, 89.99, 50.0, null,
                                "default.jpg", category_3, user_3, new ArrayList<>());
                product_9.setSpecialPrice(
                                (product_9.getPrice() / 100) * (100 - product_9.getDiscount()));
                this.productRepository.saveAll(List.of(product_1, product_2, product_3, product_4,
                                product_5, product_6, product_7, product_8, product_9));
        }
}
