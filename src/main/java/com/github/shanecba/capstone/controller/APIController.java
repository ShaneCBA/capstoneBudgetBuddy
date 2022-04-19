package com.github.shanecba.capstone.controller;

import com.github.shanecba.capstone.entity.*;
import com.github.shanecba.capstone.repository.InventoryItemRepository;
import com.github.shanecba.capstone.repository.InventoryRepository;
import com.github.shanecba.capstone.repository.TransactionRepository;
import com.github.shanecba.capstone.repository.UserRepository;
import com.github.shanecba.capstone.util.GameUtil;
import com.github.shanecba.capstone.util.PlaidUtil;
import com.plaid.client.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
//@ResponseBody
@RequestMapping(path="/api")
public class APIController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryItemRepository storeRepository;

    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PlaidUtil plaidUtil;
    @Autowired
    private GameUtil gameUtil;

    public APIController(UserRepository userRepository, InventoryItemRepository storeRepository, InventoryRepository inventoryRepository, PlaidUtil plaidUtil, GameUtil gameUtil, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.inventoryRepository = inventoryRepository;
        this.plaidUtil = plaidUtil;
        this.gameUtil = gameUtil;
        this.transactionRepository = transactionRepository;
    }

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

    @RequestMapping("/storeItems")
    public ResponseEntity<Map> getStoreItems() {
        Map<String, Object> response = new HashMap<>();

        List<InventoryItem> storeItems = new ArrayList<>();
        Iterator<InventoryItem> items = storeRepository.findAll().iterator();

        while (items.hasNext()) {
            storeItems.add(items.next());
        }

        response.put("storeItems", storeItems);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/buyItem")
    public ResponseEntity<Map> buyStoreItems(@RequestParam(name="id") int id, Model model) {
        Map<String, Object> response = new HashMap<>();

        User user = (User) model.getAttribute("user");

        if (user == null) {
            response.put("error", "User is not logged in");
            return ResponseEntity.ok(response);
        }

        Optional<InventoryItem> itemOptional = storeRepository.findById(id);
        if (itemOptional.isEmpty()) {
            response.put("error", "Could not find item of given ID");
            return ResponseEntity.ok(response);
        }

        InventoryItem item = itemOptional.get();

        if (user.getPoints() < item.getCost()) {
            response.put("error", "Not enough points");
            response.put("points", user.getPoints());
            return ResponseEntity.ok(response);
        }

        Inventory addedItem = new Inventory(user, item);
        user.deductPoints(item.getCost());
        inventoryRepository.save(addedItem);

        response.put("points", user.getPoints());

        response.put("item", addedItem);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/useItem")
    public ResponseEntity<Map> useInventoryItems(@RequestParam(name="id") int id, Model model) {
        Map<String, Object> response = new HashMap<>();

        User user = (User) model.getAttribute("user");

        if (user == null) {
            response.put("error", "User is not logged in");
            return ResponseEntity.ok(response);
        }

        Optional<Inventory> itemOptional = inventoryRepository.findById(id);

        if (itemOptional.isEmpty()) {
            response.put("error", "Could not find item of given ID: " + Integer.toString(id));
            return ResponseEntity.ok(response);
        }

        Inventory item = itemOptional.get();

        if (item.getUser().getId() != user.getId()) {
            response.put("error", "This item does not belong to the user");
            return ResponseEntity.ok(response);
        }

        if (gameUtil.useItem(item.getItem(), user))
            inventoryRepository.deleteById(id);
        else
            response.put("alert", "Your pet doesn't need that right now");//TODO replace with status code

        response.put("pet", user.getPet());

        response.put("inventory", user.getInventory());

        return ResponseEntity.ok(response);
    }

    @RequestMapping("/goalInfo")
    public ResponseEntity<Map> getUserGoal(Model model) {
        Map<String, Object> response = new HashMap<>();

        User user = (User) model.getAttribute("user");
        if (user == null) {
            response.put("error", "User not logged in");
            return ResponseEntity.ok(response);
        }

        Goal goal = user.getGoal();

        response.put("goal", goal);

        return ResponseEntity.ok(response);
    }

    @RequestMapping("/userInfo")
    public ResponseEntity<Map> getUserInfo(Model model) {
        Map<String, Object> response = new HashMap<>();

        User user = (User) model.getAttribute("user");
        if (user == null) {
            response.put("error", "User not logged in");
            return ResponseEntity.ok(response);
        }

        plaidUtil.updateUser(user);

        response.put("user", user);

        return ResponseEntity.ok(response);
    }

    @RequestMapping("/petInfo")
    public ResponseEntity<Map> getPetInfo(Model model) {
        Map<String, Object> response = new HashMap<>();

        User user = (User) model.getAttribute("user");
        if (user == null) {
            response.put("error", "User not logged in");
            return ResponseEntity.ok(response);
        }

        plaidUtil.updateUser(user);

        response.put("pet", user.getPet());

        return ResponseEntity.ok(response);
    }

    @RequestMapping("/latestTransactions")
    public ResponseEntity<Map> getLatestTransactions(Model model) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) model.getAttribute("user");
        if (user == null) {
            response.put("error", "User not logged in");
            return ResponseEntity.ok(response);
        }

        List<Transaction> transactions = transactionRepository.findByPlaidTransactionDateGreaterThanAndUser(LocalDate.now().minusWeeks(1), user);

        response.put("transactions", transactions);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/latestTransactions", params = {"start","end"})
    public ResponseEntity<Map> getLatestTransactionsFromTo(@RequestParam("start") LocalDate start, @RequestParam("end") LocalDate end, Model model) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) model.getAttribute("user");
        if (user == null) {
            response.put("error", "User not logged in");
            return ResponseEntity.ok(response);
        }

        List<Transaction> transactions = transactionRepository.findByPlaidTransactionDateGreaterThanAndPlaidTransactionDateLessThanEqualAndUser(start, end, user);

        response.put("transactions", transactions);

        return ResponseEntity.ok(response);
    }
    @GetMapping(value = "/latestTransactions", params = {"start"})
    public ResponseEntity<Map> getLatestTransactionsFromTo(@RequestParam("start") LocalDate start, Model model) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) model.getAttribute("user");
        if (user == null) {
            response.put("error", "User not logged in");
            return ResponseEntity.ok(response);
        }

        List<Transaction> transactions = transactionRepository.findByPlaidTransactionDateGreaterThanAndPlaidTransactionDateLessThanEqualAndUser(start, LocalDate.now(), user);

        response.put("transactions", transactions);

        return ResponseEntity.ok(response);
    }

    @RequestMapping("/getTransactionCategories")
    public ResponseEntity<Map> getTransactionCategory(Model model) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) model.getAttribute("user");
        if (user == null) {
            response.put("error", "User not logged in");
            return ResponseEntity.ok(response);
        }

        List<Category> categories = plaidUtil.getTransactionCategories();
        if (categories == null) {
            response.put("error", "Could not retrieve groups from group id");
            return ResponseEntity.ok(response);
        }

        response.put("categories", categories);
        return ResponseEntity.ok(response);
    }

    @RequestMapping("/getInventory")
    public ResponseEntity<Map> getTransactions(Model model) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) model.getAttribute("user");
        if (user == null) {
            response.put("error", "User not logged in");
            return ResponseEntity.ok(response);
        }

        plaidUtil.updateUser(user);
//
//        Map<Integer, InventoryItem> items = new HashMap<>();
//
//        for (Inventory item : user.getInventory()) {
//            items.put(item.getId(), item.getItem());
//        }

        response.put("inventory", user.getInventory());



        return ResponseEntity.ok(response);
    }
}
