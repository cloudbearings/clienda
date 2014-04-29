package com.arturobermejo.clienda;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;


public class EditOrderActivity extends ActionBarActivity implements DialogDatePickerFragment.DialogDatePickerListener {

    private static final String TAG = "clienda-edit-order";
    Fragment orderFormFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get order id
        Intent intent = getIntent();
        Long orderId = intent.getLongExtra("orderId", 0);

        // Load order form fragment
        orderFormFragment = getSupportFragmentManager().findFragmentByTag(OrderFormFragment.EDIT_ORDER_TAG);

        if (orderFormFragment == null) {
            Bundle bundle = new Bundle();
            bundle.putLong("orderId", orderId);
            orderFormFragment = new OrderFormFragment();
            orderFormFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.newOrderActivity, orderFormFragment, OrderFormFragment.EDIT_ORDER_TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSetDate(DatePicker view, int year, int month, int day) {
        OrderFormFragment fragment = (OrderFormFragment) getSupportFragmentManager().findFragmentByTag("edit-order");
        fragment.setDate(view, year, month, day);
    }

}
