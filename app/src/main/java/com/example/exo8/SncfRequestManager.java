package com.example.exo8;

import android.content.Context;
import android.util.Base64;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class SncfRequestManager {
    private static final String BASE_URL = "https://api.sncf.com/v1/coverage/sncf/journeys";

    public static void getJourneys(Context context, String from, String to, String datetime, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "?from=" + from + "&to=" + to + "&datetime=" + datetime;

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener,
                errorListener
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String credentials = context.getString(R.string.sncf_api_key) + ":";
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }
        };

        queue.add(stringRequest);
    }
}
