package com.hua.testhook.binder2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

public class BinderProxy implements IGradeInterface {

    private final IBinder mBinder;

    private BinderProxy(IBinder binder) {
        mBinder = binder;
    }

    @Override
    public int getStudentGrade(String name) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        int grade = 0;
        data.writeString(name);
        try {
            if (mBinder == null) {
                throw new IllegalStateException("Need Bind Remote Server...");
            }
            mBinder.transact(1000, data, reply, 0);
            grade = reply.readInt();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return grade;
    }

    public static IGradeInterface asInterface(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }

        if (iBinder instanceof IGradeInterface) {
            Log.e("bindermy", "当前进程");
            return (IGradeInterface) iBinder;
        } else {
            Log.e("bindermy", "跨进程");
            return new BinderProxy(iBinder);
        }
    }
}
