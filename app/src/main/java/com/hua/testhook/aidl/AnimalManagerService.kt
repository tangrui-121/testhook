package com.hua.testhook.aidl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.hua.testhook.aidl_interface.IAnimalManager
import com.hua.testhook.aidl_model.Animal

class AnimalManagerService : Service() {

    val stone: MutableList<Animal> = ArrayList<Animal>()

    override fun onBind(intent: Intent?): IBinder? {
        println(this.toString() + " onBind")
        return object : IAnimalManager.Stub() {
            override fun addAnimal(animal: Animal?) {
                animal?.let {
                    stone.add(it)
                    println("目前有：${stone.size}，新增的是：${it.name}")
                }
            }

            override fun getAnimals(): MutableList<Animal> {
                return stone
            }

        }
    }
}