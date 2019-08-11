package com.videostatusiranna.utilities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.videostatusiranna.R;



public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        Log.e(TAG, "sendRegistrationToServer: " + token);
        if (pref.getBoolean("firstrun", true)) {
            if (token != null) {
                postRegistrationId(token);
            }
        }
    }

    private void postRegistrationId(final String token) {
        // TODO: Implement this method to send token to your app server.
        //creating a string request to send request to the url
        String hp = getString(R.string.link) + "api/token.php";
        hp = hp.replace(" ", "%20");
        Log.w(getClass().getName(), hp);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, hp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        Log.e("Response", response);
                        try {
                            JSONObject jo_main = new JSONObject(response);
                            JSONArray jo_data = jo_main.getJSONArray("Status");
                            if (jo_data.getJSONObject(0).getString("id").equals("True")) {
                                Toast.makeText(getApplicationContext(), "WELLCOME!", Toast.LENGTH_SHORT).show();
                                getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE).edit().putBoolean("firstrun", false).apply();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Registration UnSuccessful!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurs
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Log.e("Status code", String.valueOf(networkResponse.statusCode));
                            Toast.makeText(getApplicationContext(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("token_id", token);
                param.put("device_type", "android");
                return param;
            }
        };
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        Log.e(TAG, "sendRegistrationToServer11: " + token);
        editor.putString("regId", token);
        editor.apply();
    }


}

