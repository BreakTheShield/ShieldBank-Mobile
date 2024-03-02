package com.app.damnvulnerablebank;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class BankMainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private AccountListFragment accountListFragment = new AccountListFragment();
    private TransactionFragment transactionFragment = new TransactionFragment();
    private HomeFragment homeFragment = new HomeFragment();
    private LoanFragment loanFragment = new LoanFragment();
    private MydataFragment mydataFragment = new MydataFragment();


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        System.exit(0);
                    }
                }).create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.tabs_layout, homeFragment).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_bar);
        bottomNavigationView.getMenu().findItem(R.id.tab_home).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());


        if(RootUtil.isDeviceRooted()) {
            Toast.makeText(getApplicationContext(), "Phone is Rooted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }



    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (menuItem.getItemId()) {
                case R.id.tab_account_list:
                    transaction.replace(R.id.tabs_layout, accountListFragment).commitAllowingStateLoss();
                    break;
                case R.id.tab_transaction:
                    transaction.replace(R.id.tabs_layout, transactionFragment).commitAllowingStateLoss();
                    break;
                case R.id.tab_home:
                    transaction.replace(R.id.tabs_layout, homeFragment).commitAllowingStateLoss();
                    break;
                case R.id.tab_loan:
                    transaction.replace(R.id.tabs_layout, loanFragment).commitAllowingStateLoss();
                    break;
                case R.id.tab_mypage:
                    transaction.replace(R.id.tabs_layout, mydataFragment).commitAllowingStateLoss();
                    break;
            }

            return true;
        }
    }


}