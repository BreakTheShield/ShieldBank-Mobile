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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import kotlin.Result;
import okhttp3.*;




public class LoanFragment extends Fragment {

    public interface LoanCallback {
        void onLoanResult(String isLoan);
    }


    public String get_loan="";

    public void getLoan(final LoanCallback callback) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("jwt", Context.MODE_PRIVATE);
        final String retrivedToken = sharedPreferences.getString("accesstoken", null);
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        sharedPreferences = getActivity().getSharedPreferences("apiurl", Context.MODE_PRIVATE);
        final String url = sharedPreferences.getString("apiurl", null);
        String endpoint = "/api/user/profile";
        String finalurl = url + endpoint;

        final JsonObjectRequest stringRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, finalurl, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject decryptedResponse = new JSONObject(EncryptDecrypt.decrypt(response.get("enc_data").toString()));

                            // Check for error message
                            if (decryptedResponse.getJSONObject("status").getInt("code") != 200) {
                                Toast.makeText(getActivity(), "Error: " + decryptedResponse.getJSONObject("data").getString("message"), Toast.LENGTH_SHORT).show();
                                return;
                                // This is buggy. Need to call Login activity again if incorrect credentials are given
                            }

                            JSONObject obj = decryptedResponse.getJSONObject("data");
                            String isLoan = obj.getString("is_loan");
                            callback.onLoanResult(isLoan);
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_loan_n, container, false);

        getLoan(new LoanCallback() {
            @Override
            public void onLoanResult(String isLoan) {
                Log.d("is_loan", isLoan);

                // Remove all views from rootView
                rootView.removeAllViews();

                if ("true".equals(isLoan)) {
                    // Show fragment_loan_y layout
                    View loanYView = inflater.inflate(R.layout.fragment_loan_y, container, false);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("jwt", Context.MODE_PRIVATE);
                    final String retrivedToken = sharedPreferences.getString("accesstoken", null);
                    final RequestQueue queue = Volley.newRequestQueue(getActivity());
                    sharedPreferences = getActivity().getSharedPreferences("apiurl", Context.MODE_PRIVATE);
                    final String url = sharedPreferences.getString("apiurl", null);
                    String endpoint = "/api/loan/loan";
                    String finalurl = url + endpoint;
                    final JsonObjectRequest stringRequest2 = new JsonObjectRequest(Request.Method.POST, finalurl,null,
                            new Response.Listener<JSONObject>()  {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {

                                        JSONObject decryptedResponse = new JSONObject(EncryptDecrypt.decrypt(response.get("enc_data").toString()));

                                        // Check for error message
                                        if(decryptedResponse.getJSONObject("status").getInt("code") != 200) {
                                            Toast.makeText(getActivity(), "Error: " + decryptedResponse.getJSONObject("data").getString("message"), Toast.LENGTH_SHORT).show();
                                            return;
                                            // This is buggy. Need to call Login activity again if incorrect credentials are given
                                        }

                                        JSONObject obj = decryptedResponse.getJSONObject("data");



                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Override
                        public Map getHeaders() throws AuthFailureError {
                            HashMap headers=new HashMap();
                            headers.put("Authorization","Bearer "+retrivedToken);
                            return headers;
                        }
                    };

                    rootView.addView(loanYView, params);
                } else {
                    // Show fragment_loan_n layout
                    View loanNView = inflater.inflate(R.layout.fragment_loan_n, container, false);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    rootView.addView(loanNView, params);
                }
            }
        });

        return rootView;
    }
}