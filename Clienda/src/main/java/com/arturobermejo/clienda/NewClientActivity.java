package com.arturobermejo.clienda;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class NewClientActivity extends Activity implements OnClickListener{

    private static final String TAG = "clienda-new-client";
    EditText mName, mAddress, mPhone, mEmail, mNotes;
    Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);

        getActionBar().setDisplayHomeAsUpEnabled(true);

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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_client, menu);
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

        Intent intent = getIntent();

        if (view == btnSave) {

            if (formIsValid()) {
                ContentValues values = new ContentValues();
                values.put(DataContract.Clients.CLIENTS_KEY_NAME, mName.getText().toString());
                values.put(DataContract.Clients.CLIENTS_KEY_ADDRESS, mAddress.getText().toString());
                values.put(DataContract.Clients.CLIENTS_KEY_PHONE, mPhone.getText().toString());
                values.put(DataContract.Clients.CLIENTS_KEY_EMAIL, mEmail.getText().toString());
                values.put(DataContract.Clients.CLIENTS_KEY_NOTES, mNotes.getText().toString());
                Uri uri = getContentResolver().insert(DataContract.Clients.CONTENT_URI, values);
                intent.putExtra("clientId", uri.getLastPathSegment());
                intent.putExtra("clientName", mName.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }

        } else if (view == btnCancel) {
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    private boolean formIsValid() {
        if (mName.getText().toString().length() == 0) {
            mName.setError(getString(R.string.validation_client_name_required));
            return false;
        }

        return true;
    }
}
