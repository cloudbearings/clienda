package com.arturobermejo.clienda;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class NewOrderActivity extends FragmentActivity implements OnClickListener, DialogDatePickerFragment.DialogDatePickerListener {

    private static final String TAG = "new-order";
    Button mClient, mNewClient, mSave, mCancel, mChangeDate;
    EditText mProduct, mPrice, mQuantity, mNotes;
    TextView mDate;
    long dateInMillis;
    String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        mProduct = (EditText) findViewById(R.id.formOrderProduct);
        mPrice = (EditText) findViewById(R.id.formOrderPrice);
        mQuantity = (EditText) findViewById(R.id.formOrderQuantity);
        mDate = (TextView) findViewById(R.id.formOrderDate);
        mNotes = (EditText) findViewById(R.id.formOrderNotes);

        // Date picker
        mChangeDate = (Button) findViewById(R.id.formOrderChangeDate);
        Date date = new Date();
        String currentDateString = DateFormat.getDateInstance().format(date);
        dateInMillis = date.getTime();
        mDate.setText(currentDateString);
        mChangeDate.setOnClickListener(this);

        // Select Client Activity
        mClient = (Button) findViewById(R.id.formOrderClient);
        mClient.setOnClickListener(this);

        // Start New Client Activity
        mNewClient = (Button) findViewById(R.id.formOrderNewClient);
        mNewClient.setOnClickListener(this);

        // Save Order
        mSave = (Button) findViewById(R.id.formOrderSave);
        mSave.setOnClickListener(this);

        // Cancel Order
        mCancel = (Button) findViewById(R.id.formOrderCancel);
        mCancel.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_order, menu);
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

        } else if (view == mNewClient) {
            intent = new Intent(this, NewClientActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, 2);

        } else if (view == mSave) {
            if (formIsValid()) {
                ContentValues values = new ContentValues();
                values.put(DataContract.Orders.ORDERS_KEY_CLIENT, clientId);
                values.put(DataContract.Orders.ORDERS_KEY_PRODUCT, mProduct.getText().toString());
                values.put(DataContract.Orders.ORDERS_KEY_PRICE, mPrice.getText().toString());
                values.put(DataContract.Orders.ORDERS_KEY_QUANTITY, mQuantity.getText().toString());
                values.put(DataContract.Orders.ORDERS_KEY_DATE, dateInMillis);
                values.put(DataContract.Orders.ORDERS_KEY_NOTES, mNotes.getText().toString());
                getContentResolver().insert(DataContract.Orders.CONTENT_URI, values);
                finish();
            }

        } else if (view == mChangeDate) {
            DialogFragment dialogFragment = new DialogDatePickerFragment();
            dialogFragment.show(getSupportFragmentManager(), "datePicker");

        } else if (view == mCancel) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || requestCode == 2) {
            if(resultCode == RESULT_OK){
                clientId = data.getStringExtra("clientId");
                String clientName = data.getStringExtra("clientName");
                mClient.setText(clientName);
                mClient.setError(null);
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public void onDialogPositiveClick(DatePicker view, int year, int month, int day) {
        dateInMillis = new GregorianCalendar(year, month, day).getTimeInMillis();
        String dateTimeString = DateFormat.getDateInstance().format(new Date(dateInMillis));
        mDate.setText(dateTimeString);
    }

    private boolean formIsValid() {
        if (clientId == null) {
            String message = getString(R.string.validation_order_client_required);
            mClient.setError(message);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }

        if (mProduct.getText().toString().length() == 0) {
            mProduct.setError(getString(R.string.validation_order_product_required));
            return false;
        }

        if (mPrice.getText().toString().length() == 0) {
            mPrice.setError(getString(R.string.validation_order_price_required));
            return false;
        }

        if (Float.parseFloat(mPrice.getText().toString()) == 0.0) {
            mPrice.setError(getString(R.string.validation_order_price_greater_zero));
            return false;
        }

        if (mQuantity.getText().toString().length() == 0) {
            mQuantity.setError(getString(R.string.validation_order_quantity_required));
            return false;
        }

        if (Float.parseFloat(mQuantity.getText().toString()) == 0.0) {
            mPrice.setError(getString(R.string.validation_order_price_greater_zero));
            return false;
        }

        return true;
    }
}
