package com.hua.testhook.hook.hooklaunch;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HookLaunchContextHelper {

    public static void hook(){
        try {
            Class<?> ActivityThreadClz = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = ActivityThreadClz.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            Object activityThreadObj = sCurrentActivityThreadField.get(null);//静态变量的属性get不需要参数，传null即可.
            Field mInstrumentationField = activityThreadObj.getClass().getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation base = (Instrumentation) mInstrumentationField.get(activityThreadObj);

            ProxyInstrumentation proxyInstrumentation = new ProxyInstrumentation(base);
            mInstrumentationField.set(activityThreadObj, proxyInstrumentation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ProxyInstrumentation extends Instrumentation {
        Instrumentation base;

        public ProxyInstrumentation(Instrumentation base) {
            this.base = base;
        }

        public ActivityResult execStartActivity(
                Context who, IBinder contextThread, IBinder token, Activity target,
                Intent intent, int requestCode, Bundle options) {

            Log.e("HookLaunchActivity", "Launcher Context 自己的逻辑");
            try {
                Class<?> InstrumentationClz = Class.forName("android.app.Instrumentation");
                Method execStartActivity = InstrumentationClz.getDeclaredMethod("execStartActivity",
                        Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
                return (ActivityResult) execStartActivity.invoke(base, who, contextThread, token, target, intent, requestCode, options);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
