package com.example.funmaps.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {
    private EditText editAccount;
    private EditText editPassWord;
    private static final String TAG = "RegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();
        setContentView(R.layout.activity_register);
        initView();
    }
    //隐藏标题栏
    public void hideTitleBar(){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏应用程序的标题栏，即当前activity的label
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏
    }

    private void initView(){
        Toolbar toolbar = findViewById(R.id.toolbar_register);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        editAccount = findViewById(R.id.register_username);
        editPassWord = findViewById(R.id.register_password);
        Button btnCancel = findViewById(R.id.register_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                //startActivity(intent);
                finish();
            }
        });
       Button btnInput = findViewById(R.id.register_input);
       btnInput.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (editAccount.getText().toString().equals("")||editPassWord.getText().toString().equals("")){
                   System.out.println(editAccount.getText().toString()+"  "+editPassWord.getText().toString());
                   AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                   dialog.setTitle("警告");
                   dialog.setMessage("输入的账号或密码为空，请重新输入！");
                   dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                       }
                   });
                   dialog.show();
               }else {
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                               OkHttpClient client = new OkHttpClient();
                               RequestBody requestBody = new FormBody.Builder()
                                       .add("user",editAccount.getText().toString())
                                       .add("password",editPassWord.getText().toString())
                                       .build();
                               final Request.Builder builder = new Request.Builder()
                                       .url("http://192.168.43.35:8080/FunMapWebService/register")
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
                                           if(responseData.equals("true")){
                                               Log.d(TAG, "onResponse: "+"注册成功");
                                               Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
//                                               intent.putExtra("extraUserName",editAccount.getText().toString());
//                                               intent.putExtra("extraPassWord",editPassWord.getText().toString());
                                               startActivity(intent);
                                               finish();//用于销毁当前活动
                                           }
                                           else{
                                               runOnUiThread(new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                                                       dialog.setTitle("警告");
                                                       dialog.setMessage("输入的账号已存在，请重新输入！");
                                                       dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                           @Override
                                                           public void onClick(DialogInterface dialog, int which) {
                                                               editAccount.setText("");
                                                               editPassWord.setText("");
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
           }
       });
    }

}