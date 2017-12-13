package com.ljh.im.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ljh.im.model.bean.UserInfo;
import com.ljh.im.model.db.UserAccountDB;

/**
 * Created by Administrator on 2017/12/12.
 * 用户账号数据库操作类
 */

public class UserAccountDao {

    private static final String TAG = UserAccountDao.class.getSimpleName();
    private final UserAccountDB mHelper;

    public UserAccountDao(Context context) {
        mHelper = new UserAccountDB(context);
    }

    //添加用户到数据库
    public void addAccount(UserInfo user){
        //获取数据库对象
        SQLiteDatabase db = mHelper.getWritableDatabase();
        //执行添加操作
        ContentValues values = new ContentValues();
        values.put(UserAccountTable.COL_NAME, user.getName());
        values.put(UserAccountTable.COL_HXID, user.getHxid());
        values.put(UserAccountTable.COL_NICK, user.getNick());
        values.put(UserAccountTable.COL_PHOTO, user.getPhoto());

        db.replace(UserAccountTable.TAB_NAME, null, values);
    }

    //根据环信id获取所有用户信息
    public UserInfo getAccountByHxId(String hxId){
        //获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //操作数据库
        String sql = "select * from " + UserAccountTable.TAB_NAME
                + " where " + UserAccountTable.COL_HXID
                + "=?";
        UserInfo userInfo = null;
        Cursor cursor = db.rawQuery(sql, new String[]{hxId});
        if (cursor.moveToNext()){
            userInfo = new UserInfo();
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO)));
        }
        //关闭资源
        cursor.close();
        Log.i(TAG, "getAccountByHxId: " + userInfo.toString());
        //返回数据
        return userInfo;
    }
}
