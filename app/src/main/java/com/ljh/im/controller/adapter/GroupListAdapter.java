package com.ljh.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;
import com.ljh.im.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/15.
 * 群组列表适配器
 */

public class GroupListAdapter extends BaseAdapter {
    private Context mContext;
    private List<EMGroup> mGroups = new ArrayList<>();

    public GroupListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    //刷新方法
    public void refresh(List<EMGroup> groups) {
        if (groups != null && groups.size() >= 0) {
            mGroups.clear();
            mGroups.addAll(groups);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override
    public Object getItem(int i) {
        return mGroups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View converView, ViewGroup viewGroup) {
        //创建或获取viewHolder
        ViewHolder holder = null;
        if (converView == null){
            holder = new ViewHolder();
            converView = View.inflate(mContext, R.layout.item_grouplist, null);
            holder.name = converView.findViewById(R.id.tv_grouplist_name);
            converView.setTag(holder);
        }else{
            holder = (ViewHolder) converView.getTag();
        }
        //获取当前Item数据
        EMGroup emGroup = mGroups.get(i);
        //显示数据
        holder.name.setText(emGroup.getGroupName());
        //返回数据
        return converView;
    }

    private class ViewHolder{
        TextView name;
    }
}
