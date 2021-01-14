package com.example.funmaps.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.funmaps.R;

public class AccountActivity extends AppCompatActivity {
    private String userName;
    private String passWord;
    private TextView textUserName;
    private TextView textPassWord;
    private Button btnAlter;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();
        setContentView(R.layout.activity_account);
        initData();
        initView();
    }
    public void initData(){
        Intent intent = getIntent();
        userName = intent.getStringExtra("extraUserName");
        passWord = intent.getStringExtra("extraPassWord");
        System.out.println(userName+passWord);
    }
    public void initView(){
        textUserName = findViewById(R.id.account_username);
        textPassWord = findViewById(R.id.account_password);
        btnAlter = findViewById(R.id.account_alter);
        btnLogin = findViewById(R.id.account_login);
        textUserName.setText(userName);
        textPassWord.setText(passWord);
        btnAlter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this,AlterActivity.class);
                intent.putExtra("extraUserName",userName);
                intent.putExtra("extraPassWord",passWord);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    //隐藏标题栏
    public void hideTitleBar(){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏应用程序的标题栏，即当前activity的label
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏
    }
}