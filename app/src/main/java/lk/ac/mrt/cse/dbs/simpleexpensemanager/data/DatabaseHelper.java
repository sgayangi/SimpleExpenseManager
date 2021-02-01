package lk.ac.mrt.cse.dbs.simpleexpensemanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DatabaseHelper  extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "180587d.db";
    public static final String ACCOUNTS_TABLE = "accounts";
    public static final String TRANSACTIONS_TABLE = "transactions";
    public static SQLiteDatabase db;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ACCOUNTS_TABLE = "CREATE TABLE IF NOT EXISTS " + ACCOUNTS_TABLE
                + "(account_no TEXT PRIMARY KEY UNIQUE, bank TEXT, account_holder TEXT, balance NUMERIC(10,2))";

        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE IF NOT EXISTS "+TRANSACTIONS_TABLE
                +"(transaction_no INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, account_no TEXT, type TEXT,amount NUMERIC (10,2),"
                +"FOREIGN KEY(account_no) REFERENCES accounts(account_no))";

        db.execSQL(CREATE_ACCOUNTS_TABLE);
        db.execSQL(CREATE_TRANSACTIONS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TRANSACTIONS_TABLE);
        onCreate(db);
    }

    public void addAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("account_no", account.getAccountNo());
        values.put("bank", account.getBankName());
        values.put("account_holder", account.getAccountHolderName());
        values.put("balance", account.getBalance());

        // Inserting Row
        db.insert(ACCOUNTS_TABLE, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection

    }

    public Account getAccount(String account_no){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ACCOUNTS_TABLE, new String[] { "bank",
                        "account_holder", "balance" }, "account_no" + "=?",
                new String[] { String.valueOf(account_no) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
            Account account = new Account(account_no,
                    cursor.getString(0), cursor.getString(1),Integer.parseInt(cursor.getString(2)));
            // return contact
            cursor.close();
            return account; // Closing database connection
        }


    public  void deleteAccount(String account_no){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(ACCOUNTS_TABLE, "account_id" + " = ?",
                new String[] { account_no});
        db.close();

    }

    public  List<String> getAccountNumbersList() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> accountNumbersList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ACCOUNTS_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        Transaction transaction;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                accountNumbersList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return accountNumbersList;
    }

    public  List<Account> getAllAccounts() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Account> accountsList = new ArrayList<Account>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ACCOUNTS_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        Account account;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                account = new Account(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        Integer.parseInt(cursor.getString(3)));
                accountsList.add(account);

            } while (cursor.moveToNext());
        }
        cursor.close();
        System.out.println("Transactions");
        System.out.println(accountsList.toString());
        // return contact list
        return accountsList;
    }

    public  boolean updateBalance(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("bank",account.getBankName());
        contentValues.put("account_holder",account.getAccountHolderName());
        contentValues.put("balance",account.getBalance());
        long result = db.update(ACCOUNTS_TABLE,contentValues,"account_no = ?",new String[]{account.getAccountNo()});
        return result != -1;
    }

    public  void addTransaction(Transaction transaction){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("date", transaction.getDate().toString());
        values.put("account_no", transaction.getAccountNo());
        values.put("type", transaction.getExpenseType().toString());
        values.put("amount", transaction.getAmount());

        // Inserting Row
        db.insert(TRANSACTIONS_TABLE, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection

    }

    public  List<Transaction> getAllTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Transaction> transactionList = new ArrayList<Transaction>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TRANSACTIONS_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        Transaction transaction;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                try {
                    transaction = new Transaction(
                            new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
                                    .parse(cursor.getString(0)),
                                    cursor.getString(1),
                                    ExpenseType.valueOf(cursor.getString(2)),
                                    Double.parseDouble(cursor.getString(3)));
                    transactionList.add(transaction);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        System.out.println("Transactions");
        System.out.println(transactionList.toString());
        // return contact list
        return transactionList;
    }

    public  List<Transaction> getTransactions(int limit) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Transaction> transactionList = new ArrayList<Transaction>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TRANSACTIONS_TABLE + " LIMIT "+limit;
        Cursor cursor = db.rawQuery(selectQuery, null);
        Transaction transaction;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                try {
                    transaction = new Transaction(
                            new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
                                    .parse(cursor.getString(1)),
                            cursor.getString(2),
                            ExpenseType.valueOf((cursor.getString(3)).toUpperCase()),
                            Double.parseDouble(cursor.getString(4)));
                    transactionList.add(transaction);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        System.out.println("Transactions");
        System.out.println(transactionList.toString());
        // return contact list
        return transactionList;
    }
}
