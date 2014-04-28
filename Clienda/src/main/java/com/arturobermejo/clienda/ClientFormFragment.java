package com.arturobermejo.clienda;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by abermejo on 4/27/14.
 */
public class ClientFormFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "clienda=client-form-fragment";
    public static final String NEW_CLIENT_TAG = "new-client";
    public static final String EDIT_CLIENT_TAG = "edit-client";
    EditText mName, mAddress, mPhone, mEmail, mNotes;
    Button btnSave, btnCancel;
    Long clientId;

    public ClientFormFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_form, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get UI elements
        mName = (EditText) getView().findViewById(R.id.formClientName);
        mAddress = (EditText) getView().findViewById(R.id.formClientAddress);
        mPhone = (EditText) getView().findViewById(R.id.formClientPhone);
        mEmail = (EditText) getView().findViewById(R.id.formClientEmail);
        mNotes = (EditText) getView().findViewById(R.id.formClientNotes);

        // Save Client
        btnSave = (Button) getView().findViewById(R.id.formClientSave);
        btnSave.setOnClickListener(this);

        // Cancel Client
        btnCancel = (Button) getView().findViewById(R.id.formClientCancel);
        btnCancel.setOnClickListener(this);

        // Set data for edit mode
        if(getTag() == EDIT_CLIENT_TAG){
            setData();
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = getActivity().getIntent();

        if (view == btnSave) {
            if (formIsValid()) {
                ContentValues values = new ContentValues();
                values.put(DataContract.Clients.CLIENTS_KEY_NAME, mName.getText().toString());
                values.put(DataContract.Clients.CLIENTS_KEY_ADDRESS, mAddress.getText().toString());
                values.put(DataContract.Clients.CLIENTS_KEY_PHONE, mPhone.getText().toString());
                values.put(DataContract.Clients.CLIENTS_KEY_EMAIL, mEmail.getText().toString());
                values.put(DataContract.Clients.CLIENTS_KEY_NOTES, mNotes.getText().toString());

                if(getTag() == NEW_CLIENT_TAG){
                    Uri uri = getActivity().getContentResolver().insert(DataContract.Clients.CONTENT_URI, values);
                    intent.putExtra("clientId", uri.getLastPathSegment());
                    intent.putExtra("clientName", mName.getText().toString());
                } else if (getTag() == EDIT_CLIENT_TAG){
                    getActivity().getContentResolver().update(DataContract.Clients.CONTENT_URI, values,
                            "_id=?", new String[] { clientId.toString() });
                }

                getActivity().setResult(getActivity().RESULT_OK, intent);
                getActivity().finish();
            }

        } else if (view == btnCancel) {
            getActivity().setResult(getActivity().RESULT_CANCELED, intent);
            getActivity().finish();
        }
    }

    private boolean formIsValid() {
        if (mName.getText().toString().length() == 0) {
            mName.setError(getString(R.string.validation_client_name_required));
            return false;
        }

        return true;
    }

    private void setData() {
        clientId = getArguments().getLong("clientId");
        Cursor data = getActivity().getContentResolver().query(Uri.withAppendedPath(DataContract.Clients.CONTENT_URI, clientId.toString()),
                DataContract.Clients.ALL_COLUMNS, "_id=?", new String[] {clientId.toString()}, null);

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
}
