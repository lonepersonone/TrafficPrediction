package com.example.funmaps.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.baidu.mapapi.bikenavi.params.BikeRouteNodeInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo;
import com.example.funmaps.R;
import com.example.funmaps.bean.FavoriteAddress;
import com.example.funmaps.bean.NaviAddress;
import com.example.funmaps.bean.TargetAddress;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class BNaviMainActivity extends AppCompatActivity {
    private final static String TAG = BNaviMainActivity.class.getSimpleName();

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private TextView textStartLocation;
    private TextView textEndLocation;
    /*导航起终点Marker，可拖动改变起终点的坐标*/
    private Marker mStartMarker;
    private Marker mEndMarker;
    private LatLng startPt;
    private LatLng endPt;
    private BikeNaviLaunchParam bikeParam;
    private WalkNaviLaunchParam walkParam;
    private static boolean isPermissionRequested = false;
    private BitmapDescriptor bdStart = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_start);
    private BitmapDescriptor bdEnd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_end);
    private TargetAddress startAddress;
    private TargetAddress endAddress;
    private TargetAddress targetAddress;
    private TargetAddress targetAddress1;
    private NaviAddress naviAddress;
    private String Key;
    private String username;
    private List<NaviAddress> naviAddressList = null;
    private TextView textNaviHistory1 = null;
    private TextView textNaviHistory2 = null;
    private TextView textNaviHistory3 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏应用程序的标题栏，即当前activity的label
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏
        setContentView(R.layout.activity_nav);
        requestPermission();
        initData();
        mMapView = (MapView) findViewById(R.id.navi_mapview);
        initMapStatus();

        /*骑行导航入口*/
        Button bikeBtn = (Button) findViewById(R.id.btn_bikenavi);
        bikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBikeNavi();
            }
        });

        /*普通步行导航入口*/
        Button walkBtn = (Button) findViewById(R.id.btn_walknavi_normal);
        walkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walkParam.extraNaviMode(0);
                startWalkNavi();
            }
        });

        /*AR步行导航入口*/
        Button arWalkBtn = (Button) findViewById(R.id.btn_walknavi_ar);
        arWalkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walkParam.extraNaviMode(1);
                startWalkNavi();
            }
        });

        startPt = new LatLng(naviAddress.getStartLatitude(),naviAddress.getStartLongitude());
        endPt = new LatLng(naviAddress.getEndLatitude(), naviAddress.getEndLongitude());

        /*构造导航起终点参数对象*/
        BikeRouteNodeInfo bikeStartNode = new BikeRouteNodeInfo();
        bikeStartNode.setLocation(startPt);
        BikeRouteNodeInfo bikeEndNode = new BikeRouteNodeInfo();
        bikeEndNode.setLocation(endPt);
        bikeParam = new BikeNaviLaunchParam().startNodeInfo(bikeStartNode).endNodeInfo(bikeEndNode);

        WalkRouteNodeInfo walkStartNode = new WalkRouteNodeInfo();
        walkStartNode.setLocation(startPt);
        WalkRouteNodeInfo walkEndNode = new WalkRouteNodeInfo();
        walkEndNode.setLocation(endPt);
        walkParam = new WalkNaviLaunchParam().startNodeInfo(walkStartNode).endNodeInfo(walkEndNode);

        /* 初始化起终点Marker */
        initOverlay();
        textStartLocation = findViewById(R.id.navi_textStartLocation);
        textStartLocation.setText(naviAddress.getStartLocation());
        textStartLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BNaviMainActivity.this,SearchActivity.class);
                intent.putExtra("Key","BNaviMainActivityStart");
                endAddress.setAddr(naviAddress.getEndLocation());
                endAddress.setLatitude(naviAddress.getEndLatitude());
                endAddress.setLongitude(naviAddress.getEndLongitude());
                endAddress.setUsername(username);
                intent.putExtra("targetAddress1",endAddress);
                startActivity(intent);
                finish();
            }
        });
        textEndLocation = findViewById(R.id.navi_textEndLocation);
        textEndLocation.setText(naviAddress.getEndLocation());
        textEndLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BNaviMainActivity.this,SearchActivity.class);
                intent.putExtra("Key","BNaviMainActivityEnd");
                startAddress.setAddr(naviAddress.getStartLocation());
                startAddress.setLatitude(naviAddress.getStartLatitude());
                startAddress.setLongitude(naviAddress.getStartLongitude());
                startAddress.setUsername(username);
                intent.putExtra("targetAddress1",startAddress);
                startActivity(intent);
                finish();
            }
        });
        textNaviHistory1 = findViewById(R.id.navi_history1);
        textNaviHistory1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = naviAddressList.size();
                naviAddress = naviAddressList.get(length-1);
                textStartLocation.setText(naviAddress.getStartLocation());
                textEndLocation.setText(naviAddress.getEndLocation());
                
                startPt = new LatLng(naviAddress.getStartLatitude(),naviAddress.getStartLongitude());
                endPt = new LatLng(naviAddress.getEndLatitude(), naviAddress.getEndLongitude());
                /*构造导航起终点参数对象*/
                BikeRouteNodeInfo bikeStartNode = new BikeRouteNodeInfo();
                bikeStartNode.setLocation(startPt);
                BikeRouteNodeInfo bikeEndNode = new BikeRouteNodeInfo();
                bikeEndNode.setLocation(endPt);
                bikeParam = new BikeNaviLaunchParam().startNodeInfo(bikeStartNode).endNodeInfo(bikeEndNode);

                WalkRouteNodeInfo walkStartNode = new WalkRouteNodeInfo();
                walkStartNode.setLocation(startPt);
                WalkRouteNodeInfo walkEndNode = new WalkRouteNodeInfo();
                walkEndNode.setLocation(endPt);
                walkParam = new WalkNaviLaunchParam().startNodeInfo(walkStartNode).endNodeInfo(walkEndNode);

            }
        });

    }

    public void initData(){

        startAddress = new TargetAddress();
        endAddress = new TargetAddress();
        naviAddress = new NaviAddress();
        naviAddress = (NaviAddress) getIntent().getSerializableExtra("naviAddress");
        naviAddressList = new ArrayList<>();
        username = naviAddress.getUsername();
//        targetAddress = new TargetAddress();
//        targetAddress1 = new TargetAddress();
//        Key = getIntent().getStringExtra("returnKey");
//        targetAddress = (TargetAddress) getIntent().getSerializableExtra("targetAddress");//获取序列化传进来的参数
//        targetAddress1 = (TargetAddress) getIntent().getSerializableExtra("targetAddress1");//获取序列化传进来的参数
//        if (Key.equals("BNaviMainActivityStart")){
//            startAddress = targetAddress;
//            endAddress = targetAddress1;
//            System.out.println("startAddress"+startAddress.getAddr()+"endAddress"+endAddress.getAddr());
//        }else if (Key.equals("BNaviMainActivityEnd")){
//            endAddress = targetAddress;
//            startAddress = targetAddress1;
//            System.out.println("startAddress"+startAddress.getAddr()+"endAddress"+endAddress.getAddr());
//        }else if(Key.equals("MainActivity")){
//            startAddress = targetAddress;
//            endAddress = targetAddress1;
//            System.out.println("startAddress"+startAddress.getAddr()+"endAddress"+endAddress.getAddr());
//        }
        loadHistoryQuery();
    }

    /**
     * 初始化地图状态
     */
    private void initMapStatus(){
        mBaiduMap = mMapView.getMap();
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(new LatLng(40.048424, 116.313513)).zoom(15);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

    }

    /**
     * 初始化导航起终点Marker
     */
    public void initOverlay() {

        MarkerOptions ooA = new MarkerOptions().position(startPt).icon(bdStart)
                .zIndex(9).draggable(true);

        mStartMarker = (Marker) (mBaiduMap.addOverlay(ooA));
        mStartMarker.setDraggable(true);
        MarkerOptions ooB = new MarkerOptions().position(endPt).icon(bdEnd)
                .zIndex(5);
        mEndMarker = (Marker) (mBaiduMap.addOverlay(ooB));
        mEndMarker.setDraggable(true);

        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
            }

            public void onMarkerDragEnd(Marker marker) {
                if(marker == mStartMarker){
                    startPt = marker.getPosition();
                }else if(marker == mEndMarker){
                    endPt = marker.getPosition();
                }

                BikeRouteNodeInfo bikeStartNode = new BikeRouteNodeInfo();
                bikeStartNode.setLocation(startPt);
                BikeRouteNodeInfo bikeEndNode = new BikeRouteNodeInfo();
                bikeEndNode.setLocation(endPt);
                bikeParam = new BikeNaviLaunchParam().startNodeInfo(bikeStartNode).endNodeInfo(bikeEndNode);

                WalkRouteNodeInfo walkStartNode = new WalkRouteNodeInfo();
                walkStartNode.setLocation(startPt);
                WalkRouteNodeInfo walkEndNode = new WalkRouteNodeInfo();
                walkEndNode.setLocation(endPt);
                walkParam = new WalkNaviLaunchParam().startNodeInfo(walkStartNode).endNodeInfo(walkEndNode);

            }

            public void onMarkerDragStart(Marker marker) {
            }
        });
    }

    /**
     * 开始骑行导航
     */
    private void startBikeNavi() {
        Log.d(TAG, "startBikeNavi");
        try {
            BikeNavigateHelper.getInstance().initNaviEngine(this, new IBEngineInitListener() {
                @Override
                public void engineInitSuccess() {
                    Log.d(TAG, "BikeNavi engineInitSuccess");
                    routePlanWithBikeParam();
                }

                @Override
                public void engineInitFail() {
                    Log.d(TAG, "BikeNavi engineInitFail");
                    BikeNavigateHelper.getInstance().unInitNaviEngine();
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "startBikeNavi Exception");
            e.printStackTrace();
        }
    }

    /**
     * 开始步行导航
     */
    private void startWalkNavi() {
        Log.d(TAG, "startWalkNavi");
        try {
            WalkNavigateHelper.getInstance().initNaviEngine(this, new IWEngineInitListener() {
                @Override
                public void engineInitSuccess() {
                    Log.d(TAG, "WalkNavi engineInitSuccess");
                    routePlanWithWalkParam();
                }

                @Override
                public void engineInitFail() {
                    Log.d(TAG, "WalkNavi engineInitFail");
                    WalkNavigateHelper.getInstance().unInitNaviEngine();
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "startBikeNavi Exception");
            e.printStackTrace();
        }
    }

    /**
     * 发起骑行导航算路
     */
    private void routePlanWithBikeParam() {
        BikeNavigateHelper.getInstance().routePlanWithRouteNode(bikeParam, new IBRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d(TAG, "BikeNavi onRoutePlanStart");
            }
            @Override
            public void onRoutePlanSuccess() {
                Log.d(TAG, "BikeNavi onRoutePlanSuccess");
                saveHistoryQuery();
                Intent intent = new Intent();
                intent.setClass(BNaviMainActivity.this, BNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(BikeRoutePlanError error) {
                Log.d(TAG, "BikeNavi onRoutePlanFail");
            }

        });
    }

    /**
     * 发起步行导航算路
     */
    private void routePlanWithWalkParam() {
        WalkNavigateHelper.getInstance().routePlanWithRouteNode(walkParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d(TAG, "WalkNavi onRoutePlanStart");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.d(TAG, "onRoutePlanSuccess");
                saveHistoryQuery();
                Intent intent = new Intent();
                intent.setClass(BNaviMainActivity.this, WNaviGuideActivity.class);
                startActivity(intent);

            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError error) {
                Log.d(TAG, "WalkNavi onRoutePlanFail");
            }

        });
    }


    public void saveHistoryQuery(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String JsonString = gson.toJson(naviAddress);
                RequestBody requestBody = new FormBody.Builder()
                        .add("historyQuery",JsonString)
                        .build();
                final Request.Builder builder = new Request.Builder()
                        .url("http://192.168.43.35:8080/FunMapWebService/SaveHistoryQueryServlet")
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
//                            Gson gson = new Gson();
//                            Type userListType = new TypeToken<ArrayList<FavoriteAddress>>(){}.getType();
//                            favoriteAddressesList = gson.fromJson(responseData, userListType);
//                            FavoriteAddress address = favoriteAddressesList.get(0);
//                            System.out.println("第一个数据账号"+address.getUsername());
//                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                            intent.putExtra("extraUserName",editUserName.getText().toString());
//                            intent.putExtra("extraPassWord",editPassWord.getText().toString());
//                            intent.putExtra("favoriteInfo",responseData);
//                            startActivity(intent);
//                            finish();//用于销毁当前活动
                        }
                    }
                });
            }
        }).start();
    }

    public void loadHistoryQuery(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("username",username)
                        .build();
                final Request.Builder builder = new Request.Builder()
                        .url("http://192.168.43.35:8080/FunMapWebService/LoadHistoryQueryServlet")
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
                            Type userListType = new TypeToken<ArrayList<NaviAddress>>(){}.getType();
                            naviAddressList = gson.fromJson(responseData, userListType);
                            System.out.println(naviAddressList.toString());
                            final int length = naviAddressList.size();
                            if (length == 0){
                                naviAddressList.add(new NaviAddress());
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String start1 = deleteCharString4(naviAddressList.get(length-1).getStartLocation(),'-').substring(deleteCharString4(naviAddressList.get(length-1).getStartLocation(),'-').indexOf("区")+1,
                                                deleteCharString4(naviAddressList.get(length-1).getStartLocation(),'-').length()-1);
                                        String end1 = deleteCharString4(naviAddressList.get(length-1).getEndLocation(),'-').substring(deleteCharString4(naviAddressList.get(length-1).getEndLocation(),'-').indexOf("区")+1,
                                                deleteCharString4(naviAddressList.get(length-1).getEndLocation(),'-').length()-1);
                                        textNaviHistory1.setText(start1+"->"+end1);
                                    }
                                });
                            }

                        }
                    }
                });
            }
        }).start();
    }
    //用于删除指定的字符串
    public String deleteCharString4(String sourceString, char chElemData) {
        String deleteString = "";
        final String strTable = "|^$*+?.(){}\\";
        String tmpRegex = "[";
        for (int i = 0; i < strTable.length(); i++) {
            if (strTable.charAt(i) == chElemData) {
                tmpRegex += "//";
                break;
            }
        }
        tmpRegex += chElemData + "]";
        deleteString = sourceString.replaceAll(tmpRegex, "");
        return deleteString;
    }

    /**
     * Android6.0之后需要动态申请权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {

            isPermissionRequested = true;

            ArrayList<String> permissionsList = new ArrayList<>();

            String[] permissions = {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_MULTICAST_STATE


            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }

            if (permissionsList.isEmpty()) {
                return;
            } else {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 0);
            }
        }
    }

    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        bdStart.recycle();
        bdEnd.recycle();
    }

}