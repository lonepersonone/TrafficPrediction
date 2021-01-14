package com.example.funmaps.activity;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.example.funmaps.R;
import com.example.funmaps.bean.FavoriteAddress;
import com.example.funmaps.bean.HotAddress;
import com.example.funmaps.bean.NaviAddress;
import com.example.funmaps.bean.TargetAddress;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Random random;
    Timer timer;
    TimerTask timerTask;
    private int TimeOut = 101;
    private int TimeInterval = 1500;
    private int TimeStart = 0;
    private LocationClient locationClient;
    private String userName = null;
    private String passWord = null;
    TextView textPersonLocation = null;
    TextView textPersonRate = null ;
    TextView textLocalLocation1 = null;
    TextView textLocalRate1 = null;
    TextView textLocalLocation2 = null;
    TextView textLocalRate2 = null;
    TextView textLocalLocation3 = null;
    TextView textLocalRate3 = null;
    Button btnPersonHeat = null;
    Button btnPersonSearch = null;
    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private List<FavoriteAddress> favoriteAddressesList;
    private TargetAddress targetAddress;
    private TargetAddress targetAddress1;
    private HotAddress hotAddress;
    private NaviAddress naviAddress;
    private static final String TAG = "MainActivity";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏应用程序的标题栏，即当前activity的label
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏
        setContentView(R.layout.activity_main);
        initLocation();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler(){
            public void handleMessage(@SuppressLint("Han·dlerLeak") Message msg) {
                setLocal();
                List<HotAddress> hotAddressList = new ArrayList<>();
                int s =random.nextInt(100);
                s =random.nextInt(10000)+5000;
                //textLocalRate.setText(s+"位");
                HotAddress ha1 = new HotAddress();
                ha1.setAddress("五一广场");
                ha1.setRate(s);
                hotAddressList.add(ha1);
                s = random.nextInt(10000)+5000;
                HotAddress ha2 = new HotAddress();
                ha2.setAddress("黄兴广场");
                ha2.setRate(s);
                hotAddressList.add(ha2);
                s = random.nextInt(10000)+5000;
                HotAddress ha3 = new HotAddress();
                ha3.setAddress("橘子洲");
                ha3.setRate(s);
                hotAddressList.add(ha3);
                s = random.nextInt(10000)+5000;
                HotAddress ha4 = new HotAddress();
                ha4.setAddress("岳麓山院");
                ha4.setRate(s);
                hotAddressList.add(ha4);
                s = random.nextInt(10000);
                HotAddress ha5 = new HotAddress();
                ha5.setAddress("省博物馆");
                ha5.setRate(s);
                hotAddressList.add(ha5);
                Collections.sort(hotAddressList);
                System.out.println(hotAddressList.toString());
                setSeniority(hotAddressList);
            }
        };
        initData(handler);
        navigationBarClickEvent();
    }
    public void setLocal(){
        textPersonLocation.setText(targetAddress.getAddr());
        int s =random.nextInt(100);
        s =random.nextInt(1000);
        textPersonRate.setText(s+"");
    }
    public void setSeniority(List<HotAddress> hotAddressList){
        textLocalLocation1.setText(hotAddressList.get(0).getAddress());
        textLocalRate1.setText(hotAddressList.get(0).getRate()+"");
        textLocalLocation2.setText(hotAddressList.get(1).getAddress());
        textLocalRate2.setText(hotAddressList.get(1).getRate()+"");
        textLocalLocation3.setText(hotAddressList.get(2).getAddress());
        textLocalRate3.setText(hotAddressList.get(2).getRate()+"");
    }
    public void initData(Handler handler){
        //获取账号密码
        Intent intent = getIntent();
        random = new Random();
        userName = intent.getStringExtra("extraUserName");
        passWord = intent.getStringExtra("extraPassWord");
        favoriteAddressesList = new ArrayList<>();
        targetAddress = new TargetAddress();
        targetAddress1 = new TargetAddress();
        targetAddress.setUsername(userName);
        targetAddress.setPassword(passWord);
        naviAddress = new NaviAddress();
        Gson gson = new Gson();
        Type userListType = new TypeToken<ArrayList<FavoriteAddress>>(){}.getType();
        favoriteAddressesList = gson.fromJson(intent.getStringExtra("favoriteInfo"), userListType);
        if (favoriteAddressesList.size() == 0){
            FavoriteAddress favoriteAddress = new FavoriteAddress();
            favoriteAddress.setAddr("你还没有收藏地址");
            favoriteAddress.setUsername(userName);
            favoriteAddress.setLatitude(1.00);
            favoriteAddress.setLongitude(1.00);
            favoriteAddressesList.add(favoriteAddress);
        }
        initView(handler);
        hotAddress = new HotAddress();
    }

    public  void initView(final Handler handler){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        textPersonLocation = findViewById(R.id.person_location);
        textPersonRate = findViewById(R.id.person_rate);
        btnPersonHeat = findViewById(R.id.person_heat);
        btnPersonSearch = findViewById(R.id.person_search);
        textLocalLocation1 = findViewById(R.id.local_location1);
        textLocalRate1 = findViewById(R.id.local_rate1);
        textLocalLocation2 = findViewById(R.id.local_location2);
        textLocalRate2 = findViewById(R.id.local_rate2);
        textLocalLocation3 = findViewById(R.id.local_location3);
        textLocalRate3 = findViewById(R.id.local_rate3);
        recyclerView =  findViewById(R.id.main_recylerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        favoriteAdapter = new FavoriteAdapter(favoriteAddressesList);
        recyclerView.setAdapter(favoriteAdapter);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        if(timer == null) timer = new Timer();
        if (timerTask == null) timerTask = new TimerTask() {
            @Override
            public void run() {
                Message ms = new Message();
                ms.what = TimeOut;
                handler.sendMessage(ms);
            }

        };
        if(timer !=null ) timer.cancel();
        if (timerTask != null) timerTask.cancel();

        timer.schedule(timerTask,TimeStart,TimeInterval);
        btnPersonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("Key","MainActivity");
                intent.putExtra("targetAddress1",targetAddress1);
                startActivity(intent);
            }
        });

        btnPersonHeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HeatMapActivity.class);
                intent.putExtra("targetAddress",targetAddress);
                startActivity(intent);
            }
        });

    }

    public void setFavoriteAddress(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("username",targetAddress.getUsername())
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
                            List<FavoriteAddress> favoriteAddressesLists;
                            favoriteAddressesLists = gson.fromJson(responseData, userListType);
                            //favoriteAdapter.reFresh(favoriteAddressesLists);
                        }
                    }
                });
            }
        }).start();

    }

    public void deleteFavoriteAddress(final String addr){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("addr",addr)
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
//                                                setFavoriteAddress();
                        }
                    }
                });
            }
        }).start();
        //此处对收藏的地址信息进行删除处理
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_accounts:
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.action_register:
                Intent intent2 = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent2);
                break;
            case R.id.action_settings:
                Intent intent1 = new Intent(MainActivity.this,AccountActivity.class);
                intent1.putExtra("extraUserName",userName);
                intent1.putExtra("extraPassWord",passWord);
                startActivity(intent1);
                break;
            case R.id.action_circum:
                Intent intent3 = new Intent(MainActivity.this,CircumActivity.class);
                startActivity(intent3);
                break;
            case R.id.action_music:
                Intent intent4 = new Intent(MainActivity.this,MusicActivity.class);
                startActivity(intent4);
                break;
            case R.id.action_navi:
                Intent intent5 = new Intent(MainActivity.this,BNaviMainActivity.class);
                naviAddress.setStartLocation(targetAddress.getAddr());
                naviAddress.setStartLatitude(targetAddress.getLatitude());
                naviAddress.setStartLongitude(targetAddress.getLongitude());
                naviAddress.setUsername(userName);
                intent5.putExtra("naviAddress",naviAddress);
                intent5.putExtra("returnKey","MainActivity");
                startActivity(intent5);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //抽屉桌面显示5
    public void navigationBarClickEvent(){
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.navigation_account:
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_setting:
                        Intent intent1 = new Intent(MainActivity.this,AccountActivity.class);
                        intent1.putExtra("extraUserName",userName);
                        intent1.putExtra("extraPassWord",passWord);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_register:
                        Intent intent2 = new Intent(MainActivity.this,RegisterActivity.class);
                        startActivity(intent2);
                        break;
                }

                return false;
            }
        });
    }


    //定位服务初始化
    public void initLocation(){
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        locationClient = new LocationClient(this.getApplicationContext());
        LocationClientOption locationOption = new LocationClientOption();//声明LocationClient类实例并配置定位参数
        MainActivity.MyLocationListener myLocationListener = new MainActivity.MyLocationListener();//注册监听函数
        locationClient.registerLocationListener(myLocationListener);
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        locationOption.setWifiCacheTimeOut(5*60*1000);
        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        //locationOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);
        //开始定位
        locationClient.start();
    }

    //位置时间监听
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null){
                locationClient.start();
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            targetAddress.setLatitude(latitude);
            targetAddress.setLongitude(longitude);
            targetAddress.setAddr(location.getAddrStr());
            System.out.println("MainActivity"+"详细地址"+location.getAddrStr()+"经度:"+targetAddress.getLatitude()+"纬度："+location.getLongitude());

        }
    }


    //内部类适配器
    public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>{
        private List<FavoriteAddress> addressList;
        FavoriteAdapter(){
        }
        @NonNull
        //绑定当前视图布局
        @Override
        public FavoriteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_favorite_list_item,parent,false);
            final FavoriteAdapter.MyViewHolder vh = new FavoriteAdapter.MyViewHolder(view);
            vh.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("提示框");
                    dialog.setMessage("是否删除？");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                    return false;
                }
            });
            vh.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                int position = vh.getAdapterPosition();
                FavoriteAddress favoriteAddress = addressList.get(position);
                TargetAddress targetAddress = new TargetAddress();
                targetAddress.setAddr(favoriteAddress.getAddr());
                targetAddress.setUsername(userName);
                targetAddress.setPassword(passWord);
                targetAddress.setLatitude(favoriteAddress.getLatitude());
                targetAddress.setLongitude(favoriteAddress.getLongitude());
                Intent intent = new Intent(MainActivity.this, HeatMapActivity.class);
                intent.putExtra("targetAddress",targetAddress);
                startActivity(intent);
                }
            });
            return vh;
        }

        public FavoriteAdapter(List<FavoriteAddress> addressList){
            this.addressList = addressList;
        }

        @Override
        public void onBindViewHolder(@NonNull FavoriteAdapter.MyViewHolder holder, int position) {
            FavoriteAddress address = addressList.get(position);
            holder.textOrder.setText("No."+position);
            holder.textLocation.setText(address.getAddr());
        }

        @Override
        public int getItemCount() {
            return addressList.size();
        }

        public  class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            View view;
            TextView textLocation;
            TextView textOrder;
            public MyViewHolder(View v)
            {
                super(v);
                view = v;
                textLocation = v.findViewById(R.id.main_favorite_location);
                textOrder = v.findViewById(R.id.main_favorite_order);
            }
        }

        public void reFresh(List<FavoriteAddress> favoriteAddressesList){
            addressList = favoriteAddressesList;
            notifyDataSetChanged();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setFavoriteAddress();
    }
}