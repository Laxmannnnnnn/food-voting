package com.example.foodvoting.config;

import com.example.foodvoting.entity.FoodItem;
import com.example.foodvoting.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final FoodItemRepository foodItemRepository;

    @Override
    public void run(String... args) {
        if (foodItemRepository.count() > 0) return;

        foodItemRepository.save(FoodItem.builder()
                .name("Pizza Margherita")
                .description("Classic pizza with tomato sauce, mozzarella, and basil")
                .category("Italian")
                .imageUrl("/img/pizza.jpg")
                .build());

        foodItemRepository.save(FoodItem.builder()
                .name("Sushi Platter")
                .description("Assorted fresh sushi and sashimi")
                .category("Japanese")
                .imageUrl("/img/sushi.jpg")
                .build());

        foodItemRepository.save(FoodItem.builder()
                .name("Beef Burger")
                .description("Juicy beef patty with lettuce, tomato, and special sauce")
                .category("American")
                .imageUrl("/img/burger.jpg")
                .build());

        foodItemRepository.save(FoodItem.builder()
                .name("Pad Thai")
                .description("Stir-fried rice noodles with shrimp, peanuts, and tamarind")
                .category("Thai")
                .imageUrl("/img/pad-thai.jpg")
                .build());

        foodItemRepository.save(FoodItem.builder()
                .name("Caesar Salad")
                .description("Crisp romaine lettuce with Caesar dressing, croutons, and parmesan")
                .category("Salad")
                .imageUrl("/img/salad.jpg")
                .build());
    }
}
