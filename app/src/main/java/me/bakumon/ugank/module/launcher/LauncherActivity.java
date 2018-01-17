package me.bakumon.ugank.module.launcher;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import me.bakumon.ugank.R;
import me.bakumon.ugank.databinding.ActivityLauncherBinding;
import me.bakumon.ugank.module.home.HomeActivity;

/**
 * 启动页
 *
 * @author bakumon https://bakumon.me
 * @date 2016/12/8
 */
public class LauncherActivity extends AppCompatActivity implements LauncherContract.View {

    private ActivityLauncherBinding binding;

    /**
     * 记录该 Activity 是否在前台显示
     */
    private boolean isResume;

    private LauncherContract.Presenter mLauncherPresenter = new LauncherPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_launcher);

        mLauncherPresenter.subscribe();
    }

    @Override
    public void loadImg(String url) {
        try {
            Picasso.with(this)
                    .load(url)
                    .into(binding.imgLauncherWelcome, new Callback() {
                        @Override
                        public void onSuccess() {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isResume) {
                                        finish();
                                        return;
                                    }
                                    goHomeActivity();
                                }
                            }, 1200);
                        }

                        @Override
                        public void onError() {
                            goHomeActivity();
                        }
                    });
        } catch (Exception e) {
            goHomeActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    public void goHomeActivity() {
        Intent intent = new Intent(LauncherActivity.this, HomeActivity.class);
        startActivity(intent);
        // Activity 切换淡入淡出动画
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        // 禁掉返回键
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLauncherPresenter.unsubscribe();
    }
}
