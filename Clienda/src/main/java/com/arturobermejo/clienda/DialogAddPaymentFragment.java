package com.arturobermejo.clienda;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.GregorianCalendar;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class DialogAddPaymentFragment extends DialogFragment {

    TextView mAmount;
    DatePicker mDate;

    public interface DialogAddPaymentListener {
        public void onConfirmAddPayment(DialogInterface dialog, String amount, Long date, TextView amountView);
        public void onCancelAddPayment(DialogFragment dialog);
    }

    DialogAddPaymentListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogAddPaymentListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DialogConfirmDeleteListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_add_payment, null);
        mAmount = (TextView) view.findViewById(R.id.addPaymentAmount);
        mDate = (DatePicker) view.findViewById(R.id.addPaymentDate);

        builder.setView(view);
        builder.setTitle(R.string.add_payment);
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String amount = mAmount.getText().toString();
                Long date = new GregorianCalendar(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth()).getTimeInMillis();
                mListener.onConfirmAddPayment(dialog, amount, date, mAmount);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onCancelAddPayment(DialogAddPaymentFragment.this);
                dialog.dismiss();
            }
        });

        return builder.show();
    }

}
