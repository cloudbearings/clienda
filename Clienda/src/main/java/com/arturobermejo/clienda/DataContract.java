package com.arturobermejo.clienda;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by abermejo on 4/14/14.
 */
public final class DataContract {

    public static final String DB_NAME = "clienda.sqlite";

    public static final String AUTHORITY = "com.arturobermejo.clienda.provider";

    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    private DataContract() {}

    public static final class Clients implements BaseColumns {

        public static final String CLIENTS_TABLE_NAME = "clients";
        public static final String CLIENTS_KEY_NAME = "name";
        public static final String CLIENTS_KEY_ADDRESS = "address";
        public static final String CLIENTS_KEY_PHONE = "phone_number";
        public static final String CLIENTS_KEY_EMAIL = "email";
        public static final String CLIENTS_KEY_NOTES = "notes";
        public static final String [] ALL_COLUMNS = new String[] { _ID, CLIENTS_KEY_NAME,
                                                                   CLIENTS_KEY_ADDRESS, CLIENTS_KEY_PHONE,
                                                                   CLIENTS_KEY_EMAIL, CLIENTS_KEY_NOTES };
        public static final String [] JOIN_PROJECTION = new String[] { "_id", "name", "product as last_order", "total(product_debt) debt" };
        public static final String SQL_CREATE_CLIENTS_TABLE = "CREATE TABLE " + CLIENTS_TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CLIENTS_KEY_NAME + " TEXT NOT NULL, " +
                CLIENTS_KEY_ADDRESS + " TEXT, " +
                CLIENTS_KEY_PHONE + " TEXT, " +
                CLIENTS_KEY_EMAIL + " TEXT, " +
                CLIENTS_KEY_NOTES + " TEXT);";
        public static final String SQL_DROP_CLIENTS_TABLE = "DROP TABLE IF EXISTS " + CLIENTS_TABLE_NAME;

        public static final String SQL_JOIN_TABLES= "(select clients._id, clients.name, orders.product, orders.date, price*quantity - total(amount) product_debt from clients " +
                "left join orders on clients._id == orders.client_id " +
                "left join payments on orders._id == payments.order_id " +
                "group by orders._id " +
                "order by clients.name, orders.date asc)";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CLIENTS_TABLE_NAME);

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "." + CLIENTS_TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "." + CLIENTS_TABLE_NAME;
    }

    public static final class Orders implements BaseColumns {

        public static final String ORDERS_TABLE_NAME = "orders";
        public static final String ORDERS_KEY_CLIENT = "client_id";
        public static final String ORDERS_KEY_PRODUCT = "product";
        public static final String ORDERS_KEY_PRICE = "price";
        public static final String ORDERS_KEY_QUANTITY = "quantity";
        public static final String ORDERS_KEY_DATE = "date";
        public static final String ORDERS_KEY_NOTES = "notes";
        public static final String [] ALL_COLUMNS = new String[] { _ID, ORDERS_KEY_CLIENT, ORDERS_KEY_PRODUCT,
                                                                ORDERS_KEY_PRICE, ORDERS_KEY_DATE, ORDERS_KEY_NOTES };
        public static final String [] JOIN_PROJECTION = new String[] { "orders.*, clients.name, total(payments.amount) total_payments, " +
                                                                       "orders.quantity * orders.price - total(payments.amount) debt" };
        public static final String SQL_CREATE_ORDERS_TABLE = "CREATE TABLE " + ORDERS_TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ORDERS_KEY_CLIENT + " INT, " +
                ORDERS_KEY_PRODUCT + " TEXT, " +
                ORDERS_KEY_PRICE + " REAL, " +
                ORDERS_KEY_QUANTITY + " INT, " +
                ORDERS_KEY_DATE + " INT, " +
                ORDERS_KEY_NOTES + " TEXT, " +
                "FOREIGN KEY (" + ORDERS_KEY_CLIENT + ") REFERENCES clients(" + _ID + ") ON DELETE CASCADE);";
        public static final String SQL_DROP_ORDERS_TABLE = "DROP TABLE IF EXISTS " + ORDERS_TABLE_NAME;
        public static final String SQL_JOIN_ORDERS_CLIENTS = "orders JOIN clients ON (orders.client_id=clients._id) LEFT JOIN payments ON (orders._id = payments.order_id)";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, ORDERS_TABLE_NAME);

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "." + ORDERS_TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "." + ORDERS_TABLE_NAME;
    }

    public static final class Payments implements BaseColumns {

        public static final String PAYMENTS_TABLE_NAME = "payments";
        public static final String PAYMENTS_KEY_ORDER = "order_id";
        public static final String PAYMENTS_KEY_DATE = "date";
        public static final String PAYMENTS_KEY_AMOUNT = "amount";
        public static final String [] ALL_COLUMNS = new String[] { _ID, PAYMENTS_KEY_ORDER,
                                                                    PAYMENTS_KEY_DATE, PAYMENTS_KEY_AMOUNT};

        public static final String SQL_CREATE_PAYMENTS_TABLE = "CREATE TABLE " + PAYMENTS_TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PAYMENTS_KEY_ORDER + " INT, " +
                PAYMENTS_KEY_DATE + " INT, " +
                PAYMENTS_KEY_AMOUNT + " REAL, " +
                "FOREIGN KEY (" + PAYMENTS_KEY_ORDER + ") REFERENCES orders(" + _ID + ") ON DELETE CASCADE);";
        public static final String SQL_DROP_PAYMENTS_TABLE = "DROP TABLE IF EXISTS " + PAYMENTS_TABLE_NAME;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PAYMENTS_TABLE_NAME);

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "." + PAYMENTS_TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "." + PAYMENTS_TABLE_NAME;
    }
}
