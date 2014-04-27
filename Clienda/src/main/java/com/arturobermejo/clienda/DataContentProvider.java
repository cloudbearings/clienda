package com.arturobermejo.clienda;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class DataContentProvider extends ContentProvider {

    private static final String TAG = "content-provider";

    private DbHelper dbHelper;
    private SQLiteDatabase db;

    private static final int CLIENTS = 1;
    private static final int CLIENTS_ID = 2;
    private static final int ORDERS = 3;
    private static final int ORDERS_ID = 4;
    private static final int PAYMENTS = 5;
    private static final int PAYMENTS_ID = 6;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(DataContract.AUTHORITY, "clients", CLIENTS);
        sURIMatcher.addURI(DataContract.AUTHORITY, "clients/#", CLIENTS_ID);
        sURIMatcher.addURI(DataContract.AUTHORITY, "orders", ORDERS);
        sURIMatcher.addURI(DataContract.AUTHORITY, "orders/#", ORDERS_ID);
        sURIMatcher.addURI(DataContract.AUTHORITY, "payments", PAYMENTS);
        sURIMatcher.addURI(DataContract.AUTHORITY, "payments/#", PAYMENTS_ID);
    }

    public DataContentProvider() {
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;
        db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = sURIMatcher.match(uri);

        switch (uriType) {
            case CLIENTS:
                /*cursor = db.rawQuery("select _id, name, product as last_order, total(price) - total(amount) debt \n" +
                        "from (select clients.*, orders.product, orders.date, orders.price, amount from clients\n" +
                        "left join orders on clients._id = orders.client_id\n" +
                        "left join payments on orders._id = payments.order_id\n" +
                        "order by clients.name, orders.date)\n" +
                        "group by name", null);*/
                queryBuilder.setTables(DataContract.Clients.SQL_JOIN_TABLES);
                cursor = queryBuilder.query(db,
                        projection, selection, selectionArgs, "name", null, "name COLLATE NOCASE");
                break;
            case CLIENTS_ID:
                queryBuilder.setTables(DataContract.Clients.CLIENTS_TABLE_NAME);
                cursor = queryBuilder.query(db,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ORDERS:
                queryBuilder.setTables(DataContract.Orders.SQL_JOIN_ORDERS_CLIENTS);
                String having = selection == "client_id=?" ? null : "total_payments < orders.quantity*orders.price";
                cursor = queryBuilder.query(db,
                        projection, selection, selectionArgs, "product", having, "orders.date DESC");
                break;
            case ORDERS_ID:
                queryBuilder.setTables(DataContract.Orders.SQL_JOIN_ORDERS_CLIENTS);
                cursor = queryBuilder.query(db,
                        projection, selection, selectionArgs, "product", null, "orders.date DESC");
                break;
            case PAYMENTS: case PAYMENTS_ID:
                queryBuilder.setTables(DataContract.Payments.PAYMENTS_TABLE_NAME);
                cursor = queryBuilder.query(db,
                        projection, selection, selectionArgs, null, null, "date DESC");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        db = dbHelper.getWritableDatabase();

        long id;
        switch (sURIMatcher.match(uri)) {
            case CLIENTS:
                id = db.insert(DataContract.Clients.CLIENTS_TABLE_NAME, null, values);
                break;
            case ORDERS:
                id = db.insert(DataContract.Orders.ORDERS_TABLE_NAME, null, values);
                break;
            case PAYMENTS:
                id = db.insert(DataContract.Payments.PAYMENTS_TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(DataContract.Orders.CONTENT_URI, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(DataContract.Clients.CLIENTS_TABLE_NAME + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        db = dbHelper.getWritableDatabase();
        int rows = 0;
        switch (sURIMatcher.match(uri)) {
            case CLIENTS: case CLIENTS_ID:
                rows = db.update(DataContract.Clients.CLIENTS_TABLE_NAME, values, selection, selectionArgs);
                break;
            case ORDERS: case ORDERS_ID:
                rows = db.update(DataContract.Orders.ORDERS_TABLE_NAME, values, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = dbHelper.getWritableDatabase();
        int rows = 0;
        switch (sURIMatcher.match(uri)) {
            case CLIENTS: case CLIENTS_ID:
                rows = db.delete(DataContract.Clients.CLIENTS_TABLE_NAME, selection, selectionArgs);
                break;
            case ORDERS: case ORDERS_ID:
                rows = db.delete(DataContract.Orders.ORDERS_TABLE_NAME, selection, selectionArgs);
                break;
            case PAYMENTS: case PAYMENTS_ID:
                rows = db.delete(DataContract.Payments.PAYMENTS_TABLE_NAME, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }
}
