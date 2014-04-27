package com.arturobermejo.clienda;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class EditClientActivity extends FragmentActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "clienda-edit-client";
    public final static String EXTRA_MESSAGE = "clientId";
    Long clientId;
    EditText mName, mAddress, mPhone, mEmail, mNotes;
    Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);

        mName = (EditText) findViewById(R.id.formClientName);
        mAddress = (EditText) findViewById(R.id.formClientAddress);
        mPhone = (EditText) findViewById(R.id.formClientPhone);
        mEmail = (EditText) findViewById(R.id.formClientEmail);
        mNotes = (EditText) findViewById(R.id.formClientNotes);

        // Save Client
        btnSave = (Button) findViewById(R.id.formClientSave);
        btnSave.setOnClickListener(this);

        // Cancel Client
        btnCancel = (Button) findViewById(R.id.formClientCancel);
        btnCancel.setOnClickListener(this);

        Intent intent = getIntent();
        clientId = intent.getLongExtra(ClientListFragment.EXTRA_MESSAGE, 0);

        getSupportLoaderManager().initLoader(0, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_client, menu);
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
            Intent intent = new Intent(this, ClientDetailActivity.class);
            intent.putExtra(EXTRA_MESSAGE, clientId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view == btnSave) {
            ContentValues values = new ContentValues();
            values.put(DataContract.Clients.CLIENTS_KEY_NAME, mName.getText().toString());
            values.put(DataContract.Clients.CLIENTS_KEY_ADDRESS, mAddress.getText().toString());
            values.put(DataContract.Clients.CLIENTS_KEY_PHONE, mPhone.getText().toString());
            values.put(DataContract.Clients.CLIENTS_KEY_EMAIL, mEmail.getText().toString());
            values.put(DataContract.Clients.CLIENTS_KEY_NOTES, mNotes.getText().toString());
            getContentResolver().update(DataContract.Clients.CONTENT_URI, values,
                                        "_id=?", new String[] {clientId.toString()});
            finish();
        } else if (view == btnCancel) {
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Uri.withAppendedPath(DataContract.Clients.CONTENT_URI, clientId.toString()),
                DataContract.Clients.ALL_COLUMNS, "_id=?", new String[] {clientId.toString()}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
