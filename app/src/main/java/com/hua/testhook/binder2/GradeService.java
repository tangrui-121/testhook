package com.hua.testhook.binder2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class GradeService extends Service {

    public static final int REQUEST_CODE = 1001;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new GradeBinder();
    }
}
