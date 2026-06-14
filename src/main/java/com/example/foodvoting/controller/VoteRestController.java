package com.example.foodvoting.controller;

import com.example.foodvoting.entity.Vote;
import com.example.foodvoting.service.VoteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteRestController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<Vote> castVote(@Valid @RequestBody VoteRequest request) {
        Vote vote = voteService.castVote(
                request.foodItemId(),
                request.voterName(),
                request.rating(),
                request.comment());
        return ResponseEntity.status(HttpStatus.CREATED).body(vote);
    }

    @GetMapping("/results")
    public List<Map<String, Object>> getResults() {
        return voteService.getResults();
    }

    @GetMapping("/food-item/{foodItemId}")
    public List<Vote> getByFoodItem(@PathVariable Long foodItemId) {
        return voteService.getVotesForFoodItem(foodItemId);
    }

    public record VoteRequest(
            @NotNull Long foodItemId,
            @NotBlank @Size(max = 120) String voterName,
            @Min(1) @Max(5) int rating,
            @Size(max = 1000) String comment) {}
}
