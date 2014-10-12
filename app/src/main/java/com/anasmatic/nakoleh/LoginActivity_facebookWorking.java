package com.anasmatic.nakoleh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anasmatic.nakoleh.service.FacebookLoginService;
import com.anasmatic.nakoleh.service.LoginService;
import com.anasmatic.nakoleh.service.parser.UserJSONParser;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

public class LoginActivity extends Activity
                           implements View.OnClickListener,
                                      ConnectionCallbacks,
                                      OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;

    private static final String TAG = "LoginActivity";

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;
    protected static final int REQUEST_CODE_RESOLVE_FACEBOOK = 64206;
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;

    /**
     * Called when the activity is starting. Restores the activity state.
     */
    public ProgressBar progressBar;

//--- g+
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);



        mPlusClient = new PlusClient.Builder(this,this,this)
                .setActions("http://schemas.google.com/AddActivity",
                        "http://scheme.google.com/ListenActivity")
                .setScopes(Scopes.PLUS_LOGIN)
                .build();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        checkConnectivity();
    }

    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }

    protected void onStop() {
        super.onStop();
            mPlusClient.disconnect();
    }

    private void checkConnectivity() {

        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.login_button);


        if(isOnline()) {

            fbLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FacebookLoginService.facbookSession(LoginActivity.this);
                }
            });
            progressBar.setVisibility(View.INVISIBLE);

            findViewById(R.id.sign_in_button).setOnClickListener(LoginActivity.this);

            //LoginService.requestUser("10153455940356002",true);//test line for getuser
        }
        else
        {
            fbLoginButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);

            return;
        }
    }

    public void chickMyAccount(GraphUser user) {
        //TODO: chick if user has an account related to his fbid,
        Log.d(TAG+"chickMyAccount","user:"+user.toString());
        LoginService.requestUser(LoginActivity.this, user.getId(), true);
        //if has, get his data
        //if not, create new account.
    }

    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }


    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG+".onActivityResult","requestCode:"+requestCode+",resultCode:"+resultCode);
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                Log.d(TAG+".onActivityResult","REQUEST_CODE_RESOLUTION");
                break;
            case REQUEST_CODE_RESOLVE_FACEBOOK:
                Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
                break;
            case REQUEST_CODE_RESOLVE_ERR:
                Log.d(TAG+".onActivityResult","REQUEST_CODE_RESOLVE_ERR");

                //if (responseCode != RESULT_OK) {}
                if (resultCode == RESULT_OK) {
                    Log.d(TAG+".onActivityResult","mGoogleApiClient.connect();");
                    mConnectionResult = null;
                    mPlusClient.connect();

                }
                break;
        }
    }

    public void updateDisplay(String result) {
        Log.d(TAG+"updateDisplay","result:"+result);
        Intent intent;
        if(UserJSONParser.parseFeed(result)==null)
        {
            Log.d(TAG+"updateDisplay","go to create user Activity");
            //go to create user Activity
            intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
        }
        else
        {
            Log.d(TAG+"updateDisplay","go to food time line activity");
            // go to food time line activity
        }

    }




// ------------ Google+ login


    @Override
    public void onClick(View view) {
        Log.d("TOUCH !!!!!",view.toString());
        switch (view.getId())
        {
                case R.id.sign_in_button:
                    signInWithGplus();
        }
    }

    private void signInWithGplus() {
        Log.d(TAG+".signInWithGplus","");
        if (!mPlusClient.isConnected() && mConnectionResult != null) {
            Log.d(TAG+".signInWithGplus","isConnected() & mConnectionResult:"+mConnectionResult.toString());
            try {
                Log.d(TAG+".signInWithGplus","try");
                mConnectionResult.startResolutionForResult(LoginActivity.this, REQUEST_CODE_RESOLVE_ERR);
            }catch (IntentSender.SendIntentException e)
            {
                Log.d(TAG+".signInWithGplus","catch");
                e.printStackTrace();
                mConnectionResult = null;
                mPlusClient.connect();
            }
        }
    }



    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // get Profile Information
        Person person = mPlusClient.getCurrentPerson();
        Log.d(TAG+".onConnected Person","id:"+person.getId());
        Log.d(TAG+".onConnected Person","name:"+person.getDisplayName());
        // Update the UI after signin
    }


    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG+".onConnectionFailed.connectionResult",connectionResult.toString());
        mConnectionResult = connectionResult;
    }
}
