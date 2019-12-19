package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DbContract.TransactionTable;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentMemoryTransactionDAO implements TransactionDAO {
    private transient SQLiteDatabase database;
    public PersistentMemoryTransactionDAO(SQLiteDatabase database) {
        this.database=database;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {


        String sql="INSERT INTO "+ TransactionTable.TABLE_NAME+"("+ TransactionTable.COLUMN_NAME_ACCOUNT_NO+","+ TransactionTable.COLUMN_NAME_TRANSACTION_TYPE+","+
                TransactionTable.COLUMN_NAME_DATE+","+ TransactionTable.COLUMN_NAME_AMOUNT+") VALUES(?,?,?,?);";

        SQLiteStatement statement=database.compileStatement(sql);
        statement.bindString(1,accountNo);
        statement.bindString(2,expenseType.name());
        statement.bindString(3,date.toString());
        statement.bindDouble(4,amount);

        statement.executeInsert();

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {

        Cursor cursor = database.query(
                TransactionTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        List<Transaction> transactionList=new ArrayList<>();
        while(cursor.moveToNext()){
            String accountNo=cursor.getString(cursor.getColumnIndex(TransactionTable.COLUMN_NAME_ACCOUNT_NO));
            double amount=cursor.getDouble(cursor.getColumnIndex(TransactionTable.COLUMN_NAME_AMOUNT));
            String type=cursor.getString(cursor.getColumnIndex(TransactionTable.COLUMN_NAME_TRANSACTION_TYPE));

            String date=cursor.getString(cursor.getColumnIndex(TransactionTable.COLUMN_NAME_DATE));
            Transaction transaction=new Transaction(new Date(date),accountNo, ExpenseType.valueOf(type),amount);
            transactionList.add(transaction);
        }
        cursor.close();
        return transactionList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {


        String sql="SELECT * FROM "+ TransactionTable.TABLE_NAME+" LIMIT "+limit;
        Cursor cursor=database.rawQuery(sql,null);
        List<Transaction> transactionList=new ArrayList<>();
        while(cursor.moveToNext()){
            String accountNo=cursor.getString(cursor.getColumnIndex(TransactionTable.COLUMN_NAME_ACCOUNT_NO));
            double amount=cursor.getDouble(cursor.getColumnIndex(TransactionTable.COLUMN_NAME_AMOUNT));
            String type=cursor.getString(cursor.getColumnIndex(TransactionTable.COLUMN_NAME_TRANSACTION_TYPE));

            String date=cursor.getString(cursor.getColumnIndex(TransactionTable.COLUMN_NAME_DATE));
            Transaction transaction=new Transaction(new Date(date),accountNo, ExpenseType.valueOf(type),amount);
            transactionList.add(transaction);
        }
        cursor.close();
        return  transactionList;
    }
}
