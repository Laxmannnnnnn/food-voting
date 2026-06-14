package com.example.foodvoting.service;

import com.example.foodvoting.entity.FoodItem;
import com.example.foodvoting.exception.FoodItemNotFoundException;
import com.example.foodvoting.repository.FoodItemRepository;
import com.example.foodvoting.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;
    private final VoteRepository voteRepository;

    public List<FoodItem> findAll() {
        return foodItemRepository.findAll();
    }

    public FoodItem findById(Long id) {
        return foodItemRepository.findById(id)
                .orElseThrow(() -> new FoodItemNotFoundException(id));
    }

    public FoodItem save(FoodItem foodItem) {
        return foodItemRepository.save(foodItem);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!foodItemRepository.existsById(id)) {
            throw new FoodItemNotFoundException(id);
        }
        voteRepository.deleteByFoodItemId(id);
        foodItemRepository.deleteById(id);
    }
}
