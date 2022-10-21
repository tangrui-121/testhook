package com.hua.testhook.binder;


import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.hua.testhook.R;
import com.hua.testhook.aidl_interface.IAnimalManager;
import com.hua.testhook.aidl_model.Animal;
import com.hua.testhook.binder2.BinderProxy;
import com.hua.testhook.binder2.IGradeInterface;

// https://blog.csdn.net/cike110120/article/details/85333101
public class BinderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder);
        binder();
        binder2();
        binder3();
    }

    private void binder() {
        findViewById(R.id.btn_bind_service).setOnClickListener(view -> {
            bindGradeService();
        });
        findViewById(R.id.btn_find_grade).setOnClickListener(view -> {
            Toast.makeText(this, "grade = " + getStudentGrade("nihao2"), Toast.LENGTH_SHORT).show();
        });
    }

    private IBinder mRemoteBinder;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteBinder = service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteBinder = null;
        }
    };

    private void bindGradeService() {
        String action = "android.intent.action.server.gradeservice";
        Intent intent = new Intent(action);
        intent.setPackage(getPackageName());
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private int getStudentGrade(String name) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        int grade = 0;
        data.writeString(name);
        try {
            if (mRemoteBinder == null) {
                throw new IllegalStateException("Need Bind Remote Server...");
            }
            mRemoteBinder.transact(1000, data, reply, 0);
            grade = reply.readInt();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return grade;
    }

    private void binder2() {
        findViewById(R.id.btn_bind_service2).setOnClickListener(view -> {
            bindService2();
        });
        findViewById(R.id.btn_find_grade2).setOnClickListener(view -> {
            Toast.makeText(this, "grade = " + mBinderProxy.getStudentGrade("nihao3"), Toast.LENGTH_SHORT).show();
        });
    }

    private IGradeInterface mBinderProxy;
    private final ServiceConnection mServiceConnection2 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderProxy = BinderProxy.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinderProxy = null;
        }
    };

    private void bindService2() {
        String action = "android.intent.action.server.gradeservice";
        Intent intent = new Intent(action);
        intent.setPackage(getPackageName());
        bindService(intent, mServiceConnection2, BIND_AUTO_CREATE);
    }

    private void binder3() {
        findViewById(R.id.btn_bind_service3).setOnClickListener(view -> {
            bindService3();
        });
        EditText editText = findViewById(R.id.ed_animal);
        findViewById(R.id.btn_animal_add).setOnClickListener(view -> {
            try {
                iAnimalManager.addAnimal(new Animal(editText.getText().toString()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.btn_animal_print).setOnClickListener(view -> {
            try {
                for (int i = 0; i < iAnimalManager.getAnimals().size(); i++) {
                    System.out.println(iAnimalManager.getAnimals().get(i).getName());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    private IAnimalManager iAnimalManager;
    private final ServiceConnection mServiceConnection3 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iAnimalManager = IAnimalManager.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iAnimalManager = null;
        }
    };

    private void bindService3() {
        String action = "android.intent.action.server.animalService";
        Intent intent = new Intent(action);
        intent.setPackage(getPackageName());
        bindService(intent, mServiceConnection3, BIND_AUTO_CREATE);
    }
}