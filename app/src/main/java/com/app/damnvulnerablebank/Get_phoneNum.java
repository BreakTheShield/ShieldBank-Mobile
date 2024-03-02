package com.app.damnvulnerablebank;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Get_phoneNum {
    private static Get_phoneNum instance;
    private String phoneNumber;
    private Context context;

    private Get_phoneNum(Context context) {
        this.context = context;
        fetchPhoneNumber(); // 생성 시에 phoneNumber를 가져옵니다.
    }

    // Singleton 패턴을 사용하여 유일한 인스턴스를 반환합니다.
    public static synchronized Get_phoneNum getInstance(Context context) {
        if (instance == null) {
            instance = new Get_phoneNum(context);
        }
        return instance;
    }

    // 사용자의 프로필 정보를 가져와서 phoneNumber를 설정합니다.
    private void fetchPhoneNumber() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("jwt", Context.MODE_PRIVATE);
        final String accessToken = sharedPreferences.getString("accesstoken", null);
        sharedPreferences = context.getSharedPreferences("apiurl", Context.MODE_PRIVATE);
        final String apiUrl = sharedPreferences.getString("apiurl", null);

        final RequestQueue queue = Volley.newRequestQueue(context);
        final String endpoint = "/api/user/profile";
        final String finalUrl = apiUrl + endpoint;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, finalUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject profileData = response.getJSONObject("data");
                            phoneNumber = profileData.getString("phone_number");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
        queue.getCache().clear();
    }

    // phoneNumber를 반환하는 메서드
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
