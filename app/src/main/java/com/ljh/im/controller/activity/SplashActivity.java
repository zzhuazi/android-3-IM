package com.ljh.im.controller.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hyphenate.chat.EMClient;
import com.ljh.im.R;
import com.ljh.im.model.Model;
import com.ljh.im.model.bean.UserInfo;

/**
 * 欢迎页面
 */
public class SplashActivity extends AppCompatActivity {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            //如果当前activity已经退出，那么就不处理handler中的消息，直接返回
            if (isFinishing()) {
                return;
            }
            //判断进入主页面还是登录页面
            toMainOrLogin();
        }
    };

    private void toMainOrLogin() {
//        new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }.run();
        //在全局线程池的控制下使用新线程
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //判断当前用户是否已经登录过
                if (EMClient.getInstance().isLoggedInBefore()) { //登录过
                    //获取到当前登录用户信息
                    UserInfo account = Model.getInstance().getUserAccountDao().getAccountByHxId(EMClient.getInstance().getCurrentUser());
                    if (account == null) {
                        //跳转到登录页面
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        //登录成功后的方法
                        Model.getInstance().loginSuccess(account);
                        //跳转主页面
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else { //没登陆过
                    //跳转到登录页面
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //发送2s延时消息
        handler.sendMessageDelayed(Message.obtain(), 2000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
