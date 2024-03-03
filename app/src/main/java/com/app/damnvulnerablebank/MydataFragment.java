package com.app.damnvulnerablebank;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    LinearLayout mydata_sms_auth;

    private String phoneNumber;
    public String userName;

    private String user_name;

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mydata, container, false);
        mydata_sms_auth = getView().findViewById(R.id.mydata_sms_auth);
        Button myButton = rootView.findViewById(R.id.mydata_sms_auth);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 시 실행할 코드 작성
                //openPopup();

                fetchPhoneNumberFromServer(new FetchPhoneNumberCallback() {
                    @Override
                    public void onComplete(String phoneNumber, String userName) {
                        // fetchPhoneNumberFromServer 메서드가 완료된 후에 호출되는 콜백 메서드
                        //Log.d("MyDataFragment", "Phone number1111: " + phoneNumber); // 로그에 출력
                        Log.d("MyDataFragment", "User Name 2222: " + userName);

                        user_name = userName;

                    }
                });

            }
        });
        mydata_sms_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "linear_layout_send_money" 버튼 클릭 시 실행할 코드

                // Intent를 통해 새로운 Activity 시작
                Intent intent = new Intent(getActivity(), Mydata_auth.class);
                //startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });
        return inflater.inflate(R.layout.fragment_mydata, container, false);
    }

    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // 1은 startActivityForResult()에서 사용한 요청 코드입니다.
            if (resultCode == Activity.RESULT_OK) {
                // 활동이 성공적으로 반환된 경우
                fetchAccountData();
            }
        }
    }*/

    // 서버에서 phone_number를 가져오는 메서드
    private void fetchPhoneNumberFromServer(final FetchPhoneNumberCallback callback) {
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
                                    String phoneNumber = decryptedResponse.getJSONObject("data").getString("phone");
                                    String userName = decryptedResponse.getJSONObject("data").getString("username");
                                    // 클릭이 잘 되었는지 로그로 확인
                                    //Log.d("MyDataFragment", "Phone number: " + phoneNumber);
                                    Log.d("MyDataFragment", "User Name : " + userName);


                                    // 콜백을 통해 결과 전달
                                    callback.onComplete(phoneNumber, userName);
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
    private String processUserName(String userName) {
        // userName을 사용하는 로직 작성
        Log.d("MyDataFragment", "1233323 user name: " + userName);
        user_name = userName;
        Log.d("MyDataFragment", "3231213123213213 user name: " + user_name);
        // 이후 필요한 작업 수행
        return user_name;
    }
    private void openPopup() {
        // 팝업창을 띄우는 코드 작성
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_sms_mydata, null); // 팝업창에 사용할 XML 파일
        EditText editTextNumber = dialogView.findViewById(R.id.editTextNumber);

        builder.setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // OK 버튼 클릭 시 처리할 내용 작성
                        String auth_number = editTextNumber.getText().toString();
                        // 여기서 입력된 숫자를 사용하여 원하는 동작을 수행할 수 있음
                        Toast.makeText(getContext(), "AUThNumber entered: " + auth_number, Toast.LENGTH_SHORT).show();
                        Log.d("MyDataFragment", "auth num is : " + auth_number);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel 버튼 클릭 시 처리할 내용 작성
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 콜백 인터페이스 정의
    interface FetchPhoneNumberCallback {
        void onComplete(String phoneNumber, String userName);
    }

}
