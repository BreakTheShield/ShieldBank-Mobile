package com.app.damnvulnerablebank;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MydataAccountAdapter extends RecyclerView.Adapter<MydataAccountAdapter.ViewHolder> {


    ArrayList<BankAccount> MyBankAccounts;
    Activity context;

    public MydataAccountAdapter(ArrayList<BankAccount> myData, Activity activity) {
        this.MyBankAccounts = myData;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.mydata_account_cardview,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < MyBankAccounts.size()) {
            final BankAccount account = MyBankAccounts.get(position);
            holder.textviewmoney.setText(String.valueOf(account.getBalance()));
            holder.textviewbankno.setText(String.valueOf(account.getAccount_number()));
            holder.textviewcode.setText(String.valueOf(account.getBank_code()));
        }
    }

    @Override
    public int getItemCount() {
        return MyBankAccounts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textviewbankno;
        TextView textviewmoney;
        TextView textviewcode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textviewbankno = itemView.findViewById(R.id.text_view_bank_account_no);
            textviewmoney = itemView.findViewById(R.id.text_view_bank_account_money);
            textviewcode = itemView.findViewById(R.id.text_view_bank_code);
        }
    }
}
