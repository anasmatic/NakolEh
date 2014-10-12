package com.anasmatic.nakoleh.service;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.anasmatic.nakoleh.LoginActivity;
import com.anasmatic.nakoleh.R;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;

import java.util.Arrays;

/**
 * Created by Omnia on 10/10/2014.
 */
public class FacebookLoginService {

    private static final String TAG = "FacebookLoginService";

    private static LoginActivity loginActivity;

    public static void facbookSession(Activity mainActivity) {

        loginActivity = (LoginActivity) mainActivity;

        checkForFacebookApp();

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        Session fb_session = Session.openActiveSessionFromCache(loginActivity);
        if (fb_session != null && fb_session.isOpened())
        {
            Log.d(TAG + ".facbookSession.fb_session:", String.valueOf(fb_session.getState()));
            makeMeRequest(fb_session);
        }
        else
        {
            Log.d(TAG + ".facbookSession.fb_session:", "is NULL !!!");
            //get session
            if (fb_session == null) {
                fb_session = new Session(loginActivity);
            }

            Session.setActiveSession(fb_session);
            connectToFacebook();

        }
        return;
    }

    private static void connectToFacebook() {
        Session session = Session.getActiveSession();

        if (!session.isOpened() && !session.isClosed())
        {
            Log.d(TAG+"ConnectToFacebook  if == >", "ConnectToFacebook if");
            Session.OpenRequest newSession = new Session.OpenRequest(loginActivity);
            newSession.setCallback(callback);
            session.openForRead(newSession);
            try {
                Session.OpenRequest request = new Session.OpenRequest(loginActivity);
                request.setPermissions(Arrays.asList("email"));
                Log.d(TAG+"ConnectToFacebook  try == >", "set permissions 'email'");
            } catch (Exception e) {
                Log.d(TAG+"ConnectToFacebook  catch == >", "cant open request or set permissions !!");
                e.printStackTrace();
            }
        }
        else
        {
            Log.d("ConnectToFacebook  else == >", "ConnectToFacebook else");
            Session.openActiveSession(loginActivity, true, callback);
        }
    }

    private static Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(final Session session, final SessionState state, final Exception exception) {
            Log.d(TAG + ".callback","state:"+state.toString());
            onSessionStateChange(session, state, exception);
        }
    };

    private static void onSessionStateChange(final Session session, SessionState state, Exception exception) {

        if (session != null) {
            if (session.isOpened()) {
                Log.d(TAG + ".onSessionStateChange.isOpened=true", session.getState().toString());
                makeMeRequest(session);
            }
            else
                Log.d(TAG + ".onSessionStateChange",session.getState().toString());
        }
        else
            Log.d(TAG + ".onSessionStateChange","session is NUll !!");
    }

    private static void makeMeRequest(final Session session) {
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            public void onCompleted(GraphUser user, Response response) {
                try{
                    if (user != null) {
                        Log.d(TAG + ".makeMeRequest.user", "Hello " + user.getName() + " ,id:" + user.getId() + " ,location:" + user.getLocation());
                        if(user.getProperty("email") != null)
                            Log.d(TAG+".makeMeRequest.user","email:"+user.getProperty("email").toString());
                        loginActivity.chickMyAccount(user);
                    }else
                        Log.d(TAG+".makeMeRequest","User is NULL !");
                    if(response != null)
                        Log.d(TAG+".makeMeRequest.response",response.getRawResponse());
                    else
                        Log.d(TAG+".makeMeRequest.response","response is NULL!!!");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        request.executeAsync();
    }


    private static void checkForFacebookApp() {
        //check for facebook app
        ApplicationInfo facebookAppInfo;
        try {
            facebookAppInfo = loginActivity.getPackageManager().getApplicationInfo("com.facebook.katana", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            facebookAppInfo = null;
        }
        if(facebookAppInfo == null)
            Toast.makeText(loginActivity, loginActivity.getString(R.string.facebookLoginWithNoFacebookAppToast), Toast.LENGTH_LONG).show();
        else
            Log.d(TAG + ".facebookAppInfo", facebookAppInfo.toString());
    }

    private Session.StatusCallback sessionStateWatcher() {
        Log.d(TAG+".facebookSession.sessionStateWatcher",Session.getActiveSession().getState().toString());

        return null;
    }

}
