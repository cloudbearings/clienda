package com.arturobermejo.clienda;



import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class PaymentListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemLongClickListener {

    private static final String TAG = "clienda-payment-list";
    private final static int PAYMENTS_LOADER = 1;
    SimpleCursorAdapter mAdapter;
    CursorLoader cursorLoader;
    Long paymentId;
    Long orderId;
    SharedPreferences sharedPref;

    public PaymentListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Get order ID
        orderId = getArguments().getLong(OrderDetailActivity.EXTRA_MESSAGE);

        // Detect long click
        getListView().setOnItemLongClickListener(this);

        // Get data
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.row_payment_item, null,
                                            new String [] { DataContract.Payments.PAYMENTS_KEY_DATE, DataContract.Payments.PAYMENTS_KEY_AMOUNT },
                                            new int[] { R.id.paymentDate, R.id.paymentAmount }, 0);
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {
                TextView textView = (TextView) aView;
                if (aColumnIndex == 2) {
                    Long dateInMillis = aCursor.getLong(aColumnIndex);
                    String dateTimeString = DateFormat.getDateInstance().format(new Date(dateInMillis));
                    textView.setText(dateTimeString);
                    return true;
                } else if (aColumnIndex == 3) {
                    Float price = aCursor.getFloat(aColumnIndex);
                    String currencySymbol = sharedPref.getString(SettingsActivity.KEY_PREF_CURRENCY, "");
                    textView.setText(currencySymbol + price.toString());
                    return true;
                }
                return false;
            }
        });
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(PAYMENTS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == PAYMENTS_LOADER) {
            cursorLoader = new CursorLoader(getActivity(), DataContract.Payments.CONTENT_URI,
                    DataContract.Payments.ALL_COLUMNS, "order_id=?", new String[] {orderId.toString()}, null);
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public Long getPaymentId() {
        return paymentId;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        paymentId = id;
        Vibrator v = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
        v.vibrate(50);
        DialogFragment dialogFragment = new DialogConfirmDeleteFragment();
        dialogFragment.show(getFragmentManager(), "confirm-payment-delete");
        return true;
    }
}
