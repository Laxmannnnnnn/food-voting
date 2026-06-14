package com.example.foodvoting.controller;

import com.example.foodvoting.entity.FoodItem;
import com.example.foodvoting.service.FoodItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food-items")
@RequiredArgsConstructor
public class FoodItemRestController {

    private final FoodItemService foodItemService;

    @GetMapping
    public List<FoodItem> getAll() {
        return foodItemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItem> getById(@PathVariable Long id) {
        return ResponseEntity.ok(foodItemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<FoodItem> create(@Valid @RequestBody FoodItem foodItem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(foodItemService.save(foodItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        foodItemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
