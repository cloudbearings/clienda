package com.arturobermejo.clienda;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;


public class NewOrderActivity extends FragmentActivity implements DialogDatePickerFragment.DialogDatePickerListener {

    private static final String TAG = "clienda-new-order";
    Fragment orderFormFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Load order form fragment
        orderFormFragment = getSupportFragmentManager().findFragmentByTag(OrderFormFragment.NEW_ORDER_TAG);

        if (orderFormFragment == null) {
            orderFormFragment = new OrderFormFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.newOrderActivity, orderFormFragment, OrderFormFragment.NEW_ORDER_TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_order, menu);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSetDate(DatePicker view, int year, int month, int day) {
        OrderFormFragment fragment = (OrderFormFragment) getSupportFragmentManager().findFragmentByTag("new-order");
        fragment.setDate(view, year, month, day);
    }
}
