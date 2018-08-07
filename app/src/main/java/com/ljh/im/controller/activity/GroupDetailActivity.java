package com.ljh.im.controller.activity;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.ljh.im.R;
import com.ljh.im.controller.adapter.GroupDetailAdapter;
import com.ljh.im.model.Model;
import com.ljh.im.model.bean.UserInfo;
import com.ljh.im.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 群详情页面
 */
public class GroupDetailActivity extends AppCompatActivity {
    private GridView gv_groupdetail;
    private Button bt_groupdetail_out;
    private EMGroup mGroup;
    private GroupDetailAdapter.OnGroupDetailListener mOnGroupDetailListener = new GroupDetailAdapter.OnGroupDetailListener() {
        //添加群成员
        @Override
        public void onAddMembers() {
            //跳转选择联系人页面
            Intent intent = new Intent(GroupDetailActivity.this, PickContactActivity.class);
            //传递群ID
            intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());
            startActivityForResult(intent, 2);
        }

        //删除群成员方法
        @Override
        public void onDeleteMember(final UserInfo userInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //从环信服务器中删除数据
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(), userInfo.getHxid());

                        //更新页面
                        getMembersFromHxServer();
                        
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };
    private List<UserInfo> mUsers;
    private GroupDetailAdapter groupDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        initView();
        getData();
        initData();
        initListener();
    }

    private void initListener() {
        gv_groupdetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        // 判断当前是否是删除模式,如果是删除模式
                        if(groupDetailAdapter.ismIsDeleteModel()) {
                            // 切换为非删除模式
                            groupDetailAdapter.setmIsDeleteModel(false);

                            // 刷新页面
                            groupDetailAdapter.notifyDataSetChanged();
                        }
                        break;
                }

                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            //获取返回的准备邀请的群成员信息
            final String[] members = data.getStringArrayExtra("members");
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //去环信服务器发送邀请信息
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(), members);
                        
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private void initData() {
        //初始化Button显示
        initButtonDisplay();
        
        //初始化GridView
        initGridView();

        //从环信服务器获取所有群成员
        getMembersFromHxServer();

    }

    //从环信服务器获取所有群成员
    private void getMembersFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMGroup emGroup = EMClient.getInstance().groupManager().getGroupFromServer(mGroup.getGroupId());
                    List<String> members = emGroup.getMembers();
                    if (members != null && members.size()>=0){
                        mUsers = new ArrayList<>();
                        //转换
                        for (String member : members){
                            UserInfo userInfo = new UserInfo(member);
                            mUsers.add(userInfo);
                        }
                    }
                    //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //刷新适配器
                            groupDetailAdapter.refresh(mUsers);
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this, "获取群信息失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initGridView() {
        //当前用户是群主 或者 群公开了
        boolean isCanModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner()) || mGroup.isPublic();
        groupDetailAdapter = new GroupDetailAdapter(this, isCanModify, mOnGroupDetailListener);
        gv_groupdetail.setAdapter(groupDetailAdapter);
    }

    private void initButtonDisplay() {
        //判断当前用户是否为群主
        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())){
            //群主的处理
            bt_groupdetail_out.setText("解散群");
            bt_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //去环信服务器解散群
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());

                                //发送群解散广播
                                exitGroupBroatCast();

                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群成功", Toast.LENGTH_SHORT).show();
                                        //结束当前页面
                                        finish();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }else {
            //群成员的处理
            bt_groupdetail_out.setText("退群");
            bt_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //告诉环信服务器退群
                                EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());

                                //发送退群广播
                                exitGroupBroatCast();

                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    //发送退群和解散群广播
    private void exitGroupBroatCast() {
        LocalBroadcastManager mLBM = LocalBroadcastManager.getInstance(GroupDetailActivity.this);
        Intent intent = new Intent(Constant.EXIT_GROUP);
        intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());
        mLBM.sendBroadcast(intent);
    }

    //获取传递过来的数据
    private void getData() {
        Intent intent = getIntent();
        String groupId = intent.getStringExtra(Constant.GROUP_ID);
        if (groupId == null){
            return;
        }else{
            mGroup = EMClient.getInstance().groupManager().getGroup(groupId);
        }
    }

    private void initView() {
        gv_groupdetail = findViewById(R.id.gv_groupdetail);
        bt_groupdetail_out = findViewById(R.id.bt_groupdetail_out);
    }
}
