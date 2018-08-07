package com.ljh.im.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMucSharedFile;
import com.ljh.im.model.bean.GroupInfo;
import com.ljh.im.model.bean.InvitationInfo;
import com.ljh.im.model.bean.UserInfo;
import com.ljh.im.utils.Constant;
import com.ljh.im.utils.SpUtils;

import java.util.List;

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

        //注册一个群信息变化的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(emGroupChangeListener);
    }

    private final EMGroupChangeListener emGroupChangeListener = new EMGroupChangeListener() {
        //接收到群组加入邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            //数据库更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, inviter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_INVITE);
            Model.getInstance().getDbManager().getInviteDao().addInvitation(invitationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //用户申请加入群
        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applyer, String reason) {
            //数据更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, applyer));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
            Model.getInstance().getDbManager().getInviteDao().addInvitation(invitationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //加群申请被同意
        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {
            //更新数据
            InvitationInfo invationInfo = new InvitationInfo();
            invationInfo.setGroup(new GroupInfo(groupName, groupId, accepter));
            invationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
            Model.getInstance().getDbManager().getInviteDao().addInvitation(invationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //加群申请被拒绝
        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
            //更新数据
            InvitationInfo invationInfo = new InvitationInfo();
            invationInfo.setGroup(new GroupInfo(groupName, groupId, decliner));
            invationInfo.setReason(reason);
            invationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
            Model.getInstance().getDbManager().getInviteDao().addInvitation(invationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //群组邀请被同意
        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {
            //更新数据
            InvitationInfo invationInfo = new InvitationInfo();
            invationInfo.setGroup(new GroupInfo(groupId, groupId, inviter));
            invationInfo.setReason(reason);
            invationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDbManager().getInviteDao().addInvitation(invationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //群组邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
            //更新数据
            InvitationInfo invationInfo = new InvitationInfo();
            invationInfo.setGroup(new GroupInfo(groupId, groupId, invitee));
            invationInfo.setReason(reason);
            invationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);
            Model.getInstance().getDbManager().getInviteDao().addInvitation(invationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override
        public void onUserRemoved(String s, String s1) {
            //群成员被删除
        }

        @Override
        public void onGroupDestroyed(String s, String s1) {
            //群被解散
        }

        //接收邀请时自动加入到群组的通知
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            //更新数据
            InvitationInfo invationInfo = new InvitationInfo();
            invationInfo.setGroup(new GroupInfo(groupId, groupId, inviter));
            invationInfo.setReason(inviteMessage);
            invationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDbManager().getInviteDao().addInvitation(invationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override
        public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
            //成员禁言的通知
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override
        public void onMuteListRemoved(String groupId, final List<String> mutes) {
            //成员从禁言列表里移除通知
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override
        public void onAdminAdded(String groupId, String administrator) {
            //增加管理员的通知
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override
        public void onAdminRemoved(String groupId, String administrator) {
            //管理员移除的通知
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override
        public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
            //群所有者变动通知
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
        @Override
        public void onMemberJoined(final String groupId,  final String member){
            //群组加入新成员通知
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
        @Override
        public void onMemberExited(final String groupId, final String member) {
            //群成员退出通知
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override
        public void onAnnouncementChanged(String groupId, String announcement) {
            //群公告变动通知
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override
        public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile) {
            //增加共享文件的通知
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override
        public void onSharedFileDeleted(String groupId, String fileId) {
            //群共享文件删除通知
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
    };


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
