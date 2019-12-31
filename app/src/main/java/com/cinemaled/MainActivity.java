package com.cinemaled;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cinemaled.bean.MovieBean;
import com.cinemaled.utils.XmlParser;
import com.google.gson.Gson;
import com.maning.updatelibrary.InstallUtils;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final int VIEW_DISS = 1;
    RelativeLayout set_bar;
    TextView more, set;
    boolean isShow = true;//顶部导航是否显示
    CustomerVideoView videoView;
    int number = 2;
    public static final String APK_SAVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MNUpdateAPK/cinemaled.apk";
    private MyHandler handler = new MyHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==VIEW_DISS){
                if(isShow){
                    set_bar.setVisibility(View.GONE);
                    isShow=false;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        materialSynchronization();
    }

    private void init() {
        set_bar = findViewById(R.id.set_bar);
        set_bar.setClickable(false);
        set = findViewById(R.id.set);
        more = findViewById(R.id.more);

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreSet(MainActivity.this);
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListWindow(MainActivity.this);
            }
        });

        String uri = "android.resource://" + getPackageName() + "/" + R.raw.one;
        videoView = (CustomerVideoView) this.findViewById(R.id.videoView);
        //设置视频控制器
        MediaController mediaController = new MediaController(MainActivity.this);
        mediaController.setVisibility(View.GONE);//隐藏进度条
        videoView.setMediaController(mediaController);

        //播放完成回调
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);
        //videoView.setOnInfoListener(this);
        //设置视频路径
        videoView.setVideoURI(Uri.parse(uri));
        //开始播放视频
        videoView.start();
        handler.sendEmptyMessageDelayed(VIEW_DISS,3000);

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("fred", "按下");
                        if (isShow == false) {
                            set_bar.setVisibility(View.VISIBLE);
                            isShow = true;
                            handler.sendEmptyMessageDelayed(VIEW_DISS,5000);
                        } else {
                            set_bar.setVisibility(View.GONE);
                            isShow = false;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.e("fred", "移动");

                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("fred", "抬起");

                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        String uri = null;
        if (number == 1) {
            uri = "android.resource://" + getPackageName() + "/" + R.raw.one;
            number++;
        } else if (number == 2) {
            uri = "android.resource://" + getPackageName() + "/" + R.raw.two;
            number = 1;
        }

        videoView.setVideoURI(Uri.parse(uri));
        videoView.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        videoView.stopPlayback();
        return false;
    }

    @Override
    protected void onRestart() {
        init();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        videoView.pause();
        super.onPause();
    }

    static class MyHandler extends Handler {
        WeakReference<Activity> mActivityReference;

        MyHandler(Activity activity) {
            mActivityReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }

    //列表弹窗
    private void showListWindow(Activity activity) {
        materialSynchronization();
        OBDDialogP dialog = new OBDDialogP(activity);
        View v = View.inflate(activity, R.layout.layout_list_window, null);

        dialog.setContentView(v);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.RIGHT;
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        dialog.show();
    }

    //设置弹窗
    private void showMoreSet(Activity activity) {
        OBDDialogP dialog = new OBDDialogP(activity);
        View v = View.inflate(activity, R.layout.layout_more_set_window, null);
        TextView customer_service=v.findViewById(R.id.customer_service);
        TextView help_document=v.findViewById(R.id.help_document);
        TextView version_number=v.findViewById(R.id.version_number);
        TextView version=v.findViewById(R.id.version);
        version.setText(getVersionName());

        dialog.setContentView(v);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.RIGHT;
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        dialog.show();
    }

    /**
     * 获取当前版本号
     * @return
     */
    private int getVersionCode() {
        // 包管理器 可以获取清单文件信息
        PackageManager packageManager = getPackageManager();
        try {
            // 获取包信息
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前版本名
     * @return
     */
    private String getVersionName(){
        String localVersion = "";
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(),0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }
    //下载新版本apk
    private void getNewApk(String url){
        InstallUtils.with(MainActivity.this)
                //必须-下载地址
                .setApkUrl(url)
                //非必须-下载保存的文件的完整路径+/name.apk，使用自定义路径需要获取读写权限
                .setApkPath(APK_SAVE_PATH)
                //非必须-下载回调
                .setCallBack(new InstallUtils.DownloadCallBack() {
                    @Override
                    public void onStart() {
                        //下载开始
                    }
                    @Override
                    public void onComplete(final String path) {
                        //下载完成
                        //先判断有没有安装权限---适配8.0
                        InstallUtils.checkInstallPermission(MainActivity.this, new InstallUtils.InstallPermissionCallBack() {
                            @Override
                            public void onGranted() {
                                //去安装APK
                                InstallUtils.installAPK(MainActivity.this, path, new InstallUtils.InstallCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        //onSuccess：表示系统的安装界面被打开
                                        //防止用户取消安装，在这里可以关闭当前应用，以免出现安装被取消
                                        Toast.makeText(MainActivity.this, "正在安装程序", Toast.LENGTH_SHORT).show();
                                        System.exit(0);
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        //安装出现异常，这里可以提示用用去用浏览器下载安装
                                    }
                                });
                            }

                            @Override
                            public void onDenied() {
                                //打开设置页面
                                InstallUtils.openInstallPermissionSetting(MainActivity.this, new InstallUtils.InstallPermissionCallBack() {
                                    @Override
                                    public void onGranted() {
                                        //安装APK
                                        InstallUtils.installAPK(MainActivity.this, path, new InstallUtils.InstallCallBack() {
                                            @Override
                                            public void onSuccess() {
                                                //onSuccess：表示系统的安装界面被打开
                                                //防止用户取消安装，在这里可以关闭当前应用，以免出现安装被取消
                                                Toast.makeText(MainActivity.this, "正在安装程序", Toast.LENGTH_SHORT).show();
                                                System.exit(0);
                                            }

                                            @Override
                                            public void onFail(Exception e) {
                                                //安装出现异常，这里可以提示用用去用浏览器下载安装
                                            }
                                        });
                                    }

                                    @Override
                                    public void onDenied() {
                                        System.exit(0);
                                        //还是不允许咋搞？
                                        //Toast.makeText(MainActivity.this, "不允许安装咋搞？强制更新就退出应用程序吧！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                    }

                    @Override
                    public void onLoading(long total, long current) {
                        //下载中

                    }

                    @Override
                    public void onFail(Exception e) {
                        //下载失败
                    }

                    @Override
                    public void cancle() {
                        //下载取消
                    }
                })
                //开始下载
                .startDownload();
    }

    /**
     * 素材同步
     */
    private void materialSynchronization(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url(ApiCotent.material_synchronization)
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象

                    if (response.isSuccessful()) {
                        String str=response.body().string();
                        Log.d("素材：","response.code()=="+str);
                        MovieBean jsonRootBean = new Gson().fromJson(str, MovieBean.class);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
