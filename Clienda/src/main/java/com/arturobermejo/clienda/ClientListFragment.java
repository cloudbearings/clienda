package com.arturobermejo.clienda;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
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

/**
 * Created by abermejo on 3/31/14.
 */
public class ClientListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener {

    private static final String TAG = "clienda-client-list";
    public final static String EXTRA_MESSAGE = "clientId";
    SimpleCursorAdapter mAdapter;
    String mCurFilter;
    SharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        setHasOptionsMenu(true);

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.row_client_item, null,
                                            new String [] { DataContract.Clients.CLIENTS_KEY_NAME,
                                                            "debt", "last_order"},
                                            new int[] { R.id.clientItemName, R.id.clientItemDebt,
                                                        R.id.clientItemLastOrder }, 0);
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {
                TextView textView = (TextView) aView;
                if (aColumnIndex == 2) {
                    String lastOrder = aCursor.getString(aColumnIndex);
                    lastOrder = lastOrder == null? getString(R.string.client_no_orders) : lastOrder;
                    textView.setText(lastOrder);
                    return true;
                } else if (aColumnIndex == 3) {
                    String currencySymbol = sharedPref.getString(SettingsActivity.KEY_PREF_CURRENCY, "");
                    Float debt = aCursor.getFloat(aColumnIndex);
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
        if (mCurFilter != null) {
            select = "name LIKE ?";
            selectArgs = new String[] {"%"+mCurFilter+"%"};
        }
        return new CursorLoader(getActivity(), DataContract.Clients.CONTENT_URI,
                                DataContract.Clients.JOIN_PROJECTION, select, selectArgs, null);
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
        if (getTag() == "pick-client") {
            Intent intent = getActivity().getIntent();
            intent.putExtra("clientId", Long.toString(id));
            TextView view = (TextView) v.findViewById(R.id.clientItemName);
            intent.putExtra("clientName", view.getText());
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
            Intent intent = new Intent(getActivity().getApplicationContext(), ClientDetailActivity.class);
            intent.putExtra(EXTRA_MESSAGE, id);
            startActivity(intent);
        }
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
