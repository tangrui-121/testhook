package com.hua.testhook.hook.hooklaunch;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookLaunchASMHelper {

    public static void hook() {
        try {
            // 取hook对象
            Class<?> ActivityManagerClz;
            final Object IActivityManagerObj;
            // 26前后源码实现方式不一样
            if (Build.VERSION.SDK_INT >= 26) {
                ActivityManagerClz = Class.forName("android.app.ActivityManager");
                Method getServiceMethod = ActivityManagerClz.getDeclaredMethod("getService");
                IActivityManagerObj = getServiceMethod.invoke(null);
            } else {
                ActivityManagerClz = Class.forName("android.app.ActivityManagerNative");
                Method getServiceMethod = ActivityManagerClz.getDeclaredMethod("getDefault");
                IActivityManagerObj = getServiceMethod.invoke(null);
            }

            // 创建自己的代理类对象
            Class<?> IActivityManagerClz = Class.forName("android.app.IActivityManager");
            Object proxyIActivityManager = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{IActivityManagerClz}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (method.getName().equals("startActivity")) {
                                Log.e("HookLaunchActivity", "全局hook 到了 startActivity");
                            }
                            return method.invoke(IActivityManagerObj, args);
                        }
                    });

            Field IActivityManagerSingletonField;
            if (Build.VERSION.SDK_INT >= 26) {
                IActivityManagerSingletonField = ActivityManagerClz.getDeclaredField("IActivityManagerSingleton");
            } else {
                IActivityManagerSingletonField = ActivityManagerClz.getDeclaredField("gDefault");
            }
            IActivityManagerSingletonField.setAccessible(true);
            Object IActivityManagerSingletonObj = IActivityManagerSingletonField.get(null);
            Class<?> SingletonClz = Class.forName("android.util.Singleton");
            Field mInstanceField = SingletonClz.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            mInstanceField.set(IActivityManagerSingletonObj, proxyIActivityManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}