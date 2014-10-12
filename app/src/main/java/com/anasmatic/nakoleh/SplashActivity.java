package com.anasmatic.nakoleh;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anasmatic.nakoleh.constant.C;
import com.anasmatic.nakoleh.service.connectivity.CheckConnectivity;
import com.anasmatic.nakoleh.service.connectivity.ConnectivityChangeReceiver;


public class SplashActivity extends ActionBarActivity {

    //private ConnectivityChangeReceiver mReceiver;
    TextView errorMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        errorMsg = (TextView) findViewById(R.id.connectionErrorTextView);
        errorMsg.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
/*
        this.mReceiver = new ConnectivityChangeReceiver();
        registerReceiver(
                this.mReceiver,
                new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION));
*/
        if(CheckConnectivity.isOnline(SplashActivity.this) == false)
        {
            errorMsg.setVisibility(View.VISIBLE);
            Toast.makeText(SplashActivity.this,"إتأكد إنك عندك نت!!",Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences sharedUserDate = getSharedPreferences(C.SHARED_USER_DATA_NAME,MODE_PRIVATE);
        int userId = sharedUserDate.getInt("userId",0);//in case users are in billions or id system changed to bigger values , use long and getLong()
        Intent intent = null;
        if(userId == 0)
        {
            // go to LoginActivity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        else
        {
            //go to MealsActivity
        }

        startActivity(intent);

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash_acti3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
