package com.example.funmaps.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.funmaps.R;
import com.example.funmaps.service.MusicInterface;
import com.example.funmaps.service.MusicService;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicActivity extends AppCompatActivity {
    MyServiceConn conn;
    Intent intent;
    MusicInterface mi;
    //用于设置音乐播放器的播放进度
    private static SeekBar sb;
    private static TextView tv_progress;
    private static TextView tv_total;
    private int flag_pause = 0;
    private int current_song = 0;
    private int[] song_list = {R.raw.cmj_suonianjiexinghe,R.raw.cmj_yinhefuyue,R.raw.cmj_qianyuqianxun};
    private String[] song_name = {"CMJ-所念皆星河","CMJ-银河赴约","CMJ-千与千寻"};
    private int[] song_fengmian = {R.drawable.suonianjiexinghe_fengmian,R.drawable.yinhefuyue_fengmian,R.drawable.qianyuqianxun_fengmian};
    private CircleImageView circleImageView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏应用程序的标题栏，即当前activity的label
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏
        setContentView(R.layout.activity_music);
        circleImageView = findViewById(R.id.fengmian);
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (operatingAnim != null) {
            circleImageView.startAnimation(operatingAnim);//开启旋转
        }
        //circleImageView.clearAnimation();//停止旋转
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv_total = (TextView) findViewById(R.id.tv_total);

        //创建意图对象
        intent = new Intent(this, MusicService.class);

        //启动服务
        startService(intent);

        //创建服务连接对象
        conn = new MyServiceConn();

        //绑定服务
        bindService(intent, conn, BIND_AUTO_CREATE);

        //获得布局文件上的滑动条
        sb = (SeekBar) findViewById(R.id.sb);

        //为滑动条添加事件监听
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //当滑动条中的进度改变后,此方法被调用
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            //滑动条刚开始滑动,此方法被调用
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }


            //当滑动条停止滑动,此方法被调用
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //根据拖动的进度改变音乐播放进度
                int progress = seekBar.getProgress();

                //改变播放进度
                mi.seekTo(progress);
            }
        });
    }

    //创建消息处理器对象
    public static Handler handler = new Handler(){

        //在主线程中处理从子线程发送过来的消息
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {

            //获取从子线程发送过来的音乐播放的进度
            Bundle bundle = msg.getData();

            //歌曲的总时长(毫秒)
            int duration = bundle.getInt("duration");

            //歌曲的当前进度(毫秒)
            int currentPostition = bundle.getInt("currentPosition");

            //刷新滑块的进度
            sb.setMax(duration);
            sb.setProgress(currentPostition);

            //歌曲的总时长
            int minute = duration / 1000 / 60;
            int second = duration / 1000 % 60;

            String strMinute = null;
            String strSecond = null;

            //如果歌曲的时间中的分钟小于10
            if(minute < 10) {

                //在分钟的前面加一个0
                strMinute = "0" + minute;
            } else {

                strMinute = minute + "";
            }

            //如果歌曲的时间中的秒钟小于10
            if(second < 10)
            {
                //在秒钟前面加一个0
                strSecond = "0" + second;
            } else {

                strSecond = second + "";
            }

            tv_total.setText(strMinute + ":" + strSecond);

            //歌曲当前播放时长
            minute = currentPostition / 1000 / 60;
            second = currentPostition / 1000 % 60;

            //如果歌曲的时间中的分钟小于10
            if(minute < 10) {

                //在分钟的前面加一个0
                strMinute = "0" + minute;
            } else {

                strMinute = minute + "";
            }

            //如果歌曲的时间中的秒钟小于10
            if(second < 10) {

                //在秒钟前面加一个0
                strSecond = "0" + second;
            } else {

                strSecond = second + "";
            }

            tv_progress.setText(strMinute + ":" + strSecond);
        }
    };

//    //播放音乐按钮响应函数
//    public void play(View view) {
//
//        //播放音乐
//        mi.play();
//    }
//
//    //暂停播放音乐按钮响应函数
//    public void pausePlay(View view) {
//
//        //暂停播放音乐
//        mi.pausePlay();
//    }
//
//    //继续播放音乐按钮响应函数
//    public void continuePlay (View view) {
//
//        //继续播放音乐
//        mi.continuePlay();
//    }
//
//    //退出音乐播放按钮响应函数
//    public void exit(View view) {
//
//        //解绑服务
//        unbindService(conn);
//
//        //停止服务
//        stopService(intent);
//
//        //结束这个activity
//        finish();
//    }

    public void play(View view){
        mi.play(current_song);
    }

    public void pausePlay(View view){
        ImageView play_Or_Pause = (ImageView) findViewById(R.id.play_Or_Pause);
        this.flag_pause++;
        if(this.flag_pause%2==0){
            mi.pausePlay();
            play_Or_Pause.setImageResource(R.drawable.music_pause);
            
        }else{
            if(flag_pause==1)
                mi.play(song_list[current_song]);
            else
                mi.continuePlay();
            play_Or_Pause.setImageResource(R.drawable.music_play);
        }
    }

    public void previous_Song(View view){
        mi.stopPlay();
        if(current_song==0)
            current_song=2;
        else
            current_song-=1;
        TextView textview = (TextView) findViewById(R.id.music_name);
        textview.setText(song_name[current_song]);
        ImageView imageview = (ImageView) findViewById(R.id.fengmian);
        imageview.setImageResource(song_fengmian[current_song]);
        mi.play(song_list[current_song]);
    }

    public void next_Song(View view){
        mi.stopPlay();
        if(current_song==2)
            current_song=0;
        else
            current_song+=1;
        TextView textview = (TextView) findViewById(R.id.music_name);
        textview.setText(song_name[current_song]);
        ImageView imageview = (ImageView) findViewById(R.id.fengmian);
        imageview.setImageResource(song_fengmian[current_song]);
        mi.play(song_list[current_song]);
    }

    public void exit(View view){
        mi.stopPlay();
        finish();
    }

    //实现服务器连接接口
    class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获得中间人对象
            mi = (MusicInterface) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

    }
}