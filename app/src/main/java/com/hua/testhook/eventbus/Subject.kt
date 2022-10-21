//package com.hua.testhook.eventbus
//
//
//// 传递数据类
//data class Entity(
//    val key: String,
//    var value: Any
//)
//
//// 更新回调接口
//interface IUpdate {
//    fun updateData(entity: Entity)
//}
//
//// 观察者抽象类
//abstract class Observer : IUpdate
//
//object Subject {
//    private val observerMap = hashMapOf<String, ArrayList<Observer>>()
//
//    fun register(key: String, observer: Observer) {
//        val observerList = observerMap[key]
//        if (observerList.isNullOrEmpty()) {
//            observerMap[key] = arrayListOf()
//        }
//        observerMap[key]!!.add(observer)
//    }
//
//    fun unregister(key: String, observer: Observer) {
//        if (observerMap[key].isNullOrEmpty()) return
//        observerMap[key]!!.remove(observer)
//    }
//
//    fun postMessage(key: String, entity: Entity) {
//        if (observerMap[key].isNullOrEmpty()) return
//        observerMap[key]!!.forEach { it.updateData(entity) }
//    }
//}
//
//fun main() {
//    val mObserver: Observer = object : Observer() {
//        override fun updateData(entity: Entity) {
//
//        }
//    }
//    Subject.register("test", mObserver)
//    Subject.unregister("test", mObserver)
//    Subject.postMessage("test", Entity("test", "页面D的返回数据~"))
//}