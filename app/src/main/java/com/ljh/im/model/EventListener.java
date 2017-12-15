package com.ljh.im.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.ljh.im.model.bean.InvitationInfo;
import com.ljh.im.model.bean.UserInfo;
import com.ljh.im.utils.Constant;
import com.ljh.im.utils.SpUtils;

/**
 * Created by Administrator on 2017/12/14.
 * 全局事件监听类
 */

public class EventListener {
    private Context mContext;
    private final LocalBroadcastManager mLBM;

    public EventListener(Context context) {
        mContext = context;

        //创建一个发送广播的管理者对象
        mLBM = LocalBroadcastManager.getInstance(mContext);
        //注册一个联系人变化的监听
        EMClient.getInstance().contactManager().setContactListener(emContactListener);
    }

    private final EMContactListener emContactListener = new EMContactListener() {
        //联系人增加后执行
        @Override
        public void onContactAdded(String hxId) {
            //数据库更新
            Model.getInstance().getDbManager().getContactDao().saveContact(new UserInfo(hxId), true);
            //发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        //联系人删除后执行
        @Override
        public void onContactDeleted(String hxId) {
            //数据库更新
            Model.getInstance().getDbManager().getContactDao().deleteContactByHxId(hxId);
            Model.getInstance().getDbManager().getInviteDao().removeInvitation(hxId);
            //发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        //接受到联系人的新邀请后执行
        @Override
        public void onContactInvited(String hxId, String reason) {
            // 数据库更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setUser(new UserInfo(hxId));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_INVITE); //新邀请
            Model.getInstance().getDbManager().getInviteDao().addInvitation(invitationInfo);
            //UI红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        //别人同意了好友邀请
        @Override
        public void onFriendRequestAccepted(String hxId) {
            //数据更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setUser(new UserInfo(hxId));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);  //别人同意了你的邀请
            Model.getInstance().getDbManager().getInviteDao().addInvitation(invitationInfo);

            //UI红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        //别人拒绝了好友邀请
        @Override
        public void onFriendRequestDeclined(String s) {
            //UI红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
    };

}
