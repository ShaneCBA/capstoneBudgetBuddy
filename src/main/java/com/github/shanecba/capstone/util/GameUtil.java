package com.github.shanecba.capstone.util;

import com.github.shanecba.capstone.entity.*;
import com.github.shanecba.capstone.types.ItemCategory;
import com.github.shanecba.capstone.types.PetCondition;
import jdk.jfr.Category;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GameUtil {
    @Value("${game.weekly-credit}")
    private int weeklyCredit;

    public GameUtil() {

    }
    public GameUtil(int weeklyCredit) {
        this.weeklyCredit = weeklyCredit;
    }

    public int calculatePointUsage(PlaidTransaction plaidTransaction, User user) {
        return (int) ((weeklyCredit/2) * (plaidTransaction.getAmount()/user.getGoal().getTarget()));
    }

    public Transaction generateTransaction(User user, Goal goal, PlaidTransaction plaidTransaction) {
        Transaction transaction = new Transaction(user, user.getGoal());

        transaction.setPlaidTransaction(plaidTransaction);

        transaction.setAmount(calculatePointUsage(plaidTransaction, user));

        user.deductPoints(transaction.getAmount());

        return transaction;
    }

    public boolean useItem(InventoryItem item, User user) {
        ItemCategory category = item.getCategory();

        switch (category) {
            case HAPPINESS:
                return user.getPet().incrementHappiness();
            case FOOD:
                return user.getPet().incrementSatiation();
            case CONDITION: {
                if (user.getPet().getCondition() != PetCondition.NORMAL) {
                    user.getPet().setCondition(PetCondition.NORMAL);
                    return true;
                }
            }
        }
        return false;
    }
}
