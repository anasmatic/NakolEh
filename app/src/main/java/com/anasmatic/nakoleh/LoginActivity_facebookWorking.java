package com.anasmatic.nakoleh;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity_facebookWorking extends Activity implements
        View.OnClickListener{

    private static final int RC_SIGN_IN = 0;

    private static final String TAG = "LoginActivity_facebookWorking";

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;

    /**
     * Called when the activity is starting. Restores the activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }

        setContentView(R.layout.activity_login);

        ApplicationInfo facebookAppInfo;
        try {
            facebookAppInfo = getPackageManager().getApplicationInfo("com.facebook.katana", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            facebookAppInfo = null;
        }
        if(facebookAppInfo == null)
            Log.d(TAG+".facebookAppInfo","NO FACEBOOK APP !");
        else
            Log.d(TAG+".facebookAppInfo",facebookAppInfo.toString());

        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.login_button);
        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facbookSession();
            }
        });

    }


    private void facbookSession()
    {
        List<String> permissions = new ArrayList<String>();
        permissions.add("email");
        // start Facebook Login
        Session.openActiveSession(this, true,permissions, new Session.StatusCallback() {

            // callback when session changes state
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                Log.d(TAG+".facebookSession","isOpened:"+session.isOpened()+" ,isClosed:"+session.isClosed());

                if (session.isOpened()) {

                    // make request to the /me API
                    Request.newMeRequest(session, new Request.GraphUserCallback() {

                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                chickMyAccount(user);
                                Log.d(TAG + "facebookSession", "Hello " + user.getName() + " ,id:" + user.getId() + " ,location:" + user.getLocation());
                                Log.d(TAG+".facebookSession","email:"+user.getProperty("email").toString());
                            }else
                                Log.d(TAG+"facebookSession","User is NULL !");
                        }
                    }).executeAsync();
                }

            }
        });
    }

    private void chickMyAccount(GraphUser user) {
        //TODO: chick if user has an account related to his fbid,
        //if has, get his data
        //if not, create new account.
    }

    /**
     * Called when activity gets invisible. Connection to Play Services needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case REQUEST_CODE_RESOLUTION:
            Log.d("LoginActivity_facebookWorking.onActivityResult","REQUEST_CODE_RESOLUTION");
            break;
        }

        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
        //    case R.id.sign_in_button:
        //        googleBtnClicked();
        }
    }
}
