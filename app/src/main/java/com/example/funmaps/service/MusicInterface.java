package com.example.funmaps.service;

public interface MusicInterface {
    //播放音乐
    void play(int cmj);

    //暂停播放音乐
    void pausePlay();

    //继续播放音乐
    void continuePlay();

    //修改音乐的播放位置
    void seekTo(int progress);

    //停止播放此音乐
    void stopPlay();
}
