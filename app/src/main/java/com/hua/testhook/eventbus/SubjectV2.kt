package com.hua.testhook.eventbus


// 传递数据类
data class Entity(
    val key: String,
    var value: Any
)

// 更新回调接口
interface IUpdate {
    fun updateData(entity: Entity)
}

// 观察者抽象类
abstract class Observer : IUpdate {
    abstract val key: String
}

object Subject {
    private val observerMap = hashMapOf<String, ArrayList<Observer>>()

    fun register(observer: Observer) {
        val observerList = observerMap[observer.key]
        if (observerList.isNullOrEmpty()) {
            observerMap[observer.key] = arrayListOf()
        }
        observerMap[observer.key]!!.add(observer)
    }

    fun unregister(observer: Observer) {
        if (observerMap[observer.key].isNullOrEmpty()) return
        observerMap[observer.key]!!.remove(observer)
    }

    fun postMessage(entity: Entity) {
        if (observerMap[entity.key].isNullOrEmpty()) return
        observerMap[entity.key]!!.forEach { it.updateData(entity) }
    }
}

fun main() {
    val mObserver: Observer = object : Observer() {
        override val key: String = "test"
        override fun updateData(entity: Entity) {

        }
    }
    Subject.register(mObserver)
    Subject.unregister(mObserver)
    Subject.postMessage(Entity("test", "页面D的返回数据~"))
}