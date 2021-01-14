package com.example.funmaps.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.funmaps.bean.Account;
import com.example.funmaps.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AlterActivity extends AppCompatActivity {
    private String userName;
    private String passWord;
    private TextView textUserName;
    private EditText editPassword;
    private Button btnCancel;
    private Button btnInput;
    private Account account;
    private static final String TAG = "AlterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏应用程序的标题栏，即当前activity的label
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter);
        initData();
        initView();
    }
    public void initData(){
        Intent intent = getIntent();
        userName = intent.getStringExtra("extraUserName");
        passWord = intent.getStringExtra("extraPassWord");
        System.out.println("AlterActivity"+userName+passWord);
        account = new Account();

    }
    public void initView(){
        textUserName = findViewById(R.id.alter_username);
        textUserName.setText(userName);
        editPassword = findViewById(R.id.alter_password);
        btnCancel = findViewById(R.id.alter_cancel);
        btnInput = findViewById(R.id.alter_input);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("username",userName)
                                .add("password",editPassword.getText().toString())
                                .build();
                        final Request.Builder builder = new Request.Builder()
                                .url("http://192.168.43.35:8080/FunMapWebService/AlterAccountServlet")
                                .post(requestBody);
                        Request request = builder.build();
                        Call call  = client.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Log.d(TAG, "onFailure: "+e);
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                if(response.isSuccessful()){
                                    Log.d(TAG, "连接服务器成功 ");
                                    String responseData = response.body().string();
                                    Log.d(TAG, "onResponse: "+responseData);
                                    if (responseData.equals("true")){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder dialog = new AlertDialog.Builder(AlterActivity.this);
                                                dialog.setTitle("提示");
                                                dialog.setMessage("修改成功");
                                                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(AlterActivity.this,LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                                dialog.show();
                                            }
                                        });
                                    }else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder dialog = new AlertDialog.Builder(AlterActivity.this);
                                                dialog.setTitle("警告");
                                                dialog.setMessage("输入的密码为原密码！");
                                                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        editPassword.setText("");
                                                    }
                                                });
                                                dialog.show();
                                            }
                                        });
                                    }

                                }
                            }
                        });
                    }
                }).start();
            }
        });
    }
}