package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DbContract.AccountTable;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentMemoryAccountDAO implements AccountDAO {
    private transient SQLiteDatabase database;

    public PersistentMemoryAccountDAO(SQLiteDatabase database) {
        this.database=database;
    }

    @Override
    public List<String> getAccountNumbersList() {

        String[] projection = {
                AccountTable.COLUMN_NAME_ACCOUNT_NO
        };
        Cursor cursor =  database.query(AccountTable.TABLE_NAME,
                projection,
                null ,
                null ,
                null,
                null,
                null);

        List<String> accountNumberList = new ArrayList<>();

        while(cursor.moveToNext()){
            String accountNo=cursor.getString (cursor.getColumnIndex (AccountTable.COLUMN_NAME_ACCOUNT_NO));
            accountNumberList.add(accountNo);
        }
        cursor.close();
        return accountNumberList;


    }

    @Override
    public List<Account> getAccountsList() {

        Cursor cursor =  database.query(AccountTable.TABLE_NAME,
                null,
                null ,
                null ,
                null,
                null,
                null);

        List<Account> accountList = new ArrayList<>();

        while(cursor.moveToNext()){
            String accountNo=cursor.getString (cursor.getColumnIndex (AccountTable.COLUMN_NAME_ACCOUNT_NO));
            String bank=cursor.getString(cursor.getColumnIndex(AccountTable.COLUMN_NAME_BANK));
            String accountHolder=cursor.getString(cursor.getColumnIndex(AccountTable.COLUMN_NAME_ACCOUNT_HOLDER));
            double balance=cursor.getDouble(cursor.getColumnIndex(AccountTable.COLUMN_NAME_BALANCE));
            Account account=new Account(accountNo,bank,accountHolder,balance);
            accountList.add(account);
        }
        cursor.close();
        return accountList;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        String selection = AccountTable.COLUMN_NAME_ACCOUNT_NO + " = ?";
        String[] selectionArgs = {accountNo};

        Cursor cursor = database.query(AccountTable.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if(cursor.getCount()==0){
            String msg = "Account doesn't exists.";
            cursor.close();
            throw new InvalidAccountException(msg);
        }
        else{
            cursor.moveToFirst();
            String bank=cursor.getString(cursor.getColumnIndex(AccountTable.COLUMN_NAME_BANK));
            String accountHolder=cursor.getString(cursor.getColumnIndex(AccountTable.COLUMN_NAME_ACCOUNT_HOLDER));
            double balance=cursor.getDouble(cursor.getColumnIndex(AccountTable.COLUMN_NAME_BALANCE));
            cursor.close();
            return  new Account(accountNo,bank,accountHolder,balance);
        }


    }

    @Override
    public void addAccount(Account account) {
        String sql="INSERT INTO "+ AccountTable.TABLE_NAME+"("+ AccountTable.COLUMN_NAME_ACCOUNT_NO+","+ AccountTable.COLUMN_NAME_BANK+","+
                AccountTable.COLUMN_NAME_ACCOUNT_HOLDER+","+ AccountTable.COLUMN_NAME_BALANCE+") VALUES(?,?,?,?);";
        SQLiteStatement statement=database.compileStatement(sql); //avoid sql injection
        statement.bindString(1,account.getAccountNo());
        statement.bindString(2,account.getBankName());
        statement.bindString(3,account.getAccountHolderName());
        statement.bindDouble(4,account.getBalance());
        try{
            statement.executeInsert();
        }
        catch (SQLiteConstraintException ex){
            Log.e("Error","Integrity error occurred");
        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        String sql="DELETE FROM "+ AccountTable.TABLE_NAME+" WHERE "+ AccountTable.COLUMN_NAME_ACCOUNT_NO+" = ?;";
        SQLiteStatement statement=database.compileStatement(sql); //avoid sql injection
        statement.bindString(1,accountNo);
        statement.executeUpdateDelete();

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        Account account=getAccount(accountNo);
        //Check the type of the transaction and do the relevant operation
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }

        String sql="UPDATE "+ AccountTable.TABLE_NAME+" SET "+ AccountTable.COLUMN_NAME_BALANCE+" = ? WHERE "+
                AccountTable.COLUMN_NAME_ACCOUNT_NO+" = ? ;"  ;
        SQLiteStatement statement=database.compileStatement(sql); //avoid sql injection
        statement.bindDouble(1,account.getBalance());
        statement.bindString(2,accountNo);
        statement.executeUpdateDelete();
    }
}
