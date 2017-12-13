package com.ljh.im.model;

import android.content.Context;

import com.ljh.im.model.bean.UserInfo;
import com.ljh.im.model.dao.UserAccountDao;
import com.ljh.im.model.db.DBManager;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/12/11.
 */
//数据模型层全局类
public class Model {
    private Context mContext;
    private ExecutorService executors = Executors.newCachedThreadPool();

    //创建对象
    private static Model model = new Model();
    private UserAccountDao userAccountDao;
    private DBManager dbManager;

    //私有化构造
    private Model() {
    }

    //获得单例对象
    public static Model getInstance(){
        return model;
    }

    // 初始化的方法
    public void init(Context context){
        mContext = context;
        //创建用户账号操作类对象
        userAccountDao = new UserAccountDao(mContext);
    }

    //获取全局线程池对象
    public ExecutorService getGlobalThreadPool(){
        return executors;
    }

    //用户登录成功的处理方法
    public void loginSuccess(UserInfo account) {
        //校验
        if (account == null){
            return;
        }
        if (dbManager != null){
            dbManager.close();
        }
        dbManager = new DBManager(mContext, account.getName());
    }

    //获取DBManager
    public DBManager getDbManager(){
        return dbManager;
    }

    //获取用户账号数据的操作类方法
    public UserAccountDao getUserAccountDao(){
        return userAccountDao;
    }
}
