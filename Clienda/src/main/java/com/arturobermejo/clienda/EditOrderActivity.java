package com.arturobermejo.clienda;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;


public class EditOrderActivity extends FragmentActivity implements View.OnClickListener, DialogDatePickerFragment.DialogDatePickerListener {

    private static final String TAG = "clienda-edit-order";
    public final static String EXTRA_MESSAGE = "orderId";
    Button mClient, btnNewClient, btnSave, btnCancel, btnChangeDate;
    EditText mProduct, mPrice, mQuantity, mNotes;
    TextView mDate;
    Long orderId, dateInMillis, clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        // Get Views
        mProduct = (EditText) findViewById(R.id.formOrderProduct);
        mPrice = (EditText) findViewById(R.id.formOrderPrice);
        mQuantity = (EditText) findViewById(R.id.formOrderQuantity);
        mDate = (TextView) findViewById(R.id.formOrderDate);
        mNotes = (EditText) findViewById(R.id.formOrderNotes);

        // Get Data
        Intent intent = getIntent();
        orderId = intent.getLongExtra(OrderListFragment.EXTRA_MESSAGE, 0);
        Cursor data = getContentResolver().query(DataContract.Orders.CONTENT_URI, DataContract.Orders.JOIN_PROJECTION,
                                                "orders._id=?", new String[] {orderId.toString()}, null);

        // Select Client Activity
        mClient = (Button) findViewById(R.id.formOrderClient);
        mClient.setOnClickListener(this);

        // Save Client
        btnSave = (Button) findViewById(R.id.formOrderSave);
        btnSave.setOnClickListener(this);

        // Cancel Client
        btnCancel = (Button) findViewById(R.id.formOrderCancel);
        btnCancel.setOnClickListener(this);

        // Date
        btnChangeDate = (Button) findViewById(R.id.formOrderChangeDate);
        btnChangeDate.setOnClickListener(this);

        // Show Data
        if (data.getCount() != 0) {
            data.moveToFirst();
            String product = data.getString(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_PRODUCT));
            String client = data.getString(data.getColumnIndex(DataContract.Clients.CLIENTS_KEY_NAME));
            clientId = data.getLong(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_CLIENT));
            String price = data.getString(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_PRICE));
            int quantity = data.getInt(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_QUANTITY));
            String date = data.getString(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_DATE));
            String notes = data.getString(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_NOTES));

            mProduct.setText(product);
            mClient.setText(client);
            mPrice.setText(price);
            mQuantity.setText(Integer.toString(quantity));
            dateInMillis = Long.parseLong(date);
            String dateString = DateFormat.getDateInstance().format(new Date(dateInMillis));
            mDate.setText(dateString);
            mNotes.setText(notes);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra(EXTRA_MESSAGE, orderId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if (view == mClient) {
            intent = new Intent(this, SelectClientActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, 1);
        } else if (view == btnNewClient) {
            intent = new Intent(this, NewClientActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, 1);

        } else if (view == btnSave) {
            ContentValues values = new ContentValues();
            values.put(DataContract.Orders.ORDERS_KEY_CLIENT, clientId.toString());
            values.put(DataContract.Orders.ORDERS_KEY_PRODUCT, mProduct.getText().toString());
            values.put(DataContract.Orders.ORDERS_KEY_PRICE, mPrice.getText().toString());
            values.put(DataContract.Orders.ORDERS_KEY_QUANTITY, mQuantity.getText().toString());
            values.put(DataContract.Orders.ORDERS_KEY_DATE, dateInMillis);
            values.put(DataContract.Orders.ORDERS_KEY_NOTES, mNotes.getText().toString());
            Log.i(TAG, orderId.toString());
            getContentResolver().update(DataContract.Orders.CONTENT_URI, values,
                    "_id=?", new String[] {orderId.toString()});
            finish();

        } else if (view == btnChangeDate) {
            DialogFragment dialogFragment = new DialogDatePickerFragment();
            dialogFragment.show(getSupportFragmentManager(), "datePicker");

        } else if (view == btnCancel) {
            finish();
        }
    }

    @Override
    public void onDialogPositiveClick(DatePicker view, int year, int month, int day) {
        dateInMillis = new GregorianCalendar(year, month, day).getTimeInMillis();
        String dateTimeString = DateFormat.getDateInstance().format(new Date(dateInMillis));
        mDate.setText(dateTimeString);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String result = data.getStringExtra("client");
                mClient.setText(result);
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
