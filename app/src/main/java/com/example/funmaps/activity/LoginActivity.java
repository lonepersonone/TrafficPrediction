package com.example.funmaps.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.funmaps.bean.FavoriteAddress;
import com.example.funmaps.service.OKHttp3;
import com.example.funmaps.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private OKHttp3 okHttp3;
    private static final String TAG = "LoginActivity";
    private List<FavoriteAddress> favoriteAddressesList;
    private String favoriteInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initData();
        initView();
        if (Build.VERSION.SDK_INT>=23){
            initPermission();//动态申请需要的权限
        }

    }

    public void initData(){
        okHttp3 = new OKHttp3();
    }

    //定义权限申请数组
    String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS};

    private final int mRequestCode = 100;//权限请求码

    public void initPermission(){
        ActivityCompat.requestPermissions(this, permissions, mRequestCode);
    }

    //权限申请函数的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @SuppressLint("WrongViewCast")
    private void initView(){
        final EditText editUserName = findViewById(R.id.login_username);
        final EditText editPassWord = findViewById(R.id.login_password);
        ImageButton btnLogin = findViewById(R.id.login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(!editUserName.getText().toString().isEmpty()&&!editPassWord.getText().toString().isEmpty()){
                            OkHttpClient client = new OkHttpClient();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("user",editUserName.getText().toString())
                                    .add("password",editPassWord.getText().toString())
                                    .build();
                            final Request.Builder builder = new Request.Builder()
                                    .url("http://192.168.43.35:8080/FunMapWebService/login")
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
                                        Log.d(TAG, "onResponse:123 ");
                                        String responseData = response.body().string();
                                        Log.d(TAG, "onResponse: "+responseData);

                                        if(responseData.equals("true")){
                                            Log.d(TAG, "onResponse: "+"登录成功");
                                            //二次查询，查询获取到主活动中的主要数据
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    OkHttpClient client = new OkHttpClient();
                                                    RequestBody requestBody = new FormBody.Builder()
                                                            .add("username",editUserName.getText().toString())
                                                            .build();
                                                    final Request.Builder builder = new Request.Builder()
                                                            .url("http://192.168.43.35:8080/FunMapWebService/FavoriteAddressViewServlet")
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
                                                                Gson gson = new Gson();
                                                                Type userListType = new TypeToken<ArrayList<FavoriteAddress>>(){}.getType();
                                                                favoriteAddressesList = gson.fromJson(responseData, userListType);
                                                                if (favoriteAddressesList.size() == 0){
                                                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                                    intent.putExtra("extraUserName",editUserName.getText().toString());
                                                                    intent.putExtra("extraPassWord",editPassWord.getText().toString());
                                                                    intent.putExtra("favoriteInfo",responseData);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }else {
                                                                    FavoriteAddress address = favoriteAddressesList.get(0);
                                                                    System.out.println("第一个数据账号"+address.getUsername());
                                                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                                    intent.putExtra("extraUserName",editUserName.getText().toString());
                                                                    intent.putExtra("extraPassWord",editPassWord.getText().toString());
                                                                    intent.putExtra("favoriteInfo",responseData);
                                                                    startActivity(intent);
                                                                    finish();//用于销毁当前活动
                                                                }

                                                            }
                                                        }
                                                    });
                                                }
                                            }).start();
                                        }
                                        else{
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                                                    dialog.setTitle("警告");
                                                    dialog.setMessage("输入的账号或密码有误，请重新输入！");
                                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            editUserName.setText("");
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
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                                    dialog.setTitle("警告");
                                    dialog.setMessage("输入的账号或密码为空，请重新输入！");
                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    dialog.show();
                                }
                            });

                        }

                    }
                }).start();
            }
        });

        Button btnRegister = findViewById(R.id.register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

//    public String setFavoriteAddress(final String username){
//        final String flag;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                OkHttpClient client = new OkHttpClient();
//                RequestBody requestBody = new FormBody.Builder()
//                        .add("username",username)
//                        .build();
//                final Request.Builder builder = new Request.Builder()
//                        .url("http://10.0.2.2:8080/FunMapWebService/FavoriteAddressViewServlet")
//                        .post(requestBody);
//                Request request = builder.build();
//                Call call  = client.newCall(request);
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                        Log.d(TAG, "onFailure: "+e);
//                    }
//
//                    @Override
//                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                        if(response.isSuccessful()){
//                            Log.d(TAG, "连接服务器成功 ");
//                            String responseData = response.body().string();
//                            Log.d(TAG, "onResponse: "+responseData);
//                            Gson gson = new Gson();
//                            Type userListType = new TypeToken<ArrayList<FavoriteAddress>>(){}.getType();
//                            favoriteAddressesList = gson.fromJson(responseData, userListType);
//                            FavoriteAddress address = favoriteAddressesList.get(0);
//                            System.out.println("第一个数据账号"+address.getUsername());
//                        }
//                    }
//                });
//            }
//        }).start();
//        return null;
//    }

}