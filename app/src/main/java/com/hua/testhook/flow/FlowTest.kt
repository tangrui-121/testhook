package com.hua.testhook.flow

import android.util.Log
import com.hua.testhook.flow.FlowTest.oj1
import com.hua.testhook.flow.FlowTest.requestApiFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlin.concurrent.thread

object FlowTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun oj1(): Flow<Int> {
        val flow = callbackFlow<Int> {
            offer(1)
            trySendBlocking(2)
            trySendBlocking(3)
            awaitClose { Log.d("testflow", "oj1 awaitClose") }
        }
        return flow
    }

    fun requestApi(block: (Int) -> Unit) {
        thread {
            Log.d("testflow", "网络请求")
            Thread.sleep(3000)
            Log.d("testflow", "等待结束")
            block.invoke(3)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun requestApiFlow() {

        val flow = callbackFlow {
            //模拟网络请求
            requestApi {
                Log.d("testflow", "发起请求")
                trySend(it)
            }

            awaitClose {
                Log.d("testflow", "requestApiFlow awaitClose")
            }
        }

        GlobalScope.launch {
            flow.collect {
                Log.d("testflow", "接收结果:$it")
            }
        }

        val channelFlow = channelFlow<String> {
            send("")
            withContext(Dispatchers.IO) {
                send("")
            }
        }


    }
}

fun main(args: Array<String>) {
    GlobalScope.launch {
        oj1().collect {
            Log.d("testflow", "it = " + it)
        }

        oj1().collectIndexed { index, value ->
            Log.d("testflow", "it = " + value)
        }

        flow {
            emit(1)
            delay(50)
            emit(2)
        }.collectLatest {
            delay(100)
            Log.d("testflow", "it = " + it)  // 2  1丢弃
        }

        val array = arrayListOf(0)
        flow {
            emit(1)
            emit(2)
        }.toCollection(array)
        array.forEach {
            println(it) // 0 1 2
        }

        // tolist toset

        val lastFlow = flow {
            emit(1)
            emit(2)
        }
        println(lastFlow.last())
        // lastOrNull first firstOrNull single singleOrNull count fold reduce

        flow { emit("Success") }
            .onStart { emit("Loading") }
            .onCompletion { emit("End") }
            .onEach { println(it) }
            .onEmpty { emit("兜底数据") }


        //在建立订阅之后 回调。和 onStart 有些区别 ，SharedFlow 是热流，因此如果在onStart里发送值，则下游可能接收不到。
        val state = MutableSharedFlow<String>().onSubscription {
            emit("onSubscription")
        }
        state.collect { }

        flow {
            emit(1)
            emit(2)
            emit(3)
            emit(4)
        }.transformWhile {
            emit(it)
            it == 2 || it == 1
        }.collect {
            Log.d("testflow", "it = " + it)  // 1 2 3  ,  4丢弃
        }


        requestApiFlow()
    }
}