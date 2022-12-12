package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.Persistent_account_and_transaction;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLite_Database_helper.SQLiteDBhelper.ACCOUNT_NUMBER;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLite_Database_helper.SQLiteDBhelper.AMOUNT;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLite_Database_helper.SQLiteDBhelper.DATE_OF_TRN;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLite_Database_helper.SQLiteDBhelper.TRANSACTION_TBL;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLite_Database_helper.SQLiteDBhelper.TYPE_OF_EXPENSE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLite_Database_helper.SQLiteDBhelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private final SQLiteDBhelper helper;
    private SQLiteDatabase d_b;

    public PersistentTransactionDAO(Context context) {
        helper = new SQLiteDBhelper(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        d_b = helper.getWritableDatabase();
        DateFormat date_format = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues values = new ContentValues();
        values.put(DATE_OF_TRN, date_format.format(date));
        values.put(ACCOUNT_NUMBER, accountNo);
        values.put(TYPE_OF_EXPENSE, String.valueOf(expenseType));
        values.put(AMOUNT, amount);

        // insert row
        d_b.insert(TRANSACTION_TBL, null, values);
        d_b.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List<Transaction> transactions = new ArrayList<Transaction>();

        d_b = helper.getReadableDatabase();

        String[] projection = {
                DATE_OF_TRN,
                ACCOUNT_NUMBER,
                TYPE_OF_EXPENSE,
                AMOUNT
        };

        Cursor cursor = d_b.query(
                TRANSACTION_TBL,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while(cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex(DATE_OF_TRN));
            Date date_1 = new SimpleDateFormat("dd-MM-yyyy").parse(date);
            String accountNumber = cursor.getString(cursor.getColumnIndex(ACCOUNT_NUMBER));
            String type = cursor.getString(cursor.getColumnIndex(TYPE_OF_EXPENSE));
            ExpenseType expenseType = ExpenseType.valueOf(type);
            double amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT));
            Transaction transaction = new Transaction(date_1,accountNumber,expenseType,amount);

            transactions.add(transaction);
        }
        cursor.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {

        List<Transaction> transactions = new ArrayList<Transaction>();

        d_b = helper.getReadableDatabase();

        String[] projection = {
                DATE_OF_TRN,
                ACCOUNT_NUMBER,
                TYPE_OF_EXPENSE,
                AMOUNT
        };

        Cursor cursor = d_b.query(
                TRANSACTION_TBL,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        int size = cursor.getCount();

        while(cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex(DATE_OF_TRN));
            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date);
            String accountNumber = cursor.getString(cursor.getColumnIndex(ACCOUNT_NUMBER));
            String type = cursor.getString(cursor.getColumnIndex(TYPE_OF_EXPENSE));
            ExpenseType expenseType = ExpenseType.valueOf(type);
            double amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT));
            Transaction transaction = new Transaction(date1,accountNumber,expenseType,amount);

            transactions.add(transaction);
        }

        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);


    }

}


