package com.arturobermejo.clienda;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;


public class OrderDetailActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>,
             DialogConfirmDeleteFragment.DialogConfirmDeleteListener, DialogAddPaymentFragment.DialogAddPaymentListener,
             View.OnClickListener {

    private static final String TAG = "clienda-order-detail";
    public final static String EXTRA_MESSAGE = "orderId";
    private final static int ORDER_LOADER = 1;
    CursorLoader cursorLoader;
    Long orderId;
    TextView mProduct, mClient, mPrice, mQuantity, mDate, mNotes, mTotalPayments, mDebt, mTotal, mPaymentsText;
    SimpleCursorAdapter mAdapter;
    Float price, totalPayments;
    int quantity;
    SharedPreferences sharedPref;
    Boolean is_collapsed = false;
    int layoutHeight;
    Long clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        mProduct = (TextView) findViewById(R.id.orderDetailProduct);
        mClient = (TextView) findViewById(R.id.orderDetailClient);
        mPrice = (TextView) findViewById(R.id.orderDetailPrice);
        mQuantity = (TextView) findViewById(R.id.orderDetailQuantity);
        mTotalPayments = (TextView) findViewById(R.id.orderDetailPaymentsTotal);
        mDebt = (TextView) findViewById(R.id.orderDetailDebt);
        mTotal = (TextView) findViewById(R.id.orderDetailTotal);
        mDate = (TextView) findViewById(R.id.orderDetailDate);
        mNotes = (TextView) findViewById(R.id.orderDetailNotes);


        Intent intent = getIntent();
        orderId = intent.getLongExtra(OrderListFragment.EXTRA_MESSAGE, 0);

        getSupportLoaderManager().initLoader(ORDER_LOADER, null, this);

        // Add fragment payment list
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_MESSAGE, orderId);
        Fragment paymentListFragment = new PaymentListFragment();
        paymentListFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.scrollViewPaymentList, paymentListFragment, "payment-list-fragment");
        fragmentTransaction.commit();

        // Animation click
        mPaymentsText = (TextView) findViewById(R.id.orderDetailPaymentsText);
        mPaymentsText.setOnClickListener(this);
        mTotalPayments.setOnClickListener(this);

        // Client Detail
        mClient.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.order_detail, menu);
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
        } else if (id == R.id.action_add_payment) {
            if (price - totalPayments == 0.0 ) {
                String message = getString(R.string.validation_add_payments);
                Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                DialogFragment dialogFragment = new DialogAddPaymentFragment();
                dialogFragment.show(getSupportFragmentManager(), "add-payment");
            }
        } else if (id == R.id.action_edit) {
            Intent intent = new Intent(this, EditOrderActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(EXTRA_MESSAGE, orderId);
            startActivity(intent);
        } else if (id == R.id.action_remove) {
            DialogFragment dialogFragment = new DialogConfirmDeleteFragment();
            dialogFragment.show(getSupportFragmentManager(), "confirm-order-delete");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog.getTag() == "confirm-order-delete") {
            getContentResolver().delete(DataContract.Orders.CONTENT_URI,
                    "_id=?", new String[] {orderId.toString()});
            finish();
        } else if (dialog.getTag() == "confirm-payment-delete") {
            PaymentListFragment fragment = (PaymentListFragment) getSupportFragmentManager().findFragmentByTag("payment-list-fragment");
            Long paymentId = fragment.getPaymentId();

            getContentResolver().delete(DataContract.Payments.CONTENT_URI,
                    "_id=?", new String[]{paymentId.toString()});
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ORDER_LOADER) {
            cursorLoader = new CursorLoader(this, Uri.withAppendedPath(DataContract.Orders.CONTENT_URI, orderId.toString()),
                    DataContract.Orders.JOIN_PROJECTION, "orders._id=?", new String[] {orderId.toString()}, null);
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0) {
            data.moveToFirst();

            String currencySymbol = sharedPref.getString(SettingsActivity.KEY_PREF_CURRENCY, "");

            String product = data.getString(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_PRODUCT));
            String client = data.getString(data.getColumnIndex(DataContract.Clients.CLIENTS_KEY_NAME));
            clientId = data.getLong(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_CLIENT));
            String date = data.getString(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_DATE));
            price = data.getFloat(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_PRICE));
            totalPayments = data.getFloat(data.getColumnIndex("total_payments"));
            quantity = data.getInt(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_QUANTITY));
            String total = currencySymbol + Integer.toString(Math.round(price*quantity));
            Float debt =data.getFloat(data.getColumnIndex("debt"));
            String notes = data.getString(data.getColumnIndex(DataContract.Orders.ORDERS_KEY_NOTES));

            mProduct.setText(product);
            mClient.setText(client);
            Long dateInMillis = Long.parseLong(date);
            String dateString = DateFormat.getDateInstance().format(new Date(dateInMillis));
            mDate.setText(dateString);

            mPrice.setText(currencySymbol + price);
            mQuantity.setText(Integer.toString(quantity));
            mTotalPayments.setText(currencySymbol + totalPayments);
            mDebt.setText(currencySymbol+debt);
            mTotal.setText(total);
            mNotes.setText(notes);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onConfirmAddPayment(DialogInterface dialog, String amount, Long date, TextView amountView) {
        // Save payment
        if (formIsValid(amount, amountView)) {
            ContentValues values = new ContentValues();
            values.put(DataContract.Payments.PAYMENTS_KEY_AMOUNT, amount);
            values.put(DataContract.Payments.PAYMENTS_KEY_DATE, date);
            values.put(DataContract.Payments.PAYMENTS_KEY_ORDER, orderId);
            getContentResolver().insert(DataContract.Payments.CONTENT_URI, values);
            dialog.dismiss();
        }
    }

    @Override
    public void onCancelAddPayment(DialogFragment dialog) {

    }

    private boolean formIsValid(String amount, TextView amountView) {
        if (amount.length() == 0) {
            amountView.setError(getString(R.string.validation_payment_amount_required));
            return false;
        }

        if (Float.parseFloat(amount) == 0.0) {
            amountView.setError(getString(R.string.validation_payment_amount_greater_zero));
            return false;
        }

        if (Float.parseFloat(amount) + totalPayments > quantity*price) {
            String message = getString(R.string.validation_payment_exceed_price) +
                             Float.toString(price-totalPayments);
            amountView.setError(message);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == mTotalPayments || view == mPaymentsText) {
            // Collapse-expand oder info layout
            RelativeLayout.LayoutParams params;
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.orderInfo);
            if (is_collapsed) {
                params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                params.height = layoutHeight;
                is_collapsed = false;
            } else {
                params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                layoutHeight = params.height;
                params.height = 70;
                is_collapsed = true;
            }
            layout.setLayoutParams(params);

        } else if (view == mClient) {
            Intent intent = new Intent(this, ClientDetailActivity.class);
            intent.putExtra("clientId", clientId);
            startActivity(intent);
        }
    }
}
