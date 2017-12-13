package com.ljh.im.controller.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.ljh.im.R;
import com.ljh.im.controller.fragment.ChatFragment;
import com.ljh.im.controller.fragment.ContactFragment;
import com.ljh.im.controller.fragment.SettingFragment;

public class MainActivity extends AppCompatActivity {
    private RadioGroup rg_main;
    private ChatFragment chatFragment;
    private ContactFragment contactFragment;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        //RadioGroup的选择事件
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Fragment fragment = null;
                switch (i){
                    //会话列表页面
                    case R.id.rb_main_chat:
                        fragment = chatFragment;
                        break;
                    //联系人列表页面
                    case R.id.rb_main_contact:
                        fragment = contactFragment;
                        break;
                    //设置页面
                    case R.id.rb_main_setting:
                        fragment = settingFragment;
                        break;
                }
                //实现fragment切换方法
                switchFragment(fragment);
            }
        });
        //默认选择会话列表fragment
        rg_main.check(R.id.rb_main_chat);
    }

    //实现fragment切换方法
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_main,fragment).commit();
    }

    private void initData() {
        chatFragment = new ChatFragment();
        contactFragment = new ContactFragment();
        settingFragment = new SettingFragment();
    }

    private void initView() {
        rg_main = findViewById(R.id.rg_main);
    }
}
