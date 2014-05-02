package com.arturobermejo.clienda;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by abermejo on 4/12/14.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "clienda-dbhelper";
    private static final String DB_NAME = DataContract.DB_NAME;
    private static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Client Table
        try {
            db.execSQL(DataContract.Clients.SQL_CREATE_CLIENTS_TABLE);
            db.execSQL(DataContract.Orders.SQL_CREATE_ORDERS_TABLE);
            db.execSQL(DataContract.Payments.SQL_CREATE_PAYMENTS_TABLE);
        } catch (Error e) {

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            db.execSQL(DataContract.Clients.SQL_DROP_CLIENTS_TABLE);
            db.execSQL(DataContract.Orders.SQL_DROP_ORDERS_TABLE);
            db.execSQL(DataContract.Payments.SQL_DROP_PAYMENTS_TABLE);
        } catch (Error e) {

        }

        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
