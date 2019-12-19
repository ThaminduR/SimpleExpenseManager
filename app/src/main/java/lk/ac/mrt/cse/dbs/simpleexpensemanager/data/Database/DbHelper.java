package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DbContract.AccountTable;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DbContract.TransactionTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "170025V.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + AccountTable.TABLE_NAME + " (" +
                AccountTable.COLUMN_NAME_ACCOUNT_NO + " varchar(20) "+" PRIMARY KEY "+"," +
                AccountTable.COLUMN_NAME_BANK + " varchar(20) " + "," +
                AccountTable.COLUMN_NAME_ACCOUNT_HOLDER + " varchar(20) " + ","+
                AccountTable.COLUMN_NAME_BALANCE+ " float "+" )");

        db.execSQL( "CREATE TABLE " + TransactionTable.TABLE_NAME + " (" +
                DbContract.TransactionTable._ID + " INTEGER PRIMARY KEY "+"," +
                TransactionTable.COLUMN_NAME_ACCOUNT_NO + " varchar(20) " + "," +
                TransactionTable.COLUMN_NAME_DATE + " date " + ","+
                TransactionTable.COLUMN_NAME_TRANSACTION_TYPE + " varchar(20) " +","+
                TransactionTable.COLUMN_NAME_AMOUNT+ " float "+","+
                " FOREIGN KEY("+ TransactionTable.COLUMN_NAME_ACCOUNT_NO+") REFERENCES "+ AccountTable.TABLE_NAME+"("+ AccountTable.COLUMN_NAME_ACCOUNT_NO+")"+
                " );");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + AccountTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TransactionTable.TABLE_NAME);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);

    }
}
