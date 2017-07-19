package com.example.rzahab.generator;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;


public class GsonRequest {

    private static String RequestURL;
    Generator app;
    Activity currentActivity;
    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(currentActivity.getClass().getSimpleName(), error.toString());
        }
    };
    String methodName;
    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d("PostsActivity", response);
            callMethod(response);
        }
    };
    private RequestQueue requestQueue;

    public GsonRequest(Activity activity, String methodName) {

        this.currentActivity = activity;
        this.methodName = methodName;

        requestQueue = Volley.newRequestQueue(currentActivity.getApplicationContext());

        app = ((Generator) this.currentActivity.getApplication());
        RequestURL = app.getGeneratorURL();
    }

    public void post(final Map<String, String> postParams) {
        StringRequest request = new StringRequest(Request.Method.POST, RequestURL, onPostsLoaded, onPostsError) {

            @Override
            protected Map<String, String> getParams() {
                for (Map.Entry<String, String> entry : postParams.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    postParams.put(key, value);
                }
                return postParams;
            }
        };
        requestQueue.add(request);
    }

    private void callMethod(String returnedJson) {
        Method method;

        try {
            method = currentActivity.getClass().getMethod(methodName, String.class);
            method.invoke(currentActivity, returnedJson);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}