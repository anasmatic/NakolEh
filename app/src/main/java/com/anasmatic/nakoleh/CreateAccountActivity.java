package com.anasmatic.nakoleh;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;


public class CreateAccountActivity extends ActionBarActivity {

    private static final String TAG = "CreateAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Bundle userDataBundle = getIntent().getExtras();
        EditText uNameEditText = (EditText) findViewById(R.id.uNameEditText);
                 uNameEditText.setText(userDataBundle.getString("uName"), TextView.BufferType.EDITABLE);
        Boolean isMale = (userDataBundle.getString("gender").equals("male")) ? true:false;
        Log.d(TAG+".gender?",userDataBundle.getString("gender"));
        Log.d(TAG+".isMale?",isMale.toString());
        RadioButton maleRadioButton = (RadioButton) findViewById(R.id.maleRadioButton);
        maleRadioButton.setChecked(isMale);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_account, menu);
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
