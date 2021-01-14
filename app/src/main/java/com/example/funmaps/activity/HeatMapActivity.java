package com.example.funmaps.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Gradient;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.WeightedLatLng;
import com.baidu.mapapi.model.LatLng;
import com.example.funmaps.R;
import com.example.funmaps.bean.TargetAddress;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HeatMapActivity extends AppCompatActivity {
    private TextureMapView mapView = null;
    private BaiduMap mBaiduMap;
    private float zoom ;
    private Boolean isFirstReposition = true;//首次确定到自己的位置
    List<LatLng> randomList;
    List<WeightedLatLng> randomWeightList;
    private static final String TAG = "HeatMapActivity";
    private TextView textLocation = null;
    private TextView textRate = null;
    private TextView textRate1 = null;
    private TextView textRate2 = null;
    private TextView textRate3 = null;
    private TextView textZoom = null;
    private TextView textWeather = null;
    private TargetAddress targetAddress;
    private MapStatus.Builder builder;
    Timer timer;
    TimerTask timerTask;
    private int TimeOut = 101;
    private int TimeInterval = 1500;
    private int TimeStart = 0;
    private Random random;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏应用程序的标题栏，即当前activity的label
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏
        setContentView(R.layout.activity_heat_map);
        initData();
        initView();
        firstReposition();
    }

    public void initData(){
        targetAddress = new TargetAddress();
        targetAddress = (TargetAddress) getIntent().getSerializableExtra("targetAddress");//获取序列化传进来的参数
        System.out.println("精度："+targetAddress.getLatitude()+"纬度："+targetAddress.getLongitude());
        random = new Random();
    }

    public void initView(){
        Toolbar toolbar = findViewById(R.id.toolbar_heatMap);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        textLocation = findViewById(R.id.heat_location);
        textRate = findViewById(R.id.heat_rate);
        textRate1 = findViewById(R.id.text_forecast_num30);
        textRate2 = findViewById(R.id.text_forecast_num60);
        textRate3 = findViewById(R.id.text_forecast_num90);
        textZoom = findViewById(R.id.heat_zoom);
        textWeather = findViewById(R.id.heat_weather);
        textWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeatMapActivity.this,WeatherSearchActivity.class);
                intent.putExtra("targetAddress",targetAddress);
                startActivity(intent);
            }
        });
        Button btnCancel = findViewById(R.id.heat_cancel);
        Button btnCollect = findViewById(R.id.heat_collect);
        mapView = findViewById(R.id.bMapView);
        mBaiduMap = mapView.getMap();
        mBaiduMap.setBaiduHeatMapEnabled(true); //开启热力图
        mBaiduMap.setMyLocationEnabled(true);//开启定位图层
        //定义缩放级别
        builder = new MapStatus.Builder();
        builder.zoom(18.0f);
        textZoom.setText("18.0f");
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        textLocation.setText(targetAddress.getAddr());
        zoom = mBaiduMap.getMapStatus().zoom;
        BaiduMap.OnMapStatusChangeListener onMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                //获取地图缩放级别
                zoom = mBaiduMap.getMapStatus().zoom;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textZoom.setText(zoom+"");//确定当前缩放级别
                    }
                });
                //根据获取到的地图中心点(图标地点)坐标获取地址
                if (zoom>=21.0f||zoom<=11.0f){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(HeatMapActivity.this);
                    dialog.setTitle("警告");
                    dialog.setMessage("地图缩放级别不在热力图范围"+"\n"+"热力图缩放级别为11.0f-21.0f"+"\n"+"是否重置标准缩放级别？");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            builder.zoom(18.0f);
                            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                            isFirstReposition = true;
                            firstReposition();
                        }
                    });
                    dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                }
            }
        };
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBaiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
        btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("username",targetAddress.getUsername())
                                .add("addr",targetAddress.getAddr())
                                .add("latitude", String.valueOf(targetAddress.getLatitude()))
                                .add("longitude", String.valueOf(targetAddress.getLongitude()))
                                .build();
                        System.out.println("HeatMapActivity:"+"精度："+String.valueOf(targetAddress.getLatitude())+"纬度："+targetAddress.getLongitude());
                        final Request.Builder builder = new Request.Builder()
                                .url("http://192.168.43.35:8080/FunMapWebService/collect")
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
                                        Log.d(TAG, "onResponse: "+"收藏成功");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder dialog = new AlertDialog.Builder(HeatMapActivity.this);
                                                dialog.setTitle("提示");
                                                dialog.setMessage("地址收藏成功！");
                                                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                });
                                                dialog.show();
                                            }
                                        });
                                    }
                                    else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder dialog = new AlertDialog.Builder(HeatMapActivity.this);
                                                dialog.setTitle("警告");
                                                dialog.setMessage("改地点已被收藏！是否取消收藏该地点？");
                                                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                OkHttpClient client = new OkHttpClient();
                                                                RequestBody requestBody = new FormBody.Builder()
                                                                        .add("addr",targetAddress.getAddr())
                                                                        .build();
                                                                final Request.Builder builder = new Request.Builder()
                                                                        .url("http://192.168.43.35:8080/FunMapWebService/DeleteFavoriteServlet")
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
                                                                            String responseData1 = response.body().string();
                                                                            Log.d(TAG, "onResponse: "+responseData1);

                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }).start();
                                                        //此处对收藏的地址信息进行删除处理
                                                    }
                                                });
                                                dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

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
        Button btnForecast30 = findViewById(R.id.btn_forecast_num30);
        btnForecast30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heatMap();
            }
        });
        if(timer == null) timer = new Timer();
        if (timerTask == null) timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int s= random.nextInt(2000)+200;
                        textRate.setText(s+"");
                        s = random.nextInt(2000)+300;
                        textRate1.setText(s+"");
                        s = random.nextInt(2000)+400;
                        textRate2.setText(s+"");
                        s = random.nextInt(2000)+500;
                        textRate3.setText(s+"");
                    }
                });
            }
        };
        timer.schedule(timerTask,TimeStart,TimeInterval);
    }

    //首次定位
    public void firstReposition(){
        if(isFirstReposition){
            LatLng ll = new LatLng(targetAddress.getLatitude(),targetAddress.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(update);
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_mark1);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(ll)
                    .icon(bitmap);
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
        }
        isFirstReposition = false;
    }

//    class MyMapStatuChangeListen implements BaiduMap.OnMapStatusChangeListener {
//
//        @Override
//        public void onMapStatusChangeStart(MapStatus mapStatus) {
//
//        }
//
//        @Override
//        public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
//
//        }
//
//        @Override
//        public void onMapStatusChange(MapStatus mapStatus) {
//
//        }
//
//        @Override
//        public void onMapStatusChangeFinish(MapStatus mapStatus) {
//            //获取地图缩放级别
//            zoom = mBaiduMap.getMapStatus().zoom;
//            //根据获取到的地图中心点(图标地点)坐标获取地址
//            if (zoom>=21.0f||zoom<=11.0f){
//                AlertDialog.Builder dialog = new AlertDialog.Builder(HeatMapActivity.this);
//                dialog.setTitle("警告");
//                dialog.setMessage("地图缩放级别不在热力图范围"+"\n"+"热力图缩放级别为11.0f-21.0f"+"\n"+"是否重置标准缩放级别？");
//                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//                dialog.show();
//            }
//        }
//    }
    //自定义热力图
    public void heatMap(){
        //设置渐变颜色值
        int[] DEFAULT_GRADIENT_COLORS = {Color.rgb(102, 225, 0), Color.rgb(255, 0, 0)};
        //设置渐变颜色起始值
        float[] DEFAULT_GRADIENT_START_POINTS = {0.2f, 1f};
        //构造颜色渐变对象
        Gradient gradient = new Gradient(DEFAULT_GRADIENT_COLORS, DEFAULT_GRADIENT_START_POINTS);
        //以下数据为随机生成地理位置点，开发者根据自己的实际业务，传入自有位置数据即可
        randomList = new ArrayList<>();
        randomWeightList = new ArrayList<>();
        Random r = new Random();

        for (int i = 0; i < 1000; i++) {
            // 116.220000,39.780000 116.570000,40.150000
            int rlat = r.nextInt(370000);
            int rlng = r.nextInt(370000);
            double lat = targetAddress.getLatitude() + rlat/1E6;
            double lng = targetAddress.getLongitude() + rlng/1E6;
            int weight = i*10000;
            LatLng ll = new LatLng(lat,lng);
            System.out.println("精度："+lat+"纬度："+lng);
            WeightedLatLng ls = new WeightedLatLng(ll,weight);
            //randomList.add(ll);
            randomWeightList.add(ls);
        }

        //构造HeatMap
        //在大量热力图数据情况下，build过程相对较慢，建议放在新建线程实现
        HeatMap mCustomHeatMap = new HeatMap.Builder()
                .weightedData(randomWeightList)
                .gradient(gradient)
                .build();
        //在地图上添加自定义热力图
        mBaiduMap.addHeatMap(mCustomHeatMap);

    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}