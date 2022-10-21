package com.hua.testhook

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View.inflate
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import com.hua.permissionmonitor.PermissionMonitor
import com.hua.testhook.binder.BinderActivity
import com.hua.testhook.hook.hookclick.HookSetOnClickListenerHelper
import com.hua.testhook.hook.hooklaunch.HookLaunchASMHelper
import com.hua.testhook.hook.hooklaunch.HookLaunchActivityHelper
import com.hua.testhook.hook.hooklaunch.HookLaunchContextHelper
import com.hua.testhook.hook.hooktoast.HookToastHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        LayoutInflater.from(this).inflate()


        PermissionMonitor.start(false)
        findViewById<TextView>(R.id.tv).setOnClickListener {
            val clip = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clip.setPrimaryClip(ClipData.newPlainText("t", "text"))
        }

        findViewById<TextView>(R.id.tv2).setOnClickListener {
            startActivity(Intent(this, BinderActivity::class.java))
        }

         HookToastHelper.hookToast()
        findViewById<TextView>(R.id.tv3).setOnClickListener {
            Toast.makeText(this, "hhhh", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.tv4).setOnClickListener {
            Log.e("ProxyOnClickListener", "点击事件本身")
        }
        HookSetOnClickListenerHelper.hook(this, findViewById<TextView>(R.id.tv4))

        HookLaunchActivityHelper.hook(this)
        findViewById<TextView>(R.id.tv5).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        HookLaunchContextHelper.hook()
        findViewById<TextView>(R.id.tv6).setOnClickListener {
            applicationContext.startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        HookLaunchASMHelper.hook()
        findViewById<TextView>(R.id.tv7).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        packageManager.getInstalledPackages(0)

        val clip = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clip.setPrimaryClip(ClipData.newPlainText("t", "text"))

    }
}