package com.ljh.im.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ljh.im.model.dao.UserAccountTable;

/**
 * Created by Administrator on 2017/12/12.
 */

public class UserAccountDB extends SQLiteOpenHelper {
    //构造
    public UserAccountDB(Context context) {
        super(context, "account.db", null, 1);
    }

    //数据库创建时调用
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 创建数据库表的语句
        sqLiteDatabase.execSQL(UserAccountTable.CREATE_TAB);
    }

    //数据库更新时调用
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
