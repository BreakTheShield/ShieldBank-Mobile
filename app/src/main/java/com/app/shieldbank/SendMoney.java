package com.app.shieldbank;
// 송금 Activity

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.app.shieldbank.PendingBeneficiary.beneficiary_account_number;

public class SendMoney extends AppCompatActivity {

    Button send;
    TextView tt;
    Date currentDate = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmoney);

        Toolbar toolbar = findViewById(R.id.toolbar);
        // 툴바에 뒤로가기 버튼 표시
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tt = findViewById(R.id.actid);
        Intent i = getIntent();
        String p = i.getStringExtra(beneficiary_account_number);
        tt.setText(p);
        send = findViewById(R.id.sendbutton);

        send.setOnClickListener(v -> sendMoney());
    }

    public void sendMoney() {
        SharedPreferences sharedPreferences = getSharedPreferences("jwt", Context.MODE_PRIVATE);
        final String retrivedToken = sharedPreferences.getString("accesstoken", null);
        SharedPreferences sharedPreferences1 = getSharedPreferences("apiurl", Context.MODE_PRIVATE);
        final String url = sharedPreferences1.getString("apiurl", null);
        String endpoint = "/api/balance/transfer";
        final String finalUrl = url + endpoint;

        EditText ed = findViewById(R.id.edact);     // 송금계좌
        EditText ed2 = findViewById(R.id.edact2);     // 수취계좌
        EditText ed3 = findViewById(R.id.edamt);    // 이체금액
        int from_account = 0;
        int to_account = 0;
        int amount = 0;
        String sendtime = dateFormat.format(currentDate);

        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject requestData = new JSONObject();
        JSONObject requestDataEncrypted = new JSONObject();
        try {
            // fetch values
            if (!ed.getText().toString().isEmpty() && !ed2.getText().toString().isEmpty() && !ed3.getText().toString().isEmpty()) {
                from_account = Integer.parseInt(ed.getText().toString());
                to_account = Integer.parseInt(ed2.getText().toString());
                amount = Integer.parseInt(ed3.getText().toString());
            } else {
                Toast.makeText(getApplicationContext(), "Invalid Input ", Toast.LENGTH_SHORT).show();
                onRestart();
            }

            // input your API parameters
            requestData.put("from_account", from_account);  // 송금계좌 varchar
            requestData.put("to_account", to_account);      // 수취계좌 varchar
            requestData.put("amount", amount);              // 이체금액 int
            requestData.put("sendtime", sendtime);          // 전송시간 datetime

            //Log.d("formmmmmmmmmmmm", requestData.toString());

            // Encrypt data before sending
            requestDataEncrypted.put("enc_data", EncryptDecrypt.encrypt(requestData.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Enter the correct url for your api service site
        final int initialTimeoutMs = 2000; // 초기 타임아웃 값 (5초)
        final int maxNumRetries = 0; // 최대 재시도 횟수
        final float backoffMultiplier = 1f; // 재시도 간격의 배수

        RetryPolicy policy = new DefaultRetryPolicy(initialTimeoutMs, maxNumRetries, backoffMultiplier);




        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, finalUrl, requestDataEncrypted,
                response -> {
                    Log.d("sendxxx","yes");
                    try {
                        JSONObject decryptedResponse = new JSONObject(EncryptDecrypt.decrypt(response.get("enc_data").toString()));
                        //Log.d("Send Money", decryptedResponse.toString());

                        if (decryptedResponse.getJSONObject("status").getInt("code") != 200) {
                            Toast.makeText(getApplicationContext(), "Error: " + decryptedResponse.getJSONObject("data").getString("message"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(getApplicationContext(), "" + EncryptDecrypt.decrypt(response.get("enc_data").toString()), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Log.d("1234", String.valueOf(e));
                        e.printStackTrace();
                    }

                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }, error -> Toast.makeText(getApplicationContext(), "Something went wrong[Send]", Toast.LENGTH_SHORT).show()) {
                    @Override
                    public Map getHeaders() throws AuthFailureError {
                        HashMap headers = new HashMap();
                        headers.put("Authorization", "Bearer " + retrivedToken);
                        return headers;
                    }

                    @Override
                    public RetryPolicy getRetryPolicy() {
                        // RetryPolicy 설정
                        return policy;
                    }
                };

        requestQueue.add(jsonObjectRequest);
        requestQueue.getCache().clear();
    }
}