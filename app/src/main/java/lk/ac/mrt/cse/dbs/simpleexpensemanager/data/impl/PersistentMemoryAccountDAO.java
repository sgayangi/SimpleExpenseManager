package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;


import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DatabaseHelper;

public class PersistentMemoryAccountDAO  implements AccountDAO {
    DatabaseHelper db;
    public PersistentMemoryAccountDAO(DatabaseHelper db){

        this.db = db;
    }

    @Override
    public List<String> getAccountNumbersList(){
        return db.getAccountNumbersList();
    }

    @Override
    public List<Account> getAccountsList() {
        return db.getAllAccounts();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        if (accountNo==null){
            throw new InvalidAccountException("Invalid Account Number");
        }
        return db.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        db.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (accountNo==null){
            throw new InvalidAccountException("Invalid Account Number");
        }
        db.deleteAccount(accountNo);

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (accountNo==null){
            throw new InvalidAccountException("Invalid Account Number");
        }
        Account account = db.getAccount(accountNo);
        double balance = account.getBalance();
        if(expenseType.toString().equals("Income")){
            account.setBalance(balance+amount);
        }else if (expenseType.toString().equals("Expense")){
            account.setBalance(balance-amount);
        }
        if(account.getBalance()<0 ){
            throw new InvalidAccountException("Insufficient credit");
        }
        else{
            db.updateBalance(account);
        }
    }
}
