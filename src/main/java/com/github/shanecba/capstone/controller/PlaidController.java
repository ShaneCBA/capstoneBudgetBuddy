package com.github.shanecba.capstone.controller;

import com.github.shanecba.capstone.entity.PlaidItem;
import com.github.shanecba.capstone.entity.User;
import com.github.shanecba.capstone.repository.UserRepository;
import com.github.shanecba.capstone.util.PlaidUtil;
import com.plaid.client.model.*;
import com.plaid.client.request.PlaidApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path="/plaid")
public class PlaidController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private final PlaidApi plaidClient;

    @Autowired
    private PlaidUtil plaidUtil;

    private final Environment env;
//    private final ApiClient plaidApiClient;

    @Autowired
    public PlaidController(PlaidApi plaidClient, Environment env) {
        this.plaidClient = plaidClient;
        this.env = env;
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

    @RequestMapping("/setup")
    public String getPlaidLinkToken(Model model, @AuthenticationPrincipal OidcUser principal) throws IOException {
        if (principal == null) {
            return "redirect:/";
        }

        String sub = principal.getClaim("sub");
        String name = principal.getClaim("name");
        String email = principal.getClaim("email");

        LinkTokenCreateRequestUser plaidUser = new LinkTokenCreateRequestUser()
                .clientUserId(sub);
        if (!name.isEmpty())
            plaidUser.setLegalName(name);
        if (!email.isEmpty())
            plaidUser.setEmailAddress(email);

        LinkTokenCreateRequest plaidRequest = new LinkTokenCreateRequest()
                .user(plaidUser)
                .clientId(env.getProperty("plaid.clientId")).secret(env.getProperty("plaid.secret"))
                .clientName("Capstone - Saving Gamification")
                .products(Arrays.asList(Products.AUTH, Products.TRANSACTIONS))
                .countryCodes(Arrays.asList(CountryCode.US))
                .language("en");
        Response<LinkTokenCreateResponse> response = plaidClient
                .linkTokenCreate(plaidRequest)
                .execute();
        String linkToken = response.body().getLinkToken();
        model.addAttribute("linkToken", linkToken);
        return "plaidsetup";
    }


    @PostMapping("/getAccessToken")
    @ResponseStatus(value = HttpStatus.OK)
    public void updateLinkToken(@RequestParam(value="public_token") String publicToken,
                                  @AuthenticationPrincipal OidcUser principal) throws IOException {

        String sub = principal.getClaim("sub");
        String username = principal.getClaim("nickname");

        User user = userRepository.findBySub(sub);

        if (user == null) {
            user = new User(sub, username);
        }

        ItemPublicTokenExchangeRequest request = new ItemPublicTokenExchangeRequest()
                .publicToken(publicToken);

        Response<ItemPublicTokenExchangeResponse> response = plaidClient
                .itemPublicTokenExchange(request)
                .execute();
        String accessToken = response.body().getAccessToken();
        String itemId = response.body().getItemId();

        PlaidItem newItem = new PlaidItem(user, itemId, accessToken);

        if (!user.hasItem(itemId)) {
            user.addItem(newItem);
            plaidUtil.updateUser(user);
        }
    }

    @RequestMapping("/showItems")
    public String showItems(Model model) throws IOException {
        User user = (User) model.getAttribute("user");

        if (user == null)
            return "redirect:/";

        Map<String, PlaidItem> items = user.getItems();


        Map<String, List<AccountBase>> itemMap = new HashMap<>();

        for (String itemID : items.keySet()) {
            String accessToken = items.get(itemID).getAccessToken();
            AccountsGetRequest request = new AccountsGetRequest()
                    .accessToken(accessToken);
            Response<AccountsGetResponse> response = plaidClient
                    .accountsGet(request)
                    .execute();

            List<AccountBase> accounts = response.body().getAccounts();
            itemMap.put(itemID, accounts);
        }

        model.addAttribute("items", itemMap);
        return "showItems";
    }
}
