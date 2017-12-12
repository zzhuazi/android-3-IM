package com.ljh.im.controller.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.ljh.im.R;
import com.ljh.im.model.Model;

/**
 * 登录界面
 */
public class LoginActivity extends AppCompatActivity {
    private EditText et_login_name;
    private EditText et_login_pwd;
    private Button bt_login_register;
    private Button bt_login_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //初始化控件
        initView();

        //初始化监听
        initListener();
    }

    private void initListener() {
        //注册按钮的点击事件处理
        bt_login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        //登录按钮的点击事件处理
        bt_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    //登录按钮的业务逻辑处理
    private void login() {
    }

    //注册按钮的业务逻辑处理
    private void register() {
        //1、获取输入的用户名和密码
        final String registerName = et_login_name.getText().toString();
        final String registerPwd = et_login_pwd.getText().toString();

        //2、校验输入的用户名和密码
        if (TextUtils.isEmpty(registerName) || TextUtils.isEmpty(registerPwd)){
            Toast.makeText(this, "输入的用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //3、去服务器注册账号
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //去环信服务器注册账号
                    EMClient.getInstance().createAccount(registerName,registerPwd);

                    //更新页面显示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        et_login_name = findViewById(R.id.et_login_name);
        et_login_pwd = findViewById(R.id.et_login_pwd);
        bt_login_login = findViewById(R.id.bt_login_login);
        bt_login_register = findViewById(R.id.bt_login_register);

    }
}
