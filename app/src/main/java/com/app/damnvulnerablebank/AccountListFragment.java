package com.app.damnvulnerablebank;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AccountListFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_account_list, container, false);
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


        recyclerViewbankaccount.setHasFixedSize(true);
        recyclerViewbankaccount.setLayoutManager(new LinearLayoutManager(getActivity()));


        MyBankAccountAdapter myBankAccountAdapter = new MyBankAccountAdapter(myBankAccount,getActivity() );
        recyclerViewbankaccount.setAdapter(myBankAccountAdapter);
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

    public void click(){
        add_bank_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText editText = new EditText(getContext());
                editText.setHint("0");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                ad.setTitle("How Much Money Do You Want?");
                ad.setIcon(R.drawable.icon_save_money);
                ad.setView(editText);
                ad.setNegativeButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            myBankAccount.add(new BankAccount());
                            MyBankAccountAdapter myBankAccountAdapter = new MyBankAccountAdapter(myBankAccount,getActivity() );
                            recyclerViewbankaccount.setAdapter(myBankAccountAdapter);
                            setTotalMoney(myBankAccount);

                        }catch (NumberFormatException e){
                            myBankAccount.add(new BankAccount());
                            MyBankAccountAdapter myBankAccountAdapter = new MyBankAccountAdapter(myBankAccount,getActivity() );
                            recyclerViewbankaccount.setAdapter(myBankAccountAdapter);
                            setTotalMoney(myBankAccount);

                        }
                    }
                });
                ad.create().show();
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