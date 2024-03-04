package com.app.damnvulnerablebank;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.damnvulnerablebank.EncryptDecrypt;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import kotlin.Result;
import okhttp3.*;




public class LoanFragment extends Fragment {




    public interface LoanCallback {
        void onLoanResult(String isLoan) throws JSONException;
    }


    public void getLoan(final LoanCallback callback) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("jwt", Context.MODE_PRIVATE);
        final String retrivedToken = sharedPreferences.getString("accesstoken", null);
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        sharedPreferences = getActivity().getSharedPreferences("apiurl", Context.MODE_PRIVATE);
        final String url = sharedPreferences.getString("apiurl", null);
        String endpoint = "/api/loan/loan";
        String finalurl = url + endpoint;

        final JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, finalurl, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject decryptedResponse = new JSONObject(EncryptDecrypt.decrypt(response.get("enc_data").toString()));
                            // Check for error message
                            if (decryptedResponse.getJSONObject("status").getInt("code") == 200) {
                                JSONObject dataObject = decryptedResponse.getJSONObject("data");
                                // Extracting required data
                                JSONArray loanAmountArray = dataObject.getJSONArray("loan_amount");
                                JSONArray accountNumberArray = dataObject.getJSONArray("account_number");
                                JSONArray balanceArray = dataObject.getJSONArray("balance");
                                int statusCode = decryptedResponse.getJSONObject("status").getInt("code");

                                // Constructing JSON object
                                JSONObject loanData = new JSONObject();
                                loanData.put("loan_amount", loanAmountArray);
                                loanData.put("account_number", accountNumberArray);
                                loanData.put("balance", balanceArray);
                                loanData.put("status_code", statusCode);

                                callback.onLoanResult(String.valueOf(loanData));

                            } else if (decryptedResponse.getJSONObject("status").getInt("code") == 400) {
                                JSONObject dataObject = decryptedResponse.getJSONObject("data");
                                // Extracting required data
                                JSONArray accountNumberArray = dataObject.getJSONArray("account_number");
                                int statusCode = decryptedResponse.getJSONObject("status").getInt("code");

                                // Constructing JSON object
                                JSONObject loanData = new JSONObject();
                                loanData.put("account_number", accountNumberArray);
                                loanData.put("status_code", statusCode);

                                callback.onLoanResult(String.valueOf(loanData));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", "Bearer " + retrivedToken);
                return headers;
            }
        };

        queue.add(stringRequest);
        queue.getCache().clear();
    }


    private void recreateFragmentView(Bundle savedInstanceState) {
        // LayoutInflater를 가져옵니다.
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // 현재 프래그먼트의 레이아웃을 다시 생성합니다.
        View newView = onCreateView(inflater, (ViewGroup) getView(), savedInstanceState);

        // 기존 뷰를 제거하고 새로운 뷰를 추가합니다.
        ViewGroup rootView = (ViewGroup) getView();
        if (rootView != null) {
            rootView.removeAllViews();
            rootView.addView(newView);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_loan_n, container, false);

        // 대출 버튼에 대한 클릭 리스너 설정
        Button getDebtButton = rootView.findViewById(R.id.get_debt_button);


        getDebtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 대출 요청 처리
                requestLoan();

            }
        });

        getLoan(new LoanCallback() {
            @Override
            public void onLoanResult(String loanData) throws JSONException {
                Log.d("loanData", loanData);

                JSONObject jsonObject = new JSONObject(loanData);
                String statusCode = jsonObject.getString("status_code");
//                JSONArray balanceArray = jsonObject.getJSONArray("balance");
                Log.d("statusCode123", statusCode);

                rootView.removeAllViews();

                View loanYView = null;
                View loanNView = null;

                if (statusCode.equals("200")) {
                    // Show fragment_loan_y layout
                    loanYView = inflater.inflate(R.layout.fragment_loan_y, container, false);
                    JSONArray accountNumberArray = jsonObject.getJSONArray("account_number");
                    JSONArray loanAmountArray = jsonObject.getJSONArray("loan_amount");
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                    Spinner accountSpinner = loanYView.findViewById(R.id.account_list_spinner_y);
                    ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<String>());
                    accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    for (int i = 0; i < accountNumberArray.length(); i++) {
                        accountAdapter.add(accountNumberArray.getString(i));
                    }
                    accountSpinner.setAdapter(accountAdapter);

                    // Set loan amount to TextView
                    TextView debtBalanceTextView = loanYView.findViewById(R.id.debt_amount_text_view);
                    if (loanAmountArray.length() > 0) {
                        debtBalanceTextView.setText("대출 잔액: " + loanAmountArray.getInt(0));
                    }

                    rootView.addView(loanYView, params);

                } else if (statusCode.equals("400")){
                    // Show fragment_loan_n layout
                    loanNView = inflater.inflate(R.layout.fragment_loan_n, container, false);
                    JSONArray accountNumberArray = jsonObject.getJSONArray("account_number");
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                    Spinner accountSpinner = loanNView.findViewById(R.id.account_list_spinner_n);
                    ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<String>());
                    accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    for (int i = 0; i < accountNumberArray.length(); i++) {
                        accountAdapter.add(accountNumberArray.getString(i));
                    }
                    accountSpinner.setAdapter(accountAdapter);

                    rootView.addView(loanNView, params);
                }
            }
        });

        return rootView;
    }

    // 대출 요청 메서드
    private void requestLoan() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("jwt", Context.MODE_PRIVATE);
        final String retrivedToken = sharedPreferences.getString("accesstoken", null);
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        sharedPreferences = getActivity().getSharedPreferences("apiurl", Context.MODE_PRIVATE);
        final String url = sharedPreferences.getString("apiurl", null);
        String endpoint = "/api/loan/get_debt";
        String finalurl = url + endpoint;

        JSONObject requestBody = new JSONObject();
        try {
            // 여기서 필요한 요청 데이터를 추가하세요 (예: 사용자 이름, 대출 금액 등)
            requestBody.put("username", "사용자 이름");
            requestBody.put("loan_amount", "50000000");
            // 계좌 선택과 관련된 데이터를 추가할 수도 있습니다.
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, finalurl, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 대출 요청 성공 시 처리
                        try {
                            // 응답 처리 코드 추가
                            String message = response.getJSONObject("data").getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            // 대출 상태 업데이트 등 추가 작업 수행 가능
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 대출 요청 실패 시 처리
                        Toast.makeText(getActivity(), "대출 요청 실패", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + retrivedToken);
                return headers;
            }
        };

        queue.add(request);
    }
}