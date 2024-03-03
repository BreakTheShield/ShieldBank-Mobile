package com.app.damnvulnerablebank;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.util.Log;

public class TransactionFragment extends Fragment {
    private ViewGroup rootView;
    private TransactionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_transaction, container, false);
        getTransaction();
        return rootView;
    }

    public void getTransaction() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("apiurl", Context.MODE_PRIVATE);
        final String url = sharedPreferences.getString("apiurl", null);
        String endpoint = "/api/transactions/view/search";
        final String finalurl = url + endpoint;

        // JSON 데이터 생성
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("tripstart", "1998-02-20 00:00:00");
            jsonBody.put("tripend", "2024-03-03 00:00:00");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 헤더에 JWT 토큰 추가
        SharedPreferences jwtPreferences = getActivity().getSharedPreferences("jwt", Context.MODE_PRIVATE);
        final String retrivedToken = jwtPreferences.getString("accesstoken", null);

        // 요청 큐 생성
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // POST 요청 생성
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, finalurl, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 요청이 성공한 경우 처리
                        Log.d("API Response", response.toString());
                        try {
                            JSONObject decryptedResponse = new JSONObject(EncryptDecrypt.decrypt(response.get("enc_data").toString()));

                            // Check for error message
                            if (decryptedResponse.getJSONObject("status").getInt("code") != 200) {
                                Toast.makeText(getActivity().getApplicationContext(), "Error: " + decryptedResponse.getJSONObject("data").getString("message"), Toast.LENGTH_SHORT).show();
                                return;
                                // This is buggy. Need to call Login activity again if incorrect credentials are given
                            }

                            // JSONArray jsonArray = decryptedResponse.getJSONObject("data").getJSONArray("result");
                            // Log.d("Decrypted Data2", String.valueOf(jsonArray));
                            List<JSONObject> transactionList = new ArrayList<>();
                            try {
                                JSONArray jsonArray = decryptedResponse.getJSONObject("data").getJSONArray("result");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    transactionList.add(jsonArray.getJSONObject(i));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            adapter = new TransactionAdapter(getActivity(), transactionList);
                            RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view_transaction);
                            if (recyclerView != null) {
                                // LinearLayoutManager 설정
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                // 어댑터 설정
                                recyclerView.setAdapter(adapter);
                            } else {
                                Log.e("RecyclerView Error", "RecyclerView is null");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 요청이 실패한 경우 처리
                Log.e("API Error", "Error during API request", error);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                // 헤더에 JWT 토큰 추가
                HashMap headers = new HashMap();
                headers.put("Authorization", "Bearer " + retrivedToken);
                return headers;
            }
        };

        // 요청 큐에 추가
        queue.add(jsonArrayRequest);
        queue.getCache().clear(); // 요청이 캐시되는 것을 방지하기 위해 캐시를 클리어합니다.
    }
}
