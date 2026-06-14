package com.example.foodvoting.controller;

import com.example.foodvoting.entity.FoodItem;
import com.example.foodvoting.entity.Vote;
import com.example.foodvoting.service.FoodItemService;
import com.example.foodvoting.service.VoteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final FoodItemService foodItemService;
    private final VoteService voteService;

    @Value("${admin.password}")
    private String adminPassword;

    private boolean isNotAdmin(HttpSession session) {
        Object isAdmin = session.getAttribute("isAdmin");
        return isAdmin == null || !(Boolean) isAdmin;
    }

    @GetMapping("/login")
    public String loginForm(HttpSession session, @RequestParam(value = "error", required = false) String error, Model model) {
        if (!isNotAdmin(session)) {
            return "redirect:/admin";
        }
        if (error != null) {
            model.addAttribute("loginError", "Invalid passcode. Please try again.");
        }
        return "admin-login";
    }

    @PostMapping("/login")
    public String login(HttpSession session, @RequestParam("password") String password) {
        if (adminPassword.equals(password)) {
            session.setAttribute("isAdmin", true);
            return "redirect:/admin";
        }
        return "redirect:/admin/login?error=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("isAdmin");
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        if (isNotAdmin(session)) {
            return "redirect:/admin/login";
        }
        model.addAttribute("foodItems", foodItemService.findAll());
        model.addAttribute("results", voteService.getResults());
        return "admin-dashboard";
    }

    @GetMapping("/food/{id}/votes")
    public String viewVotes(HttpSession session, @PathVariable("id") Long id, Model model) {
        if (isNotAdmin(session)) {
            return "redirect:/admin/login";
        }
        FoodItem foodItem = foodItemService.findById(id);
        List<Vote> votes = voteService.getVotesForFoodItem(id);
        model.addAttribute("foodItem", foodItem);
        model.addAttribute("votes", votes);
        return "admin-votes";
    }

    @PostMapping("/votes/{id}/delete")
    public String deleteVote(HttpSession session, @PathVariable("id") Long id, @RequestParam("foodItemId") Long foodItemId) {
        if (isNotAdmin(session)) {
            return "redirect:/admin/login";
        }
        voteService.deleteVoteById(id);
        return "redirect:/admin/food/" + foodItemId + "/votes";
    }

    @PostMapping("/food/{id}/delete")
    public String deleteFood(HttpSession session, @PathVariable("id") Long id) {
        if (isNotAdmin(session)) {
            return "redirect:/admin/login";
        }
        foodItemService.deleteById(id);
        return "redirect:/admin";
    }

    @PostMapping("/votes/reset")
    public String resetVotes(HttpSession session) {
        if (isNotAdmin(session)) {
            return "redirect:/admin/login";
        }
        voteService.resetAllVotes();
        return "redirect:/admin";
    }
}
