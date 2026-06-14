package com.example.foodvoting.repository;

import com.example.foodvoting.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findByFoodItemIdOrderByCreatedAtDesc(Long foodItemId);

    @Modifying
    void deleteByFoodItemId(Long foodItemId);

    @Query("SELECT v.foodItem.id, v.foodItem.name, AVG(v.rating) as avgRating, COUNT(v) as voteCount " +
           "FROM Vote v GROUP BY v.foodItem.id, v.foodItem.name ORDER BY avgRating DESC")
    List<Object[]> findVoteResults();
}
