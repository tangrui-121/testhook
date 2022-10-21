package com.hua.testhook.binder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class GradeService extends Service {

    public static final int REQUEST_CODE = 1000;

    public HashMap<String, Integer> StudentMap = new HashMap() {{
        put("nihao", 2);
        put("nihao2", 22);
        put("nihao3", 23);
        put("nihao4", 24);
    }};

    private final Binder mBinder = new Binder() {
        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            if (code == REQUEST_CODE) {
                String name = data.readString();
                int studentGrade = getStudentGrade(name);
                if (reply != null) {
                    reply.writeInt(studentGrade);
                }
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        public int getStudentGrade(String name) {
            return StudentMap.get(name);
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
