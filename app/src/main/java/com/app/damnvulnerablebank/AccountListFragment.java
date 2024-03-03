package com.app.damnvulnerablebank;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.app.damnvulnerablebank.EncryptDecrypt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.*;

public class AccountListFragment extends Fragment {
    private JSONArray dataArray;

    LinearLayout linear_layout_request_money,linear_layout_send_money, linear_layout_history;
    ImageView add_bank_account, add_credit_card;
    RecyclerView recyclerView;
    RecyclerView recyclerViewbankaccount, recyclerViewHistory;
    TextView text_view_name, date,text_view_total_money;
    ArrayList<BankAccount> myBankAccount;
    BankAccount sendUser = null;
    String bankAccountAnother = null;
    String anotherUserid;


    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        OkHttpClient client2 = new OkHttpClient();
        EncryptDecrypt endecryptor2 = new EncryptDecrypt();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("jwt", Context.MODE_PRIVATE);
        final String retrivedToken2  = sharedPreferences.getString("accesstoken",null);

        String apiUrl2 = "http://59.16.223.162:38888/api/Account/view";

        RequestBody requestBody2 = new FormBody.Builder()
                .add("username", "username")
                .add("balance", "balance")
                .add("account_number", "account_number")
                .add("bank_code", "bank_code")
                // 다른 필요한 데이터도 추가해주세요
                .build();
        Request request2 = new Request.Builder()
                .url(apiUrl2)
                .post(requestBody2)
                .addHeader("Authorization", "1 " + retrivedToken2)
                .build();

        Log.d("request", "data " + request2);

        String encryptedData2 = endecryptor2.encrypt(request2.toString());
        Log.d("request", "data123123 " + encryptedData2);

        // 비동기적으로 API 요청 보내기
        client2.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 요청 실패 처리
                e.printStackTrace();
                Log.e("API_RESPONSE", "JSON parsing error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 요청 성공 시 처리
                if (response.isSuccessful()) {
                    String responseData2 = response.body().string();
                    Log.d("API_RESPONSE", "JSON Response: " + responseData2);


                    // 응답 데이터 파싱
                    try {
                        JSONObject jsonResponse3 = new JSONObject(responseData2);
                        String encData2 = jsonResponse3.getString("enc_data");
                        String data2 = endecryptor2.decrypt(encData2);
                        Log.d("API_RESPONSE", "JSON Response: " + data2);

                        JSONObject dataObject2 = new JSONObject(data2);

                        dataArray = dataObject2.getJSONArray("data");

                        Log.d("API_RESPONSE", "JSON Response: " + dataArray);

                        updateRecyclerView(dataArray);

                        // TODO: 가져온 값들을 사용하여 원하는 작업 수행
                        // 예를 들면 UI 업데이트 등
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("API_RESPONSE", "Error: " + e.getMessage());
                    }
                } else {
                    // 서버에서 오류 응답이 온 경우 처리
                    // response.code() 및 response.message()를 통해 상세한 정보를 얻을 수 있음
                }
            }
        });
        return inflater.inflate(R.layout.fragment_account_list, container, false);
    }



    private void updateRecyclerView(JSONArray dataArray) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("API_RESPONSE", "JSON Response2222222222222: " + dataArray);
                ArrayList<BankAccount> bankAccounts = convertJSONArrayToArrayList(dataArray);

                recyclerViewbankaccount.setHasFixedSize(true);
                recyclerViewbankaccount.setLayoutManager(new LinearLayoutManager(getActivity()));

                MyBankAccountAdapter myBankAccountAdapter = new MyBankAccountAdapter(bankAccounts, getActivity());
                recyclerViewbankaccount.setAdapter(myBankAccountAdapter);
            }
        });
    }

    private ArrayList<BankAccount> convertJSONArrayToArrayList(JSONArray jsonArray) {
        ArrayList<BankAccount> bankAccounts = new ArrayList<>();
        Log.d("API_RESPONSE", "lengthglafjsafaas: " + jsonArray.length());

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    BankAccount bankAccount = new BankAccount();
                    bankAccount.setBalance(jsonObject.getInt("balance"));
                    bankAccount.setAccount_number(jsonObject.getInt("account_number"));
                    // 다른 필요한 데이터도 추가해주세요
                    Log.d("API_RESPONSE", "Balance123123: " + bankAccount.getBalance());
                    Log.d("API_RESPONSE", "Account Number123123: " + bankAccount.getAccount_number());

                    bankAccounts.add(bankAccount);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("API_RESPONSE", "arrayerrorrrrrrrrrrrrrrrrr: " + e.getMessage());
                }
            }
        }

        return bankAccounts;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //myBankAccount = mainUser.getBankAccounts();

        define();
        setDate();
        click();
        //setTotalMoney(myBankAccount);
        //text_view_name.setText("HELLO, " + mainUser.getName().toUpperCase()+".");

        OkHttpClient client2 = new OkHttpClient();
        EncryptDecrypt endecryptor2 = new EncryptDecrypt();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("jwt", Context.MODE_PRIVATE);
        final String retrivedToken2  = sharedPreferences.getString("accesstoken",null);

        String apiUrl2 = "http://59.16.223.162:38888/api/Account/view";

        RequestBody requestBody2 = new FormBody.Builder()
                .add("username", "username")
                .add("balance", "balance")
                .add("account_number", "account_number")
                .add("bank_code", "bank_code")
                // 다른 필요한 데이터도 추가해주세요
                .build();
        Request request2 = new Request.Builder()
                .url(apiUrl2)
                .post(requestBody2)
                .addHeader("Authorization", "1 " + retrivedToken2)
                .build();

        Log.d("request", "data " + request2);

        String encryptedData2 = endecryptor2.encrypt(request2.toString());
        Log.d("request", "data123123 " + encryptedData2);

        // 비동기적으로 API 요청 보내기
        client2.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 요청 실패 처리
                e.printStackTrace();
                Log.e("API_RESPONSE", "JSON parsing error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 요청 성공 시 처리
                if (response.isSuccessful()) {
                    String responseData2 = response.body().string();
                    Log.d("API_RESPONSE", "JSON Response: " + responseData2);


                    // 응답 데이터 파싱
                    try {
                        JSONObject jsonResponse3 = new JSONObject(responseData2);
                        String encData2 = jsonResponse3.getString("enc_data");
                        String data2 = endecryptor2.decrypt(encData2);
                        Log.d("API_RESPONSE", "JSON Response: " + data2);

                        JSONObject dataObject2 = new JSONObject(data2);

                        dataArray = dataObject2.getJSONArray("data");

                        Log.d("API_RESPONSE", "JSON Response: " + dataArray);

                        updateRecyclerView(dataArray);

                        // TODO: 가져온 값들을 사용하여 원하는 작업 수행
                        // 예를 들면 UI 업데이트 등
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("API_RESPONSE", "Error: " + e.getMessage());
                    }
                } else {
                    // 서버에서 오류 응답이 온 경우 처리
                    // response.code() 및 response.message()를 통해 상세한 정보를 얻을 수 있음
                }
            }
        });
    }

    public void define(){
        text_view_name = getView().findViewById(R.id.text_view_name);
        date = getView().findViewById(R.id.text_view_date_main);
        recyclerViewbankaccount = getView().findViewById(R.id.recyclerview_bank_account);
        add_bank_account = getView().findViewById(R.id.image_view_add_bank_account);
        linear_layout_request_money = getView().findViewById(R.id.linear_layout_request_money);
        text_view_total_money = getView().findViewById(R.id.text_view_total_money);
        linear_layout_send_money = getView().findViewById(R.id.linear_layout_send_money);
        //linear_layout_history = getView().findViewById(R.id.linear_layout_history);

    }

    public void setTotalMoney(ArrayList<BankAccount> MyBankAccounts){
        int totalmoney = 0;

        for (int i = 0; i<MyBankAccounts.size();i++){
            totalmoney += MyBankAccounts.get(i).getBalance();
        }
        text_view_total_money.setText(Integer.toString(totalmoney));
    }

    public void click() {
        // "ADD" 버튼 클릭 이벤트 처리 코드
        add_bank_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 버튼을 눌렀을 때 실행되는 코드

                // OkHttp 클라이언트 생성
                OkHttpClient client = new OkHttpClient();
                EncryptDecrypt endecryptor = new EncryptDecrypt();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("jwt", Context.MODE_PRIVATE);
                final String retrivedToken  = sharedPreferences.getString("accesstoken",null);

                // API 엔드포인트 URL 설정
                String apiUrl = "http://59.16.223.162:38888/api/Account/create";

                // 요청 바디에 필요한 데이터 설정 (예: 사용자 정보, 계좌 정보 등)
                // 아래는 예시일 뿐 실제로는 사용자 입력 등을 통해 값을 동적으로 설정해야 합니다.
                RequestBody requestBody = new FormBody.Builder()
                        .add("username", "username")
                        .add("balance", "balance")
                        .add("account_number", "account_number")
                        .add("bank_code", "bank_code")
                        // 다른 필요한 데이터도 추가해주세요
                        .build();


                // API 요청 생성
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .post(requestBody)
                        .addHeader("Authorization", "1 " + retrivedToken)
                        .build();

                Log.d("request", "data " + request);

                String encryptedData = endecryptor.encrypt(request.toString());
                Log.d("request", "data123123 " + encryptedData);

                // 비동기적으로 API 요청 보내기
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // 요청 실패 처리
                        e.printStackTrace();
                        Log.e("API_RESPONSE", "JSON parsing error: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // 요청 성공 시 처리
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            Log.d("API_RESPONSE", "JSON Response: " + responseData);


                            // 응답 데이터 파싱
                            try {
                                JSONObject jsonResponse2 = new JSONObject(responseData);
                                String encData = jsonResponse2.getString("enc_data");
                                String data = endecryptor.decrypt(encData);
                                Log.d("API_RESPONSE", "JSON Response: " + data);
                                JSONObject jsonResponse = new JSONObject(data);
                                JSONObject dataObject = jsonResponse.getJSONObject("data");

                                String balance = dataObject.getString("balance");
                                String accountNumber = dataObject.getString("account_number");
                                String bankCode = dataObject.getString("bank_code");

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("API_RESPONSE", "Error: " + e.getMessage());
                            }
                        } else {
                            // 서버에서 오류 응답이 온 경우 처리
                            // response.code() 및 response.message()를 통해 상세한 정보를 얻을 수 있음
                        }
                    }
                });
            }
        });


        // "linear_layout_send_money" 버튼 클릭 이벤트 처리 코드
        linear_layout_send_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "linear_layout_send_money" 버튼 클릭 시 실행할 코드

                // Intent를 통해 새로운 Activity 시작
                Intent intent = new Intent(getActivity(), SendMoney.class);
                startActivity(intent);
            }
        });


    }


    public void setDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date currentTime = Calendar.getInstance().getTime();
        date.setText(format.format(currentTime));

    }
    public Date getDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime;
    }

}