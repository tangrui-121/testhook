package com.hua.testhook.hook.hooktoast

import android.annotation.SuppressLint
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.lang.reflect.Field
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object HookToastHelper {

    @SuppressLint("SoonBlockedPrivateApi")
    fun hookToast() {
        val toastClass = Toast::class.java
        val sServiceField: Field = toastClass.getDeclaredField("sService")
        sServiceField.setAccessible(true)

        val getServiceMethod = toastClass.getDeclaredMethod("getService")
        getServiceMethod.isAccessible = true
        val service = getServiceMethod.invoke(toastClass)

        val INotificationManager = Class.forName("android.app.INotificationManager")

        val proxy = Proxy.newProxyInstance(Thread::class.java.classLoader,
            arrayOf(INotificationManager),
            object : InvocationHandler {
                override fun invoke(proxy: Any, method: Method, args: Array<out Any>): Any? {
                    Log.e("hook", method.getName() + " args = " + args.size)
                    if (method.name == "enqueueToast") {
                        Log.e("hook", method.getName())
                        getContent(args[1])
                    }
                    return method.invoke(service, *args)
                }
            })
        sServiceField.set(toastClass, proxy)
    }

    @Throws(
        ClassNotFoundException::class,
        NoSuchFieldException::class,
        IllegalAccessException::class
    )
    private fun getContent(arg: Any) {
        // 获取TN的class
        val tnClass = Class.forName(Toast::class.java.name + "\$TN")
        // 获取mNextView的Field
        val mNextViewField = tnClass.getDeclaredField("mNextView")
        mNextViewField.isAccessible = true
        // 获取mNextView实例
        val mNextView = mNextViewField[arg] as LinearLayout
        // 获取textview
        val childView = mNextView.getChildAt(0) as TextView
        // 获取文本内容
        val text = childView.text
        // 替换文本并赋值
        childView.text = "hook了：" + text.toString()
        Log.e("hook", "content: " + childView.text)
    }
}