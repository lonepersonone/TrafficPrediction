package com.example.funmaps.service;

import android.content.Intent;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OKHttp3 {
    private  OkHttpClient client;

    public Boolean login(String username,String password) throws Exception{
        client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("user", username)
                .add("password",password)
                .build();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/FunMapWebService/login")
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        System.out.println("OkHttp3：是否允许登录："+response.body().string());
        if (response.body().string().equals("true")){
            return true;
        }else {
            return false;
        }
    }
}
