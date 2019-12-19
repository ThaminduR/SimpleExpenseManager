package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DbHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemoryTransactionDAO;

public class PersistentExpenseManager extends ExpenseManager {
    private transient Context mContext;

    public PersistentExpenseManager(Context context) {
        this.mContext=context;
        setup();
    }

    @Override
    public void setup() {

        SQLiteDatabase database=new DbHelper(mContext).getWritableDatabase();

        TransactionDAO inMemoryTransactionDAO = new PersistentMemoryTransactionDAO(database);
        setTransactionsDAO(inMemoryTransactionDAO);

        AccountDAO inMemoryAccountDAO = new PersistentMemoryAccountDAO(database);
        setAccountsDAO(inMemoryAccountDAO);
    }
}
