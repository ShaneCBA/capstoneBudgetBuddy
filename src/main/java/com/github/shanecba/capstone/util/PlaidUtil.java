package com.github.shanecba.capstone.util;

import com.github.shanecba.capstone.entity.Account;
import com.github.shanecba.capstone.entity.PlaidItem;
import com.github.shanecba.capstone.entity.PlaidTransaction;
import com.github.shanecba.capstone.entity.User;
import com.github.shanecba.capstone.repository.PlaidTransactionRepository;
import com.github.shanecba.capstone.repository.TransactionRepository;
import com.github.shanecba.capstone.repository.UserRepository;
import com.github.shanecba.capstone.types.TransactionState;
import com.plaid.client.model.*;
import com.plaid.client.request.PlaidApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PlaidUtil {
    @Autowired
    private final PlaidApi plaidClient;
    @Autowired
    private GameUtil gameUtil;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlaidTransactionRepository plaidTransactionRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public PlaidUtil(PlaidApi plaidClient) {
        this.plaidClient = plaidClient;
    }

    public void updateUser(User user) {
        Map<String, PlaidItem> items = user.getItems();

        boolean changed = false;

        for (PlaidItem item : items.values()) {
            LocalDateTime lastUpdate = getItemLastTransaction(item);
            if (item.getLastUpdate().isBefore(lastUpdate)){
                if (updateItemAccounts(item))
                    changed = true;
                item.setLastUpdate(lastUpdate);
            }
        }

        if (changed) {
            userRepository.save(user);
            List<PlaidTransaction> plaidTransactions = new ArrayList<>();
            for (PlaidItem item : user.getItems().values()) {
                for (Account account : item.getAccountMap().values()) {
                    List<PlaidTransaction> accountTransactions =
                            plaidTransactionRepository.findByDateGreaterThanAndAccount(
                                    LocalDate.now().minusDays(1).with(DayOfWeek.FRIDAY), account
                            );
                    for (PlaidTransaction plaidTransaction : accountTransactions) {
                        if (transactionRepository.findByPlaidTransaction(plaidTransaction).size() == 0 && plaidTransaction.getState().equals(TransactionState.posted)) {

                            com.github.shanecba.capstone.entity.Transaction transaction =
                                    gameUtil.generateTransaction(user, user.getGoal(), plaidTransaction);

                            transactionRepository.save(transaction);
                        }
                    }
                    plaidTransactions.addAll(
                            accountTransactions
                    );
                }
            }
        }
    }



    public LocalDateTime getItemLastTransaction(PlaidItem plaidItem) {
        ItemGetRequest request = new ItemGetRequest()
                .accessToken(plaidItem.getAccessToken());
        try {
            Response<ItemGetResponse> response = plaidClient.itemGet(request).execute();
            return response.body()
                    .getStatus()
                    .getTransactions()
                    .getLastSuccessfulUpdate()
                    .toLocalDateTime();
        } catch (IOException e) {
            System.err.printf("Could not retrieve status of plaid item with id %s\n", plaidItem.getItemId());
            e.printStackTrace();
        }
        return plaidItem.getLastUpdate();
    }
    private boolean updateItemAccounts(PlaidItem item){
        boolean changed = false;

        List<AccountBase> accountBases = getAccountsFromPlaidForItem(item);
        Map<String, Account> accounts = item.getAccountMap();

        for (AccountBase base : accountBases) {
            //TODO : allow user to also consider credit card usage data (AccountType.CREDIT)
            if (base.getType() == AccountType.DEPOSITORY) {
                Account account = accounts.get(base.getAccountId());
                if (account == null) {
                    accounts.put(base.getAccountId(), convertAccountBaseToAccount(item, base));
                    changed = true;
                } else if (updateAccountTransaction(account))
                    changed = true;
            }
        }
        return changed;
    }

    private boolean updateAccountTransaction(Account account) {
        PlaidItem item = account.getItem();

        boolean changed = false;
        LocalDate firstDate;
        LocalDate lastDate = LocalDate.now();

        Response<TransactionsGetResponse> response;

        List<PlaidTransaction> dbTransactions = plaidTransactionRepository.findFirstByAccountOrderByDateDesc(account);

        if (dbTransactions.size() > 0)
            firstDate = dbTransactions.get(0).getDate().minusDays(1);
        else// If the account's transactions have yet to be read (or if there are no recorded transaction),
            // get all from six months ago up to now
            firstDate = LocalDate.now().minusMonths(6);

        // Pull transactions for a date range
        TransactionsGetRequest request = new TransactionsGetRequest()
                .accessToken(account.getItem().getAccessToken())
                .startDate(firstDate)
                .endDate(lastDate);

        try {
            response = plaidClient.transactionsGet(request).execute();

            List<Transaction> transactions = new ArrayList <Transaction>();

            transactions.addAll(response.body().getTransactions());
            // Manipulate the offset parameter to paginate
            // transactions and retrieve all available data
            while (transactions.size() < response.body().getTotalTransactions()) {
                TransactionsGetRequestOptions options = new TransactionsGetRequestOptions()
                        .offset(transactions.size());
                request = new TransactionsGetRequest()
                        .accessToken(item.getAccessToken())
                        .startDate(firstDate)
                        .endDate(lastDate)
                        .options(options);
                response = plaidClient.transactionsGet(request).execute();
                transactions.addAll(response.body().getTransactions());
            }
// TODO : place this for loop in the above while loop to improve performance
            for (Transaction transaction : transactions) {
                if (plaidTransactionRepository.findByTransactionID(transaction.getTransactionId()).size() == 0) {
                    if (!transaction.getPending() && transaction.getTransactionId() != null)
                    {
                        List<PlaidTransaction> pendingTransactionsWithId = plaidTransactionRepository.findByTransactionID(transaction.getTransactionId());
                        if (pendingTransactionsWithId.size() > 0) {
                            PlaidTransaction pending = pendingTransactionsWithId.get(0);
                            account.removeTransaction(pending);
                            pending.setState(TransactionState.posted);
                            account.addTransaction(pending);
                            changed = true;
//                            plaidTransactionRepository.save(pending);
                        }
                        else
                            account.addTransaction(convertTransactionToPlaidTransaction(account, transaction));
                    }
                    else
                        account.addTransaction(convertTransactionToPlaidTransaction(account, transaction));

                    changed = true;
                }
            }

        } catch (IOException e) {
            System.err.printf("Error retrieving transactions for account %s, item %s, date range %s-%s\n",
                    account.getAccountId(), item.getItemId(), firstDate.toString(), lastDate.toString());
            e.printStackTrace();
        }

        return changed;
    }

    private List<AccountBase> getAccountsFromPlaidForItem(PlaidItem item) {
        String accessToken = item.getAccessToken();
        AccountsGetRequest request = new AccountsGetRequest()
                .accessToken(accessToken);
        List<AccountBase> accountBases = null;
        try {
            Response<AccountsGetResponse> response = plaidClient
                    .accountsGet(request)
                    .execute();
            accountBases = response.body().getAccounts();

        } catch (IOException e) {
            System.err.println("Could not retrieve accounts for ID : " + item.getItemId());
            e.printStackTrace();
        }

        return accountBases;
    }

    private Account convertAccountBaseToAccount(PlaidItem item, AccountBase base) {
        Account account = new Account(base.getAccountId(), item, base.getName(), base.getType());
        return account;
    }

    private PlaidTransaction convertTransactionToPlaidTransaction(Account account, Transaction transaction) {
        PlaidTransaction plaidTransaction = new PlaidTransaction();
        plaidTransaction.setTransactionID(transaction.getTransactionId());
        plaidTransaction.setAccount(account);
        plaidTransaction.setDate(transaction.getDate());
        plaidTransaction.setCategory(transaction.getCategoryId());
        plaidTransaction.setState(transaction.getPending() ? TransactionState.pending : TransactionState.posted);
        plaidTransaction.setAmount(transaction.getAmount());
        plaidTransaction.setName(transaction.getName());
        if (transaction.getMerchantName() != null)
            plaidTransaction.setMerchant(transaction.getMerchantName());
        return plaidTransaction;
    }

    public List<Category> getTransactionCategories() {
        try {
            Response<CategoriesGetResponse> response = plaidClient.categoriesGet(new Object()).execute();
            List<Category> categories =
                    response.body().getCategories();
            return categories;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
