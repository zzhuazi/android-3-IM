package com.ljh.im.model.db;

import android.content.Context;

import com.ljh.im.model.dao.ContactDao;
import com.ljh.im.model.dao.InviteDao;

/**
 * Created by Administrator on 2017/12/13.
 * 联系人和邀请信息表的操作类的管理类
 */

public class DBManager {
    private DBHelper dbHelper;
    private final ContactDao contactDao;
    private final InviteDao inviteDao;

    public DBManager(Context context, String name) {
        //创建数据库
        dbHelper = new DBHelper(context,name);

        //数据该数据中两张表的操作类
        contactDao = new ContactDao(dbHelper);
        inviteDao = new InviteDao(dbHelper);
    }

    //获取联系人表的操作类对象
    public ContactDao getContactDao() {
        return contactDao;
    }

    //获取邀请信息表的操作类对象
    public InviteDao getInviteDao() {
        return inviteDao;
    }

    //关闭数据库的方法
    public void close() {
        dbHelper.close();
    }
}
