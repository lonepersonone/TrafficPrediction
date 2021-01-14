package com.example.funmaps.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import com.example.funmaps.activity.MusicActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    private MediaPlayer player;
    private Timer timer;
    //绑定服务时,调用此方法
    //@Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new MusicControl();
    }

    //创建播放音乐的服务
    @Override
    public void onCreate() {
        super.onCreate();

        //创建音乐播放器对象
        player = new MediaPlayer();
    }

    //销毁播放音乐服务
    @Override
    public void onDestroy() {
        super.onDestroy();

        //停止播放音乐
        player.stop();

        //释放占用的资源
        player.release();

        //将player置为空
        player = null;
    }

    //播放音乐
    public void play(int cmj) {
        if(player == null)
        {
            player = new MediaPlayer();
        }
        //player.setDataSource("/music01/cmj.mp3");
        player =MediaPlayer.create(this,cmj);
        //重置
        //player.reset();

        //加载多媒体文件

        //准备播放音乐
        //player.prepare();

        //播放音乐
        player.start();

        //添加计时器
        addTimer();
    }

    //暂停播放音乐
    public void pausePlay() {

        player.pause();
    }

    //继续播放音乐
    public void continuePlay() {

        player.start();
    }

    //创建一个实现音乐接口的音乐控制类
    class MusicControl extends Binder implements MusicInterface {

        @Override
        public void play(int cmj) {

            MusicService.this.play(cmj);
        }

        @Override
        public void pausePlay() {

            MusicService.this.pausePlay();
        }

        @Override
        public void continuePlay() {

            MusicService.this.continuePlay();
        }

        @Override
        public void seekTo(int progress) {

            MusicService.this.seekTo(progress);
        }

        @Override
        public void stopPlay() {

            MusicService.this.onDestroy();
        }

    }

    //设置音乐的播放位置
    public void seekTo(int progress) {

        player.seekTo(progress);
    }

    //添加计时器用于设置音乐播放器中的播放进度
    public void addTimer() {

        //如果没有创建计时器对象
        if(timer == null) {

            //创建计时器对象
            timer = new Timer();

            timer.schedule(new TimerTask() {

                               //执行计时任务
                               @Override
                               public void run() {

                                   //获得歌曲总时长
                                   int duration = player.getDuration();

                                   //获得歌曲的当前播放进度
                                   int currentPosition = player.getCurrentPosition();

                                   //创建消息对象
                                   Message msg = MusicActivity.handler.obtainMessage();

                                   //将音乐的播放进度封装至消息对象中
                                   Bundle bundle = new Bundle();
                                   bundle.putInt("duration", duration);
                                   bundle.putInt("currentPosition", currentPosition);
                                   msg.setData(bundle);

                                   //将消息发送到主线程的消息队列
                                   MusicActivity.handler.sendMessage(msg);
                               }
                           },

                    //开始计时任务后的5毫秒，第一次执行run方法，以后每500毫秒执行一次
                    5, 500);
        }
    }
}
