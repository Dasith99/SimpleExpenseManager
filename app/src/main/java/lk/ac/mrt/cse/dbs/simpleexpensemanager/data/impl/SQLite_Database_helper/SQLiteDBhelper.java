package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLite_Database_helper;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class SQLiteDBhelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "200436U.sqlite";
    private static final int VERSION = 1;

    //There are names of two tables
    public static final String TRANSACTION_TBL = "transactions";
    public static final String ACCOUNT_TBL = "accounts";

    //There is common name for two columns
    public static final String ACCOUNT_NUMBER = "account_num";

    //There are column names of two tables
    //Account table
    public static final String NAME_OF_BANK = "bank_name";
    public static final String NAME_OF_HOLDER = "account_holder_name";
    public static final String BANK_BALANCE = "balance";

    //Tansaction Table
    public static final String TRN_ID = "trn_id";
    public static final String DATE_OF_TRN = "date_of_trn";
    public static final String TYPE_OF_EXPENSE = "expense_type";
    public static final String AMOUNT = "amount";



    public SQLiteDBhelper( Context context) {
        super(context,DB_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query_1=("CREATE TABLE "+ACCOUNT_TBL+"("+
                ACCOUNT_NUMBER+ " TEXT PRIMARY KEY, " +
                NAME_OF_BANK + " TEXT NOT NULL, " +
                NAME_OF_HOLDER + " TEXT NOT NULL, " +
                BANK_BALANCE + " REAL NOT NULL)");
        sqLiteDatabase.execSQL(query_1);

        String query_2=("CREATE TABLE " + TRANSACTION_TBL + "(" +
                TRN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE_OF_TRN + " TEXT NOT NULL, " +
                TYPE_OF_EXPENSE + " TEXT NOT NULL, " +
                AMOUNT + " REAL NOT NULL, " +
                ACCOUNT_NUMBER + " TEXT," +
                "FOREIGN KEY (" + ACCOUNT_NUMBER + ") REFERENCES " + TRANSACTION_TBL + "(" + ACCOUNT_NUMBER + "))");
        sqLiteDatabase.execSQL(query_2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old_version, int new_version) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TBL);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TBL);

        //if there are no any table created yet it will create new tables
        onCreate(sqLiteDatabase);
    }
}
