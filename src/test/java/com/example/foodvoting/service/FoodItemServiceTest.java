package com.example.foodvoting.service;

import com.example.foodvoting.entity.FoodItem;
import com.example.foodvoting.exception.FoodItemNotFoundException;
import com.example.foodvoting.repository.FoodItemRepository;
import com.example.foodvoting.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodItemServiceTest {

    @Mock
    private FoodItemRepository repository;

    @Mock
    private VoteRepository voteRepository;

    private FoodItemService service;

    @BeforeEach
    void setUp() {
        service = new FoodItemService(repository, voteRepository);
    }

    @Test
    void findAll_returnsAllItems() {
        when(repository.findAll()).thenReturn(List.of(new FoodItem()));
        assertThat(service.findAll()).hasSize(1);
    }

    @Test
    void findById_existing_returnsItem() {
        FoodItem item = FoodItem.builder().id(1L).name("Pizza").build();
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        assertThat(service.findById(1L).getName()).isEqualTo("Pizza");
    }

    @Test
    void findById_missing_throws() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(FoodItemNotFoundException.class)
                .hasMessage("Food item not found: 99");
    }

    @Test
    void save_delegatesToRepository() {
        FoodItem item = FoodItem.builder().name("Burger").build();
        service.save(item);
        verify(repository).save(item);
    }

    @Test
    void deleteById_removesVotesThenFoodItem() {
        when(repository.existsById(1L)).thenReturn(true);
        service.deleteById(1L);
        verify(voteRepository).deleteByFoodItemId(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void deleteById_missing_throws() {
        when(repository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> service.deleteById(99L))
                .isInstanceOf(FoodItemNotFoundException.class)
                .hasMessage("Food item not found: 99");
        verifyNoInteractions(voteRepository);
        verify(repository, never()).deleteById(any());
    }
}
