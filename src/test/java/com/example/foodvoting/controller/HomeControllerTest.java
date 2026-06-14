package com.example.foodvoting.controller;

import com.example.foodvoting.entity.FoodItem;
import com.example.foodvoting.service.FoodItemService;
import com.example.foodvoting.service.VoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FoodItemService foodItemService;

    @MockitoBean
    private VoteService voteService;

    @Test
    void index_returnsViewWithFoodItems() throws Exception {
        when(foodItemService.findAll()).thenReturn(List.of(
                FoodItem.builder().id(1L).name("Pizza").build()
        ));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("foodItems"));
    }

    @Test
    void results_returnsViewWithResults() throws Exception {
        when(voteService.getResults()).thenReturn(List.of());

        mockMvc.perform(get("/results"))
                .andExpect(status().isOk())
                .andExpect(view().name("results"))
                .andExpect(model().attributeExists("results"));
    }

    @Test
    void newFoodForm_returnsFormView() throws Exception {
        mockMvc.perform(get("/food/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-food"))
                .andExpect(model().attributeExists("foodItem"));
    }
}
