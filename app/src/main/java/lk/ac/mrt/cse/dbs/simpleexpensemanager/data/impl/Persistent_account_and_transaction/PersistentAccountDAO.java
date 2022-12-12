package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.Persistent_account_and_transaction;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLite_Database_helper.SQLiteDBhelper.ACCOUNT_NUMBER;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLite_Database_helper.SQLiteDBhelper.ACCOUNT_TBL;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLite_Database_helper.SQLiteDBhelper.BANK_BALANCE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLite_Database_helper.SQLiteDBhelper.NAME_OF_BANK;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLite_Database_helper.SQLiteDBhelper.NAME_OF_HOLDER;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLite_Database_helper.SQLiteDBhelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final SQLiteDBhelper helper;
    private SQLiteDatabase op;

    public PersistentAccountDAO(Context context) {
        helper = new SQLiteDBhelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        op = helper.getReadableDatabase();

        String[] projection = {
                ACCOUNT_NUMBER
        };

        Cursor cursor = op.query(
                ACCOUNT_TBL,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        List<String> acc_numbers = new ArrayList<String>();

        while(cursor.moveToNext()) {
            String accountNumber = cursor.getString(
                    cursor.getColumnIndexOrThrow(ACCOUNT_NUMBER));
            acc_numbers.add(accountNumber);
        }
        cursor.close();
        return acc_numbers;
    }


    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<Account>();

        op = helper.getReadableDatabase();

        String[] projection = {
                ACCOUNT_NUMBER,
                NAME_OF_BANK,
                NAME_OF_HOLDER,
                BANK_BALANCE
        };

        Cursor cursor = op.query(
                ACCOUNT_TBL,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while(cursor.moveToNext()) {

            String account_num = cursor.getString(cursor.getColumnIndex(ACCOUNT_NUMBER));
            String bank_name = cursor.getString(cursor.getColumnIndex(NAME_OF_BANK));
            String account_holder_name = cursor.getString(cursor.getColumnIndex(NAME_OF_HOLDER));
            double balance = cursor.getDouble(cursor.getColumnIndex(BANK_BALANCE));
            Account account = new Account(account_num,bank_name,account_holder_name,balance);

            accounts.add(account);
        }
        cursor.close();
        return accounts;

    }


    @Override
    public Account getAccount(String account_num) throws InvalidAccountException {

        op = helper.getReadableDatabase();
        String[] projection = {
                ACCOUNT_NUMBER,
                NAME_OF_BANK,
                NAME_OF_HOLDER,
                BANK_BALANCE
        };

        String selection = ACCOUNT_NUMBER + " = ?";
        String[] selectionArgs = { account_num };

        Cursor kk = op.query(
                ACCOUNT_TBL,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (kk == null){
            String message = "Account " +account_num + " is invalid.";
            throw new InvalidAccountException( message);
        }
        else {
            kk.moveToFirst();

            Account account = new Account(account_num, kk.getString(kk.getColumnIndex(NAME_OF_BANK)),
                    kk.getString(kk.getColumnIndex(NAME_OF_HOLDER)), kk.getDouble(kk.getColumnIndex(BANK_BALANCE)));
            return account;
        }
    }


    @Override
    public void accountAdding(Account account) {

        op = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACCOUNT_NUMBER, account.getAccountNo());
        values.put(NAME_OF_BANK, account.getBankName());
        values.put(NAME_OF_HOLDER, account.getAccountHolderName());
        values.put(BANK_BALANCE,account.getBalance());


        op.insert(ACCOUNT_TBL, null, values);
        op.close();
    }

    @Override
    public void removeAccount(String account_num) throws InvalidAccountException {
        op = helper.getWritableDatabase();
        op.delete(ACCOUNT_TBL,  ACCOUNT_NUMBER  + " = ?",
                new String[] { account_num });
        op.close();
    }

    @Override
    public void balanceUpdating(String account_num, ExpenseType expense_type, double amount) throws InvalidAccountException {

        op = helper.getWritableDatabase();
        String[] projection = {
                BANK_BALANCE
        };

        String selection = ACCOUNT_NUMBER + " = ?";
        String[] selectionArgs = { account_num };

        Cursor cursor = op.query(
                ACCOUNT_TBL,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        double bankBalance;
        if(cursor.moveToFirst())
            bankBalance = cursor.getDouble(0);
        else{
            String msg = "Account " + account_num + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        ContentValues values = new ContentValues();
        switch (expense_type) {
            case EXPENSE:
                values.put(BANK_BALANCE, bankBalance - amount);
                break;
            case INCOME:
                values.put(BANK_BALANCE, bankBalance + amount);
                break;
        }

        // updating row
        op.update(ACCOUNT_TBL, values, ACCOUNT_NUMBER  + " = ?",
                new String[] {account_num });

        cursor.close();
        op.close();

    }
}
