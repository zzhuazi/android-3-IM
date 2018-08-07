package com.ljh.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ljh.im.R;
import com.ljh.im.model.bean.PickContactInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/12/18.
 * 选择联系人的页面适配器
 */

public class PickContactAdapter extends BaseAdapter {
    private Context mContext;
    private List<PickContactInfo> mPicks = new ArrayList<>();
    private List<String> mExistMembers = new ArrayList<>(); //保存群中已经存在的成员
    public PickContactAdapter(Context mContext, List<PickContactInfo> picks, List<String> existMembers) {
        this.mContext = mContext;
        if (picks != null && picks.size() >= 0) {
            mPicks.clear();
            mPicks.addAll(picks);
        }
        //加载已存在的群员集合
        mExistMembers.clear();
        mExistMembers.addAll(existMembers);
    }

    @Override
    public int getCount() {
        return mPicks.size();
    }

    @Override
    public Object getItem(int i) {
        return mPicks == null ? 0 : mPicks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View converView, ViewGroup viewGroup) {
        //创建和获取viewHolder
        ViewHolder holder = null;
        if (converView == null){
            holder = new ViewHolder();

            converView = View.inflate(mContext, R.layout.item_pick, null);
            holder.cb = converView.findViewById(R.id.cb_pick);
            holder.tv_name = converView.findViewById(R.id.tv_pick_name);

            converView.setTag(holder);
        }else{
            holder = (ViewHolder) converView.getTag();
        }
        //获取当前item数据
        PickContactInfo pickContactInfo = mPicks.get(i);
        //显示数据
        holder.tv_name.setText(pickContactInfo.getUser().getName());
        holder.cb.setChecked(pickContactInfo.isChecked());

        if (mExistMembers.contains(pickContactInfo.getUser().getHxid())){
            holder.cb.setChecked(true);
            pickContactInfo.setChecked(true);
        }
        //返回
        return converView;
    }

    //获取选择的联系人
    public List<String> getPickContacts() {
        List<String> picks = new ArrayList<>();
        for (PickContactInfo pick: mPicks) {
            //判断是否选中
            if (pick.isChecked()){
                picks.add(pick.getUser().getName());
            }
        }
        return picks;
    }

    private class ViewHolder{
        private CheckBox cb;
        private TextView tv_name;
    }
}
