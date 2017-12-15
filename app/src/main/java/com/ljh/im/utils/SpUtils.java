package com.ljh.im.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ljh.im.IMApplication;

/**
 * Created by Administrator on 2017/12/13.
 * Sp工具类（单例）
 * 保存与获取数据
 */

public class SpUtils {
    public static final String IS_NEW_INVITE = "is_new_invite"; //新的邀请标记
    private static  SpUtils instance = new SpUtils();
    private static SharedPreferences mSp;

    private SpUtils() {
    }

    public static SpUtils getInstance(){
        if (mSp == null){
            mSp = IMApplication.getGlobalApplication().getSharedPreferences("im", Context.MODE_PRIVATE);
        }
        return instance;
    }

    //保存
    public void save(String key, Object value){
        if (value instanceof String){
            mSp.edit().putString(key, (String) value).apply();
        }else if (value instanceof Boolean){
            mSp.edit().putBoolean(key, (Boolean) value).apply();
        }else if (value instanceof Integer){
            mSp.edit().putInt(key, (Integer) value).apply();
        }
    }

    //获取String数据的方法
    public String getString(String key, String defValue){
        return mSp.getString(key, defValue);
    }

    //获取Boolean数据的方法
    public Boolean getBoolean(String key, Boolean defValue){
        return mSp.getBoolean(key,defValue);
    }

    //获取Integer数据的方法
    public Integer getInteger(String key, Integer defValue){
        return mSp.getInt(key, defValue);
    }
}
