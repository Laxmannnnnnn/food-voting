package com.example.foodvoting.controller;

import com.example.foodvoting.entity.FoodItem;
import com.example.foodvoting.service.FoodItemService;
import com.example.foodvoting.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final FoodItemService foodItemService;
    private final VoteService voteService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("foodItems", foodItemService.findAll());
        return "index";
    }

    @GetMapping("/results")
    public String results(Model model) {
        model.addAttribute("results", voteService.getResults());
        return "results";
    }

    @GetMapping("/food/new")
    public String newFoodForm(Model model) {
        model.addAttribute("foodItem", new FoodItem());
        return "add-food";
    }

    @PostMapping("/food/new")
    public String addFood(@Valid @ModelAttribute FoodItem foodItem, BindingResult result) {
        if (result.hasErrors()) {
            return "add-food";
        }
        foodItemService.save(foodItem);
        return "redirect:/";
    }

    @PostMapping("/vote")
    public String vote(@RequestParam Long foodItemId,
                       @RequestParam String voterName,
                       @RequestParam int rating,
                       @RequestParam(required = false) String comment) {
        voteService.castVote(foodItemId, voterName, rating, comment);
        return "redirect:/results";
    }

    @PostMapping("/food/{id}/delete")
    public String deleteFood(@PathVariable Long id) {
        foodItemService.deleteById(id);
        return "redirect:/";
    }
}
