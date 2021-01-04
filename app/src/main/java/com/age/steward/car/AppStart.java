package com.age.steward.car;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import com.age.steward.car.ui.MainActivity;
import com.age.steward.car.utils.StatusBarUtil;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 应用程序启动界面类：显示欢迎界面并跳转到主界面
 */
public class AppStart extends Activity {
    @BindView(R.id.iv_activity_start_welcome)
    ImageView imageView;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_start, null);
        setContentView(view);
        ButterKnife.bind(this);
        getWidthAndHeight();
        loadWelcomeUrl(AppConfig.welcomeUrl(), imageView, view);
        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }


    // 加载欢迎url
    public void loadWelcomeUrl(final String url, final ImageView iv, View view) {

        //渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(5000);
        view.startAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                Picasso.get().load(url)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(iv);
            }

        });
    }


    /**
     * 跳转到...
     */
    private void redirectTo() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getWidthAndHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        AppConfig.getAppConfig().setDeviceWidth(dm.widthPixels);
        AppConfig.getAppConfig().setDeviceHeight(dm.heightPixels);
    }
}