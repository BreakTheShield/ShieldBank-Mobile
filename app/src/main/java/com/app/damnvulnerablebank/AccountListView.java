package com.app.damnvulnerablebank;

public class AccountListView {
    private String account_number;
    private String bank_code;
    private String username;
    private String balance;

    public AccountListView() {}

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = "계좌번호:\n"+account_number+"\n";
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = "잔액:\n"+balance+"\n";
    }

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = "은행코드:\n"+bank_code+"\n";
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = "이름:\n"+username+"\n";
    }
}
