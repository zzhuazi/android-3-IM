package com.ljh.im.model;

import android.content.Context;

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
    }

    //获取全局线程池对象
    public ExecutorService getGlobalThreadPool(){
        return executors;
    }
}
