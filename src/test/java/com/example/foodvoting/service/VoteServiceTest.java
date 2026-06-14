package com.example.foodvoting.service;

import com.example.foodvoting.entity.FoodItem;
import com.example.foodvoting.entity.Vote;
import com.example.foodvoting.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private FoodItemService foodItemService;

    private VoteService voteService;

    @BeforeEach
    void setUp() {
        voteService = new VoteService(voteRepository, foodItemService);
    }

    @Test
    void castVote_valid_savesAndReturns() {
        FoodItem item = FoodItem.builder().id(1L).name("Pizza").build();
        when(foodItemService.findById(1L)).thenReturn(item);
        when(voteRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Vote result = voteService.castVote(1L, " Alice ", 5, " Great! ");

        assertThat(result.getVoterName()).isEqualTo("Alice");
        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Great!");
        assertThat(result.getFoodItem()).isEqualTo(item);
        verify(voteRepository).save(any());
    }

    @Test
    void castVote_invalidRating_throws() {
        assertThatThrownBy(() -> voteService.castVote(1L, "Alice", 0, ""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> voteService.castVote(1L, "Alice", 6, ""))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(voteRepository);
    }

    @Test
    void castVote_blankVoterName_throws() {
        assertThatThrownBy(() -> voteService.castVote(1L, " ", 5, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Voter name is required");
        verifyNoInteractions(voteRepository);
    }

    @Test
    void getResults_returnsAggregatedData() {
        when(voteRepository.findVoteResults()).thenReturn(List.of(
                new Object[]{1L, "Pizza", 4.5d, 10L},
                new Object[]{2L, "Burger", 3.2d, 5L}
        ));

        var results = voteService.getResults();

        assertThat(results).hasSize(2);
        assertThat(results.get(0).get("foodItemName")).isEqualTo("Pizza");
        assertThat(results.get(0).get("avgRating")).isEqualTo(4.5d);
        assertThat(results.get(0).get("voteCount")).isEqualTo(10L);
    }

    @Test
    void deleteVoteById_existing_deletes() {
        when(voteRepository.existsById(1L)).thenReturn(true);
        voteService.deleteVoteById(1L);
        verify(voteRepository).deleteById(1L);
    }

    @Test
    void deleteVoteById_missing_throws() {
        when(voteRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> voteService.deleteVoteById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Vote not found: 99");
        verify(voteRepository, never()).deleteById(any());
    }

    @Test
    void resetAllVotes_deletesAll() {
        voteService.resetAllVotes();
        verify(voteRepository).deleteAll();
    }
}
