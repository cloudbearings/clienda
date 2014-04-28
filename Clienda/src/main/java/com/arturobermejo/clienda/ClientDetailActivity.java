package com.arturobermejo.clienda;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ClientDetailActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        DialogConfirmDeleteFragment.DialogConfirmDeleteListener, View.OnClickListener {

    private static final String TAG = "clienda-client-detail";
    public final static String EXTRA_MESSAGE = "clientId";
    Long clientId;
    TextView mName, mAddress, mPhone, mEmail, mNotes, mOrderHistoryText;
    Boolean is_collapsed = false;
    int layoutHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        mName = (TextView) findViewById(R.id.clientDetailName);
        mAddress = (TextView) findViewById(R.id.clientDetailAddress);
        mPhone = (TextView) findViewById(R.id.clientDetailPhone);
        mEmail = (TextView) findViewById(R.id.clientDetailEmail);
        mNotes = (TextView) findViewById(R.id.clientDetailNotes);
        mOrderHistoryText = (TextView) findViewById(R.id.orderHistoryText);

        Intent intent = getIntent();
        clientId = intent.getLongExtra(ClientListFragment.EXTRA_MESSAGE, 0);

        // Load order history list fragment
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_MESSAGE, clientId);
        Fragment orderListFragment = new OrderListFragment();
        orderListFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.clientOrderHistory, orderListFragment, "order-history-fragment");
        fragmentTransaction.commit();

        getSupportLoaderManager().initLoader(0, null, this);

        mOrderHistoryText.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_edit:
                intent = new Intent(this, EditClientActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(EXTRA_MESSAGE, clientId);
                startActivity(intent);
                return true;

            case R.id.action_remove:
                DialogFragment dialogFragment = new DialogConfirmDeleteFragment();
                dialogFragment.show(getSupportFragmentManager(), "confirm-delete");
                return true;

            case R.id.action_call:
                String number = "tel:" + mPhone.getText().toString().trim();
                intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.withAppendedPath(DataContract.Clients.CONTENT_URI, clientId.toString()),
                DataContract.Clients.ALL_COLUMNS, "_id=?", new String[] {clientId.toString()}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.getCount() != 0) {
            data.moveToFirst();

            String name = data.getString(data.getColumnIndex(DataContract.Clients.CLIENTS_KEY_NAME));
            String address = data.getString(data.getColumnIndex(DataContract.Clients.CLIENTS_KEY_ADDRESS));
            String phone = data.getString(data.getColumnIndex(DataContract.Clients.CLIENTS_KEY_PHONE));
            String email = data.getString(data.getColumnIndex(DataContract.Clients.CLIENTS_KEY_EMAIL));
            String notes = data.getString(data.getColumnIndex(DataContract.Clients.CLIENTS_KEY_NOTES));

            mName.setText(name);
            mAddress.setText(address);
            mPhone.setText(phone);
            mEmail.setText(email);
            mNotes.setText(notes);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        getContentResolver().delete(DataContract.Clients.CONTENT_URI,
                "_id=?", new String[] {clientId.toString()});
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onClick(View view) {
        if (view == mOrderHistoryText) {
            // Collapse-expand oder info layout
            RelativeLayout.LayoutParams params;
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.clientInfo);
            if (is_collapsed) {
                params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                params.height = layoutHeight;
                is_collapsed = false;
            } else {
                params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                layoutHeight = params.height;
                params.height = 50;
                is_collapsed = true;
            }
            layout.setLayoutParams(params);

        }
    }
}
