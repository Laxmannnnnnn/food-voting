package com.example.foodvoting.controller;

import com.example.foodvoting.entity.FoodItem;
import com.example.foodvoting.service.FoodItemService;
import com.example.foodvoting.service.VoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@TestPropertySource(properties = "admin.password=testadmin")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FoodItemService foodItemService;

    @MockitoBean
    private VoteService voteService;

    @Test
    void dashboard_withoutSession_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/login"));
    }

    @Test
    void loginForm_returnsView() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-login"));
    }

    @Test
    void login_withCorrectPassword_setsSessionAndRedirects() throws Exception {
        mockMvc.perform(post("/admin/login")
                        .param("password", "testadmin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(request().sessionAttribute("isAdmin", true));
    }

    @Test
    void login_withIncorrectPassword_redirectsToLoginWithError() throws Exception {
        mockMvc.perform(post("/admin/login")
                        .param("password", "wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/login?error=true"))
                .andExpect(request().sessionAttributeDoesNotExist("isAdmin"));
    }

    @Test
    void logout_clearsSessionAndRedirects() throws Exception {
        mockMvc.perform(get("/admin/logout").sessionAttr("isAdmin", true))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(request().sessionAttributeDoesNotExist("isAdmin"));
    }

    @Test
    void dashboard_withSession_rendersDashboard() throws Exception {
        when(foodItemService.findAll()).thenReturn(List.of());
        when(voteService.getResults()).thenReturn(List.of());

        mockMvc.perform(get("/admin").sessionAttr("isAdmin", true))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-dashboard"))
                .andExpect(model().attributeExists("foodItems", "results"));
    }

    @Test
    void viewVotes_withSession_rendersVotes() throws Exception {
        FoodItem item = FoodItem.builder().id(1L).name("Pizza").build();
        when(foodItemService.findById(1L)).thenReturn(item);
        when(voteService.getVotesForFoodItem(1L)).thenReturn(List.of());

        mockMvc.perform(get("/admin/food/1/votes").sessionAttr("isAdmin", true))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-votes"))
                .andExpect(model().attributeExists("foodItem", "votes"));
    }

    @Test
    void deleteVote_withSession_deletesAndRedirects() throws Exception {
        mockMvc.perform(post("/admin/votes/2/delete")
                        .sessionAttr("isAdmin", true)
                        .param("foodItemId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/food/1/votes"));

        verify(voteService).deleteVoteById(2L);
    }

    @Test
    void deleteFood_withSession_deletesAndRedirects() throws Exception {
        mockMvc.perform(post("/admin/food/1/delete").sessionAttr("isAdmin", true))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(foodItemService).deleteById(1L);
    }

    @Test
    void resetVotes_withSession_resetsAndRedirects() throws Exception {
        mockMvc.perform(post("/admin/votes/reset").sessionAttr("isAdmin", true))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(voteService).resetAllVotes();
    }
}
