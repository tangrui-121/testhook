package com.hua.testhook.binder2;

import static com.hua.testhook.binder2.GradeService.REQUEST_CODE;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class GradeBinder extends Binder implements IGradeInterface {

    public HashMap<String, Integer> StudentMap = new HashMap() {{
        put("nihao", 2);
        put("nihao2", 22);
        put("nihao3", 23);
        put("nihao4", 24);
    }};

    @Override
    public int getStudentGrade(String name) {
        return StudentMap.get(name);
    }

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
}
