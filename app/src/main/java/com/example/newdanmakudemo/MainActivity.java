package com.example.newdanmakudemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;


public class MainActivity extends AppCompatActivity {
    private OrientationUtils orientationUtils;
    private boolean isPlay,isPause;
    private DanmakuVideoPlayer videoPlayerTest;
    private GSYVideoOptionBuilder gsyVideoOptionBuilder;






    private boolean showDanmaku;

    private DanmakuView danmakuView;

    private DanmakuContext danmakuContext;

    private String url1 = "rtmp://202.69.69.180:443/webcast/bshdlive-pc";
    private String url6 = "rtmp://125.46.13.246:19350/oflaDemo/test";
    private String url2 = "https://ali-v4d.xiaoying.tv/20181027/zfll0y/3zlmkoU574.mp4";
    private String url4= "file://"+Environment.getExternalStorageDirectory().getPath()+"/[JYFanSub][Boku Dake ga Inai Machi][01][GB][720P].mp4";


    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        /*弹幕开始*/
        danmakuView = (DanmakuView) findViewById(R.id.danmaku_view);
        danmakuView.enableDanmakuDrawingCache(true);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                showDanmaku = true;
                danmakuView.start();
                generateSomeDanmaku();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        danmakuContext = DanmakuContext.create();
        danmakuView.prepare(parser, danmakuContext);



        final LinearLayout operationLayout = (LinearLayout) findViewById(R.id.operation_layout);
        final Button send = (Button) findViewById(R.id.send);
        final EditText editText = (EditText) findViewById(R.id.edit_text);
        danmakuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (operationLayout.getVisibility() == View.GONE) {
                    operationLayout.setVisibility(View.VISIBLE);
                } else {
                    operationLayout.setVisibility(View.GONE);
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editText.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    addDanmaku(content, true);
                    editText.setText("");
                }
            }
        });
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
                    onWindowFocusChanged(true);
                }
            }
        });
        /*弹幕结束*/

    }

    private void resolveNormalVideoUI() {
//增加title
        View view = (View) videoPlayerTest.getParent();
        SeekBar mPrgress = view.findViewById(R.id.progress);
        ProgressBar mBottomPrgress = view.findViewById(R.id.bottom_progressbar);
        TextView mTotalTimeTextView = view.findViewById(R.id.total);
        mTotalTimeTextView.setVisibility(View.INVISIBLE);
        mPrgress.setVisibility(View.INVISIBLE);
        videoPlayerTest.getTitleTextView().setVisibility(View.GONE);
        videoPlayerTest.getBackButton().setVisibility(View.GONE);
        videoPlayerTest.setBottomProgressBarDrawable(getResources().getDrawable(R.color.black));




    }
    private void initView() {
        videoPlayerTest = (DanmakuVideoPlayer) findViewById(R.id.video_player);
        resolveNormalVideoUI();
        orientationUtils = new OrientationUtils(this, videoPlayerTest);
        gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
//        videoPlayerTest.setPath
        gsyVideoOptionBuilder
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setCacheWithPlay(false)
                .setVideoTitle("title")
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
//开始播放了才能旋转和全屏
                        orientationUtils.setEnable(true);
                        isPlay = true;
                    }
                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                })
                .setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        if (orientationUtils != null) {
// 配合下方的onConfigurationChanged
                            orientationUtils.setEnable(!lock);
                        }
                    }
                });

       if(ContextCompat.checkSelfPermission(
               MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
               != PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(MainActivity.this,
                   new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
       }else {
           Cursor cursor =getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                   new String[]{MediaStore.Video.Media._ID}, null, null, null);
           gsyVideoOptionBuilder.setUrl(url4);
       }


        gsyVideoOptionBuilder.build(videoPlayerTest);
        videoPlayerTest.startPlayLogic();
        videoPlayerTest.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//直接横屏
                orientationUtils.resolveByClick();
//第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                videoPlayerTest.startWindowFullscreen(MainActivity.this, true, true);
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//如果旋转了就全屏
        if (isPlay && !isPause) {
            videoPlayerTest.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
// 当前为横屏
            StatusBarUtil.hideFakeStatusBarView(MainActivity.this);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
// 当前为竖屏
            setStatusBar();
        }
    }
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary),60);
    }



    /**
     * 向弹幕View中添加一条弹幕
     * @param content
     *          弹幕的具体内容
     * @param  withBorder
     *          弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = sp2px(20);
        danmaku.textColor = Color.WHITE;
        danmaku.setTime(danmakuView.getCurrentTime());
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        danmakuView.addDanmaku(danmaku);
    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(showDanmaku) {
                    int time = new Random().nextInt(300);
                    String content = "" + time + time;
                    addDanmaku(content, false);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showDanmaku = false;
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    gsyVideoOptionBuilder.setUrl(url4);
                }else {
                    Toast.makeText(MainActivity.this,"申请失败",Toast.LENGTH_SHORT).show();
                }
                break;
            default:

        }
    }
}
