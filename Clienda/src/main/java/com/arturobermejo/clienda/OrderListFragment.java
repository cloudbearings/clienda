package com.arturobermejo.clienda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;

import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by abermejo on 3/31/14.
 */
public class OrderListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener {

    private static final String TAG = "clienda-order-list";
    public final static String EXTRA_MESSAGE = "orderId";
    SimpleCursorAdapter mAdapter;
    int rowLayout;
    String [] from;
    int [] to;
    Long clientId;
    String mCurFilter;
    SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        setHasOptionsMenu(true);

        if (getTag() == "order-history-fragment") {
            clientId = getArguments().getLong(ClientDetailActivity.EXTRA_MESSAGE);
            rowLayout = R.layout.row_order_history_item;
            from = new String [] { DataContract.Orders.ORDERS_KEY_PRODUCT, DataContract.Orders.ORDERS_KEY_DATE, "debt" };
            to = new int[] { R.id.orderItemProduct, R.id.orderItemDate, R.id.orderItemDebt };
        } else {
            rowLayout = R.layout.row_order_item;
            from = new String [] { DataContract.Orders.ORDERS_KEY_PRODUCT, DataContract.Orders.ORDERS_KEY_DATE,
                                   DataContract.Clients.CLIENTS_KEY_NAME, "debt" };
            to = new int[] { R.id.orderItemProduct, R.id.orderItemDate, R.id.orderItemClient, R.id.orderItemDebt };
        }

        mAdapter = new SimpleCursorAdapter(getActivity(), rowLayout, null, from, to, 0);

        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {
                TextView textView = (TextView) aView;
                if (aColumnIndex == 5) {
                    Long dateinMillis = aCursor.getLong(aColumnIndex);
                    String dateTimeString = DateFormat.getDateInstance().format(new Date(dateinMillis));
                    textView.setText(dateTimeString);
                    return true;
                } else if (aColumnIndex == 9) {
                    Float debt = aCursor.getFloat(aColumnIndex);
                    String currencySymbol = sharedPref.getString(SettingsActivity.KEY_PREF_CURRENCY, "");
                    textView.setText(currencySymbol + debt.toString());
                    return true;
                }
                return false;
            }
        });

        setListAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String select = null;
        String [] selectArgs = null;
        if (getTag() == "order-history-fragment") {
            select = "client_id=?";
            selectArgs = new String[] {clientId.toString()};
            if (mCurFilter != null) {
                select = "client_id=? and product LIKE ?";
                selectArgs = new String[] {clientId.toString(), "%"+mCurFilter+"%"};
            }
        } else {
            if (mCurFilter != null) {
                select = "product LIKE ?";
                selectArgs = new String[] {"%"+mCurFilter+"%"};
            }
        }
        return new CursorLoader(getActivity(), DataContract.Orders.CONTENT_URI,
                DataContract.Orders.JOIN_PROJECTION, select, selectArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getActivity().getApplicationContext(), OrderDetailActivity.class);
        intent.putExtra(EXTRA_MESSAGE, id);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String text) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String text) {
        mCurFilter = !TextUtils.isEmpty(text) ? text : null;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }
}
