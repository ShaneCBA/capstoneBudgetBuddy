package com.github.shanecba.capstone.controller;

import com.github.shanecba.capstone.entity.Goal;
import com.github.shanecba.capstone.entity.Pet;
import com.github.shanecba.capstone.entity.PlaidItem;
import com.github.shanecba.capstone.entity.User;
import com.github.shanecba.capstone.repository.GoalRepository;
import com.github.shanecba.capstone.repository.PetRepository;
import com.github.shanecba.capstone.repository.UserRepository;

import com.github.shanecba.capstone.util.PlaidUtil;
import com.plaid.client.ApiClient;
import com.plaid.client.model.*;
import com.plaid.client.request.PlaidApi;

import org.apache.catalina.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.core.env.Environment;
import org.springframework.data.util.Pair;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping(path="/")
public class MainController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private PlaidUtil plaidUtil;


    @ModelAttribute
    public void addAttributes(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            Map<String, Object> profile = principal.getClaims();

            model.addAttribute("profile", profile);

            String sub = (String) profile.get("sub");

            User user = userRepository.findBySub(sub);
            if (user != null)
                model.addAttribute("user", user);
        }
    }

    @RequestMapping("/")
    public String showIndex(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal == null) {
            return "redirect:/oauth2/authorization/auth0";
        }

        User user;
        user = (User) model.getAttribute("user");

        if (user == null) {
            return  "redirect:/plaid/setup";
        }

        if (user.countItems() == 0) {
            return "redirect:/plaid/setup";
        }

        if (user.getPet() == null) {
            return "redirect:/configPet";
        }

        if (user.getGoal() == null) {
            return "redirect:/configGoal";
        }

        return "app";
    }

    @GetMapping("/configGoal")
    public String showGoalSetupPage(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal == null) {
            return "redirect:/";
        }

        User user = (User) model.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        Goal goal = user.getGoal();

        if (goal == null)
            goal = new Goal(user);

        model.addAttribute("goal", goal);
        return "setupGoal";
    }

    @PostMapping("/configGoal")
    public String showGoalSetupPage(@ModelAttribute Goal newGoal, Model model, @AuthenticationPrincipal OidcUser principal) {
        System.out.println(newGoal);
        if (newGoal == null) {
            return "redirect:/configGoal";
        }

        if (principal == null) {
            return "redirect:/";
        }

        User user = (User) model.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        Goal goal = user.getGoal();

        if (goal == null) {
            user.setGoal(newGoal);
            newGoal.setUser(user);
            goalRepository.save(newGoal);
        }
        else {
            goal.update(newGoal);
            goalRepository.save(goal);
        }

        return "redirect:/";
    }

    @GetMapping("/configPet")
    public String showPetSetupPage(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal == null) {
            return "redirect:/";
        }

        User user = (User) model.getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        Pet pet = user.getPet();

        if (pet == null) {
            pet = new Pet();
            pet.setUser(user);
//            petRepository.save(pet);
        }

        model.addAttribute("pet", pet);

        return "setupPet";
    }

    @PostMapping("/configPet")
    public String showPetSetupPage(@ModelAttribute Pet newPet, Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal == null) {
            return "redirect:/";
        }

        User user = (User) model.getAttribute("user");

        if (user == null)
            return "redirect:/";

        Pet pet = user.getPet();
        //Return them to the set up page. Something went wrong
        if (pet == null && petRepository.findById(newPet.getId()).isPresent()) {
            System.err.println("User attempting to overwrite pet data");
            return "redirect:/configPet";
        } else if (pet == null) {
            System.out.println("\n\nSetting up a pet for the user\n\n");
            System.out.println(newPet);
            System.out.println("^PET^");
            newPet.setUser(user);
            pet = newPet;
        } else {
            pet.update(newPet);
        }
        petRepository.save(pet);

        return "redirect:/";
    }

}
