package com.ljh.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljh.im.R;
import com.ljh.im.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/18.
 */

public class GroupDetailAdapter extends BaseAdapter {
    private Context mContext;
    private boolean mIsCanModify;//是否允许添加或删除好友
    private List<UserInfo> mUsers = new ArrayList<>();
    private boolean mIsDeleteModel; //删除模式 true表示可以删除 false不可删除
    private OnGroupDetailListener mOnGroupDetailListener;

    public GroupDetailAdapter(Context mContext, boolean isCanModify, OnGroupDetailListener mOnGroupDetailListener) {
        this.mContext = mContext;
        this.mIsCanModify = isCanModify;
        this.mOnGroupDetailListener = mOnGroupDetailListener;
    }

    //获取当前的删除模式
    public boolean ismIsDeleteModel() {
        return mIsDeleteModel;
    }

    //设置当前的删除模式
    public void setmIsDeleteModel(boolean mIsDeleteModel) {
        this.mIsDeleteModel = mIsDeleteModel;
    }

    //刷新数据
    public void refresh(List<UserInfo> users) {
        if (users != null && users.size() >= 0) {
            mUsers.clear();

            //添加加号或减号
            initUser();
            //添加数据
            mUsers.addAll(0, users);
        }
        notifyDataSetChanged();
    }

    private void initUser() {
        UserInfo add = new UserInfo("add");
        UserInfo delete = new UserInfo("delete");
        mUsers.add(delete);
        mUsers.add(0, add);
    }

    @Override
    public int getCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return mUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        //获取或创建viewholder
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_groupdetail, null);
            holder.photo = convertView.findViewById(R.id.iv_groupdetail_photo);
            holder.delete = convertView.findViewById(R.id.iv_groupdetail_delete);
            holder.name = convertView.findViewById(R.id.tv_groupdetail_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //获取当前item数据
        final UserInfo userInfo = mUsers.get(i);
        //显示数据
        if (mIsCanModify) { //群主或开放群权限
            //布局处理
            if (i == getCount() - 1) { //减号处理
                if (mIsDeleteModel) { //删除模式
                    convertView.setVisibility(View.INVISIBLE);
                } else {
                    convertView.setVisibility(View.VISIBLE);
                    holder.photo.setImageResource(R.drawable.em_smiley_minus_btn_pressed);
                    holder.delete.setVisibility(View.GONE);
                    holder.name.setVisibility(View.INVISIBLE);
                }
            } else if (i == getCount() - 2) {//加号处理
                if (mIsDeleteModel) { //删除模式
                    convertView.setVisibility(View.INVISIBLE);
                } else {
                    convertView.setVisibility(View.VISIBLE);
                    holder.photo.setImageResource(R.drawable.em_smiley_add_btn_pressed);
                    holder.delete.setVisibility(View.GONE);
                    holder.name.setVisibility(View.INVISIBLE);
                }
            } else {//群成员处理
                convertView.setVisibility(View.VISIBLE);
                holder.name.setVisibility(View.VISIBLE);
                holder.name.setText(userInfo.getName());
                holder.photo.setImageResource(R.drawable.em_default_avatar);
                if (mIsDeleteModel) { //删除模式
                    holder.delete.setVisibility(View.VISIBLE);
                } else {
                    holder.delete.setVisibility(View.GONE);
                }
            }
            //点击事件处理
            if (i == getCount() - 1) { //减号
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mIsDeleteModel) {
                            mIsDeleteModel = true;
                            notifyDataSetChanged();
                        }
                    }
                });
            } else if (i == getCount() - 2) {//加号
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnGroupDetailListener.onAddMembers();
                    }
                });
            } else {//普通成员
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnGroupDetailListener.onDeleteMember(userInfo);
                    }
                });
            }
        } else { //普通群成员
            if (i == getCount() - 1 || i == getCount() - 2) {
                convertView.setVisibility(View.GONE);
            } else {
                convertView.setVisibility(View.VISIBLE);
                holder.name.setText(userInfo.getName());
                holder.photo.setImageResource(R.drawable.em_default_avatar);
                holder.delete.setVisibility(View.GONE);
            }
        }
        //返回view
        return convertView;
    }

    private class ViewHolder {
        private ImageView photo;
        private ImageView delete;
        private TextView name;
    }

    public interface OnGroupDetailListener {
        //添加群成员方法
        void onAddMembers();

        //删除群成员方法
        void onDeleteMember(UserInfo userInfo);
    }
}
