package com.arturobermejo.clienda;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by abermejo on 4/27/14.
 */
public class OrderFormFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "clienda-order-form-fragment";
    public static final String NEW_ORDER_TAG = "new-order";
    public static final String EDIT_ORDER_TAG = "edit-order";
    EditText mProduct, mPrice, mQuantity, mNotes;
    Button btnSelectClient, btnNewClient, btnChangeDate, btnSave, btnCancel;
    long dateInMillis;
    String clientId;

    public OrderFormFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_form, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get UI elements
        mProduct = (EditText) getView().findViewById(R.id.formOrderProduct);
        mPrice = (EditText) getView().findViewById(R.id.formOrderPrice);
        mQuantity = (EditText) getView().findViewById(R.id.formOrderQuantity);
        mNotes = (EditText) getView().findViewById(R.id.formOrderNotes);

        // Date picker
        btnChangeDate = (Button) getView().findViewById(R.id.formOrderChangeDate);
        Date date = new Date();
        String currentDateString = DateFormat.getDateInstance().format(date);
        dateInMillis = date.getTime();
        btnChangeDate.setText(currentDateString);
        btnChangeDate.setOnClickListener(this);

        // Select Client
        btnSelectClient = (Button) getView().findViewById(R.id.formOrderClient);
        btnSelectClient.setOnClickListener(this);

        // New Client
        btnNewClient = (Button) getView().findViewById(R.id.formOrderNewClient);
        btnNewClient.setOnClickListener(this);

        // Save Order
        btnSave = (Button) getView().findViewById(R.id.formOrderSave);
        btnSave.setOnClickListener(this);

        // Cancel Order
        btnCancel = (Button) getView().findViewById(R.id.formOrderCancel);
        btnCancel.setOnClickListener(this);

        // Set data for edit mode
        if(getTag() == EDIT_ORDER_TAG){
            setData();
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        if (view == btnSelectClient) {
            intent = new Intent(getActivity(), SelectClientActivity.class);
            startActivityForResult(intent, 1);

        } else if (view == btnNewClient) {
            intent = new Intent(getActivity(), NewClientActivity.class);
            startActivityForResult(intent, 2);

        } else if (view == btnSave) {
            if (formIsValid()) {
                ContentValues values = new ContentValues();
                values.put(DataContract.Orders.ORDERS_KEY_CLIENT, clientId);
                values.put(DataContract.Orders.ORDERS_KEY_PRODUCT, mProduct.getText().toString());
                values.put(DataContract.Orders.ORDERS_KEY_PRICE, mPrice.getText().toString());
                values.put(DataContract.Orders.ORDERS_KEY_QUANTITY, mQuantity.getText().toString());
                values.put(DataContract.Orders.ORDERS_KEY_DATE, dateInMillis);
                values.put(DataContract.Orders.ORDERS_KEY_NOTES, mNotes.getText().toString());
                getActivity().getContentResolver().insert(DataContract.Orders.CONTENT_URI, values);
                getActivity().finish();
            }

        } else if (view == btnChangeDate) {
            DialogFragment dialogFragment = new DialogDatePickerFragment();
            dialogFragment.show(getActivity().getSupportFragmentManager(), "datePicker");

        } else if (view == btnCancel) {
            getActivity().finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || requestCode == 2) {
            if(resultCode == getActivity().RESULT_OK){
                clientId = data.getStringExtra("clientId");
                btnSelectClient.setText(data.getStringExtra("clientName"));
                btnSelectClient.setError(null);
            }
        }
    }

    public void setDate(DatePicker view, int year, int month, int day) {
        dateInMillis = new GregorianCalendar(year, month, day).getTimeInMillis();
        String dateTimeString = DateFormat.getDateInstance().format(new Date(dateInMillis));
        btnChangeDate.setText(dateTimeString);
    }

    private boolean formIsValid() {
        if (clientId == null) {
            String message = getString(R.string.validation_order_client_required);
            btnSelectClient.setError(message);
            Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
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

    private void setData() {
        Long orderId = getArguments().getLong("orderId");
        Cursor data = getActivity().getContentResolver().query(DataContract.Orders.CONTENT_URI, DataContract.Orders.JOIN_PROJECTION,
                "orders._id=?", new String[] {orderId.toString()}, null);

        if (data.getCount() != 0) {
            data.moveToFirst();
            String product = data.getString(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_PRODUCT));
            String client = data.getString(data.getColumnIndex(DataContract.Clients.CLIENTS_KEY_NAME));
            clientId = data.getString(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_CLIENT));
            String price = data.getString(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_PRICE));
            int quantity = data.getInt(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_QUANTITY));
            String date = data.getString(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_DATE));
            String notes = data.getString(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_NOTES));

            mProduct.setText(product);
            btnSelectClient.setText(client);
            mPrice.setText(price);
            mQuantity.setText(Integer.toString(quantity));
            dateInMillis = Long.parseLong(date);
            String dateString = DateFormat.getDateInstance().format(new Date(dateInMillis));
            btnChangeDate.setText(dateString);
            mNotes.setText(notes);
        }
    }

}
