package com.anasmatic.nakoleh.service.parser;

import android.util.Log;

import com.anasmatic.nakoleh.dataobjects.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omnia on 10/10/2014.
 */
public class UserJSONParser {

    private static final String TAG = "UserJSONParser";

    public static List<User> parseFeed(String content) {

        List<User> userList = new ArrayList<>();
        try {
            if(content.charAt(0) == '{')
            {
                Log.d(TAG+"instanceof","JSONObject");
                JSONObject obj = new JSONObject(content);
                if(obj.has("error"))
                    return null;
                else
                    Log.d(TAG+"NOT has(error)","should return something");
            }
            else if (content.charAt(0) == '[')
            {
                Log.d(TAG+"instanceof","JSONArray");

                JSONArray ar = new JSONArray(content);

                for (int i = 0; i < ar.length(); i++) {

                    JSONObject obj = ar.getJSONObject(i);
                    User user = new User();
                    Log.d(TAG, obj.toString());
    /*
                    user.setProductId(obj.getInt("productId"));
                    user.setName(obj.getString("name"));
                    user.setCategory(obj.getString("category"));
                    user.setInstructions(obj.getString("instructions"));
                    user.setPhoto(obj.getString("photo"));
                    user.setPrice(obj.getDouble("price"));
    */
                    userList.add(user);
                }
            }

            return userList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
