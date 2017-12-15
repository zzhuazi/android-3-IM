package com.ljh.im.controller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.ljh.im.R;
import com.ljh.im.model.Model;

public class NewGroupActivity extends AppCompatActivity {
    private EditText et_newgroup_name;
    private EditText et_newgroup_desc;
    private CheckBox cb_newgroup_public;
    private CheckBox cb_newgroup_invite;
    private Button bt_newgroup_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        
        initView();
        initListener();
    }

    private void initListener() {
        //创建按钮的点击事件处理
        bt_newgroup_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到选择联系人页面
                Intent intent = new Intent(NewGroupActivity.this, PickContactActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //成功获取联系人
        if (resultCode == RESULT_OK){
            //创建群组
            createGroup(data.getStringArrayExtra("members"));
        }
    }

    private void createGroup(final String[] members) {
        //群名称
        final String groupName = et_newgroup_name.getText().toString();
        //群描述
        final String groupDesc = et_newgroup_desc.getText().toString();
        //环信服务器创建群
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //参数1：群名称 参数2：群描述 参数3:群成员 参数4：创建群的原因 参数5：参数设置
                EMGroupOptions options = new EMGroupOptions();
                options.maxUsers = 200; //群最多容纳多少人
                EMGroupManager.EMGroupStyle groupStyle = null;
                if (cb_newgroup_public.isChecked()){ //公开
                    if (cb_newgroup_invite.isChecked()){//开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    }else{ //不开放群邀请
//                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    }
                }else { //不公开

                }
                options.style = groupStyle;
                try {
                    EMClient.getInstance().groupManager().createGroup(groupName, groupDesc, members, "申请加入群", options);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //初始化view
    private void initView() {
        et_newgroup_name = findViewById(R.id.et_newgroup_name);
        et_newgroup_desc = findViewById(R.id.et_newgroup_desc);
        cb_newgroup_public = findViewById(R.id.cb_newgroup_public);
        cb_newgroup_invite = findViewById(R.id.cb_newgroup_invite);
        bt_newgroup_create = findViewById(R.id.bt_newgroup_create);
    }
}
