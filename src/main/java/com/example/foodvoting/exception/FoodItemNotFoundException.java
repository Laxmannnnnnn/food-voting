package com.example.foodvoting.exception;

public class FoodItemNotFoundException extends RuntimeException {

    public FoodItemNotFoundException(Long id) {
        super("Food item not found: " + id);
    }
}
