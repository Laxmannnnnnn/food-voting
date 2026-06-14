package com.example.foodvoting.service;

import com.example.foodvoting.entity.FoodItem;
import com.example.foodvoting.entity.Vote;
import com.example.foodvoting.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final FoodItemService foodItemService;

    public Vote castVote(Long foodItemId, String voterName, int rating, String comment) {
        String trimmedVoterName = voterName == null ? "" : voterName.trim();
        String trimmedComment = comment == null ? null : comment.trim();
        if (trimmedVoterName.isBlank()) {
            throw new IllegalArgumentException("Voter name is required");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        FoodItem foodItem = foodItemService.findById(foodItemId);
        Vote vote = Vote.builder()
                .foodItem(foodItem)
                .voterName(trimmedVoterName)
                .rating(rating)
                .comment(trimmedComment)
                .build();
        return voteRepository.save(vote);
    }

    public List<Vote> getVotesForFoodItem(Long foodItemId) {
        return voteRepository.findByFoodItemIdOrderByCreatedAtDesc(foodItemId);
    }

    public List<Map<String, Object>> getResults() {
        List<Object[]> raw = voteRepository.findVoteResults();
        List<Map<String, Object>> results = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("foodItemId", row[0]);
            map.put("foodItemName", row[1]);
            map.put("avgRating", Math.round(((Double) row[2]) * 100.0) / 100.0);
            map.put("voteCount", row[3]);
            results.add(map);
        }
        return results;
    }

    public void deleteVoteById(Long voteId) {
        if (!voteRepository.existsById(voteId)) {
            throw new IllegalArgumentException("Vote not found: " + voteId);
        }
        voteRepository.deleteById(voteId);
    }

    public void resetAllVotes() {
        voteRepository.deleteAll();
    }
}
