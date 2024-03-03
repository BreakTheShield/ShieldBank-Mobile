    package com.app.damnvulnerablebank;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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

import java.util.HashMap;
import java.util.Map;

public class MydataFragment extends Fragment {

    private String phoneNumber;
    private String userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mydata, container, false);

        Button myButton = rootView.findViewById(R.id.button2);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 시 실행할 코드 작성
                fetchPhoneNumberFromServer();
                Log.d("MyDataFragment", "Phone number1111: " + phoneNumber); // 로그에 출력
                Log.d("MyDataFragment", "User Name 2222: " + userName);
            }
        });
        return rootView;
    }

    // 서버에서 phone_number를 가져오는 메서드
    private void fetchPhoneNumberFromServer() {
        // SharedPreferences에서 API URL 가져오기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("apiurl", Context.MODE_PRIVATE);
        String url = sharedPreferences.getString("apiurl", null);

        if (url != null) {
            // API URL이 정상적으로 설정되어 있을 때만 요청을 보냄
            String endpoint = "/api/user/profile";
            String finalUrl = url + endpoint;

            // Volley RequestQueue 생성
            RequestQueue queue = Volley.newRequestQueue(getActivity());

            // JWT 토큰 가져오기
            SharedPreferences tokenPreferences = getActivity().getSharedPreferences("jwt", Context.MODE_PRIVATE);
            final String accessToken = tokenPreferences.getString("accesstoken", null);

            if (accessToken != null) {
                // 헤더 설정
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, finalUrl, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject decryptedResponse = new JSONObject(EncryptDecrypt.decrypt(response.get("enc_data").toString()));
                                    phoneNumber = decryptedResponse.getJSONObject("data").getString("phone");
                                    userName = decryptedResponse.getJSONObject("data").getString("username");
                                    // 클릭이 잘 되었는지 로그로 확인
                                    Log.d("MyDataFragment", "Phone number: " + phoneNumber);
                                    Log.d("MyDataFragment", "User Name : " + userName);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MyDataFragment", "Error fetching phone number: " + error.getMessage());
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + accessToken);
                        return headers;
                    }
                };

                // 요청을 큐에 추가
                queue.add(jsonObjectRequest);
            } else {
                Log.e("MyDataFragment", "Access token is null");
            }
        } else {
            Log.e("MyDataFragment", "API URL is null");
        }
    }
    private void updateRecyclerView(String jsonString) {
        try {
            JSONArray dataArray = new JSONArray(jsonString);
            Log.d("API_RESPONSE", "JSON Response2222222222222: " + dataArray);
            ArrayList<userName> userNames = convertJSONArrayToArrayList(dataArray);

            MyuserNameAdapter myuserNameAdapter = new MyuserNameAdapter(userNames, getActivity());
            recyclerViewuserName.setAdapter(myuserNameAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("API_RESPONSE", "JSON Parsing Error: " + e.getMessage());
        }
    }

    private ArrayList<userName> convertJSONArrayToArrayList(JSONArray jsonArray) {
        Log.d("API_RESPONSE", "JSON Response33333333333333333333: " + jsonArray);
        ArrayList<userName> userNames = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    userName userName = new userName();
                    userName.setBalance(jsonObject.getInt("balance"));
                    userName.setAccount_number(jsonObject.getInt("account_number"));
                    // 다른 필요한 데이터도 추가해주세요
                    Log.d("API_RESPONSE", "Balance123123: " + userName.getBalance());
                    Log.d("API_RESPONSE", "Account Number123123: " + userName.getAccount_number());

                    userNames.add(userName);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("API_RESPONSE", "JSON Parsing Error: " + e.getMessage());
                }
            }
        }

        return userNames;
}
